package com.bandotheapp.bando.com.bandotheapp.bando.libraryacti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bandotheapp.bando.ArticleDetailActivity;
import com.bandotheapp.bando.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes.BandoPost;
import classes.Utils;

import android.support.v4.app.Fragment;

/**
 * Created by benjamin.harvey on 9/2/15.
 */
public class LibraryFragment extends Fragment {
    private ObservableListView listView;
    private FeedListAdapter listAdapter;


    private List<BandoPost> listOfPostsThatAreInTheArrayToAvoidDuplicates;

    public LibraryFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
//        pw = (AnimatedCircleLoadingView) getActivity().findViewById(R.id.circle_loading_view);
//        pw.startDeterminate();
        if(listOfPostsThatAreInTheArrayToAvoidDuplicates==null){ //only restart list if null
            listOfPostsThatAreInTheArrayToAvoidDuplicates = new ArrayList<>();

        }
        refreshContent();
    }

private void refreshContent() {
    getVerifiedPosts();
}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        return rootView;
    }

    private void getVerifiedPosts() {
        final TinyDB tinydb = new TinyDB(getActivity());

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("BandoFeaturedPost");
        query1.addDescendingOrder("createdAt");

        query1.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject po : objects) {
                        if (tinydb.getString(po.getString("text")).contains(po.getString("text"))) {
                            BandoPost bp = new BandoPost();
                            bp.setPostUrl(po.getString("postLink"));
                            bp.setPostSourceSite(po.getString("siteType"));
                            bp.setPostText(po.getString("text"));
                            bp.setPostType("twitter");//benmark
                            bp.setDateString(Utils.getTimeAgo(po.getCreatedAt().getTime(), getActivity()));
                            bp.setDateTime(po.getCreatedAt());
                            bp.setImageUrl(po.getString("imageUrl"));
                            bp.setUniqueId(po.getObjectId());
                            bp.setViewCOunt(po.getInt("viewCount"));
                            bp.setPostHasImage(true);
                            if (!listOfPostsThatAreInTheArrayToAvoidDuplicates.contains(bp)) {
                                listOfPostsThatAreInTheArrayToAvoidDuplicates.add(bp);
                            }
                        }
                    }

                }
            }
        });



        ParseQuery<ParseObject> query = ParseQuery.getQuery("VerifiedBandoPost");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (ParseObject po : objects) {
                        if (tinydb.getString(po.getString("postText")).contains(po.getString("postText"))) {
                            Log.v("benmark", "!!!!" + po.getString("postLink"));
                            BandoPost bp = new BandoPost();
                            bp.setPostUrl(po.getString("postLink"));
                            bp.setPostSourceSite(po.getString("siteType"));
                            bp.setPostText(po.getString("postText"));
                            bp.setPostType("twitter");//benmark
                            bp.setDateString(Utils.getTimeAgo(po.getCreatedAt().getTime(), getActivity()));
                            bp.setDateTime(po.getCreatedAt());
                            bp.setImageUrl(po.getString("imageUrl"));
                            bp.setUniqueId(po.getObjectId());
                            bp.setViewCOunt(po.getInt("viewCount"));
                            bp.setPostHasImage(true);
                            if (!listOfPostsThatAreInTheArrayToAvoidDuplicates.contains(bp)) {
                                listOfPostsThatAreInTheArrayToAvoidDuplicates.add(bp);
                            }
                        }
                    }
                    Collections.sort(listOfPostsThatAreInTheArrayToAvoidDuplicates);

                    listView = (ObservableListView) getActivity().findViewById(R.id.list);
                    listAdapter = new FeedListAdapter(getActivity(), R.layout.feed_item, listOfPostsThatAreInTheArrayToAvoidDuplicates);
                    listView.setAdapter(listAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(listOfPostsThatAreInTheArrayToAvoidDuplicates.get(position).getPostUrl())));
                            Intent browserIntent = new Intent(getActivity(), ArticleDetailActivity.class);
                            browserIntent.putExtra("postLink", listOfPostsThatAreInTheArrayToAvoidDuplicates.get(position).getPostUrl());
                            browserIntent.putExtra("imagePath", listOfPostsThatAreInTheArrayToAvoidDuplicates.get(position).getImageUrl());
                            browserIntent.putExtra("text", listOfPostsThatAreInTheArrayToAvoidDuplicates.get(position).getPostText());
                            browserIntent.putExtra("featured", false);
                            startActivity(browserIntent);

                        }
                    });
                } else {
                    Log.v("benmark", "code = " + String.valueOf(e.getCode()));
                }
            }
        });


    }



    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }

    public class FeedListAdapter extends ArrayAdapter<BandoPost> {
        private Activity activity;
        private LayoutInflater inflater;
        private final List<BandoPost> bandoPosts;

        public FeedListAdapter(Activity activity, int resource, List<BandoPost> feedItems) {
            //cutting to sublist
            super(activity, resource, feedItems);
            this.activity = activity;
            this.bandoPosts = feedItems;
        }

        @Override
        public int getCount() {
            return bandoPosts.size();
        }

        @Override
        public BandoPost getItem(int location) {
            return bandoPosts.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            //do your sorting here
            Collections.sort(bandoPosts);
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.feed_item, null);


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView timestamp = (TextView) convertView
                    .findViewById(R.id.timestamp);
            TextView statusMsg = (TextView) convertView
                    .findViewById(R.id.txtStatusMsg);
            TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
            ImageView profilePic = (ImageView) convertView
                    .findViewById(R.id.profilePic);
            ImageView feedImageView = (ImageView) convertView
                    .findViewById(R.id.feedImage1);
            TextView postType = (TextView) convertView
                    .findViewById(R.id.postTypeTextView);


            final BandoPost item = getItem(position);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(getActivity(), ArticleDetailActivity.class);
                    browserIntent.putExtra("postLink", item.getPostUrl());
                    browserIntent.putExtra("imagePath", item.getImageUrl());
                    browserIntent.putExtra("text", item.getPostText());
                    browserIntent.putExtra("featured", false);
                    startActivity(browserIntent);
                }
            });

            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(getActivity(), ArticleDetailActivity.class);
                    browserIntent.putExtra("postLink", item.getPostUrl());
                    browserIntent.putExtra("imagePath", item.getImageUrl());
                    browserIntent.putExtra("text", item.getPostText());
                    browserIntent.putExtra("featured", false);
                    startActivity(browserIntent);
                }
            });

            name.setText("saved");

                //postType.setText("Art");


            // Converting timestamp into x ago format

            timestamp.setText(item.getDateString());

            // Chcek for empty status message
            if (!TextUtils.isEmpty(item.getPostText())) {
                statusMsg.setText(item.getPostText());
                statusMsg.setVisibility(View.VISIBLE);
            } else {
                // status is empty, remove from view
                statusMsg.setVisibility(View.GONE);
            }
            // user profile pic
            Picasso.with(getActivity()).load(item.getUserProfilePic()).into(profilePic);
            feedImageView.setVisibility(View.VISIBLE);

            // Feed image
            if (item.isPostHasImage()) {
                Picasso.with(getActivity()).load(item.getImageUrl()).into(feedImageView);
                feedImageView.setVisibility(View.VISIBLE);
            } else {
                feedImageView.setVisibility(View.GONE);
            }

            return convertView;
        }

    }
}