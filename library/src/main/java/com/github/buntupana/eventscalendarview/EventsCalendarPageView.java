package com.github.buntupana.eventscalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarPageViewListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.github.buntupana.eventscalendarview.EventsCalendarView.MONTHLY;


public class EventsCalendarPageView extends View {

    private final String TAG = EventsCalendarPageView.class.getSimpleName();

    public static final int SMALL_INDICATOR = 3;
    public static final int FILL_LARGE_INDICATOR = 1;
    public static final int NO_FILL_LARGE_INDICATOR = 2;
    private static final int DAYS_IN_WEEK = 7;

    private GestureDetectorCompat gestureDetector;

    private String[] dayColumnNames;

    // Sizes
    private int textSize = 30;
    private int textHeight;
    private int textWidth;
    private int heightPerDay;
    private int widthPerDay;
    private int targetHeight;
    private int width;
    private int height;
    private int paddingRight;
    private int paddingLeft;
    private int paddingWidth = 40;
    private int paddingHeight = 40;
    private Rect textSizeRect = new Rect();
    private float bigCircleIndicatorRadius;
    private float smallIndicatorRadius;
    private float xIndicatorOffset;
    private float multiDayIndicatorStrokeWidth;
    private float screenDensity = 1;

    // Style
    private int eventIndicatorStyle = SMALL_INDICATOR;
    private boolean currentDayIndicator;
    private int currentDayIndicatorStyle = FILL_LARGE_INDICATOR;
    private int currentSelectedDayIndicatorStyle;
    private boolean defaultSelectedPresentDay;
    private int calendarFormat = MONTHLY;
    private boolean shouldShowMondayAsFirstDay = true;
    private boolean useThreeLetterAbbreviation = false;
    private boolean inactiveWeekend;
    private boolean shouldDrawDaysHeader = true;
    private boolean shouldDrawIndicatorsBelowSelectedDays = false;

    // Colors
    private int multiEventIndicatorColor;
    private int currentDayBackgroundColor;
    private int calendarTextColor;
    private int currentSelectedDayBackgroundColor;
    private int calendarBackgroundColor = Color.WHITE;
    private TimeZone timeZone = TimeZone.getDefault();
    private Locale locale = Locale.getDefault();

    // Calendars
    private Calendar todayCalendar;
    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar minDateCalendar = null;
    private Calendar maxDateCalendar = null;
    private Calendar eventsCalendar;

    // Events
    private EventsContainer eventsContainer;

    private Paint dayPaint = new Paint();
    private Paint background = new Paint();

    private List<Integer> inactiveDaysList = new ArrayList<>();

    private EventsCalendarPageViewListener listener;

    private CalendarDrawer calendarDrawer;

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            int dayColumn = Math.round((paddingLeft + e.getX() - paddingWidth - paddingRight) / widthPerDay);
            int dayRow = Math.round((e.getY() - paddingHeight) / heightPerDay);

            long currentTime = currentCalendar.getTimeInMillis();
            setFirstDay(currentCalendar);

            //Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
            int firstDayOfMonth = getDayOfWeek(currentCalendar, shouldShowMondayAsFirstDay);

            int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

            Log.d(TAG, + dayOfMonth + "-" + currentCalendar.get(Calendar.MONTH) + "-" + currentCalendar.get(Calendar.YEAR) + " Clicked");

            if (dayOfMonth < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    && dayOfMonth >= 0) {
                currentCalendar.add(Calendar.DATE, dayOfMonth);
                if (!isInactiveDate(currentCalendar, minDateCalendar, maxDateCalendar, inactiveDaysList)) {
                    invalidate();
                    Log.d(TAG, "Day selected");
                    if (listener != null) {
                        listener.onDayClick(currentCalendar.getTime());
                    }
                } else {
                    currentCalendar.setTimeInMillis(currentTime);
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    };

    public EventsCalendarPageView(Context context, AttributeSet attrs, Calendar currentCalendar,
                                  Calendar minDateCalendar, Calendar maxDateCalendar, List<Integer> inactiveDays,
                                  TimeZone timeZone, Locale locale, EventsCalendarPageViewListener listener) {
        super(context, attrs);
        this.currentCalendar = currentCalendar;
        this.minDateCalendar = minDateCalendar;
        this.maxDateCalendar = maxDateCalendar;
        this.inactiveDaysList = inactiveDays;
        this.timeZone = timeZone;
        this.locale = locale;
        this.listener = listener;
        setAttrs(context, attrs);
        init(context);
    }

    public EventsCalendarPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttrs(context, attrs);
        init(context);
    }

