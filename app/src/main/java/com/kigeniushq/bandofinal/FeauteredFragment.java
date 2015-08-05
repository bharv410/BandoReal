package com.kigeniushq.bandofinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
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
import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class FeauteredFragment extends Fragment {
    ArrayList<BandoPost> bandoArray;
    GridViewWithHeaderAndFooter grid;
    CustomGrid adapter;
    boolean featueredHedaerSet = false;

    public FeauteredFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoPost");
        query.addAscendingOrder("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    bandoArray = new ArrayList<BandoPost>();
                    int position = -1;
                    for (ParseObject po : objects) {
                        position++;
                        BandoPost bp = new BandoPost();
                        if (po.getString("siteType").contains("instagram")) {
                            bp.setPostText(po.getString("captionText"));
                            bp.setImageUrl(po.getString("imageUrl"));
                            bp.setPostUrl(po.getString("link"));
                            bp.setUniqueId(po.getString("postUniqueId"));
                            bp.setPostType(po.getString("instagram"));
                            bp.setUsername(po.getString("username"));
                            bp.setUserProfilePic(po.getString("userProfilePic"));
                        } else if (po.getString("siteType").contains("http://feeds.feedburner.com/realhotnewhiphop.xml")) {
                            setOgImage(po.getString("postLink"), position);
                            Log.v("benmark", "getting og image for " + String.valueOf(position));
                            bp.setPostUrl(po.getString("postUrl"));
                            bp.setUsername(po.getString("username"));
                            bp.setPostText(po.getString("postText"));
                            bp.setPostType("article");
                        }
                        bandoArray.add(bp);
                    }
                    adapter = new CustomGrid(getActivity(), bandoArray);

                    grid = (GridViewWithHeaderAndFooter) getActivity().findViewById(R.id.grid);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View headerView = layoutInflater.inflate(R.layout.featuredsquare, null);
                    setHeader(headerView);
                    grid.setAdapter(adapter);
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getImageUrl())));
                        }
                    });
                } else {
                    Log.v("benmark", "code = " + String.valueOf(e.getCode()));
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();


    }

    private void setAdapterRightNow(){

    }

    private void setHeader(final View v){
if(!featueredHedaerSet) {
    grid.addHeaderView(v);
    ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoFeaturedPost");
    query.addAscendingOrder("createdAt");

    query.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> objects, ParseException e) {
            if (e == null) {
                featueredHedaerSet = true;
                Picasso.with(getActivity()).load(objects.get(0).getString("imageUrl"))
                        .placeholder(R.drawable.progress_animation)
                        .into((ImageView) v.findViewById(R.id.imageViewHeader));

                TextView featuredText = (TextView) getActivity().findViewById(R.id.featuredTextView);
                Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ptsansb.ttf");
                featuredText.setTypeface(custom_font);

                featuredText.setText(objects.get(0).getString("text"));

                final String postLink = objects.get(0).getString("postLink");
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postLink));
                        startActivity(browserIntent);
                    }
                });
            } else {
                Log.v("benmark", "code = " + String.valueOf(e.getCode()));
            }
        }
    });
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_featured, container, false);
        //startActivity(new Intent(getActivity(), LoadingScreen.class));
        return rootView;
    }

    public class CustomGrid extends ArrayAdapter<BandoPost> {
        private Context mContext;
        private final List<BandoPost> bandoPosts;

        public CustomGrid(Context context, ArrayList<BandoPost> posts) {
            super(context, 0, posts);
            this.mContext = context;
            this.bandoPosts = posts;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return bandoPosts.size();
        }

        @Override
        public BandoPost getItem(int position) {
            // TODO Auto-generated method stub
            return bandoPosts.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View grid;
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {

                grid = new View(mContext);
                grid = inflater.inflate(R.layout.grid_single, null);
                //grid.setBackgroundColor(Color.parseColor("#999999"));
                TextView textView = (TextView) grid.findViewById(R.id.grid_text);
                Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ptsansb.ttf");
                textView.setTypeface(custom_font);
                ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
                textView.setText(bandoPosts.get(position).getPostText());

                Picasso.with(getActivity()).load(bandoPosts.get(position).getImageUrl())
                        .placeholder(R.drawable.progress_animation )
                        .into(imageView);


            } else {
                grid = (View) convertView;
            }

            return grid;
        }
    }

    private void setOgImage(String url, int position){
        new DownloadTask(position).execute(url);

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
            int bandoindex;
        public DownloadTask(int position){
            this.bandoindex = position;
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
                Log.v("benmark", "theLinkSHouldBe  " + theLinkSHouldBe);

                bandoArray.get(bandoindex).setImageUrl(theLinkSHouldBe);
                //CustomGrid newAadapter = new CustomGrid(getActivity(), bandoArray);
                adapter.getItem(bandoindex).setImageUrl(theLinkSHouldBe);
                adapter.notifyDataSetChanged();
                grid.invalidateViews();
                grid.setAdapter(adapter);
                //grid.smoothScrollToPosition();
            }
        }
    }
}
