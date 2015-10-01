package com.bandotheapp.bando;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes.BandoPost;
import classes.Utils;
import classes.CustomGrid;

public class SearchResults extends ActionBarActivity implements SearchView.OnQueryTextListener{

    String searchQuery;
    private SearchView mSearchView;

    MyObservableGridView gridViewWithHeader;
    CustomGrid adapter;

    ProgressBar pb;
    Runnable updateData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        searchQuery = getIntent().getStringExtra("searchThis");
        getVerifiedPosts(searchQuery);
        getSupportActionBar().setTitle(searchQuery);
    }

    private void getVerifiedPosts(String searchQuery) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.addDescendingOrder("createdAt");
        query.whereContains("postText", searchQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                final ArrayList<BandoPost> bandoArray = new ArrayList<>();
                if (e == null) {
                    for (ParseObject po : objects) {
                        BandoPost bp = new BandoPost();
                        bp.setPostUrl(po.getString("postLink"));
                        bp.setPostSourceSite(po.getString("siteType"));
                        bp.setPostText(po.getString("postText"));
                        bp.setPostType(po.getString("postType"));
                        bp.setDateString(Utils.getTimeAgo(po.getCreatedAt().getTime(), getApplicationContext()));
                        bp.setDateTime(po.getCreatedAt());
                        bp.setImageUrl(po.getString("imageUrl"));
                        bp.setUniqueId(po.getObjectId());
                        bp.setViewCOunt(po.getInt("viewCount"));
                        if (!bandoArray.contains(bp)) {
                            bandoArray.add(bp);
                        }
                    }
                    Collections.sort(bandoArray);
                    adapter = new CustomGrid(getApplicationContext(), bandoArray);
                    gridViewWithHeader = (MyObservableGridView) findViewById(R.id.gridSearch);

                    gridViewWithHeader.setAdapter(adapter);
                    gridViewWithHeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getPostUrl())));
                        }
                    });
                } else {
                    Log.v("benmark", "code = " + String.valueOf(e.getCode()));
                }
            }
        });
    }

    private void resetVerifiedPosts(String searchQuery) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.addDescendingOrder("createdAt");
        query.whereContains("postText", searchQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                final ArrayList<BandoPost> bandoArray = new ArrayList<>();
                if (e == null) {
                    for (ParseObject po : objects) {
                        BandoPost bp = new BandoPost();
                        bp.setPostUrl(po.getString("postLink"));
                        bp.setPostSourceSite(po.getString("siteType"));
                        bp.setPostText(po.getString("postText"));
                        bp.setPostType(po.getString("postType"));
                        bp.setDateString(Utils.getTimeAgo(po.getCreatedAt().getTime(), getApplicationContext()));
                        bp.setDateTime(po.getCreatedAt());
                        bp.setImageUrl(po.getString("imageUrl"));
                        bp.setUniqueId(po.getObjectId());
                        bp.setViewCOunt(po.getInt("viewCount"));
                        if (!bandoArray.contains(bp)) {
                            bandoArray.add(bp);
                        }
                    }
                    Collections.sort(bandoArray);

                    updateData = new Runnable() {
                        public void run() {
                            adapter.clear();
                            adapter.addAll(bandoArray);
                            adapter.notifyDataSetChanged();
                            gridViewWithHeader.invalidateViews();
                            gridViewWithHeader.refreshDrawableState();

                            gridViewWithHeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getPostUrl())));
                                }
                            });
                        }
                    };

                    runOnUiThread(updateData);
                } else {
                    Log.v("benmark", "code = " + String.valueOf(e.getCode()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setOnQueryTextListener(this);
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

    @Override
    public boolean onQueryTextSubmit(String s) {

        Intent intent = new Intent(getApplicationContext(), SearchResults.class);
        intent.putExtra("searchThis", s);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return false;
    }

    public void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
