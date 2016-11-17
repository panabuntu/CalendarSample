package com.github.buntupana.eventscalendarview;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventsCalendarController {

    public static final int SMALL_INDICATOR = 3;
    public static final int FILL_LARGE_INDICATOR = 1;

    private Context mContext;

    private Date currentDate = new Date();
    private Locale locale;
    public Calendar currentCalender;
    private Calendar todayCalender;
    private Calendar calendarWithFirstDay;
    private Calendar eventsCalendar;
    private Calendar maxDateCalendar;
    private Calendar minDateCalendar;
    private EventsContainer eventsContainer;
    private PointF accumulatedScrollOffset = new PointF();
    private OverScroller scroller;
    private Paint dayPaint = new Paint();
    private Paint background = new Paint();
    private Rect textSizeRect;
    private String[] dayColumnNames;
    private List<Integer> inactiveDays = new ArrayList<>();

    // colors
    private int multiEventIndicatorColor;
    private int currentDayBackgroundColor;
    private int calenderTextColor;
    private int currentSelectedDayBackgroundColor;
    private int calenderBackgroundColor = Color.WHITE;
    private boolean shouldScroll = true;
    private TimeZone timeZone;

    public EventsCalendarController(Paint dayPaint, Rect textSizeRect, AttributeSet attrs,
                                    Context context, int currentDayBackgroundColor, int calenderTextColor,
                                    int currentSelectedDayBackgroundColor, int multiEventIndicatorColor,
                                    Locale locale, TimeZone timeZone) {

        this.dayPaint = dayPaint;
        this.textSizeRect = textSizeRect;
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.calenderTextColor = calenderTextColor;
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
        this.multiEventIndicatorColor = multiEventIndicatorColor;
        this.locale = locale;
        this.timeZone = timeZone;
        loadAttributes(attrs, context);
//        this.eventsContainer = new EventsContainer(Calendar.getInstance(), calendarFormat);
//        init();

    }

    private void loadAttributes(AttributeSet attrs, Context context) {
//        if (attrs != null && context != null) {
//            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CompactCalendarView, 0, 0);
//            try {
//                currentDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
//                calenderTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextColor, calenderTextColor);
//                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor);
//                calenderBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarBackgroundColor, calenderBackgroundColor);
//                multiEventIndicatorColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarMultiEventIndicatorColor, multiEventIndicatorColor);
//                textSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTextSize,
//                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
//                targetHeight = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTargetHeight,
//                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
//                eventIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarEventIndicatorStyle, SMALL_INDICATOR);
//                currentDayIndicator = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarCurrentDayIndicator, true);
//                currentDayIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentDayIndicatorStyle, FILL_LARGE_INDICATOR);
//                currentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayIndicatorStyle, FILL_LARGE_INDICATOR);
//                defaultSelectedPresentDay = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarDefaultSelectedPresentDay, true);
//                calendarFormat = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarFormat, MONTHLY);
//                inactiveWeekend = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarInactiveWeekend, false);
//            } finally {
//                typedArray.recycle();
//            }
//        }
    }

    private void init(){

    }


}
