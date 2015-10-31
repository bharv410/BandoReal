package com.bandotheapp.bando.comments;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bandotheapp.bando.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Miroslaw Stanek on 15.07.15.
 */
public class BaseDrawerActivity extends Activity {



    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    ImageView ivMenuUserProfilePhoto;

    private int avatarSize;
    private String profilePhoto;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);

        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        injectViews();
        setupHeader();
    }

    protected void injectViews() {
        ButterKnife.inject(this);
        setupToolbar();
    }

    protected void setupToolbar() {

    }

    private void setupHeader() {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        //this.profilePhoto = getResources().getString(R.string.user_profile_photo);
//        Picasso.with(this)
//                .load("http://i67.tinypic.com/zxk4zn.png")
//                .placeholder(R.drawable.img_circle_placeholder)
//                .resize(avatarSize, avatarSize)
//                .centerCrop()
//                .transform(new CircleTransformation())
//                .into(ivMenuUserProfilePhoto);
    }

}
