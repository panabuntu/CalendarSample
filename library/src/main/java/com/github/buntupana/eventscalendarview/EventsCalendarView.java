package com.github.buntupana.eventscalendarview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class EventsCalendarView extends RelativeLayout {
    public EventsCalendarView(Context context) {
        super(context);
        init();
    }

    public EventsCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttrs(attrs);
        init();
    }

//    public EventsCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
////        super(context, attrs, defStyleAttr);
//        setAttrs(attrs);
//        init();
//    }

    private void setAttrs(AttributeSet attrs){

    }

    private void init(){

        ViewPager viewPager = new ViewPager(getContext());




        addView(viewPager);
    }

}
