package com.bandotheapp.bando;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.Animation.AnimationListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.List;

import com.bandotheapp.bando.com.bandotheapp.bando.libraryacti.TinyDB;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.CustomTypefaceSpan;


public class ArticleDetailActivity extends ActionBarActivity {
    String imagePath, text;
    TextView articleTitleTextView, viewCountTV;
    ProgressBar pb5;

    ImageView imv;
    private WebView mWebview ;

    private ImageView earlyFindImageView;
    private TextView helpTextView;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        pb5 = (ProgressBar)findViewById(R.id.progressBar5);

        earlyFindImageView = (ImageView)findViewById(R.id.earlyFinderImageView);
        earlyFindImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindersFirstActivity.class));
            }
        });
        helpTextView = (TextView)findViewById(R.id.helpTextView);
        helpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindersFirstActivity.class));
            }
        });
        if(getIntent().getIntExtra("viewCount",10)<10){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fadeOutAndHideImage(earlyFindImageView);
                }
            }, 2000);
        } else {
            earlyFindImageView.setVisibility(View.GONE);
            helpTextView.setVisibility(View.GONE);
        }

        mWebview = (WebView) findViewById(R.id.webView);
        viewCountTV = (TextView) findViewById(R.id.viewCountTextView);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ptsansb.ttf");
        viewCountTV.setTypeface(custom_font);
        viewCountTV.setText(String.valueOf(getIntent().getIntExtra("viewCount", 33)));

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebview .loadUrl(getIntent().getStringExtra("postLink"));

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#168807")));
        actionBar.setHomeButtonEnabled(true);

        SpannableString s = new SpannableString("Bando");

        s.setSpan(new CustomTypefaceSpan(custom_font), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pb5.setVisibility(View.GONE);
            }
        }, 1000);
        text = getIntent().getStringExtra("text");

        if(getIntent().getBooleanExtra("featured", false)){
            updateViewCount();
        }else{
            updateCount();
        }

//
//        imagePath = getIntent().getStringExtra("imagePath");
//        text = getIntent().getStringExtra("text");
//        articleTitleTextView = (TextView)findViewById(R.id.textViewDetail);
//        imv = (ImageView)findViewById(R.id.imageViewHeaderDetail);
        //articleTitleTextView.setText(text);
        //Picasso.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.progress_animation).into(imv);

//        imv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("postLink")));
//
//                startActivity(browserIntent);
//            }
//        });


        //new DownloadTask(imv, getApplicationContext()).execute(getIntent().getStringExtra("postLink"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_detail, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        mTracker.setScreenName("Article ");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("ArticleView")
                .setAction(text)
                .build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }else
            finish();

        return super.onOptionsItemSelected(item);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        private ImageView imv;
        private Context context;

        public DownloadTask(ImageView imv, Context ctx){
            this.imv = imv;
            this.context = ctx;
        }
        @Override
        protected String doInBackground(String... urls) {
            HttpResponse response = null;
            HttpGet httpGet = null;
            HttpClient mHttpClient = null;
            String s = "";

            try {
                if(mHttpClient == null){
                    mHttpClient = new DefaultHttpClient();
                }


                httpGet = new HttpGet(urls[0]);


                response = mHttpClient.execute(httpGet);
                s = EntityUtils.toString(response.getEntity(), "UTF-8");


            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String result){
                if(result.contains("og:image")){
                    String[] parts = result.split("\"og:image\"");

                    String partAfterOGImage = parts[1];
                    //Log.v("benmark", "found partAfterOGImage " + partAfterOGImage);
                    String[] getRidOfTheEnd = partAfterOGImage.split("/><m");

                    String theLinkSHouldBe = getRidOfTheEnd[0].split("\"")[1];
                    //updateCount(theLinkSHouldBe);

                    Picasso.with(getApplicationContext()).load(theLinkSHouldBe).into(imv);
                }
            }
    }
    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }


    public void bookmark(View v){
Toast.makeText(getApplicationContext(), "Bookmarked!", Toast.LENGTH_LONG).show();
        trackSomething("bookmark", "bookmark");

        TinyDB tinydb = new TinyDB(getApplicationContext());

        tinydb.putString(text, text);

    }

    public void share(View v){
        if(getIntent().getIntExtra("viewCount",10)<5){
            Notification noti = new Notification.Builder(getApplicationContext())
                    .setContentTitle("You are the first to share this article!")
                    .setContentText("Congrats from Bando")
                    .setSmallIcon(R.drawable.bandologo)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(12,noti);
        }

        trackSomething("share", "share");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "\"" + getIntent().getStringExtra("text") + "\"" + " via @bandotheapp");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void updateCount() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.whereEqualTo("postText", text);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject po : list) {
                        po.put("viewCount", (int) po.get("viewCount") + 1);
                        po.saveInBackground();
                        Log.v("benmark", "updated " + text + "\n tourl");
                        if (viewCountTV != null)
                            viewCountTV.setText(String.valueOf((int) po.get("viewCount") + 1));
                    }
                }
            }
        });
    }

    private void trackSomething(String trackThis, String withExtra){
        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put("article title", text);
        dimensions.put("track", trackThis);
        dimensions.put("extra", withExtra);
        ParseAnalytics.trackEvent("opened article", dimensions);
    }

    private void updateViewCount(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoFeaturedPost");
        query.whereEqualTo("text", text);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject po : list) {
                        po.put("viewCount", (int) po.get("viewCount") + 1);
                        po.saveInBackground();
                        Log.v("benmark", "updated " + text + "\n touvl" + po.get("viewCount"));
                        if (viewCountTV != null)
                            viewCountTV.setText(String.valueOf((int) po.get("viewCount") + 1));
                    }
                } else {
                    Log.v("benmark", String.valueOf(e.getCode()));
                }
            }
        });
    }

    private void fadeOutAndHideImage(final ImageView img)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(1500);

                fadeOut.setAnimationListener(new AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        img.setVisibility(View.GONE);
                        helpTextView.setVisibility(View.GONE);
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }
                });

                img.startAnimation(fadeOut);


            }
        }, 300);
    }
}
