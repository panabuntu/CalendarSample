package com.github.buntupana.eventscalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import com.github.buntupana.eventscalendarview.events.EventsContainer;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarPageViewListener;
import com.github.buntupana.eventscalendarview.utils.CalendarUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.github.buntupana.eventscalendarview.CalendarAttr.MONTHLY;


public class EventsCalendarPageView extends View {

    private final String TAG = EventsCalendarPageView.class.getSimpleName();

    private GestureDetectorCompat gestureDetector;

    private String[] dayColumnNames;

    private CalendarAttr mCalendarAttr;

    private boolean mShouldSelect = true;

    // Sizes
//    private int mTextSize = 30;
    private int mTargetHeight;

    private int mTextHeight;
    private int mTextWidth;
    private int mHeightPerDay;
    private int mWidthPerDay;
    private int mWidth;
    private int mHeight;
    private int mPaddingRight;
    private int mPaddingLeft;
    private int mPaddingWidth = 40;
    private int mPaddingHeight = 40;
    private Rect mTextSizeRect = new Rect();
    private float mBigCircleIndicatorRadius;
    private float mSmallIndicatorRadius;
    private float mXIndicatorOffset;
    private float mMultiDayIndicatorStrokeWidth;
    private float mScreenDensity = 1;

    private boolean mShouldDrawIndicatorsBelowSelectedDays = false;
    private boolean mShouldDrawDaysHeader = true;
    private boolean mUseThreeLetterAbbreviation = false;

    private static boolean sShouldShowMondayAsFirstDay = true;

    private Paint mDayPaint = new Paint();
    private Paint mBackground = new Paint();

    // Calendars
    private Calendar mTodayCalendar;
    private Calendar mCurrentCalendar;
    private Calendar mMinDateCalendar = null;
    private Calendar mMaxDateCalendar = null;
    private Calendar mEventsCalendar;
    private TimeZone mTimeZone = TimeZone.getDefault();
    private Locale mLocale = Locale.getDefault();

    // Events
    private EventsContainer mEventsContainer;
    private EventsCalendarPageViewListener mListener;

    private List<Integer> mInactiveDaysList = new ArrayList<>();
    private CalendarDrawer mCalendarDrawer;


    private final GestureDetector.SimpleOnGestureListener mGesturelistener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            int dayColumn = Math.round((mPaddingLeft + e.getX() - mPaddingWidth - mPaddingRight) / mWidthPerDay);
            int dayRow = Math.round((e.getY() - mPaddingHeight) / mHeightPerDay);

            long currentTime = mCurrentCalendar.getTimeInMillis();
            setFirstDay(mCurrentCalendar);

            //Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
            int firstDayOfMonth = CalendarUtils.getDayOfWeek(mCurrentCalendar, sShouldShowMondayAsFirstDay);

            int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

            Log.d(TAG, +dayOfMonth + "-" + mCurrentCalendar.get(Calendar.MONTH) + "-" + mCurrentCalendar.get(Calendar.YEAR) + " Clicked");

            if (dayOfMonth < mCurrentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    && dayOfMonth >= 0) {
                mCurrentCalendar.add(Calendar.DATE, dayOfMonth);
                if (!CalendarUtils.isInactiveDate(mCurrentCalendar, mMinDateCalendar, mMaxDateCalendar, mInactiveDaysList)) {
                    invalidate();
                    Log.d(TAG, "Day selected");
                    if (mListener != null) {
                        mListener.onDayClick(mCurrentCalendar.getTime());
                    }
                } else {
                    mCurrentCalendar.setTimeInMillis(currentTime);
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    };

    public EventsCalendarPageView(Context context, Date currentCalendar,
                                  Date minDateCalendar, Date maxDateCalendar, EventsContainer eventsContainer, List<Integer> inactiveDays,
                                  TimeZone timeZone, Locale locale, CalendarAttr calendarAttr, boolean shouldSelect, EventsCalendarPageViewListener listener) {
        super(context);
        mCurrentCalendar = CalendarUtils.getCalendar(currentCalendar, sShouldShowMondayAsFirstDay, timeZone, locale);
        mMinDateCalendar = CalendarUtils.getCalendar(minDateCalendar, sShouldShowMondayAsFirstDay, timeZone, locale);
        mMaxDateCalendar = CalendarUtils.getCalendar(maxDateCalendar, sShouldShowMondayAsFirstDay, timeZone, locale);
        mEventsContainer = eventsContainer;
        mInactiveDaysList = inactiveDays;
        mTimeZone = timeZone;
        mLocale = locale;
        mListener = listener;
        mCalendarAttr = calendarAttr;
        mShouldSelect = shouldSelect;

        init(context);
    }

    public EventsCalendarPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCalendarAttr = new CalendarAttr(context, attrs);
        init(context);
    }

