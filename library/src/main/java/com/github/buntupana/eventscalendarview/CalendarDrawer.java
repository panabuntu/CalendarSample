package com.github.buntupana.eventscalendarview;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.events.Events;
import com.github.buntupana.eventscalendarview.events.EventsContainer;
import com.github.buntupana.eventscalendarview.utils.CalendarUtils;

import java.util.Calendar;
import java.util.List;

import static com.github.buntupana.eventscalendarview.CalendarAttr.NO_FILL_LARGE_INDICATOR;

public class CalendarDrawer {

    private final String TAG = CalendarDrawer.class.getSimpleName();

    // Sizes
    private int mHeightPerDay;
    private int mPaddingHeight;
    private int mWidthPerDay;
    private int mPaddingWidth;
    private int mPaddingRight;
    private int mPaddingLeft;
    private float mScreenDensity = 1;

    //Style
    private Paint mDayPaint;

    // Events
    private float mBigCircleIndicatorRadius;
    private EventsContainer mEventsContainer;

    private boolean shouldDrmShouldDrawDaysHeaderwDaysHeader;
    private Calendar mTodayCalendar;
    private List<Integer> mInactiveDays;
    private String[] mDayColumnNames;
    private Calendar mEventsCalendar;
    private boolean mShouldDrawIndicatorsBelowSelectedDays;
    private int mTextHeight;
    private float mSmallIndicatorRadius;
    private float mMultiDayIndicatorStrokeWidth;
    private Calendar mMinDateCalendar;
    private Calendar mMaxDateCalendar;
    private boolean mShouldShowMondayAsFirstDay;
    private float mXIndicatorOffset;

    private CalendarAttr mCalendarAttr;

    public CalendarDrawer(int heightPerDay, int paddingHeight, int widthPerDay, int paddingWidth, int paddingRight,
                          int paddingLeft, float screenDensity, Paint dayPaint,

                          float bigCircleIndicatorRadius, EventsContainer eventsContainer, boolean shouldDrawDaysHeader,
                          Calendar todayCalendar, List<Integer> inactiveDays, String[] dayColumnNames, Calendar eventsCalendar,
                          boolean shouldDrawIndicatorsBelowSelectedDays, int textHeight, float smallIndicatorRadius,
                          float multiDayIndicatorStrokeWidth, Calendar minDateCalendar, Calendar maxDateCalendar,
                          boolean shouldShowMondayAsFirstDay, float xIndicatorOffset, CalendarAttr calendarAttr) {

        mHeightPerDay = heightPerDay;
        mPaddingHeight = paddingHeight;
        mWidthPerDay = widthPerDay;
        mPaddingWidth = paddingWidth;
        mPaddingRight = paddingRight;
        mPaddingLeft = paddingLeft;
        mScreenDensity = screenDensity;
        mDayPaint = dayPaint;
        mBigCircleIndicatorRadius = bigCircleIndicatorRadius;
        mEventsContainer = eventsContainer;
        shouldDrmShouldDrawDaysHeaderwDaysHeader = shouldDrawDaysHeader;
        mTodayCalendar = todayCalendar;
        mInactiveDays = inactiveDays;
        mDayColumnNames = dayColumnNames;
        mEventsCalendar = eventsCalendar;
        mShouldDrawIndicatorsBelowSelectedDays = shouldDrawIndicatorsBelowSelectedDays;
        mTextHeight = textHeight;
        mSmallIndicatorRadius = smallIndicatorRadius;
        mMultiDayIndicatorStrokeWidth = multiDayIndicatorStrokeWidth;
        mMinDateCalendar = minDateCalendar;
        mMaxDateCalendar = maxDateCalendar;
        mShouldShowMondayAsFirstDay = shouldShowMondayAsFirstDay;
        mXIndicatorOffset = xIndicatorOffset;
        mCalendarAttr = calendarAttr;

    }

