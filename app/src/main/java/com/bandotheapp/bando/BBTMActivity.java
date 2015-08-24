package com.bandotheapp.bando;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BBTMActivity  extends ListActivity {

    private TextView text;
    private List<String> listValues;
    private List<String> urlValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbtm);

        text = (TextView) findViewById(R.id.mainText);

        listValues = new ArrayList<String>();
        listValues.add("01 Real Life");
        listValues.add("02 Losers");
        listValues.add("03 Tell Your Friends");
        listValues.add("04 Often");
        listValues.add("04 The Hills");
        listValues.add("06 Acquanted");
        listValues.add("07 Cant Feel My Face");
        listValues.add("08 Shameless");
        listValues.add("09 Earned It");
        listValues.add("10 In the Night");
        listValues.add("11 As You Are");
        listValues.add("12 Dark Times");
        listValues.add("13 Prisoner");
        listValues.add("14 Angel");

        urlValues = new ArrayList<String>();
        urlValues.add("http://picosong.com/uTEQ");
        urlValues.add("http://picosong.com/uT8P");
        urlValues.add("http://picosong.com/uT8Q");
        urlValues.add("http://www.youtube.com/watch?v=JPIhUaONiLU");
        urlValues.add("http://www.youtube.com/watch?v=yzTuBuRdAyA");
        urlValues.add("http://picosong.com/uT8h");
        urlValues.add("http://www.youtube.com/watch?v=dqt8Z1k0oWQ");
        urlValues.add("http://picosong.com/uT8v");
        urlValues.add("http://www.youtube.com/watch?v=waU75jdUnYw");
        urlValues.add("http://picosong.com/uThn");
        urlValues.add("http://picosong.com/uThC");
        urlValues.add("http://picosong.com/uThg");
        urlValues.add("http://picosong.com/uThu");
        urlValues.add("http://picosong.com/uTht");


        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.songlayout, R.id.listText, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);

        Intent browserIntent = new Intent(getApplicationContext(), ArticleDetailActivity.class);
        browserIntent.putExtra("postLink", urlValues.get(position));
        browserIntent.putExtra("imagePath", urlValues.get(position));
        browserIntent.putExtra("text", selectedItem);
        browserIntent.putExtra("featured", false);
        startActivity(browserIntent);

    }

}