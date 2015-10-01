package classes;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bandotheapp.bando.FeauteredFragment;
import com.bandotheapp.bando.MyStuffFragment;
import com.bandotheapp.bando.R;

import java.util.Locale;

/**
 * Created by benjamin.harvey on 8/5/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private FeauteredFragment fragmentWithSearch;
    public SectionsPagerAdapter(FragmentManager fm, Context ctx, FeauteredFragment ff) {
        super(fm);
        this.context = ctx;
        this.fragmentWithSearch = ff;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new MyStuffFragment();
            case 0:
                return fragmentWithSearch;
            default:
                return fragmentWithSearch;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
