package com.bandotheapp.bando.editprefences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View.OnClickListener;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


import com.bandotheapp.bando.R;
import com.bandotheapp.bando.SettingsActivity;

import java.util.ArrayList;

import classes.CustomTypefaceSpan;

public class ChooseCategoriesActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

        mActionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#168807")));
        //actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));


        SpannableString s = new SpannableString("Bando");
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ptsansb.ttf");
        s.setSpan(new CustomTypefaceSpan(custom_font), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBar.setTitle(s);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_categories, menu);
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
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void displayListView() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ArrayList<Country> countryList = new ArrayList<Country>();
        Country country = new Country("@drake, HotNewHipHop.com etc.", "MUSIC", preferences.getBoolean("MUSIC", false));
        countryList.add(country);
        country = new Country("@kingjames, @stephenasmith, ESPN etc.", "SPORTS", preferences.getBoolean("SPORTS", false));
        countryList.add(country);
        country = new Country("@tazsangels, @kendalljenner ", "CULTURE", preferences.getBoolean("CULTURE", false));
        countryList.add(country);
        country = new Country("@kevinhart4real, @lilduval", "COMEDY", preferences.getBoolean("COMEDY", false));
        countryList.add(country);
        country = new Country("@natgeo, @vanstyles", "PHOTOS & ART", preferences.getBoolean("PHOTOS & ART", false));
        countryList.add(country);
        country = new Country("instagram,twitter", "TRENDING", preferences.getBoolean("TRENDING", false));
        countryList.add(country);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.country_info, countryList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getItemAtPosition(position);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChooseCategoriesActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                if (view != null) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
                    checkBox.setChecked(!checkBox.isChecked());
                    editor.putBoolean(country.getName(), checkBox.isChecked());
                    editor.apply();
                    country.setSelected(checkBox.isChecked());
                }
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Country> {

        private ArrayList<Country> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Country> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Country>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Country country = (Country) cb.getTag();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChooseCategoriesActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(country.getName(), cb.isChecked());
                        editor.apply();
                        country.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Country country = countryList.get(position);
            holder.code.setText(" (" + country.getCode() + ")");
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Country> countryList = dataAdapter.countryList;
                for (int i = 0; i < countryList.size(); i++) {
                    Country country = countryList.get(i);
                    if (country.isSelected()) {
                        responseText.append("\n" + country.getName());
                    }
                }
                finish();

            }
        });

    }

}