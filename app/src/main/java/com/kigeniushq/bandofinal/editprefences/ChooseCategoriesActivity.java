package com.kigeniushq.bandofinal.editprefences;

import android.support.v7.app.ActionBar;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.kigeniushq.bandofinal.R;

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

private void displayListView() {

        //Array list of countries
        ArrayList<Country> countryList = new ArrayList<Country>();
        Country country = new Country("@drake, @abelxo etc.","MUSIC",true);
        countryList.add(country);
        country = new Country("@kingjames, @stephenasmith etc.","SPORTS",false);
        countryList.add(country);
        country = new Country("@tazsangels, @kendalljenner ","CULTURE",true);
        countryList.add(country);
        country = new Country("@kevinhart4real, @lilduval","COMEDY",true);
        countryList.add(country);
        country = new Country("@natgeo, @vanstyles","PHOTOS & ART",false);
        countryList.add(country);
        country = new Country("instagram,twitter","TRENDING",false);
        countryList.add(country);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
        R.layout.country_info, countryList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
        // When clicked, show a toast with the TextView text
        Country country = (Country) parent.getItemAtPosition(position);
        Toast.makeText(getApplicationContext(),
        "Clicked on Row: " + country.getName(),
        Toast.LENGTH_LONG).show();
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
            LayoutInflater vi = (LayoutInflater)getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.country_info, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Country country = (Country) cb.getTag();
                    Toast.makeText(getApplicationContext(),
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    country.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Country country = countryList.get(position);
        holder.code.setText(" (" +  country.getCode() + ")");
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
                for(int i=0;i<countryList.size();i++){
                    Country country = countryList.get(i);
                    if(country.isSelected()){
                        responseText.append("\n" + country.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                finish();

            }
        });

    }

}