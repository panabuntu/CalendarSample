package com.github.buntupana.eventscalendarview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.events.EventsContainer;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarViewListener;
import com.github.buntupana.eventscalendarview.utils.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.github.buntupana.eventscalendarview.CalendarAttr.MONTHLY;


public class EventsCalendarView extends RelativeLayout {

    private final String TAG = EventsCalendarView.class.getSimpleName();

    private CalendarAttr mCalendarAttr;

    private CustomViewPager mPager = null;
    private DynamicViewPagerAdapter mPagerAdapter = null;

    private Calendar mCurrentCalendar = Calendar.getInstance();
    private final Calendar mPreviousCalendar = Calendar.getInstance();
    private final Calendar mNextCalendar = Calendar.getInstance();

    private EventsContainer mEventsContainer;

    private int mCalendarUnit = Calendar.MONTH;

//    private int mCalendarFormat = MONTHLY;
//    private boolean mDefaultSelectedPresentDay = true;
    private boolean shouldShowMondayAsFirstDay = true;

    private EventsCalendarViewListener mListener;

    private Calendar mMinCalendar;
    private Calendar mMaxCalendar;

    private List<Integer> inactiveDays = new ArrayList<>();

    private Locale mLocale = Locale.getDefault();
    private TimeZone mTimeZone = TimeZone.getDefault();

//    private int mCalendarBackgroundColor;

    //---------------

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");

    //---------------

    public EventsCalendarView(Context context) {
        super(context);
        mCalendarAttr = new CalendarAttr(context, null);
        init();
    }

