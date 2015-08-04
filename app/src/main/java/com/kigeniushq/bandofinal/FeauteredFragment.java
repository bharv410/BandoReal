package com.kigeniushq.bandofinal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class FeauteredFragment extends Fragment {
    ArrayList<String> linkArray;
    ArrayList<String> photoArray;
    ArrayList<String> captionsArray;
    ArrayList<String> usernamesArray;
    ArrayList<String> urlArray;
    GridView grid;

    public FeauteredFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        linkArray = new ArrayList<String>();
        photoArray = new ArrayList<String>();
        captionsArray = new ArrayList<String>();
        usernamesArray = new ArrayList<String>();
        urlArray = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Instagram");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject po : objects) {
                        linkArray.add(po.getString("postId"));
                        photoArray.add(po.getString("imageUrl"));
                        usernamesArray.add(po.getString("username"));
                        captionsArray.add(po.getString("captionText"));
                        urlArray.add(po.getString("link"));
                        Log.v("benmark", "caption = " + String.valueOf(po.getString("captionText")));
                    }
                    CustomGrid adapter = new CustomGrid(getActivity(), photoArray, captionsArray);
                    grid = (GridView) getActivity().findViewById(R.id.grid);
                    grid.setAdapter(adapter);
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlArray.get(position))));
                        }
                    });
                }else{
                    Log.v("benmark", "code = " +String.valueOf(e.getCode()));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_featured, container, false);
        return rootView;
    }

    public class CustomGrid extends BaseAdapter {
        private Context mContext;
        private final List<String> images,captions;

        public CustomGrid(Context c, List<String> images, List<String> captions) {
            mContext = c;
            this.images = images;
            this.captions = captions;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
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
                TextView textView = (TextView) grid.findViewById(R.id.grid_text);
                ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
                textView.setText(captions.get(position));

                Picasso.with(getActivity()).load(images.get(position)).into(imageView);

            } else {
                grid = (View) convertView;
            }

            return grid;
        }
    }
}
