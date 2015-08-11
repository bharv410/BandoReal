package com.bandotheapp.bando;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.support.v7.app.ActionBar;

import classes.BandoPost;
import classes.Utils;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class FeauteredFragment extends Fragment {

    MyObservableGridView gridViewWithHeader;
    CustomGrid adapter;

    private float mActionBarHeight;
    public ActionBar mActionBar;

    ProgressBar pb;

    boolean isFeaturedHeaderSet = false;

    public FeauteredFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pb = (ProgressBar) getActivity().findViewById(R.id.progressBar2);

        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        final TypedArray mstyled = getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics())
                    / 3;
        }
        mstyled.recycle();

        getVerifiedPosts();
    }

    private void getVerifiedPosts() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.addDescendingOrder("createdAt");
        query.setLimit(14);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                final ArrayList<BandoPost> bandoArray = new ArrayList<>();
                if (e == null) {
                    for (ParseObject po : objects) {
                        BandoPost bp = new BandoPost();
                        bp.setPostUrl(po.getString("postLink"));
                        bp.setPostSourceSite(po.getString("siteType"));
                        bp.setPostText(po.getString("postText"));
                        bp.setPostType("article");
                        bp.setDateString(Utils.getTimeAgo(po.getCreatedAt().getTime(), getActivity()));
                        bp.setDateTime(po.getCreatedAt());
                        bp.setImageUrl(po.getString("imageUrl"));
                        bp.setUniqueId(po.getObjectId());
                        bp.setViewCOunt(po.getInt("viewCount"));
                        if (!bandoArray.contains(bp)) {
                            bandoArray.add(bp);
                        }
                    }
                    Collections.sort(bandoArray);
                    adapter = new CustomGrid(getActivity(), bandoArray);
                    gridViewWithHeader = (MyObservableGridView) getActivity().findViewById(R.id.grid);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View headerView = layoutInflater.inflate(R.layout.featuredsquare, null);
                    setHeader(headerView);
                    gridViewWithHeader.setAdapter(adapter);
                    gridViewWithHeader.setScrollViewCallbacks((MainActivity) getActivity());
                    gridViewWithHeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bandoArray.get(position).getPostUrl())));
                        }
                    });
                    pb.setVisibility(View.GONE);
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

    private void setHeader(final View v) {
        if (!isFeaturedHeaderSet) {
            gridViewWithHeader.addHeaderView(v, null, true);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoFeaturedPost");
            query.addDescendingOrder("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        isFeaturedHeaderSet = true;
                        Picasso.with(getActivity()).load(objects.get(0).getString("imageUrl"))
                                .placeholder(R.drawable.progress_animation)
                                .into((ImageView) v.findViewById(R.id.imageViewHeader));

                        TextView featuredText = (TextView) getActivity().findViewById(R.id.featuredTextView);
                        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ptsansb.ttf");
                            featuredText.setTypeface(custom_font);
                            featuredText.setText(objects.get(0).getString("text"));

                            final String postLink = objects.get(0).getString("postLink");
                            final String text = objects.get(0).getString("text");
                            final String imagePath = objects.get(0).getString("imageUrl");
                            final String objectid = objects.get(0).getObjectId();
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(getActivity(), ArticleDetailActivity.class);
                                    browserIntent.putExtra("postLink", postLink);
                                    browserIntent.putExtra("imagePath", imagePath);
                                    browserIntent.putExtra("text", text);
                                    browserIntent.putExtra("featured", true);
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
        return rootView;
    }

    public class CustomGrid extends ArrayAdapter<BandoPost> {
        private Context mContext;
        private final List<BandoPost> bandoPosts;
        private LayoutInflater mInflater;

        public CustomGrid(Context context, ArrayList<BandoPost> posts) {
            super(context, 0, posts);
            this.mContext = context;
            this.bandoPosts = posts;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return bandoPosts.size();
        }

        @Override
        public BandoPost getItem(int position) {
            return bandoPosts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_single, parent, false);
                holder = new ViewHolder();
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.grid_image);
                holder.position = position;
                holder.title = (TextView) convertView.findViewById(R.id.grid_text);
                Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/ptsansb.ttf");
                holder.title.setTypeface(custom_font);
                holder.dateTextView = (TextView) convertView.findViewById(R.id.textViewDate);
                holder.socialTextView = (TextView) convertView.findViewById(R.id.socialTextView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }


            final BandoPost current = bandoPosts.get(position);
            final String postLink = current.getPostUrl();
            final String text = current.getPostText();
            final String imagePath = current.getImageUrl();
            final int viewCOunt = current.getViewCOunt();
            final String siteType = current.getPostSourceSite();
            final String postText = current.getPostText();


                if (!siteType.contains("article"))
                    holder.socialTextView.setText(siteType);

                holder.title.setText(truncateAfteWords(9, postText));
                holder.dateTextView.setText(getItem(position).getDateString());

                Picasso.with(mContext).load(current.getImageUrl())
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.thumbnail);

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(mContext, ArticleDetailActivity.class);
                    browserIntent.putExtra("postLink", postLink);
                    browserIntent.putExtra("imagePath", imagePath);
                    browserIntent.putExtra("text", text);
                    browserIntent.putExtra("viewCount", viewCOunt);
                    browserIntent.putExtra("featured", false);
                    startActivity(browserIntent);

                }
            });
            return convertView;
        }

        private String truncateAfteWords(int n, String s) {
            Pattern WB_PATTERN = Pattern.compile("(?<=\\w)\\b");
            if (s == null) return null;
            if (n <= 0) return "";
            Matcher m = WB_PATTERN.matcher(s);
            for (int i = 0; i < n && m.find(); i++) ;
            if (m.hitEnd())
                return s;
            else
                return s.substring(0, m.end());
        }
    }

    private static class ViewHolder {
        public ImageView thumbnail;
        public TextView title,dateTextView,socialTextView;
        public int position;
    }
}
