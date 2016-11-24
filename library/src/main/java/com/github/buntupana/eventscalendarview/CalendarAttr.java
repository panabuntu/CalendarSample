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
    private boolean mCalendarHeaderBoldText = false;

    // Colors
    private int mMultiEventIndicatorColor;
    private int mCurrentDayBackgroundColor;
    private int mCurrentDayTextColor;
    private int mCalendarTextColor;
    private int mCurrentSelectedDayBackgroundColor;
    private int mCurrentSelectedDayTextColor;
    private int mCalendarBackgroundColor;
    private int mCalendarHeaderTextColor;


    public CalendarAttr(Context context, AttributeSet attrs) {

        mCalendarBackgroundColor = ContextCompat.getColor(context, R.color.backgroundColor);
        mCurrentSelectedDayBackgroundColor = ContextCompat.getColor(context, R.color.currentSelectedDayColor);
        mCurrentDayBackgroundColor = ContextCompat.getColor(context, R.color.currentDayColor);
        mCalendarTextColor = ContextCompat.getColor(context, R.color.textColor);
        mMultiEventIndicatorColor = mCalendarTextColor;
        mCalendarHeaderTextColor = mCalendarTextColor;

        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EventsCalendarView, 0, 0);
            try {
                mCurrentDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_currentDayBackgroundColor, mCurrentDayBackgroundColor);
                mCalendarHeaderTextColor = typedArray.getColor(R.styleable.EventsCalendarView_headerTextColor, mCalendarHeaderTextColor);
                mCalendarHeaderBoldText = typedArray.getBoolean(R.styleable.EventsCalendarView_headerTextBold, mCalendarHeaderBoldText);
                mCurrentDayTextColor = typedArray.getColor(R.styleable.EventsCalendarView_currentDayTextColor, mCalendarTextColor);
                mCalendarTextColor = typedArray.getColor(R.styleable.EventsCalendarView_textColor, mCalendarTextColor);
                mCurrentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_currentSelectedDayBackgroundColor, mCurrentSelectedDayBackgroundColor);
                mCurrentSelectedDayTextColor = typedArray.getColor(R.styleable.EventsCalendarView_currentSelectedDayTextColor, mCalendarTextColor);
                mCalendarBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_backgroundColor, mCalendarBackgroundColor);
                mMultiEventIndicatorColor = typedArray.getColor(R.styleable.EventsCalendarView_multiEventIndicatorColor, mMultiEventIndicatorColor);
                mTextSize = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_textSize, context.getResources().getDimensionPixelSize(R.dimen.textSize));
                mTargetHeight = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_targetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTargetHeight, context.getResources().getDisplayMetrics()));
                mEventIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventIndicatorStyle, SMALL_INDICATOR);
                mCurrentDayIndicator = typedArray.getBoolean(R.styleable.EventsCalendarView_currentDayIndicator, true);
                mCurrentDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_currentDayIndicatorStyle, FILL_LARGE_INDICATOR);
                mCurrentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_currentSelectedDayIndicatorStyle, FILL_LARGE_INDICATOR);
                mDefaultSelectedPresentDay = typedArray.getBoolean(R.styleable.EventsCalendarView_defaultSelectedPresentDay, true);
                mCalendarFormat = typedArray.getInt(R.styleable.EventsCalendarView_calendarFormat, MONTHLY);
                mInactiveWeekend = typedArray.getBoolean(R.styleable.EventsCalendarView_inactiveWeekend, false);
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

    public boolean isCalendarHeaderBoldText() {
        return mCalendarHeaderBoldText;
    }

    public void setCalendarHeaderBoldText(boolean calendarHeaderBoldText) {
        mCalendarHeaderBoldText = calendarHeaderBoldText;
    }

    public int getCalendarHeaderTextColor() {
        return mCalendarHeaderTextColor;
    }

    public void setCalendarHeaderTextColor(int calendarHeaderTextColor) {
        mCalendarHeaderTextColor = calendarHeaderTextColor;
    }

    public int getCurrentDayBackgroundColor() {
        return mCurrentDayBackgroundColor;
    }

    public void setCurrentDayBackgroundColor(int currentDayBackgroundColor) {
        mCurrentDayBackgroundColor = currentDayBackgroundColor;
    }

    public int getCurrentDayTextColor() {
        return mCurrentDayTextColor;
    }

    public void setCurrentDayTextColor(int currentDayTextColor) {
        mCurrentDayTextColor = currentDayTextColor;
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

    public int getCurrentSelectedDayTextColor() {
        return mCurrentSelectedDayTextColor;
    }

    public void setCurrentSelectedDayTextColor(int currentSelectedDayTextColor) {
        mCurrentSelectedDayTextColor = currentSelectedDayTextColor;
    }

    public int getCalendarBackgroundColor() {
        return mCalendarBackgroundColor;
    }

    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        mCalendarBackgroundColor = calendarBackgroundColor;
    }
}
