package com.kigeniushq.bandofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
import java.util.List;

import classes.BandoPost;
import classes.CustomTypefaceSpan;


public class ArticleDetailActivity extends ActionBarActivity {
    String imagePath, text;
    TextView articleTitleTextView;

    ImageView imv;
    private WebView mWebview ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_article_detail);

        mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebview .loadUrl(getIntent().getStringExtra("postLink"));
        setContentView(mWebview);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#168807")));

        SpannableString s = new SpannableString("Bando");
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ptsansb.ttf");

        s.setSpan(new CustomTypefaceSpan(custom_font), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);



        imagePath = getIntent().getStringExtra("imagePath");
        text = getIntent().getStringExtra("text");
        articleTitleTextView = (TextView)findViewById(R.id.textViewDetail);
        imv = (ImageView)findViewById(R.id.imageViewHeaderDetail);
        //articleTitleTextView.setText(text);
        Log.v("benmark", "imagePath = " + imagePath);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
                    updateParseOgImage(theLinkSHouldBe);

                    Picasso.with(getApplicationContext()).load(theLinkSHouldBe).into(imv);
                }
            }
    }
    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }

    private void updateParseOgImage(final String ogUrl){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.whereEqualTo("postText", text);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject po : list) {
                        if (po.getString("imageUrl").contains("og:image")) {
                            po.put("imageUrl", ogUrl);
                            po.saveInBackground();
                            Log.v("benmark", "updated " + text + "\n tourl" + ogUrl);
                        }
                    }
                }
            }
        });
    }
}
