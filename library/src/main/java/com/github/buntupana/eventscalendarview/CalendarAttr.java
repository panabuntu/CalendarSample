package com.github.buntupana.eventscalendarview;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

public class CalendarAttr {


    public static final int MONTHLY = 0;
    public static final int WEEKLY = 1;

    public static final int SMALL_INDICATOR = 3;
    public static final int FILL_LARGE_INDICATOR = 1;
    public static final int NO_FILL_LARGE_INDICATOR = 2;
    public static final int DAYS_IN_WEEK = 7;

    // Sizes
    private int mTextSize = 30;
    private int mTargetHeight;

    // Style
    private int mEventIndicatorStyle = SMALL_INDICATOR;
    private boolean mCurrentDayIndicator;
    private int mCurrentDayIndicatorStyle = FILL_LARGE_INDICATOR;
    private int mCurrentSelectedDayIndicatorStyle;
    private boolean mDefaultSelectedPresentDay;
    private int mCalendarFormat = MONTHLY;
    private boolean mInactiveWeekend;

    // Colors
    private int mMultiEventIndicatorColor;
    private int mCurrentDayBackgroundColor;
    private int mCalendarTextColor;
    private int mCurrentSelectedDayBackgroundColor;
    private int mCalendarBackgroundColor;


    public CalendarAttr(Context context, AttributeSet attrs) {

        mCalendarBackgroundColor = ContextCompat.getColor(context, R.color.backgroundColor);
        mCurrentSelectedDayBackgroundColor = ContextCompat.getColor(context, R.color.currentSelectedDayColor);
        mCurrentDayBackgroundColor = ContextCompat.getColor(context, R.color.currentDayColor);
        mCalendarTextColor = ContextCompat.getColor(context, R.color.textColor);
        mMultiEventIndicatorColor = ContextCompat.getColor(context, R.color.multiIndicatorColor);

        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EventsCalendarView, 0, 0);
            try {
                mCurrentDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentDayBackgroundColor, mCurrentDayBackgroundColor);
                mCalendarTextColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarTextColor, mCalendarTextColor);
                mCurrentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentSelectedDayBackgroundColor, mCurrentSelectedDayBackgroundColor);
                mCalendarBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarBackgroundColor, mCalendarBackgroundColor);
                mMultiEventIndicatorColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarMultiEventIndicatorColor, mMultiEventIndicatorColor);
                mTextSize = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_eventsCalendarTextSize, context.getResources().getDimensionPixelSize(R.dimen.textSize));
                mTargetHeight = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_eventsCalendarTargetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTargetHeight, context.getResources().getDisplayMetrics()));
                mEventIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarEventIndicatorStyle, SMALL_INDICATOR);
                mCurrentDayIndicator = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarCurrentDayIndicator, true);
                mCurrentDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarCurrentDayIndicatorStyle, FILL_LARGE_INDICATOR);
                mCurrentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarCurrentSelectedDayIndicatorStyle, FILL_LARGE_INDICATOR);
                mDefaultSelectedPresentDay = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarDefaultSelectedPresentDay, true);
                mCalendarFormat = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarFormat, MONTHLY);
                mInactiveWeekend = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarInactiveWeekend, false);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public int getTargetHeight() {
        return mTargetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        mTargetHeight = targetHeight;
    }

    public int getEventIndicatorStyle() {
        return mEventIndicatorStyle;
    }

    public void setEventIndicatorStyle(int eventIndicatorStyle) {
        mEventIndicatorStyle = eventIndicatorStyle;
    }

    public boolean isCurrentDayIndicator() {
        return mCurrentDayIndicator;
    }

    public void setCurrentDayIndicator(boolean currentDayIndicator) {
        mCurrentDayIndicator = currentDayIndicator;
    }

    public int getCurrentDayIndicatorStyle() {
        return mCurrentDayIndicatorStyle;
    }

    public void setCurrentDayIndicatorStyle(int currentDayIndicatorStyle) {
        mCurrentDayIndicatorStyle = currentDayIndicatorStyle;
    }

    public int getCurrentSelectedDayIndicatorStyle() {
        return mCurrentSelectedDayIndicatorStyle;
    }

    public void setCurrentSelectedDayIndicatorStyle(int currentSelectedDayIndicatorStyle) {
        mCurrentSelectedDayIndicatorStyle = currentSelectedDayIndicatorStyle;
    }

    public boolean isDefaultSelectedPresentDay() {
        return mDefaultSelectedPresentDay;
    }

    public void setDefaultSelectedPresentDay(boolean defaultSelectedPresentDay) {
        mDefaultSelectedPresentDay = defaultSelectedPresentDay;
    }

    public int getCalendarFormat() {
        return mCalendarFormat;
    }

    public void setCalendarFormat(int calendarFormat) {
        mCalendarFormat = calendarFormat;
    }

    public boolean isInactiveWeekend() {
        return mInactiveWeekend;
    }

    public void setInactiveWeekend(boolean inactiveWeekend) {
        mInactiveWeekend = inactiveWeekend;
    }

    public int getMultiEventIndicatorColor() {
        return mMultiEventIndicatorColor;
    }

    public void setMultiEventIndicatorColor(int multiEventIndicatorColor) {
        mMultiEventIndicatorColor = multiEventIndicatorColor;
    }

    public int getCurrentDayBackgroundColor() {
        return mCurrentDayBackgroundColor;
    }

    public void setCurrentDayBackgroundColor(int currentDayBackgroundColor) {
        mCurrentDayBackgroundColor = currentDayBackgroundColor;
    }

    public int getCalendarTextColor() {
        return mCalendarTextColor;
    }

    public void setCalendarTextColor(int calendarTextColor) {
        mCalendarTextColor = calendarTextColor;
    }

    public int getCurrentSelectedDayBackgroundColor() {
        return mCurrentSelectedDayBackgroundColor;
    }

    public void setCurrentSelectedDayBackgroundColor(int currentSelectedDayBackgroundColor) {
        mCurrentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
    }

    public int getCalendarBackgroundColor() {
        return mCalendarBackgroundColor;
    }

    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        mCalendarBackgroundColor = calendarBackgroundColor;
    }
}
