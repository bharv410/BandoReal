package classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.kigeniushq.bandofinal.ArticleDetailActivity;
import com.kigeniushq.bandofinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin.harvey on 8/7/15.
 */
public class CustomGrid extends ArrayAdapter<BandoPost> {
    private Context mContext;
    private final List<BandoPost> bandoPosts;
    private LayoutInflater mInflater;

    public CustomGrid(Context context, ArrayList<BandoPost> posts) {
        super(context, 0, posts);
        this.mContext = context;
        this.bandoPosts = posts;
        mInflater = LayoutInflater.from( context );
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
        ViewHolder holder;
        View grid;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_single, null);
            //grid.setBackgroundColor(Color.parseColor("#999999"));
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            TextView dateTextView = (TextView) grid.findViewById(R.id.textViewDate);
            TextView socialTextView = (TextView) grid.findViewById(R.id.socialTextView);
            dateTextView.setText(getItem(position).getDateString());
            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/ptsansb.ttf");
            textView.setTypeface(custom_font);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);

            final String postLink = getItem(position).getPostUrl();
            final String text = getItem(position).getPostText();
            final String imagePath = getItem(position).getImageUrl();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(mContext, ArticleDetailActivity.class);
                    browserIntent.putExtra("postLink", postLink);
                    browserIntent.putExtra("imagePath", imagePath);
                    browserIntent.putExtra("text", text);
                    mContext.startActivity(browserIntent);

                }
            });

            final String siteType = getItem(position).getPostSourceSite();
            if(!siteType.contains("article"))
                socialTextView.setText(siteType);

            textView.setText(bandoPosts.get(position).getPostText());

                Picasso.with(mContext).load(bandoPosts.get(position).getImageUrl())
                        .placeholder(R.drawable.progress_animation)
                        .into(imageView);

        } else {
            grid = (View) convertView;
        }

        return grid;
    }
    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }
}