    void drawMonth(Canvas canvas, Calendar monthToDrawCalendar, boolean shouldSelect) {
        drawEventsMonthly(canvas, monthToDrawCalendar);

        Calendar aux = Calendar.getInstance();
        aux.setTimeInMillis(monthToDrawCalendar.getTimeInMillis());
        aux.setFirstDayOfWeek(monthToDrawCalendar.getFirstDayOfWeek());
        aux.set(Calendar.DAY_OF_MONTH, 1);

        int textColor;

        //offset by one because of 0 index based calculations
        boolean isSameMonthAsToday = monthToDrawCalendar.get(Calendar.MONTH) == mTodayCalendar.get(Calendar.MONTH);
        boolean isSameYearAsToday = monthToDrawCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR);
        boolean isSameMonthAsCurrentCalendar = monthToDrawCalendar.get(Calendar.MONTH) == monthToDrawCalendar.get(Calendar.MONTH);
        int todayDayOfMonth = mTodayCalendar.get(Calendar.DAY_OF_MONTH);

        boolean selectedDay = false;
        int row = 0;
        int day_month = aux.get(Calendar.DAY_OF_MONTH);
        int day_week;
        do {

            float yPosition = row * mHeightPerDay + mPaddingHeight;

            for (int column = 0; column < mDayColumnNames.length; column++) {
                float xPosition = mWidthPerDay * column + (mPaddingWidth + mPaddingLeft + mPaddingRight);

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrmShouldDrawDaysHeaderwDaysHeader) {
                        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
                        mDayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        mDayPaint.setStyle(Paint.Style.FILL);
                        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
                        canvas.drawText(mDayColumnNames[column], xPosition, mPaddingHeight, mDayPaint);
                        mDayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {

                    day_month = aux.get(Calendar.DAY_OF_MONTH);
                    day_week = aux.get(Calendar.DAY_OF_WEEK);

                    if (aux.getFirstDayOfWeek() == Calendar.MONDAY) {
                        if (day_week == Calendar.SUNDAY && column != 6) {
                            continue;
                        } else if (day_week != Calendar.SUNDAY && (day_week - 1) != (column + 1)) {
                            continue;
                        }
                    } else if (day_week != (column + 1)) {
                        continue;
                    }

                    if (mCalendarAttr.isCurrentDayIndicator() && isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day_month) {
                        drawDayCircleIndicator(mCalendarAttr.getCurrentDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentDayBackgroundColor());
                        textColor = mCalendarAttr.getCurrentDayTextColor();
                    } else if (!selectedDay && mInactiveDays.size() != 7 && shouldSelect) {
                        if (monthToDrawCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !CalendarUtils.isInactiveDate(monthToDrawCalendar, mMinDateCalendar, mMaxDateCalendar, mInactiveDays) && isSameMonthAsCurrentCalendar) {
                            drawDayCircleIndicator(mCalendarAttr.getCurrentSelectedDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentSelectedDayBackgroundColor());
                            selectedDay = true;
                            textColor = mCalendarAttr.getCurrentSelectedDayTextColor();
                        } else if (CalendarUtils.isInactiveDate(monthToDrawCalendar, mMinDateCalendar, mMaxDateCalendar, mInactiveDays) && !CalendarUtils.isInactiveDate(aux, mMinDateCalendar, mMaxDateCalendar, mInactiveDays)) {
                            drawDayCircleIndicator(mCalendarAttr.getCurrentSelectedDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentSelectedDayBackgroundColor());
                            selectedDay = true;
                            textColor = mCalendarAttr.getCurrentSelectedDayTextColor();
                        } else {
                            textColor = mCalendarAttr.getCalendarTextColor();
                        }
                    } else {
                        textColor = mCalendarAttr.getCalendarTextColor();
                    }

                    mDayPaint.setStyle(Paint.Style.FILL);
                    mDayPaint.setColor(textColor);
                    if (CalendarUtils.isInactiveDate(aux, mMinDateCalendar, mMaxDateCalendar, mInactiveDays)) {
                        mDayPaint.setAlpha(127);
                    }
                    canvas.drawText(String.valueOf(day_month), xPosition, yPosition, mDayPaint);

                    if (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        aux.add(Calendar.DATE, 1);
                    }
                }
            }
            row++;
        } while (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH));
    }


    public void drawWeek(Canvas canvas, Calendar weekToDrawCalendar, boolean shouldSelect) {
        drawEventsWeekly(canvas, weekToDrawCalendar);

        Calendar aux = Calendar.getInstance();
        aux.setFirstDayOfWeek(weekToDrawCalendar.getFirstDayOfWeek());
        aux.setTimeInMillis(weekToDrawCalendar.getTimeInMillis());

        int textColor;
        //offset by one because we want to start from Monday

        //offset by one because of 0 index based calculations
        boolean isSameWeekAsToday = weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR) == mTodayCalendar.get(Calendar.WEEK_OF_YEAR);
        boolean isSameYearAsToday = weekToDrawCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR);
        boolean isSameWeekAsCurrentCalendar = weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR) == weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR);
        int todayDayOfMonth = mTodayCalendar.get(Calendar.DAY_OF_MONTH);

        boolean selectedDay = false;
        for (int column = 0; column < mDayColumnNames.length; column++) {

            float xPosition = mWidthPerDay * column + (mPaddingWidth + mPaddingLeft + mPaddingRight);

            for (int row = 0; row < 2; row++) {

                float yPosition = row * (mHeightPerDay + mPaddingHeight);

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrmShouldDrawDaysHeaderwDaysHeader) {
                        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
                        mDayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        mDayPaint.setStyle(Paint.Style.FILL);
                        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
                        canvas.drawText(mDayColumnNames[column], xPosition, mPaddingHeight, mDayPaint);
                        mDayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {

                    if (column == 0) {
                        aux.set(Calendar.DAY_OF_WEEK, aux.getFirstDayOfWeek());
                    } else {
                        aux.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    int day_month = aux.get(Calendar.DAY_OF_MONTH);

                    if (mCalendarAttr.isCurrentDayIndicator() && isSameYearAsToday && isSameWeekAsToday && todayDayOfMonth == day_month) {
                        drawDayCircleIndicator(mCalendarAttr.getCurrentDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentDayBackgroundColor());
                        textColor = mCalendarAttr.getCurrentDayTextColor();
                    } else if (!selectedDay && mInactiveDays.size() != 7 && shouldSelect) {
                        if (weekToDrawCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !CalendarUtils.isInactiveDate(weekToDrawCalendar, mMinDateCalendar, mMaxDateCalendar, mInactiveDays) && isSameWeekAsCurrentCalendar) {
                            drawDayCircleIndicator(mCalendarAttr.getCurrentSelectedDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentSelectedDayBackgroundColor());
                            selectedDay = true;
                            textColor = mCalendarAttr.getCurrentSelectedDayTextColor();
                        } else if (CalendarUtils.isInactiveDate(weekToDrawCalendar, mMinDateCalendar, mMaxDateCalendar, mInactiveDays) && !CalendarUtils.isInactiveDate(aux, mMinDateCalendar, mMaxDateCalendar, mInactiveDays)) {
                            drawDayCircleIndicator(mCalendarAttr.getCurrentSelectedDayIndicatorStyle(), canvas, xPosition, yPosition, mCalendarAttr.getCurrentSelectedDayBackgroundColor());
                            selectedDay = true;
                            textColor = mCalendarAttr.getCurrentSelectedDayTextColor();
                        } else {
                            textColor = mCalendarAttr.getCalendarTextColor();
                        }
                    } else {
                        textColor = mCalendarAttr.getCalendarTextColor();
                    }

                    mDayPaint.setStyle(Paint.Style.FILL);
                    mDayPaint.setColor(textColor);
                    if (CalendarUtils.isInactiveDate(aux, mMinDateCalendar, mMaxDateCalendar, mInactiveDays)) {
                        mDayPaint.setAlpha(127);
                    }
                    canvas.drawText(String.valueOf(day_month), xPosition, yPosition, mDayPaint);
                }
            }
        }
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color) {
        drawDayCircleIndicator(indicatorStyle, canvas, x, y, color, 1);
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color, float circleScale) {
        float strokeWidth = mDayPaint.getStrokeWidth();
        if (indicatorStyle == CalendarAttr.NO_FILL_LARGE_INDICATOR) {
            mDayPaint.setStrokeWidth(2 * mScreenDensity);
            mDayPaint.setStyle(Paint.Style.STROKE);
        } else {
            mDayPaint.setStyle(Paint.Style.FILL);
        }
        drawCircle(canvas, x, y, color, circleScale);
        mDayPaint.setStrokeWidth(strokeWidth);
        mDayPaint.setStyle(Paint.Style.FILL);
    }

    // Draw Circle on certain days to highlight them
    private void drawCircle(Canvas canvas, float x, float y, int color, float circleScale) {
        mDayPaint.setColor(color);
        drawCircle(canvas, circleScale * mBigCircleIndicatorRadius, x, y - (mTextHeight / 6));
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        canvas.drawCircle(x, y, radius, mDayPaint);
    }

    private void drawEventsMonthly(Canvas canvas, Calendar currentMonthToDrawCalendar) {
        int currentMonth = currentMonthToDrawCalendar.get(Calendar.MONTH);
        List<Events> uniqEvents = mEventsContainer.getEventsForMonthAndYear(currentMonth, currentMonthToDrawCalendar.get(Calendar.YEAR));

        boolean shouldDrawCurrentDayCircle = currentMonth == mTodayCalendar.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentMonth == currentMonthToDrawCalendar.get(Calendar.MONTH);

        int todayDayOfMonth = mTodayCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDayOfMonth = currentMonthToDrawCalendar.get(Calendar.DAY_OF_MONTH);
        float indicatorOffset = mBigCircleIndicatorRadius / 2;
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                mEventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = CalendarUtils.getDayOfWeek(mEventsCalendar, mShouldShowMondayAsFirstDay) - 1;

                int weekNumberForMonth = mEventsCalendar.get(Calendar.WEEK_OF_MONTH);
                float xPosition = mWidthPerDay * dayOfWeek + mPaddingWidth + mPaddingLeft + mPaddingRight;
                float yPosition = weekNumberForMonth * mHeightPerDay + mPaddingHeight;

                List<Event> eventsList = events.getEvents();
                int dayOfMonth = mEventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);

                if (!mCalendarAttr.isCurrentDayIndicator() || mShouldDrawIndicatorsBelowSelectedDays || (!mShouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay)) {
                    if (mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.FILL_LARGE_INDICATOR || mCalendarAttr.getEventIndicatorStyle() == NO_FILL_LARGE_INDICATOR) {
                        Event event = eventsList.get(0);
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                    } else {
                        yPosition += indicatorOffset;
                        // offset event indicators to draw below selected day indicators
                        // this makes sure that they do no overlap
                        if (mShouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += indicatorOffset;
                        }

                        if (eventsList.size() >= 3) {
                            drawEventsWithPlus(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 2) {
                            drawTwoEvents(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 1) {
                            drawSingleEvent(canvas, xPosition, yPosition, eventsList);
                        }
                    }
                }
            }
        }
    }

    private void drawEventsWeekly(Canvas canvas, Calendar currentWeekToDrawCalendar) {
        int currentWeek = currentWeekToDrawCalendar.get(Calendar.WEEK_OF_YEAR);
        List<Events> uniqEvents = mEventsContainer.getEventsForWeekAndYear(currentWeek, currentWeekToDrawCalendar.get(Calendar.YEAR));

        boolean shouldDrawCurrentDayCircle = currentWeek == mTodayCalendar.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentWeek == currentWeekToDrawCalendar.get(Calendar.MONTH);

        int todayDayOfMonth = mTodayCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDayOfMonth = currentWeekToDrawCalendar.get(Calendar.DAY_OF_MONTH);
        float indicatorOffset = mBigCircleIndicatorRadius / 2;
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                mEventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = CalendarUtils.getDayOfWeek(mEventsCalendar, mShouldShowMondayAsFirstDay) - 1;

                float xPosition = mWidthPerDay * dayOfWeek + mPaddingWidth + mPaddingLeft + mPaddingRight;
                float yPosition = mHeightPerDay + mPaddingHeight;

                List<Event> eventsList = events.getEvents();
                int dayOfMonth = mEventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);

                if (mShouldDrawIndicatorsBelowSelectedDays || (!mShouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay)) {
                    if (mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.FILL_LARGE_INDICATOR || mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.NO_FILL_LARGE_INDICATOR) {
                        Event event = eventsList.get(0);
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                    } else {
                        yPosition += indicatorOffset;
                        // offset event indicators to draw below selected day indicators
                        // this makes sure that they do no overlap
                        if (mShouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += indicatorOffset;
                        }

                        if (eventsList.size() >= 3) {
                            drawEventsWithPlus(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 2) {
                            drawTwoEvents(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 1) {
                            drawSingleEvent(canvas, xPosition, yPosition, eventsList);
                        }
                    }
                }
            }
        }
    }

    private void drawEventIndicatorCircle(Canvas canvas, float x, float y, int color) {
        mDayPaint.setColor(color);
        if (mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.SMALL_INDICATOR) {
            mDayPaint.setStyle(Paint.Style.FILL);
            drawCircle(canvas, mSmallIndicatorRadius, x, y);
        } else if (mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.NO_FILL_LARGE_INDICATOR) {
            mDayPaint.setStyle(Paint.Style.STROKE);
            drawDayCircleIndicator(CalendarAttr.NO_FILL_LARGE_INDICATOR, canvas, x, y, color);
        } else if (mCalendarAttr.getEventIndicatorStyle() == CalendarAttr.FILL_LARGE_INDICATOR) {
            drawDayCircleIndicator(CalendarAttr.FILL_LARGE_INDICATOR, canvas, x, y, color);
        }
    }

    private void drawSingleEvent(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        Event event = eventsList.get(0);
        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
    }

    private void drawTwoEvents(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        //draw fist event just left of center
        drawEventIndicatorCircle(canvas, xPosition + (mXIndicatorOffset * -1), yPosition, eventsList.get(0).getColor());
        //draw second event just right of center
        drawEventIndicatorCircle(canvas, xPosition + (mXIndicatorOffset * 1), yPosition, eventsList.get(1).getColor());
    }

    //draw 2 eventsByMonthAndYearMap followed by plus indicator to show there are more than 2 eventsByMonthAndYearMap
    private void drawEventsWithPlus(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        // k = size() - 1, but since we don't want to draw more than 2 indicators, we just stop after 2 iterations so we can just hard k = -2 instead
        // we can use the below loop to draw arbitrary eventsByMonthAndYearMap based on the current screen size, for example, larger screens should be able to
        // display more than 2 evens before displaying plus indicator, but don't draw more than 3 indicators for now
        for (int j = 0, k = -2; j < 3; j++, k += 2) {
            Event event = eventsList.get(j);
            float xStartPosition = xPosition + (mXIndicatorOffset * k);
            if (j == 2) {
                mDayPaint.setColor(mCalendarAttr.getMultiEventIndicatorColor());
                mDayPaint.setStrokeWidth(mMultiDayIndicatorStrokeWidth);
                canvas.drawLine(xStartPosition - mSmallIndicatorRadius, yPosition, xStartPosition + mSmallIndicatorRadius, yPosition, mDayPaint);
                canvas.drawLine(xStartPosition, yPosition - mSmallIndicatorRadius, xStartPosition, yPosition + mSmallIndicatorRadius, mDayPaint);
                mDayPaint.setStrokeWidth(0);
            } else {
                drawEventIndicatorCircle(canvas, xStartPosition, yPosition, event.getColor());
            }
        }
    }

    public void drawCalendarBackground(Canvas canvas, int calendarBackgroundColor, int width, int height) {
        mDayPaint.setColor(calendarBackgroundColor);
        mDayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, mDayPaint);
        mDayPaint.setStyle(Paint.Style.STROKE);
        mDayPaint.setColor(mCalendarAttr.getCalendarTextColor());
    }
}
