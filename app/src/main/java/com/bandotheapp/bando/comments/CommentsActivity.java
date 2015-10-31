package com.bandotheapp.bando.comments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bandotheapp.bando.MainActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.bandotheapp.bando.R;
import com.bandotheapp.bando.com.bandotheapp.bando.libraryacti.TinyDB;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.os.Handler;
import android.widget.Toast;



public class CommentsActivity extends Activity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;
    @InjectView(R.id.etComment)
    EditText etComment;
    @InjectView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private ParseUser parseuser;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);
        setupComments();
        setupSendCommentButton();

        if(getActionBar()!=null) {
            getActionBar().setTitle("Comments");
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

//classes.Utils.getTimeAgo(po.getCreatedAt().getTime(), getActivity())
    }

    @Override
    protected void onPause() {
        parseuser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.v("benmark", "loggedOut");
            }
        });
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startIntroAnimation();


        final TinyDB tinydb = new TinyDB(getApplicationContext());
        if (tinydb.getString("username")==null || tinydb.getString("username").length()<1){
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent_info = new Intent(CommentsActivity.this, LoginActivity.class);
                    startActivity(intent_info);
                    overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
                }
            }, 1000);
        }else{

            parseuser = ParseUser.getCurrentUser();
            if(parseuser==null) {

                ParseUser.logInInBackground(tinydb.getString("username"), tinydb.getString("password"), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            if (user != null) {
                                Snackbar.make(findViewById(android.R.id.content), "loggedin", Snackbar.LENGTH_SHORT)
                                        .setActionTextColor(Color.WHITE)
                                        .show();
                                parseuser = user;

                                commentsAdapter.updateItems();
                            } else {
                                Intent intent_info = new Intent(CommentsActivity.this, LoginActivity.class);
                                startActivity(intent_info);
                                overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
                            }
                        } else {
                            e.printStackTrace();
                        }

                    }
                });
            }else{
                commentsAdapter.updateItems();
            }
        }
    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this,getIntent().getStringExtra("key"));
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        //ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        //commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        //ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
             final Comment ocarina = new Comment();
            ocarina.setOwner(ParseUser.getCurrentUser());
            ocarina.setText(etComment.getText().toString());
            ocarina.setKey(getIntent().getStringExtra("key"));
            ocarina.setDisplayName(ParseUser.getCurrentUser().getUsername());
            ocarina.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    // Now the ocarina already knows how to play.
                    if(e==null){

                        commentsAdapter.addItem(ocarina);
                        commentsAdapter.setAnimationsLocked(false);
                        commentsAdapter.setDelayEnterAnimation(false);
                        //rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

                        etComment.setText(null);
                        btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);

                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }else
                        e.printStackTrace();
                }
            });

        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }
}
