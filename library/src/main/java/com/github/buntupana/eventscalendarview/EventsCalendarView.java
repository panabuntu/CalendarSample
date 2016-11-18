package com.github.buntupana.eventscalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class EventsCalendarView extends RelativeLayout {

    private final String TAG = EventsCalendarView.class.getSimpleName();

    public static final int MONTHLY = 0;
    public static final int WEEKLY = 1;

    private ViewPager mPager = null;
    private DynamicViewPagerAdapter mPagerAdapter = null;
    private AttributeSet attrs;

    private Calendar mCurrentCalendar = Calendar.getInstance();
    private final Calendar mPreviousCalendar = Calendar.getInstance();
    private final Calendar mNextCalendar = Calendar.getInstance();

    private int mCalendarUnit = Calendar.MONTH;

    private int mCalendarFormat = MONTHLY;
    private boolean mDefaultSelectedPresentDay = true;

    private Calendar mMinCalendar;
    private Calendar mMaxCalendar;

    private List<Integer> inactiveDays = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");

    public EventsCalendarView(Context context) {
        super(context);
        init();
    }

    public EventsCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttrs(attrs);
        init();
    }

    private void setAttrs(AttributeSet attrs) {
        this.attrs = attrs;
        if (attrs != null && getContext() != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.EventsCalendarView, 0, 0);
            try {
                mCalendarFormat = typedArray.getInt(R.styleable.EventsCalendarView_eventsCalendarFormat, MONTHLY);
                mDefaultSelectedPresentDay = typedArray.getBoolean(R.styleable.EventsCalendarView_eventsCalendarDefaultSelectedPresentDay, true);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init() {

        mCalendarUnit = mCalendarFormat == MONTHLY ? Calendar.MONTH : Calendar.WEEK_OF_YEAR;

        mPagerAdapter = new DynamicViewPagerAdapter();
        mPager = new ViewPager(getContext());
        mPager.setAdapter(mPagerAdapter);

        mPreviousCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mCurrentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mNextCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        mPreviousCalendar.setMinimalDaysInFirstWeek(1);
        mCurrentCalendar.setMinimalDaysInFirstWeek(1);
        mNextCalendar.setMinimalDaysInFirstWeek(1);

        setCurrentDate(new Date());

        mPagerAdapter.addView(getView(mPreviousCalendar), 0);
        mPagerAdapter.addView(getView(mCurrentCalendar), 1);
        mPagerAdapter.addView(getView(mNextCalendar), 2);

        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(1, false);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPager.getCurrentItem() == position && positionOffset == 0 && positionOffsetPixels == 0) {
                    if (position == 0 && !isNextLimitReached()) {

                        mPreviousCalendar.add(mCalendarUnit, -1);
                        mPagerAdapter.addView(getView(mPreviousCalendar), 0);

                    } else if (position == mPager.getCurrentItem() && !isPreviousLimitReached()) {

                        mNextCalendar.add(mCalendarUnit, 1);
                        mPagerAdapter.addView((getView(mNextCalendar)));
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        addView(mPager);
    }

    private View getView(Calendar calendarToDraw) {

        return new EventsCalendarPageView(getContext(), attrs, calendarToDraw, mMinCalendar, mMaxCalendar, inactiveDays, TimeZone.getDefault(), Locale.getDefault(), null);
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        FrameLayout v = (FrameLayout) inflater.inflate(R.layout.one_of_my_page_layouts, null);
//        ((TextView) v.findViewById(R.id.textView)).setText(sdf.format(Calendar.getTime()));
//        return v;
    }

    private void setDates() {

        mPreviousCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());
        mNextCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());

        mPreviousCalendar.add(mCalendarUnit, -1);
        mNextCalendar.add(mCalendarUnit, 1);

        setFirstDay(mPreviousCalendar);
        setFirstDay(mNextCalendar);
        if (mDefaultSelectedPresentDay) {
            resetHour(mCurrentCalendar);
        } else {
            setFirstDay(mCurrentCalendar);
        }
    }

    private void setFirstDay(Calendar calendar) {

        if (mCalendarFormat == MONTHLY) {
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

    public void setCurrentDate(Date date){
        if (date == null) {
            mCurrentCalendar = null;
        } else {
            mCurrentCalendar.setTime(date);
            resetHour(mCurrentCalendar);
            mPreviousCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());
            mPreviousCalendar.add(mCalendarUnit, -1);
            mNextCalendar.setTimeInMillis(mCurrentCalendar.getTimeInMillis());
            mNextCalendar.add(mCalendarUnit, 1);

            setFirstDay(mPreviousCalendar);
            setFirstDay(mNextCalendar);
            if (mDefaultSelectedPresentDay) {
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
            mMinCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mMinCalendar.setTime(date);
            resetHour(mMinCalendar);
        }
    }

    public void setMaxDate(Date date) {
        if (date == null) {
            mMaxCalendar = null;
        } else {
            mMaxCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            mMaxCalendar.setTime(date);
            resetHour(mMaxCalendar);
        }
    }

    private boolean isPreviousLimitReached() {

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

    private boolean isNextLimitReached() {

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
}
