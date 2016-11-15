package com.github.buntupana.eventscalendarview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomViewPager extends PagerAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View v = new CalendarPage(collection.getContext());

        ((ViewPager) collection).addView(v,0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((TextView) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