    public EventsCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCalendarAttr = new CalendarAttr(context, attrs);
        init();
    }

    private void init() {

        if(mEventsContainer == null) {
            mEventsContainer = new EventsContainer(Calendar.getInstance(), mCalendarAttr.getCalendarFormat());
        }
        mCalendarUnit = mCalendarAttr.getCalendarFormat() == CalendarAttr.MONTHLY ? Calendar.MONTH : Calendar.WEEK_OF_YEAR;

        mPagerAdapter = new DynamicViewPagerAdapter();
        mPager = new CustomViewPager(getContext());
        mPager.setAdapter(mPagerAdapter);

        setCurrentDate(new Date());

        if (mMinCalendar == null || mMinCalendar.get(mCalendarUnit) < mCurrentCalendar.get(mCalendarUnit)) {
            mPagerAdapter.addView(getView(mPreviousCalendar.getTime()), mPagerAdapter.getCount());
        }

        mPagerAdapter.addView(getView(mCurrentCalendar.getTime()), mPagerAdapter.getCount());
        mPager.setCurrentItem(mPagerAdapter.getCount()-1, false);

        if (mMaxCalendar == null || mMaxCalendar.get(mCalendarUnit) > mCurrentCalendar.get(mCalendarUnit)) {
            mPagerAdapter.addView(getView(mNextCalendar.getTime()), mPagerAdapter.getCount());
        }

        mPagerAdapter.notifyDataSetChanged();

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPager.getCurrentItem() == position && positionOffset == 0 && positionOffsetPixels == 0) {
                    if (position == 0 && !isMinDateLimitReached()) {

                        mPreviousCalendar.add(mCalendarUnit, -1);
                        Calendar aux = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, TimeZone.getDefault(), Locale.getDefault());
                        aux.setTimeInMillis(mPreviousCalendar.getTimeInMillis());
                        mPagerAdapter.addView(getView(mPreviousCalendar.getTime()), 0);

                    } else if (position == (mPagerAdapter.getCount() - 1) && !isMaxDateLimitReached()) {

                        mNextCalendar.add(mCalendarUnit, 1);
                        Calendar aux = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, TimeZone.getDefault(), Locale.getDefault());
                        aux.setTimeInMillis(mNextCalendar.getTimeInMillis());
                        mPagerAdapter.addView((getView(mNextCalendar.getTime())));
                    }

                    if (position == mPager.getCurrentItem()) {
                        mCurrentCalendar.setTime(((EventsCalendarPageView) mPagerAdapter.getView(position)).getCurrentDate());
                        if (mListener != null) {
                            mListener.onMonthScroll(mCurrentCalendar.getTime());
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        addView(mPager);
    }

    public void refresh() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            mPagerAdapter.removeView(mPager, i);
        }
        mPagerAdapter = null;
        mPager = null;
        removeViewAt(0);
        init();
    }

    private View getView(Date calendarToDraw) {
        return new EventsCalendarPageView(getContext(), calendarToDraw, mMinCalendar == null ? null : mMinCalendar.getTime(),
                mMaxCalendar == null ? null : mMaxCalendar.getTime(), mEventsContainer, inactiveDays, mTimeZone, mLocale, mCalendarAttr, mListener);
    }


    private void setFirstDay(Calendar calendar) {

        if (mCalendarAttr.getCalendarFormat() == MONTHLY) {
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

    public void setCurrentDate(Date date) {
        if (date == null) {
            mCurrentCalendar = Calendar.getInstance();
        }
//        else if (mMinCalendar != null && mMinCalendar.getTimeInMillis() < date.getTime()) {
//            throw new IllegalArgumentException("This date is less than minimal limit");
//        } else if (mMaxCalendar != null && date.getTime() > mMaxCalendar.getTimeInMillis()) {
//            throw new IllegalArgumentException("This date is greatest than maximum limit");
//        }
        else {

            mCurrentCalendar.setTime(date);
            resetHour(mCurrentCalendar);
            mPreviousCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());
            mPreviousCalendar.add(mCalendarUnit, -1);
            mNextCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());
            mNextCalendar.add(mCalendarUnit, 1);

            setFirstDay(mPreviousCalendar);
            setFirstDay(mNextCalendar);
            if (mCalendarAttr.isDefaultSelectedPresentDay()) {
                resetHour(mCurrentCalendar);
            } else {
                setFirstDay(mCurrentCalendar);
            }
        }
    }

    public void setMinDate(Date date) {
        if (date == null) {
            mMinCalendar = null;
        } else {
            mMinCalendar = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMinCalendar.setTime(date);
            resetHour(mMinCalendar);
        }
        refresh();
    }

    public void setMaxDate(Date date) {
        if (date == null) {
            mMaxCalendar = null;
        } else {
            mMaxCalendar = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMaxCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mMaxCalendar.setTime(date);
            resetHour(mMaxCalendar);
        }
        refresh();
    }

    private boolean isMinDateLimitReached() {

        boolean result;

        if (mMinCalendar == null) {
            result = false;
        } else {
            boolean isLessOrEqualYear = mPreviousCalendar.get(Calendar.YEAR) <= mMinCalendar.get(Calendar.YEAR);
            boolean isLessOrEqualUnit = mPreviousCalendar.get(mCalendarUnit) <= mMinCalendar.get(mCalendarUnit);
            result = isLessOrEqualYear && isLessOrEqualUnit;
        }
        return result;
    }

    private boolean isMaxDateLimitReached() {

        boolean result;

        if (mMaxCalendar == null) {
            result = false;
        } else {
            boolean isGreaterOrEqualYear = mNextCalendar.get(Calendar.YEAR) >= mMaxCalendar.get(Calendar.YEAR);
            boolean isGreaterOrEqualUnit = mNextCalendar.get(mCalendarUnit) >= mMaxCalendar.get(mCalendarUnit);
            result = isGreaterOrEqualYear && isGreaterOrEqualUnit;
        }
        return result;
    }

    public void setShouldShowMondayAsFirstDay(boolean shouldShowMondayAsFirstDay) {
        this.shouldShowMondayAsFirstDay = shouldShowMondayAsFirstDay;
        if (shouldShowMondayAsFirstDay) {
            mPreviousCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mCurrentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mNextCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        } else {
            mPreviousCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            mCurrentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            mNextCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        }
        refresh();
    }

    public void setListener(EventsCalendarViewListener listener) {
        mListener = listener;
        invalidate();
    }

    public void setLocale(TimeZone timeZone, Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        if (timeZone == null) {
            throw new IllegalArgumentException("TimeZone cannot be null.");
        }
        mLocale = locale;
        mTimeZone = timeZone;
//        this.mEventsContainer = new EventsContainer(Calendar.getInstance(this.mTimeZone, this.mLocale), mCalendarFormat);
        // passing null will not re-init density related values - and that's ok
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
            refresh();
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    public void addEvents(List<Event> events) {
        mEventsContainer.addEvents(events);
        refresh();
    }
}
