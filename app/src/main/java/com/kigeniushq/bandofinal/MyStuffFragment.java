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
import android.widget.AbsListView;
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
public class MyStuffFragment extends Fragment {
    public static String TWITTER_CONSUMER_KEY = "hrMzn2Q8iTsK9bzrqwDfEHwlE";
    public static String TWITTER_CONSUMER_SECRET = "CkWtGZ1zBPMOypbdwf3Q4EXCVABDeanuG3nM6nR6enLhHSnJge";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String INSTYPREFIX_URL = "https://api.instagram.com/v1/users/";
    private final String INSTSUFIX_URL = "/media/recent?client_id=";
    private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
    private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
    private final String callback_url = "http://phantom.com";

    long iGStarttime, iGEndTime;

    private InstagramApp mApp;
    private InstagramApp.OAuthAuthenticationListener listener;
    boolean gettingTwit = false;

    private ArrayList<BandoPost> bandoArray;
    private ObservableListView listView;
    private FeedListAdapter listAdapter;

    public ActionBar mActionBar;

    private ProgressBar pbHorizontal = null;
    private TextView tvProgressHorizontal = null;

    /* current progress on progress bars */
    private int progress = 0;
    private int totalForProg = 0;

    public MyStuffFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

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
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity());
        pbHorizontal = (ProgressBar) getActivity().findViewById(R.id.pb_horizontal);
        tvProgressHorizontal = (TextView) getActivity().findViewById(R.id.tv_progress_horizontal);
        progress = 0;
        totalForProg = 0;

        pbHorizontal.setVisibility(View.VISIBLE);
        tvProgressHorizontal.setVisibility(View.VISIBLE);

        bandoArray = new ArrayList<>();

        List<URL> allIGUsers = new ArrayList<>();
        if ((preferences.getBoolean("MUSIC", true)))
            allIGUsers.addAll(musiclisti);
        if (preferences.getBoolean("SPORTS", false))
            allIGUsers.addAll(sportlisti);
        if (preferences.getBoolean("CULTURE", false))
            allIGUsers.addAll(culturelisti);
        if (preferences.getBoolean("COMEDY", false))
            allIGUsers.addAll(comedylisti);
        if (preferences.getBoolean("PHOTOS & ART", false))
            allIGUsers.addAll(artlisti);

        iGStarttime = System.nanoTime();
        new GetInstagramImagesAsync().execute(allIGUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);
        return rootView;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        long startTime, endTime;

        public DownloadTask() {
            startTime = System.nanoTime();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.v("benmark", "onProgressUpdate = " + String.valueOf(values[0]));
            super.onProgressUpdate(values);
            postProgress();
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
            try {
                List<String> allTwitterUsers = new ArrayList<String>();
                if ((preferences.getBoolean("MUSIC", false)))
                    allTwitterUsers.addAll(musiclistt);
                if (preferences.getBoolean("SPORTS", false))
                    allTwitterUsers.addAll(sportlistt);
                if (preferences.getBoolean("CULTURE", false))
                    allTwitterUsers.addAll(culturelistt);
                if (preferences.getBoolean("COMEDY", false))
                    allTwitterUsers.addAll(comedylistt);
                totalForProg = totalForProg + allTwitterUsers.size();
                for (String individualId : allTwitterUsers) {
                    progress++;
                    publishProgress(progress);
                    List<twitter4j.Status> statusesMeek = twitter.getUserTimeline(individualId, paging);
                    twitter4j.Status firstStatus = statusesMeek.get(0);
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(firstStatus.getSource());
                    bp.setPostSourceSite(firstStatus.getSource());
                    bp.setPostText(firstStatus.getText());
                    bp.setPostType("twitter");
                    if (firstStatus.getMediaEntities().length > 0) {
                        bp.setPostHasImage(true);
                        bp.setImageUrl(firstStatus.getMediaEntities()[0].getMediaURL());
                    } else {
                        bp.setPostHasImage(false);
                    }

                    bp.setUsername("@" + firstStatus.getUser().getScreenName());
                    bp.setUserProfilePic(firstStatus.getUser().getOriginalProfileImageURL());
                    bp.setDateString(Utils.getTimeAgo(firstStatus.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(firstStatus.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(firstStatus.getCreatedAt());
                    if (!bandoArray.contains(bp))
                        bandoArray.add(bp);
                }
            } catch (TwitterException tw) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            gettingTwit = false;
            Collections.sort(bandoArray);
            listView = (ObservableListView) getActivity().findViewById(R.id.list);
            listAdapter = new FeedListAdapter(getActivity(), R.layout.feed_item, bandoArray);
            endTime = System.nanoTime();
            long duration = (endTime - startTime);
            double seconds = (double) duration / 1000000000.0;
            Log.v("benmark", "twitter took " + (seconds) + "senconds");
            listView.setAdapter(listAdapter);
            //listView.setScrollViewCallbacks((MainActivity)getActivity());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getPostUrl())));
                }
            });
            tvProgressHorizontal.setVisibility(View.GONE);
            pbHorizontal.setVisibility(View.GONE);
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
            //cutting to sublist
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
            ImageView socialmageView = (ImageView) convertView
                    .findViewById(R.id.socialNetworkStamp);

            if (getItem(position).getPostType().contains("instagram"))
                Picasso.with(getActivity()).load(R.drawable.instagramlogo).into(socialmageView);
            else
                Picasso.with(getActivity()).load(R.drawable.twitterlogo).into(socialmageView);

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
            Picasso.with(getActivity()).load(item.getUserProfilePic()).into(profilePic);
            feedImageView.setVisibility(View.VISIBLE);

            // Feed image
            if (item.isPostHasImage()) {
                Picasso.with(getActivity()).load(item.getImageUrl()).into(feedImageView);
                feedImageView.setVisibility(View.VISIBLE);
            } else {
                feedImageView.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    public class GetInstagramImagesAsync extends AsyncTask<List<URL>, Integer, List<JSONArray>> {

        public GetInstagramImagesAsync() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            postProgress();
        }

        protected List<JSONArray> doInBackground(List<URL>... urls) {
            ArrayList<JSONArray> listOfIGUsersTimelines = new ArrayList<JSONArray>();
            totalForProg = urls[0].size();
            for (URL currentUrl : urls[0]) {
                try {
                    URLConnection tc = currentUrl.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            tc.getInputStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        JSONObject ob = new JSONObject(line);
                        JSONArray js = ob.getJSONArray("data");
                        progress++;
                        publishProgress(progress);
                        String theusername = "@" + js.getJSONObject(0).getJSONObject("user").getString("username");
                        String thestandardImageUrl = js.getJSONObject(0).getJSONObject("images").getJSONObject("low_resolution").getString("url");


                        String link = js.getJSONObject(0).getString("link");
                        String profilePic = js.getJSONObject(0).getJSONObject("user").getString("profile_picture");

                        BandoPost bp = new BandoPost();
                        bp.setPostUrl(link);
                        bp.setUsername(theusername);
                        bp.setPostSourceSite("instagram");
                        if (js.getJSONObject(0).has("caption") && !js.getJSONObject(0).isNull("caption")) {
                            bp.setPostText(js.getJSONObject(0).getJSONObject("caption").getString("text"));
                        } else {
                            bp.setPostText("(No caption)");
                        }
                        bp.setPostType("instagram");
                        bp.setPostHasImage(true);
                        bp.setUserProfilePic(profilePic);
                        bp.setDateString(Utils.getTimeAgo(new Date(Long.parseLong(js.getJSONObject(0).getString("created_time"))).getTime(), getActivity()));
                        bp.setImageUrl(thestandardImageUrl);
                        bp.setDateTime(new Date((long) Long.parseLong(js.getJSONObject(0).getString("created_time")) * 1000));
                        if (!bandoArray.contains(bp)) {
                            bandoArray.add(bp);
                        }
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return listOfIGUsersTimelines;
        }

        protected void onPostExecute(List<JSONArray> igTimelines) {
            if (bandoArray.size() > 0) {
                Collections.sort(bandoArray);
                iGEndTime = System.nanoTime();
                long duration = (iGEndTime - iGStarttime);
                double seconds = (double) duration / 1000000000.0;
                Log.v("benmark", "instagram took " + (seconds) + "senconds");
                new DownloadTask().execute();
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

    ArrayList<URL> musiclisti = new ArrayList<URL>() {{
        try {
            add(new URL(INSTYPREFIX_URL + "14455831" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "6720655" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "25945306" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "18900337" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "266319242" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "10685362" + INSTSUFIX_URL + client_id));
        } catch (MalformedURLException e) {
        }
    }};

    ArrayList<URL> sportlisti = new ArrayList<URL>() {{
        try {
            add(new URL(INSTYPREFIX_URL + "16264572" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "19410587" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "13864937" + INSTSUFIX_URL + client_id));
        } catch (MalformedURLException e) {
        }
    }};

    ArrayList<URL> culturelisti = new ArrayList<URL>() {{
        try {
            add(new URL(INSTYPREFIX_URL + "6380930" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "174247675" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "12281817" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "185087057" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "28011380" + INSTSUFIX_URL + client_id));
        } catch (MalformedURLException e) {
        }
    }};

    ArrayList<URL> comedylisti = new ArrayList<URL>() {{
        try {
            add(new URL(INSTYPREFIX_URL + "1535836050" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "10245461" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "6590609" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "15209885" + INSTSUFIX_URL + client_id));
        } catch (MalformedURLException e) {
        }
    }};

    ArrayList<URL> artlisti = new ArrayList<URL>() {{
        try {
            add(new URL(INSTYPREFIX_URL + "143795932" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "176915912" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "13613836" + INSTSUFIX_URL + client_id));
            add(new URL(INSTYPREFIX_URL + "787132" + INSTSUFIX_URL + client_id));
        } catch (MalformedURLException e) {
        }
    }};

    private void postProgress() {
        int percent = (int) ((progress * 100.0f) / totalForProg);
        Log.v("benmark", "progerss = " + String.valueOf(progress) + "tot = " + String.valueOf(totalForProg));


        String strProgress = String.valueOf(percent) + " %";
        pbHorizontal.setProgress(percent);
                    /* update secondary progress of horizontal progress bar */
        if (percent == 0) {
            pbHorizontal.setSecondaryProgress(0);
        } else {
            pbHorizontal.setSecondaryProgress(percent + 5);
        }
        tvProgressHorizontal.setText(strProgress);
    }
}