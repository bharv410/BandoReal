package com.bandotheapp.bando;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;

public class EditFeedActivity extends AppCompatActivity {
    String[] items = new String[]{"Soundcloud", "Instagram", "Twitter", "HotNewHipHop"};
    String[] listItems = new String[]{"@champagnepapi", "@theweekndbible", "@frenchmontana", "HotNewHipHop"};
    EditText et;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);
        et = (EditText)findViewById(R.id.editText5);
        et.setVisibility(View.INVISIBLE);
        lv = (ListView)findViewById(R.id.listView5);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        et.setVisibility(View.VISIBLE);
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Do something useful withe the position of the selected radio button
                    getSupportActionBar().setTitle(items[selectedPosition]);
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_feed, menu);
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
            new AlertDialog.Builder(this)
                    .setSingleChoiceItems(items, 0, null)
                    .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            et.setVisibility(View.VISIBLE);
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            // Do something useful withe the position of the selected radio button
                            getSupportActionBar().setTitle(items[selectedPosition]);
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
