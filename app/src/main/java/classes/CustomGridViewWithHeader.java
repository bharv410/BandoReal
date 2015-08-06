package classes;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by benjamin.harvey on 8/5/15.
 */
public class CustomGridViewWithHeader extends GridViewWithHeaderAndFooter {

    public CustomGridViewWithHeader(Context context) {
        super(context);
    }

    public CustomGridViewWithHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridViewWithHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* ADD THIS */
    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }
}