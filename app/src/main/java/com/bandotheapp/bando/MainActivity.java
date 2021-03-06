package com.bandotheapp.bando;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bandotheapp.bando.com.bandotheapp.bando.libraryacti.LibraryActivity;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.bandotheapp.bando.editprefences.AlarmReceiver;
import com.bandotheapp.bando.editprefences.ChooseCategoriesActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.BandoPost;
import classes.CustomTypefaceSpan;
import classes.SectionsPagerAdapter;
import classes.Utils;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, ObservableScrollViewCallbacks {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private float mActionBarHeight;
    public ActionBar mActionBar;
    public static boolean loggedInTwitter = false;

    private Tracker mTracker;

    private SearchView mSearchView;
    FeauteredFragment fragmentWithSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentWithSearch = new FeauteredFragment();

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        setupActionBar();

        setMorningAlarm();


        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String[] parts = data.toString().split("/");
            for(String s : parts){
                Log.v("benmark", s);
            }
            String postId = parts[parts.length-1];
            Log.v("benmark", postId);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
            query.addDescendingOrder("createdAt");
            query.whereEqualTo("postId", Integer.parseInt(postId));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject po : objects) {
                            Intent browserIntent = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                            browserIntent.putExtra("postLink", po.getString("postLink"));
                            browserIntent.putExtra("imagePath", po.getString("imageUrl"));
                            browserIntent.putExtra("text", po.getString("postText"));
                            browserIntent.putExtra("featured", false);
                            startActivity(browserIntent);
                        }
                    }
                }
            });
        }
    }

    private void trackSomething(String trackThis, String withExtra){
        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put("track", trackThis);
        dimensions.put("extra", withExtra);
        ParseAnalytics.trackEvent("opened article", dimensions);
    }

    private void setMorningAlarm() {
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR, 8); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 0); // Particular minute
        firingCal.set(Calendar.SECOND, 0); // particular second

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (intendedTime >= currentTime) // you can add buffer time too here to ignore some small differences in milliseconds
        {
            //set from today
            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime, AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        } else {
            //set from next day
            // you might consider using calendar.add() for adding one day to the current day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime, AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        Log.i("benmark", "Setting screen name: " + "MainActivity");
        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayUseLogoEnabled(true);
        mActionBar.setTitle("");
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setIcon(R.drawable.bandoheader);

//        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#168807")));
        //actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));


        SpannableString s = new SpannableString("Bando");
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ptsansb.ttf");
        s.setSpan(new CustomTypefaceSpan(custom_font), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //mActionBar.setTitle(s);

        final TypedArray mstyled = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics())
                    / 3;
        }
        mstyled.recycle();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), fragmentWithSearch);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        tabs.setShouldExpand(true);
        tabs.setTextColor(Color.parseColor("#168807"));
        tabs.setUnderlineColor(Color.parseColor("#168807"));
        tabs.setIndicatorColor(Color.parseColor("#168807"));
        tabs.setBackgroundColor(Color.parseColor("#FFFFFF"));

        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    boolean previouslyStarted = prefs.getBoolean("pref_previously_started", false);
                    if (!previouslyStarted) {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean("pref_previously_started", Boolean.TRUE);
                        edit.commit();
                        startActivity(new Intent(getApplicationContext(), ChooseCategoriesActivity.class));
                    }
                }
                trackSomething("pageChange", String.valueOf(position));
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setOnQueryTextListener(fragmentWithSearch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                mSearchView.setIconified(false);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_feed_item:
                startActivity(new Intent(getApplicationContext(), ChooseCategoriesActivity.class));
                return true;
            case R.id.action_feedback_item:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "contactbando@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bando Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;
            case R.id.action_library_item:
                startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (mActionBar != null) {
            if (scrollY >= mActionBarHeight && mActionBar.isShowing()) {
                mActionBar.hide();
            } else if (scrollY == 0 && !mActionBar.isShowing()) {

                mActionBar.show();
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (mActionBar == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (mActionBar.isShowing()) {
                mActionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!mActionBar.isShowing()) {
                mActionBar.show();
            }
        }
    }
}
