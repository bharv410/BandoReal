package classes;

import android.os.AsyncTask;

import com.bandotheapp.bando.MainActivity;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;


/**
 * Created by benjamin.harvey on 8/6/15.
 */
public class GetTweetsAsync extends AsyncTask<Void, Void, ArrayList<Status>> {

    ArrayList<twitter4j.Status> tweets;
    private MainActivity activity;

    public GetTweetsAsync(MainActivity activity) {
        this.activity = activity;
        tweets = new ArrayList<twitter4j.Status>();
    }

    @Override
    protected ArrayList<twitter4j.Status> doInBackground(Void... arg0) {
        try {
            ResponseList<twitter4j.Status> response = TwitterLogin.twitter.timelines().getHomeTimeline();
            for (twitter4j.Status st : response) {
                tweets.add(st);
            }
        } catch (TwitterException twe) {
        }
        return tweets;
    }

    @Override
    protected void onPostExecute(ArrayList<twitter4j.Status> tweets) {
        //activity.getTweets(tweets);
    }
}
