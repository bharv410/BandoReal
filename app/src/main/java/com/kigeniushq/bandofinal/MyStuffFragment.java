package com.kigeniushq.bandofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URLConnection;
import java.util.Date;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import classes.BandoPost;
import classes.InstagramApp;
import classes.TwitterLogin;
import classes.Utils;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class MyStuffFragment extends Fragment{
    public static String TWITTER_CONSUMER_KEY = "hrMzn2Q8iTsK9bzrqwDfEHwlE";
    public static String TWITTER_CONSUMER_SECRET = "CkWtGZ1zBPMOypbdwf3Q4EXCVABDeanuG3nM6nR6enLhHSnJge";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String INSTYPREFIX_URL = "https://api.instagram.com/v1/users/";
    private final String INSTSUFIX_URL = "/media/recent?client_id=";
    private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
    private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
    private final String callback_url = "http://phantom.com";

    private InstagramApp mApp;
    private InstagramApp.OAuthAuthenticationListener listener;

    boolean gettingTwit = false;

    private ArrayList<BandoPost> bandoArray;
    private ObservableListView listView;
    private FeedListAdapter listAdapter;

    private ArrayList<Boolean> asyncsLoading;

    public ActionBar mActionBar;

    private ProgressBar progress;

    public MyStuffFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        listener = new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        };
        mApp = new InstagramApp(getActivity(), client_id, client_secret, callback_url);
        mApp.setListener(listener);

        progress = (ProgressBar)getActivity().findViewById(R.id.progressBar1);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity());
        Boolean showMusic = preferences.getBoolean("MUSIC", false);
        Boolean showSports = preferences.getBoolean("SPORTS",false);
        Boolean showCulture = preferences.getBoolean("CULTURE",false);
        Boolean showComedy = preferences.getBoolean("COMEDY",false);

        try{
                bandoArray = new ArrayList<>();
                asyncsLoading = new ArrayList<>();

            List<List<String>> allInstagramUsers = new ArrayList<List<String>>(4);
            if ((showMusic))
                allInstagramUsers.add(musiclisti);
            if(showSports)
                allInstagramUsers.add(sportlisti);
            if(showCulture)
                allInstagramUsers.add(culturelisti);
            if(showComedy)
                allInstagramUsers.add(comedylisti);

            int counter1 = 0;
            for(List<String> individualList : allInstagramUsers){
                for(String individualId : individualList){
                    new GetInstagramImagesAsync(counter1).execute(new URL(INSTYPREFIX_URL + individualId+INSTSUFIX_URL+ client_id));
                    counter1++;
                }
            }
        }catch (MalformedURLException e){
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);
        //startActivity(new Intent(getActivity(), LoadingScreen.class));
        return rootView;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        public DownloadTask(){
        }
        @Override
        protected String doInBackground(String... urls) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

            SharedPreferences mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences(
                    "twitter_login", Activity.MODE_PRIVATE);

            // Access Token
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token,
                    access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build())
                    .getInstance(accessToken);
