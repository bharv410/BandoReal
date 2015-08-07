package com.kigeniushq.bandofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.kigeniushq.bandofinal.editprefences.ChooseCategoriesActivity;

import classes.CustomTypefaceSpan;
import classes.SectionsPagerAdapter;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, ObservableScrollViewCallbacks {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private float mActionBarHeight;
    public ActionBar mActionBar;
    public static boolean loggedInTwitter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
    }


    private void setupActionBar(){
        mActionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#168807")));
        //actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));


        SpannableString s = new SpannableString("Bando");
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ptsansb.ttf");
        s.setSpan(new CustomTypefaceSpan(custom_font), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBar.setTitle(s);

        final TypedArray mstyled = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics())
                    / 3;
        }
        mstyled.recycle();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());
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
                if(position==1){
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    boolean previouslyStarted = prefs.getBoolean("pref_previously_started", false);
                    if(!previouslyStarted) {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean("pref_previously_started",Boolean.TRUE);
                        edit.commit();
                        startActivity(new Intent(getApplicationContext(), ChooseCategoriesActivity.class));
                    }
                }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }else if(id == R.id.action_feed_item){
            startActivity(new Intent(getApplicationContext(), ChooseCategoriesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (mActionBar != null ) {
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