    public EventsCalendarPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttrs(context, attrs);
        init(context);
    }

    private void setAttrs(Context context, AttributeSet attrs) {

        calendarBackgroundColor = ContextCompat.getColor(context, R.color.backgroundColor);
        currentSelectedDayBackgroundColor = ContextCompat.getColor(context, R.color.currentSelectedDayColor);
        currentDayBackgroundColor = ContextCompat.getColor(context, R.color.currentDayColor);
        calendarTextColor = ContextCompat.getColor(context, R.color.textColor);
        multiEventIndicatorColor = ContextCompat.getColor(context, R.color.multiIndicatorColor);

        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EventsCalendarView, 0, 0);
            try {
                currentDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
                calendarTextColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarTextColor, calendarTextColor);
                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor);
                calendarBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarBackgroundColor, calendarBackgroundColor);
                multiEventIndicatorColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarMultiEventIndicatorColor, multiEventIndicatorColor);
                textSize = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_eventsCalendarTextSize, getResources().getDimensionPixelSize(R.dimen.textSize));
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_eventsCalendarTargetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
                eventIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarEventIndicatorStyle, SMALL_INDICATOR);
                currentDayIndicator = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarCurrentDayIndicator, true);
                currentDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarCurrentDayIndicatorStyle, FILL_LARGE_INDICATOR);
                currentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarCurrentSelectedDayIndicatorStyle, FILL_LARGE_INDICATOR);
                defaultSelectedPresentDay = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarDefaultSelectedPresentDay, true);
                calendarFormat = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarFormat, MONTHLY);
                inactiveWeekend = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarInactiveWeekend, false);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(Context context) {

        todayCalendar = initCalendar();
        if(currentCalendar == null)
            currentCalendar = initCalendar();
        eventsCalendar = initCalendar();

        eventsContainer = new EventsContainer(Calendar.getInstance(), calendarFormat);

        setUseWeekDayAbbreviation(false);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(Typeface.SANS_SERIF);
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calendarTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), textSizeRect);
        textHeight = textSizeRect.height() * 3;
        textWidth = textSizeRect.width() * 2;

        gestureDetector = new GestureDetectorCompat(context, gestureListener);

        initScreenDensityRelatedValues(context);

        //scale small indicator by screen density
        smallIndicatorRadius = 2.5f * screenDensity;
        xIndicatorOffset = 3.5f * screenDensity;

    }

    private void initScreenDensityRelatedValues(Context context) {
        if (context != null) {
            screenDensity = context.getResources().getDisplayMetrics().density;
            final ViewConfiguration configuration = ViewConfiguration
                    .get(context);
//            densityAdjustedSnapVelocity = (int) (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND);
//            maximumVelocity = configuration.getScaledMaximumFlingVelocity();

            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            multiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        todayCalendar = initCalendar();

        calendarDrawer = new CalendarDrawer(heightPerDay, paddingHeight, widthPerDay, paddingWidth, paddingRight, paddingLeft,
                screenDensity, dayPaint, calendarTextColor, currentDayIndicator, currentDayIndicatorStyle, currentSelectedDayIndicatorStyle,
                currentSelectedDayBackgroundColor, currentDayBackgroundColor, bigCircleIndicatorRadius, eventsContainer,
                shouldDrawDaysHeader, todayCalendar, inactiveDaysList, dayColumnNames, eventsCalendar, shouldDrawIndicatorsBelowSelectedDays,
                textHeight, eventIndicatorStyle, smallIndicatorRadius, multiEventIndicatorColor, multiDayIndicatorStrokeWidth,
                minDateCalendar, maxDateCalendar, shouldShowMondayAsFirstDay, xIndicatorOffset);

        calendarDrawer.drawCalendarBackground(canvas, calendarBackgroundColor, width, height);
        if (calendarFormat == MONTHLY) {
            calendarDrawer.drawMonth(canvas, currentCalendar);
        } else {
            calendarDrawer.drawWeek(canvas, currentCalendar);
        }
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        int width = MeasureSpec.getSize(parentWidth);
        int height = MeasureSpec.getSize(parentHeight);
        if (width > 0 && height > 0) {
            widthPerDay = (width) / DAYS_IN_WEEK;
            heightPerDay = targetHeight > 0 ? targetHeight / 7 : height / 7;
            this.width = width;
            this.height = height;
            this.paddingRight = getPaddingRight();
            this.paddingLeft = getPaddingLeft();

            //makes easier to find radius
            bigCircleIndicatorRadius = getInterpolatedBigCircleIndicator();

            // scale the selected day indicators slightly so that event indicators can be drawn below
            bigCircleIndicatorRadius = shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == SMALL_INDICATOR ? bigCircleIndicatorRadius * 0.85f : bigCircleIndicatorRadius;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    //assume square around each day of width and height = heightPerDay and get diagonal line length
    //interpolate height and radius
    //https://en.wikipedia.org/wiki/Linear_interpolation
    private float getInterpolatedBigCircleIndicator() {
        float x0 = textSizeRect.height();
        float x1 = heightPerDay; // take into account indicator offset
        float x = (x1 + textSizeRect.height()) / 2f; // pick a point which is almost half way through heightPerDay and textSizeRect
        double y1 = 0.5 * Math.sqrt((x1 * x1) + (x1 * x1));
        double y0 = 0.5 * Math.sqrt((x0 * x0) + (x0 * x0));

        return (float) (y0 + ((y1 - y0) * ((x - x0) / (x1 - x0))));
    }

    void setUseWeekDayAbbreviation(boolean useThreeLetterAbbreviation) {
        this.useThreeLetterAbbreviation = useThreeLetterAbbreviation;
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        if (dayNames == null) {
            throw new IllegalStateException("Unable to determine weekday names from default locale");
        }
        if (dayNames.length != 8) {
            throw new IllegalStateException("Expected weekday names from default locale to be of size 7 but: "
                    + Arrays.toString(dayNames) + " with size " + dayNames.length + " was returned.");
        }

        if (useThreeLetterAbbreviation) {
            if (!shouldShowMondayAsFirstDay) {
                this.dayColumnNames = new String[]{dayNames[1], dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7]};
            } else {
                this.dayColumnNames = new String[]{dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7], dayNames[1]};
            }
        } else {
            if (!shouldShowMondayAsFirstDay) {
                this.dayColumnNames = new String[]{dayNames[1].substring(0, 1), dayNames[2].substring(0, 1),
                        dayNames[3].substring(0, 1), dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1)};
            } else {
                this.dayColumnNames = new String[]{dayNames[2].substring(0, 1), dayNames[3].substring(0, 1),
                        dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1), dayNames[1].substring(0, 1)};
            }
        }
    }

    public static boolean isInactiveDate(Calendar calendar, Calendar minDateCalendar, Calendar maxDateCalendar, List<Integer> inactiveDays) {
        boolean conditionalActiveDay = inactiveDays.contains(calendar.get(Calendar.DAY_OF_WEEK));
        boolean conditionalMinDate = minDateCalendar != null && calendar.getTimeInMillis() < minDateCalendar.getTimeInMillis();
        boolean conditionalMaxDate = maxDateCalendar != null && maxDateCalendar.getTimeInMillis() < calendar.getTimeInMillis();

        return conditionalActiveDay || conditionalMinDate || conditionalMaxDate;
    }

    void setDayColumnNames(String[] dayColumnNames) {
        if (dayColumnNames == null || dayColumnNames.length != 7) {
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }

    void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader) {
        this.shouldDrawDaysHeader = shouldDrawDaysHeader;
    }

    public void setCurrentDate(Date currentDate) {
        if (currentDate == null) {
            currentCalendar = Calendar.getInstance();
        } else {
            currentCalendar.setTime(currentDate);
        }
        invalidate();
    }

    private void setFirstDay(Calendar calendar) {

        if (calendarFormat == MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        }
        resetHour(calendar);
    }

    private void resetHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static int getDayOfWeek(Calendar calendar, boolean shouldShowMondayAsFirstDay) {

        int dayOfWeek;
        if (!shouldShowMondayAsFirstDay) {
            return calendar.get(Calendar.DAY_OF_WEEK);
        } else {
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            dayOfWeek = dayOfWeek <= 0 ? 7 : dayOfWeek;
        }
        return dayOfWeek;
    }

    /**
     * see {@link #addEvent(Event, boolean)} when adding single events
     * or {@link #addEvents(java.util.List)}  when adding multiple events
     *
     * @param event
     */
    @Deprecated
    public void addEvent(Event event) {
        addEvent(event, false);
    }

    /**
     * Adds an event to be drawn as an indicator in the calendar.
     * If adding multiple events see {@link #addEvents(List)}} method.
     *
     * @param event            to be added to the calendar
     * @param shouldInvalidate true if the view should invalidate
     */
    public void addEvent(Event event, boolean shouldInvalidate) {
        eventsContainer.addEvent(event);
        if (shouldInvalidate) {
            invalidate();
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    public void addEvents(List<Event> events) {
        eventsContainer.addEvents(events);
        invalidate();
    }

    public List<Event> getCalendarEventsFor(long epochMillis) {
        return eventsContainer.getEventsFor(epochMillis);
    }

    public List<Event> getCalendarEventsForMonth(long epochMillis) {
        return eventsContainer.getEventsForMonth(epochMillis);
    }

    public void removeEventsFor(long epochMillis) {
        eventsContainer.removeEventByEpochMillis(epochMillis);
    }

    public void removeEvent(Event event) {
        eventsContainer.removeEvent(event);
    }

    public void removeEvents(List<Event> events) {
        eventsContainer.removeEvents(events);
    }

    public void removeAllEvents() {
        eventsContainer.removeAllEvents();
    }

    /**
     * Set inactive week days to the calendar
     *
     * @param inactiveDays of the week to be set to the calendar, example: Calendar.MONDAY
     */
    public void addInactiveDays(int... inactiveDays) {
        for (int inactiveDay : inactiveDays) {
            inactiveDaysList.add(inactiveDay);
        }
        invalidate();
    }

    /**
     *
     *
     */
    public void clearInactiveDays() {
        inactiveDaysList.clear();
        invalidate();
    }

    public void removeInactiveDay(int inactiveDay) {
        for (Integer day : inactiveDaysList) {
            if (day == inactiveDay) {
                inactiveDaysList.remove(day);
                break;
            }
        }
        invalidate();
    }

    /**
     * Fetches the inactive days
     *
     * @return
     */
    public List<Integer> getInactiveDaysList() {
        return inactiveDaysList;
    }

    public void setLocale(TimeZone timeZone, Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        if (timeZone == null) {
            throw new IllegalArgumentException("TimeZone cannot be null.");
        }
        this.locale = locale;
        this.timeZone = timeZone;
        this.eventsContainer = new EventsContainer(Calendar.getInstance(this.timeZone, this.locale), calendarFormat);
        // passing null will not re-init density related values - and that's ok
        init(null);
    }

    public void setCurrentSelectedDayIndicatorStyle(final int currentSelectedDayIndicatorStyle) {
        this.currentSelectedDayIndicatorStyle = currentSelectedDayIndicatorStyle;
        invalidate();
    }

    public void setCurrentDayIndicatorStyle(final int currentDayIndicatorStyle) {
        this.currentDayIndicatorStyle = currentDayIndicatorStyle;
        invalidate();
    }

    public void setEventIndicatorStyle(final int eventIndicatorStyle) {
        this.eventIndicatorStyle = eventIndicatorStyle;
        invalidate();
    }

    private void checkTargetHeight() {
        if (targetHeight <= 0) {
            throw new IllegalStateException("Target height must be set in xml properties in order to expand/collapse CompactCalendar.");
        }
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
        checkTargetHeight();
    }

    public void showCalendar() {
        checkTargetHeight();
//        animationHandler.openCalendar();
    }

    public void hideCalendar() {
        checkTargetHeight();
//        animationHandler.closeCalendar();
    }

    public void showCalendarWithAnimation() {
        checkTargetHeight();
//        animationHandler.openCalendarWithAnimation();
    }

    public void hideCalendarWithAnimation() {
        checkTargetHeight();
//        animationHandler.closeCalendarWithAnimation();
    }

    public void setMaxDateCalendar(Date maxDate) {
        if (maxDate == null) {
            maxDateCalendar = null;
        } else {
            maxDateCalendar = initCalendar();
            maxDateCalendar.setTime(maxDate);
        }
        invalidate();
    }

    public void setMinDateCalendar(Date minDate) {
        if (minDate == null) {
            minDateCalendar = null;
        } else {
            minDateCalendar = initCalendar();
            minDateCalendar.setTime(minDate);
        }
        invalidate();
    }

    public Calendar initCalendar(){
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        // make setMinimalDaysInFirstWeek same across android versions
        calendar.setMinimalDaysInFirstWeek(1);
        if(shouldShowMondayAsFirstDay){
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
        } else {
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        }
        return calendar;
    }

    public void setShouldShowMondayAsFirstDay(boolean shouldShowMondayAsFirstDay) {
        this.shouldShowMondayAsFirstDay = shouldShowMondayAsFirstDay;
        setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        if (shouldShowMondayAsFirstDay) {
            eventsCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            todayCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        } else {
            eventsCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            todayCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            currentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        }
    }

    public void setOnDayClickListener(EventsCalendarPageViewListener listener){
        this.listener = listener;
    }
}
