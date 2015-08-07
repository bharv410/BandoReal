package com.kigeniushq.bandofinal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
    private final String INSTSUFIX_URL = "/media/recent?access_token=";
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
    }

    @Override
    public void onStart() {
        super.onStart();

        try{
            if(mApp.hasAccessToken()){
                bandoArray = new ArrayList<>();
                asyncsLoading = new ArrayList<>();
                //mApp.authorize();
                new GetInstagramImagesAsync(0).execute(new URL("https://api.instagram.com/v1/users/19410587/media/recent?access_token=" + mApp.getAccessToken()));
                new GetInstagramImagesAsync(1).execute(new URL(INSTYPREFIX_URL + "6380930"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(2).execute(new URL(INSTYPREFIX_URL + "50417061"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(3).execute(new URL(INSTYPREFIX_URL + "8947681"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(4).execute(new URL(INSTYPREFIX_URL + "6720655"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(5).execute(new URL(INSTYPREFIX_URL + "6590609"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(6).execute(new URL(INSTYPREFIX_URL + "14455831"+INSTSUFIX_URL+ mApp.getAccessToken()));
                new GetInstagramImagesAsync(7).execute(new URL(INSTYPREFIX_URL + "266319242"+INSTSUFIX_URL+ mApp.getAccessToken()));

            }else{
                Toast.makeText(getActivity(), "Log in to instagram to get posts on feed", Toast.LENGTH_LONG);
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
            Paging paging = new Paging(1, 3);
            try {
                List<twitter4j.Status> statusesMeek = twitter.getUserTimeline("Pitchfork", paging);

                List<twitter4j.Status> statusesKendall = twitter.getUserTimeline("48tweetsofpower", paging);

                List<twitter4j.Status> statusesDennis = twitter.getUserTimeline("menacetodennis", paging);

                List<twitter4j.Status> statusesBen = twitter.getUserTimeline("nyctsubway", paging);

                List<twitter4j.Status> statusesgothamist = twitter.getUserTimeline("gothamist", paging);

                List<twitter4j.Status> statusesHot97 = twitter.getUserTimeline("Hot97", paging);

                List<twitter4j.Status> statusesKendrick = twitter.getUserTimeline("Kendricklamar", paging);

                List<twitter4j.Status> statusesapple = twitter.getUserTimeline("applemusic", paging);

                List<twitter4j.Status> statusesmetro = twitter.getUserTimeline("metroboomin", paging);

                List<twitter4j.Status> statusesfutu = twitter.getUserTimeline("1future", paging);

                for(twitter4j.Status st : statusesgothamist){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }

                for(twitter4j.Status st : statusesHot97){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }

                for(twitter4j.Status st : statusesKendrick){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }
                for(twitter4j.Status st : statusesapple){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }

                for(twitter4j.Status st : statusesmetro){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }

                for(twitter4j.Status st : statusesMeek){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }

                for(twitter4j.Status st : statusesKendall){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }
                for(twitter4j.Status st : statusesDennis){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
                }
                for(twitter4j.Status st : statusesBen){
                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(st.getSource());
                    bp.setPostSourceSite(st.getSource());
                    bp.setPostText(st.getText());
                    bp.setPostType("twitter");
                    bp.setDateString(Utils.getTimeAgo(st.getCreatedAt().getTime(), getActivity()));
                    bp.setImageUrl(st.getUser().getBiggerProfileImageURL());
                    bp.setDateTime(st.getCreatedAt());
                    bandoArray.add(bp);
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
                Picasso.with(getActivity()).load(item.getImageUrl()).into(profilePic);
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

            return jsonArr.length();
        }

        protected void onPostExecute(Integer result) {
            try {
                for (int i = 0; i < jsonArr.length() && i <3; i++) {
                    String theusername = jsonArr.getJSONObject(i).getJSONObject("user").getString("username");
                    String thestandardImageUrl = jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
String caption = jsonArr.getJSONObject(i).getJSONObject("caption").getString("text");
                    String link = jsonArr.getJSONObject(i).getString("link");
                    String profilePic = jsonArr.getJSONObject(i).getJSONObject("user").getString("profile_picture");

                    BandoPost bp = new BandoPost();
                    bp.setPostUrl(link);
                    bp.setPostSourceSite("instagram");
                    bp.setPostText(caption);
                    bp.setPostType("instagram");
                    bp.setDateString(Utils.getTimeAgo(new Date(Long.parseLong(jsonArr.getJSONObject(i).getString("created_time"))).getTime(), getActivity()));
                    bp.setImageUrl(thestandardImageUrl);
                    bp.setDateTime(new Date((long)Long.parseLong(jsonArr.getJSONObject(i).getString("created_time"))*1000));
                    bandoArray.add(bp);
                }
                Collections.sort(bandoArray);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}