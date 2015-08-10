package com.bandotheapp.bando;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by benjamin.harvey on 8/6/15.
 */
public class ThreeFourthsImageView extends ImageView {

    public ThreeFourthsImageView(Context context) {
        super(context);
    }

    public ThreeFourthsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeFourthsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (getMeasuredWidth() * 3) / 4); //Snap to width
    }
}
