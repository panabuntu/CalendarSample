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

    // Calendars
    private Calendar mCurrentCalendar = Calendar.getInstance();
    private final Calendar mPreviousCalendar = Calendar.getInstance();
    private final Calendar mNextCalendar = Calendar.getInstance();
    private Calendar mMinCalendar;
    private Calendar mMaxCalendar;
    private EventsContainer mEventsContainer;
    private Locale mLocale = Locale.getDefault();
    private TimeZone mTimeZone = TimeZone.getDefault();

    private int mCalendarUnit = Calendar.MONTH;
    private boolean shouldShowMondayAsFirstDay = true;

    private EventsCalendarViewListener mListener;

    private List<Integer> inactiveDays = new ArrayList<>();

    private boolean mFirstInit = true;

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

        if (mEventsContainer == null) {
            mEventsContainer = new EventsContainer(Calendar.getInstance(), mCalendarAttr.getCalendarFormat());
        }
        mCalendarUnit = mCalendarAttr.getCalendarFormat() == CalendarAttr.MONTHLY ? Calendar.MONTH : Calendar.WEEK_OF_YEAR;

        mPagerAdapter = new DynamicViewPagerAdapter();
        mPager = new CustomViewPager(getContext());
        mPager.setAdapter(mPagerAdapter);

        setCalendars();

        if (mMinCalendar == null || mMinCalendar.get(mCalendarUnit) < mCurrentCalendar.get(mCalendarUnit)) {
            mPagerAdapter.addView(getView(mPreviousCalendar.getTime(), false), mPagerAdapter.getCount());
        }

        mPagerAdapter.addView(getView(mCurrentCalendar.getTime(), true), mPagerAdapter.getCount());
        mPager.setCurrentItem(mPagerAdapter.getCount() - 1, false);

        if (mMaxCalendar == null || mMaxCalendar.get(mCalendarUnit) > mCurrentCalendar.get(mCalendarUnit)) {
            mPagerAdapter.addView(getView(mNextCalendar.getTime(), false), mPagerAdapter.getCount());
        }

        mPagerAdapter.notifyDataSetChanged();

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPager.getCurrentItem() == position && positionOffset == 0 && positionOffsetPixels == 0 && !mFirstInit) {
                    if (position == 0 && !isMinDateLimitReached()) {

                        mPreviousCalendar.add(mCalendarUnit, -1);
                        Calendar aux = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, TimeZone.getDefault(), Locale.getDefault());
                        aux.setTimeInMillis(mPreviousCalendar.getTimeInMillis());
                        mPagerAdapter.addView(getView(mPreviousCalendar.getTime(), false), 0);

                    } else if (position == (mPagerAdapter.getCount() - 1) && !isMaxDateLimitReached()) {

                        mNextCalendar.add(mCalendarUnit, 1);
                        Calendar aux = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, TimeZone.getDefault(), Locale.getDefault());
                        aux.setTimeInMillis(mNextCalendar.getTimeInMillis());
                        mPagerAdapter.addView((getView(mNextCalendar.getTime(), false)));
                    }

                    if (mPager.getCurrentItem() == position) {
                        mCurrentCalendar.setTime(((EventsCalendarPageView) mPagerAdapter.getView(position)).getCurrentDate());
                        ((EventsCalendarPageView) mPagerAdapter.getView(position)).selectCurrentDate(true);
                        if (position != 0) {
                            ((EventsCalendarPageView) mPagerAdapter.getView(position - 1)).selectCurrentDate(false);
                        }
                        if (mPagerAdapter.getCount() > position + 1) {
                            ((EventsCalendarPageView) mPagerAdapter.getView(position + 1)).selectCurrentDate(false);
                        }
                        if (mListener != null) {
                            mListener.onPageScroll(mCurrentCalendar.getTime(), mCurrentCalendar.get(Calendar.DAY_OF_MONTH), CalendarUtils.getMonth(mCurrentCalendar, mCalendarAttr.getCalendarFormat(), mLocale),
                                    CalendarUtils.getYear(mCurrentCalendar, mCalendarAttr.getCalendarFormat(), mLocale));
                        }
                    }
                }
                mFirstInit = false;
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
        mFirstInit = true;
        init();
    }

    private View getView(Date calendarToDraw, boolean shouldSelect) {
        return new EventsCalendarPageView(getContext(), calendarToDraw, mMinCalendar == null ? null : mMinCalendar.getTime(),
                mMaxCalendar == null ? null : mMaxCalendar.getTime(), mEventsContainer, inactiveDays, mTimeZone, mLocale, mCalendarAttr, shouldSelect, mListener);
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

    public Date getCurrentDate(){
        return mCurrentCalendar.getTime();
    }

    public void setSelectedDate(Date date){
        setCalendars(date);
        refresh();
    }

    private void setCalendars(){
        setCalendars(new Date());
    }

    private void setCalendars(Date date) {

        mCurrentCalendar = CalendarUtils.getCalendar(date, shouldShowMondayAsFirstDay, mTimeZone, mLocale);

        if (date != null) {
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
        } else if (mMaxCalendar != null && date.getTime() > mMaxCalendar.getTimeInMillis()) {
            throw new IllegalArgumentException("Min time can't be greatest than Max time");
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
        } else if (mMinCalendar != null && date.getTime() < mMinCalendar.getTimeInMillis()) {
            throw new IllegalArgumentException("Max time can't be less than Min time");
        } else {
            mMaxCalendar = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMaxCalendar.setTime(date);
            resetHour(mMaxCalendar);
        }
        refresh();
    }

    public void setLimitInterval(Date minDate, Date maxDate) {

        if (minDate != null && maxDate != null && minDate.getTime() > maxDate.getTime()) {
            throw new IllegalArgumentException("Max time can't be less than Min time");
        }

        if (minDate == null) {
            mMinCalendar = null;
        } else {
            mMinCalendar = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMinCalendar.setTime(minDate);
            resetHour(mMinCalendar);
        }

        if(maxDate == null) {
            mMaxCalendar = null;
        } else {
            mMaxCalendar = CalendarUtils.initCalendar(shouldShowMondayAsFirstDay, mTimeZone, mLocale);
            mMaxCalendar.setTime(maxDate);
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
            boolean isLessOrEqualUnit = mPreviousCalendar.get(mCalendarUnit) < mMinCalendar.get(mCalendarUnit);
            result = isLessOrEqualYear && isLessOrEqualUnit;
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

    public void setListener(EventsCalendarViewListener listener) {
        mListener = listener;
        refresh();
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

    public String getDateString(){
        return CalendarUtils.getDate(mCurrentCalendar, mCalendarAttr.getCalendarFormat(), mLocale);
    }
}
