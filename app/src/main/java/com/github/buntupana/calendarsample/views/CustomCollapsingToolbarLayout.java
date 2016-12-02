package com.github.buntupana.calendarsample.views;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.view.View;


public class CustomCollapsingToolbarLayout extends CollapsingToolbarLayout {
    public CustomCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
    }
}
