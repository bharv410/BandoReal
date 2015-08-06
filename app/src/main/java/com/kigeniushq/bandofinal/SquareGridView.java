package com.kigeniushq.bandofinal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class SquareGridView extends ObservableGridView{
    public SquareGridView(Context context){
        super(context);
    }
    public SquareGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