    public EventsCalendarPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCalendarAttr = new CalendarAttr(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mTodayCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);
        if (mCurrentCalendar == null) {
            mCurrentCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);
        }
        mEventsCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);

        if(mEventsContainer == null) {
            mEventsContainer = new EventsContainer(Calendar.getInstance(), mCalendarAttr.getCalendarFormat());
        }

        setUseWeekDayAbbreviation(false);
        mDayPaint.setTextAlign(Paint.Align.CENTER);
        mDayPaint.setStyle(Paint.Style.STROKE);
        mDayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDayPaint.setTypeface(Typeface.SANS_SERIF);
        mDayPaint.setTextSize(mCalendarAttr.getTextSize());
        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
        mDayPaint.getTextBounds("31", 0, "31".length(), mTextSizeRect);
        mTextHeight = mTextSizeRect.height() * 3;
        mTextWidth = mTextSizeRect.width() * 2;

        gestureDetector = new GestureDetectorCompat(context, mGesturelistener);

        initScreenDensityRelatedValues(context);

        //scale small indicator by screen density
        mSmallIndicatorRadius = 2.5f * mScreenDensity;
        mXIndicatorOffset = 3.5f * mScreenDensity;
    }

    private void initScreenDensityRelatedValues(Context context) {
        if (context != null) {
            mScreenDensity = context.getResources().getDisplayMetrics().density;
            final ViewConfiguration configuration = ViewConfiguration
                    .get(context);
//            densityAdjustedSnapVelocity = (int) (mScreenDensity * SNAP_VELOCITY_DIP_PER_SECOND);
//            maximumVelocity = configuration.getScaledMaximumFlingVelocity();

            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mMultiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaddingWidth = mWidthPerDay / 2;
        mPaddingHeight = mHeightPerDay / 2;
        mTodayCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);

        mCalendarDrawer = new CalendarDrawer(mHeightPerDay, mPaddingHeight, mWidthPerDay, mPaddingWidth, mPaddingRight, mPaddingLeft,
                mScreenDensity, mDayPaint, mCalendarAttr.getCalendarTextColor(), mCalendarAttr.isCurrentDayIndicator(), mCalendarAttr.getCurrentDayIndicatorStyle(),
                mCalendarAttr.getCurrentSelectedDayIndicatorStyle(),
                mCalendarAttr.getCurrentSelectedDayBackgroundColor(), mCalendarAttr.getCurrentDayBackgroundColor(), mBigCircleIndicatorRadius, mEventsContainer,
                mShouldDrawDaysHeader, mTodayCalendar, mInactiveDaysList, dayColumnNames, mEventsCalendar, mShouldDrawIndicatorsBelowSelectedDays,
                mTextHeight, mCalendarAttr.getEventIndicatorStyle(), mSmallIndicatorRadius, mCalendarAttr.getMultiEventIndicatorColor(), mMultiDayIndicatorStrokeWidth,
                mMinDateCalendar, mMaxDateCalendar, sShouldShowMondayAsFirstDay, mXIndicatorOffset);

        mCalendarDrawer.drawCalendarBackground(canvas, mCalendarAttr.getCalendarBackgroundColor(), mWidth, mHeight);
        if (mCalendarAttr.getCalendarFormat() == MONTHLY) {
            mCalendarDrawer.drawMonth(canvas, mCurrentCalendar, mShouldSelect);
        } else {
            mCalendarDrawer.drawWeek(canvas, mCurrentCalendar, mShouldSelect);
        }
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        int width = MeasureSpec.getSize(parentWidth);
        int height = MeasureSpec.getSize(parentHeight);
        if (width > 0 && height > 0) {
            int numRows = mCalendarAttr.getCalendarFormat() == CalendarAttr.MONTHLY ? 7 : 2;
            mWidthPerDay = (width) / CalendarAttr.DAYS_IN_WEEK;
            mHeightPerDay = mTargetHeight > 0 ? mTargetHeight / numRows : height / numRows;
            this.mWidth = width;
            this.mHeight = height;
            this.mPaddingRight = getPaddingRight();
            this.mPaddingLeft = getPaddingLeft();

            //makes easier to find radius
            mBigCircleIndicatorRadius = getInterpolatedBigCircleIndicator();

            // scale the selected day indicators slightly so that event indicators can be drawn below
            mBigCircleIndicatorRadius = mShouldDrawIndicatorsBelowSelectedDays && mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.SMALL_INDICATOR ? mBigCircleIndicatorRadius * 0.85f : mBigCircleIndicatorRadius;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    //assume square around each day of mWidth and mHeight = mHeightPerDay and get diagonal line length
    //interpolate mHeight and radius
    //https://en.wikipedia.org/wiki/Linear_interpolation
    private float getInterpolatedBigCircleIndicator() {
        float x0 = mTextSizeRect.height();
        float x1 = mHeightPerDay; // take into account indicator offset
        float x = (x1 + mTextSizeRect.height()) / 2f; // pick a point which is almost half way through mHeightPerDay and mTextSizeRect
        double y1 = 0.5 * Math.sqrt((x1 * x1) + (x1 * x1));
        double y0 = 0.5 * Math.sqrt((x0 * x0) + (x0 * x0));

        return (float) (y0 + ((y1 - y0) * ((x - x0) / (x1 - x0))));
    }

    void setUseWeekDayAbbreviation(boolean useThreeLetterAbbreviation) {
        this.mUseThreeLetterAbbreviation = useThreeLetterAbbreviation;
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(mLocale);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        if (dayNames == null) {
            throw new IllegalStateException("Unable to determine weekday names from default mLocale");
        }
        if (dayNames.length != 8) {
            throw new IllegalStateException("Expected weekday names from default mLocale to be of size 7 but: "
                    + Arrays.toString(dayNames) + " with size " + dayNames.length + " was returned.");
        }

        if (useThreeLetterAbbreviation) {
            if (!sShouldShowMondayAsFirstDay) {
                this.dayColumnNames = new String[]{dayNames[1], dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7]};
            } else {
                this.dayColumnNames = new String[]{dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7], dayNames[1]};
            }
        } else {
            if (!sShouldShowMondayAsFirstDay) {
                this.dayColumnNames = new String[]{dayNames[1].substring(0, 1), dayNames[2].substring(0, 1),
                        dayNames[3].substring(0, 1), dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1)};
            } else {
                this.dayColumnNames = new String[]{dayNames[2].substring(0, 1), dayNames[3].substring(0, 1),
                        dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1), dayNames[1].substring(0, 1)};
            }
        }
    }

    void setDayColumnNames(String[] dayColumnNames) {
        if (dayColumnNames == null || dayColumnNames.length != 7) {
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }

    void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader) {
        this.mShouldDrawDaysHeader = shouldDrawDaysHeader;
    }

    public void setCurrentDate(Date currentDate) {
        if (currentDate == null) {
            mCurrentCalendar = Calendar.getInstance();
        } else {
            mCurrentCalendar.setTime(currentDate);
        }
        invalidate();
    }

    public Date getCurrentDate() {
        return mCurrentCalendar.getTime();
    }

    private void setFirstDay(Calendar calendar) {

        if (mCalendarAttr.getCalendarFormat() == CalendarAttr.MONTHLY) {
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
        mEventsContainer.addEvent(event);
        if (shouldInvalidate) {
            invalidate();
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    public void addEvents(List<Event> events) {
        mEventsContainer.addEvents(events);
        invalidate();
    }

    public List<Event> getCalendarEventsFor(long epochMillis) {
        return mEventsContainer.getEventsFor(epochMillis);
    }

    public List<Event> getCalendarEventsForMonth(long epochMillis) {
        return mEventsContainer.getEventsForMonth(epochMillis);
    }

    public void removeEventsFor(long epochMillis) {
        mEventsContainer.removeEventByEpochMillis(epochMillis);
    }

    public void removeEvent(Event event) {
        mEventsContainer.removeEvent(event);
    }

    public void removeEvents(List<Event> events) {
        mEventsContainer.removeEvents(events);
    }

    public void removeAllEvents() {
        mEventsContainer.removeAllEvents();
    }

    /**
     * Set inactive week days to the calendar
     *
     * @param inactiveDays of the week to be set to the calendar, example: Calendar.MONDAY
     */
    public void addInactiveDays(int... inactiveDays) {
        for (int inactiveDay : inactiveDays) {
            mInactiveDaysList.add(inactiveDay);
        }
        invalidate();
    }

    /**
     *
     *
     */
    public void clearInactiveDays() {
        mInactiveDaysList.clear();
        invalidate();
    }

    public void removeInactiveDay(int inactiveDay) {
        for (Integer day : mInactiveDaysList) {
            if (day == inactiveDay) {
                mInactiveDaysList.remove(day);
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
        return mInactiveDaysList;
    }

    public void setLocale(TimeZone timeZone, Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        if (timeZone == null) {
            throw new IllegalArgumentException("TimeZone cannot be null.");
        }
        this.mLocale = locale;
        this.mTimeZone = timeZone;
        this.mEventsContainer = new EventsContainer(Calendar.getInstance(this.mTimeZone, this.mLocale), mCalendarAttr.getCalendarFormat());
        // passing null will not re-init density related values - and that's ok
        init(null);
    }

    public void setCurrentSelectedDayIndicatorStyle(final int currentSelectedDayIndicatorStyle) {
        mCalendarAttr.setCurrentSelectedDayIndicatorStyle(currentSelectedDayIndicatorStyle);
        invalidate();
    }

    public void setCurrentDayIndicatorStyle(final int currentDayIndicatorStyle) {
        mCalendarAttr.setCurrentDayIndicatorStyle(currentDayIndicatorStyle);
        invalidate();
    }

    public void setEventIndicatorStyle(final int eventIndicatorStyle) {
        mCalendarAttr.setEventIndicatorStyle(eventIndicatorStyle);
        invalidate();
    }

    private void checkTargetHeight() {
        if (mTargetHeight <= 0) {
            throw new IllegalStateException("Target mHeight must be set in xml properties in order to expand/collapse CompactCalendar.");
        }
    }

    public void setTargetHeight(int targetHeight) {
        this.mTargetHeight = targetHeight;
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
            mMaxDateCalendar = null;
        } else {
            mMaxDateCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMaxDateCalendar.setTime(maxDate);
        }
        invalidate();
    }

    public void setMinDateCalendar(Date minDate) {
        if (minDate == null) {
            mMinDateCalendar = null;
        } else {
            mMinDateCalendar = CalendarUtils.initCalendar(sShouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMinDateCalendar.setTime(minDate);
        }
        invalidate();
    }

    public void setShouldShowMondayAsFirstDay(boolean shouldShowMondayAsFirstDay) {
        this.sShouldShowMondayAsFirstDay = shouldShowMondayAsFirstDay;
        setUseWeekDayAbbreviation(mUseThreeLetterAbbreviation);
        if (shouldShowMondayAsFirstDay) {
            mEventsCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mTodayCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mCurrentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        } else {
            mEventsCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            mTodayCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            mCurrentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        }
    }

    public void setOnDayClickListener(EventsCalendarPageViewListener listener) {
        this.mListener = listener;
    }

    public boolean isShouldSelect() {
        return mShouldSelect;
    }

    public void selectCurrentDate(boolean shouldSelect) {
        mShouldSelect = shouldSelect;
        if(shouldSelect) {
            if (mCalendarAttr.getCalendarFormat() == CalendarAttr.MONTHLY) {
                mCurrentCalendar.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                mCurrentCalendar.set(Calendar.DAY_OF_WEEK, mCurrentCalendar.getFirstDayOfWeek());
            }
        }
        invalidate();
    }
}
