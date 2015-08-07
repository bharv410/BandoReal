package classes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.kigeniushq.bandofinal.R;

public class TwitterWebView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_webview);

        WebView twitterWebView = (WebView) findViewById(R.id.twitter_web_view);

        twitterWebView.getSettings().setJavaScriptEnabled(true);
        twitterWebView.loadUrl(getIntent().getExtras().get("twitter_url").toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TwitterWebView onStop", "onStop");
        this.finish();
    }
}