//First param of Paging() is the page number, second is the number per page (this is capped around 200 I think.
            Paging paging = new Paging(1, 1);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity());
            Boolean showMusic = preferences.getBoolean("MUSIC", false);
            Boolean showSports = preferences.getBoolean("SPORTS",false);
            Boolean showCulture = preferences.getBoolean("CULTURE", false);
            Boolean showComedy = preferences.getBoolean("COMEDY",false);
            try {
                List<List<String>> allTwitterUsers = new ArrayList<List<String>>(4);
                if ((showMusic))
                    allTwitterUsers.add(musiclistt);
                if(showSports)
                    allTwitterUsers.add(sportlistt);
                if(showCulture)
                    allTwitterUsers.add(culturelistt);
                if(showComedy)
                    allTwitterUsers.add(comedylistt);

                int counter1 = 0;
                for(List<String> individualList : allTwitterUsers){
                    for(String individualId : individualList){
                        List<twitter4j.Status> statusesMeek = twitter.getUserTimeline(individualId, paging);
                        for(twitter4j.Status st : statusesMeek){
                            BandoPost bp = new BandoPost();
                            bp.setPostUrl(st.getSource());
                            bp.setPostSourceSite(st.getSource());
                            bp.setPostText(st.getText());
                            bp.setPostType("twitter");
                            bp.setUsername("@" + st.getUser().getScreenName());
                            bp.setUserProfilePic(st.getUser().getOriginalProfileImageURL());
                            bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                            bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                            bp.setDateTime(st.getCreatedAt());
                            if(!bandoArray.contains(bp))
                                bandoArray.add(bp);
                        }
                    }
                }
            }catch (TwitterException tw){

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            gettingTwit = false;
            Collections.sort(bandoArray);
            listView = (ObservableListView) getActivity().findViewById(R.id.list);
            listAdapter = new FeedListAdapter(getActivity(), R.layout.feed_item, bandoArray);
            listView.setAdapter(listAdapter);
            //listView.setScrollViewCallbacks((MainActivity)getActivity());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getPostUrl())));
                }
            });
            progress.setVisibility(View.GONE);
        }
    }
    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }

    public class FeedListAdapter extends ArrayAdapter<BandoPost> {
        private Activity activity;
        private LayoutInflater inflater;
        private final List<BandoPost> bandoPosts;

        public FeedListAdapter(Activity activity, int resource, List<BandoPost> feedItems) {
            super(activity, resource, feedItems);
            this.activity = activity;
            this.bandoPosts = feedItems;
        }

        @Override
        public int getCount() {
            return bandoPosts.size();
        }

        @Override
        public BandoPost getItem(int location) {
            return bandoPosts.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.feed_item, null);


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView timestamp = (TextView) convertView
                    .findViewById(R.id.timestamp);
            TextView statusMsg = (TextView) convertView
                    .findViewById(R.id.txtStatusMsg);
            TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
            ImageView profilePic = (ImageView) convertView
                    .findViewById(R.id.profilePic);
            ImageView feedImageView = (ImageView) convertView
                    .findViewById(R.id.feedImage1);

            BandoPost item = getItem(position);

            name.setText(item.getUsername());

            // Converting timestamp into x ago format

            timestamp.setText(item.getDateString());

            // Chcek for empty status message
            if (!TextUtils.isEmpty(item.getPostText())) {
                statusMsg.setText(item.getPostText());
                statusMsg.setVisibility(View.VISIBLE);
            } else {
                // status is empty, remove from view
                statusMsg.setVisibility(View.GONE);
            }
            // user profile pic
            //profilePic.setImageUrl(item.getProfilePic(), imageLoader);

            // Feed image
            if (item.getImageUrl() != null) {
                if(!item.getPostType().contains("twitter")) {
                    Picasso.with(getActivity()).load(item.getImageUrl()).into(feedImageView);
                    feedImageView.setVisibility(View.VISIBLE);
                }
                Picasso.with(getActivity()).load(item.getUserProfilePic()).into(profilePic);
            } else {
                feedImageView.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    public class GetInstagramImagesAsync extends AsyncTask<URL, Void, Integer> {
        private JSONArray jsonArr;
        private int pos;

        public GetInstagramImagesAsync (int position){
            this.pos = position;
        }


        protected Integer doInBackground(URL... urls) {
            try{
                if(asyncsLoading.size()>0)
                asyncsLoading.add(pos,new Boolean(true));

                URLConnection tc = urls[0].openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    jsonArr = ob.getJSONArray("data");
                }
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            if(jsonArr!=null)
                return jsonArr.length();
            else
                return null;
        }

        protected void onPostExecute(Integer result) {
            try {
                if(jsonArr!=null) {
                    for (int i = 0; i < jsonArr.length() && i < 3; i++) {
                        String theusername = "@"+jsonArr.getJSONObject(i).getJSONObject("user").getString("username");
                        String thestandardImageUrl = jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                        String caption = jsonArr.getJSONObject(i).getJSONObject("caption").getString("text");
                        String link = jsonArr.getJSONObject(i).getString("link");
                        String profilePic = jsonArr.getJSONObject(i).getJSONObject("user").getString("profile_picture");

                        BandoPost bp = new BandoPost();
                        bp.setPostUrl(link);
                        bp.setUsername(theusername);
                        bp.setPostSourceSite("instagram");
                        bp.setPostText(caption);
                        bp.setPostType("instagram");
                        bp.setUserProfilePic(profilePic);
                        bp.setDateString(Utils.getTimeAgo(new Date(Long.parseLong(jsonArr.getJSONObject(i).getString("created_time"))).getTime(), getActivity()));
                        bp.setImageUrl(thestandardImageUrl);
                        bp.setDateTime(new Date((long) Long.parseLong(jsonArr.getJSONObject(i).getString("created_time")) * 1000));
                        if (!bandoArray.contains(bp))
                            bandoArray.add(bp);
                    }
                    Collections.sort(bandoArray);
                    if (asyncsLoading.size() > 0)
                        asyncsLoading.set(pos, new Boolean(false));
                    ArrayList<Boolean> localCopyOfInstagramTasksLoading = asyncsLoading;
                    boolean downloadIGDone = true;
                    for (Boolean currentCircle: localCopyOfInstagramTasksLoading) {
                        if(currentCircle==true){
                            Log.v("benmark", "not done yet ");
                            downloadIGDone = false;
                        }
                    };
                    if(!gettingTwit && downloadIGDone){
                        Log.v("benmark", "downloadIGDone ");
                        gettingTwit = true;
                        new DownloadTask().execute();
                    }
                }else{
                    new DownloadTask().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ArrayList<String> musiclistt = new ArrayList<String>() {{
        add("Hot97");
        add("Kendricklamar");
        add("drake");
        add("metroboomin");
        add("applemusic");
        add("1future");
    }};

    ArrayList<String> sportlistt = new ArrayList<String>() {{
        add("KingJames");
        add("kobebryant");
        add("kdtrey5");
        add("Chris_Broussard");
        add("NikeBasketball");
        add("uabasketball");
        add("stephenasmith");
        add("RealSkipBayless");
    }};

    ArrayList<String> culturelistt = new ArrayList<String>() {{
        add("nicekicks");
    }};

    ArrayList<String> comedylistt = new ArrayList<String>() {{
        add("desusnice");
        add("dahoodvines");
        add("lilduval");
    }};

    ArrayList<String> artlistt = new ArrayList<String>() {{
        add("Streetartnews");
        add("History_Pics");
    }};

    ArrayList<String> musiclisti = new ArrayList<String>() {{
        add("14455831");
        add("6720655");
        add("546693819");
        add("18900337");
        add("6720655");
        add("266319242");
        add("10685362");
    }};

    ArrayList<String> sportlisti = new ArrayList<String>() {{
        add("16264572");
        add("19410587");
        add("13864937");
    }};

    ArrayList<String> culturelisti = new ArrayList<String>() {{
        add("6380930");
        add("174247675");
        add("12281817");
        add("185087057");
        add("28011380");
    }};

    ArrayList<String> comedylisti = new ArrayList<String>() {{
        add("atown0705");
        add("BdotAdot5");
        add("kevinhart4real");
        add("dormtainment");
    }};

    ArrayList<String> artlisti = new ArrayList<String>() {{
        add("theo.skudra");
        add("vanstyles");
        add("thecamkirk");
        add("natgeo");
    }};
}