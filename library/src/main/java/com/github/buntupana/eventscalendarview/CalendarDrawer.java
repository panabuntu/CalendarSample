package com.github.buntupana.eventscalendarview;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.github.buntupana.eventscalendarview.domain.Event;

import java.util.Calendar;
import java.util.List;

import static com.github.buntupana.eventscalendarview.EventsCalendarPageView.FILL_LARGE_INDICATOR;
import static com.github.buntupana.eventscalendarview.EventsCalendarPageView.NO_FILL_LARGE_INDICATOR;
import static com.github.buntupana.eventscalendarview.EventsCalendarPageView.SMALL_INDICATOR;

public class CalendarDrawer {

    private final String TAG = CalendarDrawer.class.getSimpleName();

    // Sizes
    private int heightPerDay;
    private int paddingHeight;
    private int widthPerDay;
    private int paddingWidth;
    private int paddingRight;
    private int paddingLeft;
    private float screenDensity = 1;

    //Style
    private Paint dayPaint;
    private int calendarTextColor;
    private boolean currentDayIndicator;
    private int currentDayIndicatorStyle;
    private int currentSelectedDayIndicatorStyle;
    private int currentSelectedDayBackgroundColor;
    private int currentDayBackgroundColor;

    // Events
    private float bigCircleIndicatorRadius;
    private EventsContainer eventsContainer;

    private boolean shouldDrawDaysHeader;
    private Calendar todayCalendar;
    private List<Integer> inactiveDays;
    private String[] dayColumnNames;
    private Calendar eventsCalendar;
    private boolean shouldDrawIndicatorsBelowSelectedDays;
    private int textHeight;
    private int eventIndicatorStyle;
    private float smallIndicatorRadius;
    private int multiEventIndicatorColor;
    private float multiDayIndicatorStrokeWidth;
    private Calendar minDateCalendar;
    private Calendar maxDateCalendar;
    private boolean shouldShowMondayAsFirstDay;
    private float xIndicatorOffset;


    public CalendarDrawer(int heightPerDay, int paddingHeight, int widthPerDay, int paddingWidth, int paddingRight,
                          int paddingLeft, float screenDensity, Paint dayPaint, int calendarTextColor, boolean currentDayIndicator,
                          int currentDayIndicatorStyle, int currentSelectedDayIndicatorStyle, int currentSelectedDayBackgroundColor,
                          int currentDayBackgroundColor, float bigCircleIndicatorRadius, EventsContainer eventsContainer, boolean shouldDrawDaysHeader,
                          Calendar todayCalendar, List<Integer> inactiveDays, String[] dayColumnNames, Calendar eventsCalendar,
                          boolean shouldDrawIndicatorsBelowSelectedDays, int textHeight, int eventIndicatorStyle, float smallIndicatorRadius,
                          int multiEventIndicatorColor, float multiDayIndicatorStrokeWidth, Calendar minDateCalendar, Calendar maxDateCalendar,
                          boolean shouldShowMondayAsFirstDay, float xIndicatorOffset) {

        this.heightPerDay = heightPerDay;
        this.paddingHeight = paddingHeight;
        this.widthPerDay = widthPerDay;
        this.paddingWidth = paddingWidth;
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;
        this.screenDensity = screenDensity;
        this.dayPaint = dayPaint;
        this.calendarTextColor = calendarTextColor;
        this.currentDayIndicator = currentDayIndicator;
        this.currentDayIndicatorStyle = currentDayIndicatorStyle;
        this.currentSelectedDayIndicatorStyle = currentSelectedDayIndicatorStyle;
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.bigCircleIndicatorRadius = bigCircleIndicatorRadius;
        this.eventsContainer = eventsContainer;
        this.shouldDrawDaysHeader = shouldDrawDaysHeader;
        this.todayCalendar = todayCalendar;
        this.inactiveDays = inactiveDays;
        this.dayColumnNames = dayColumnNames;
        this.eventsCalendar = eventsCalendar;
        this.shouldDrawIndicatorsBelowSelectedDays = shouldDrawIndicatorsBelowSelectedDays;
        this.textHeight = textHeight;
        this.eventIndicatorStyle = eventIndicatorStyle;
        this.smallIndicatorRadius = smallIndicatorRadius;
        this.multiEventIndicatorColor = multiEventIndicatorColor;
        this.multiDayIndicatorStrokeWidth = multiDayIndicatorStrokeWidth;
        this.minDateCalendar = minDateCalendar;
        this.maxDateCalendar = maxDateCalendar;
        this.shouldShowMondayAsFirstDay = shouldShowMondayAsFirstDay;
        this.xIndicatorOffset = xIndicatorOffset;
    }

    void drawMonth(Canvas canvas, Calendar monthToDrawCalendar) {
        drawEventsMonthly(canvas, monthToDrawCalendar);

        Calendar aux = Calendar.getInstance();
        aux.setTimeInMillis(monthToDrawCalendar.getTimeInMillis());
        aux.setFirstDayOfWeek(monthToDrawCalendar.getFirstDayOfWeek());
        aux.set(Calendar.DAY_OF_MONTH, 1);

        //offset by one because of 0 index based calculations
        boolean isSameMonthAsToday = monthToDrawCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH);
        boolean isSameYearAsToday = monthToDrawCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
        boolean isSameMonthAsCurrentCalendar = monthToDrawCalendar.get(Calendar.MONTH) == monthToDrawCalendar.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);

        boolean selectedDay = false;
        int row = 0;
        int day_month = aux.get(Calendar.DAY_OF_MONTH);
        int day_week;
        do {

            float yPosition = row * heightPerDay + paddingHeight;

            for (int column = 0; column < dayColumnNames.length; column++) {
                float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + paddingRight);

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrawDaysHeader) {
                        dayPaint.setColor(calendarTextColor);
                        dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(calendarTextColor);
                        canvas.drawText(dayColumnNames[column], xPosition, paddingHeight, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {

                    day_month = aux.get(Calendar.DAY_OF_MONTH);
                    day_week = aux.get(Calendar.DAY_OF_WEEK);

                    if (aux.getFirstDayOfWeek() == Calendar.MONDAY) {
                        if (day_week == Calendar.SUNDAY && column != 6) {
                            continue;
                        } else if (day_week != Calendar.SUNDAY && day_week - 1 != column + 1) {
                            continue;
                        }
                    } else if (day_week != (column + 1)) {
                        continue;
                    }

                    if (currentDayIndicator && isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day_month) {
                        drawDayCircleIndicator(currentDayIndicatorStyle, canvas, xPosition, yPosition, currentDayBackgroundColor);
                    } else if (!selectedDay && inactiveDays.size() != 7) {
                        if (monthToDrawCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !inactiveDays.contains(monthToDrawCalendar.get(Calendar.DAY_OF_WEEK)) && isSameMonthAsCurrentCalendar) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        } else if (inactiveDays.contains(monthToDrawCalendar.get(Calendar.DAY_OF_WEEK)) && !inactiveDays.contains(aux.get(Calendar.DAY_OF_WEEK))) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        }
                    }

                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(calendarTextColor);
                    if (EventsCalendarPageView.isInactiveDate(aux, minDateCalendar, maxDateCalendar, inactiveDays)) {
                        dayPaint.setAlpha(127);
                    }
                    canvas.drawText(String.valueOf(day_month), xPosition, yPosition, dayPaint);

                    if (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        aux.add(Calendar.DATE, 1);
                    }
                }
            }
            row++;
        } while (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH));
    }


    public void drawWeek(Canvas canvas, Calendar weekToDrawCalendar) {
        drawEventsWeekly(canvas, weekToDrawCalendar);

        Calendar aux = Calendar.getInstance();
        aux.setFirstDayOfWeek(weekToDrawCalendar.getFirstDayOfWeek());
        aux.setTimeInMillis(weekToDrawCalendar.getTimeInMillis());
        //offset by one because we want to start from Monday

        //offset by one because of 0 index based calculations
        boolean isSameWeekAsToday = weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR);
        boolean isSameYearAsToday = weekToDrawCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
        boolean isSameWeekAsCurrentCalendar = weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR) == weekToDrawCalendar.get(Calendar.WEEK_OF_YEAR);
        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
//        boolean isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION;

        boolean selectedDay = false;
        for (int column = 0; column < dayColumnNames.length; column++) {

//            float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight);
            float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + paddingRight);

            for (int row = 0; row < 2; row++) {

                float yPosition = row * (heightPerDay + paddingHeight);

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrawDaysHeader) {
                        dayPaint.setColor(calendarTextColor);
                        dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(calendarTextColor);
                        canvas.drawText(dayColumnNames[column], xPosition, paddingHeight, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {

                    if (column == 0) {
                        aux.set(Calendar.DAY_OF_WEEK, aux.getFirstDayOfWeek());
                    } else {
                        aux.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    int day_month = aux.get(Calendar.DAY_OF_MONTH);
                    int day_week = aux.get(Calendar.DAY_OF_WEEK);

                    if (currentDayIndicator && isSameYearAsToday && isSameWeekAsToday && todayDayOfMonth == day_month) {
                        // TODO calculate position of circle in a more reliable way
                        drawDayCircleIndicator(currentDayIndicatorStyle, canvas, xPosition, yPosition, currentDayBackgroundColor);
                    } else if (!selectedDay && inactiveDays.size() != 7) {
                        if (weekToDrawCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !inactiveDays.contains(weekToDrawCalendar.get(Calendar.DAY_OF_WEEK)) && isSameWeekAsCurrentCalendar) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        } else if (inactiveDays.contains(weekToDrawCalendar.get(Calendar.DAY_OF_WEEK)) && !inactiveDays.contains(aux.get(Calendar.DAY_OF_WEEK))) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        }
                    }

                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(calendarTextColor);
                    if (EventsCalendarPageView.isInactiveDate(aux, minDateCalendar, maxDateCalendar, inactiveDays)) {
                        dayPaint.setAlpha(127);
                    }
                    canvas.drawText(String.valueOf(day_month), xPosition, yPosition, dayPaint);
                }
            }
        }
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color) {
        drawDayCircleIndicator(indicatorStyle, canvas, x, y, color, 1);
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color, float circleScale) {
        float strokeWidth = dayPaint.getStrokeWidth();
        if (indicatorStyle == NO_FILL_LARGE_INDICATOR) {
            dayPaint.setStrokeWidth(2 * screenDensity);
            dayPaint.setStyle(Paint.Style.STROKE);
        } else {
            dayPaint.setStyle(Paint.Style.FILL);
        }
        drawCircle(canvas, x, y, color, circleScale);
        dayPaint.setStrokeWidth(strokeWidth);
        dayPaint.setStyle(Paint.Style.FILL);
    }

    // Draw Circle on certain days to highlight them
    private void drawCircle(Canvas canvas, float x, float y, int color, float circleScale) {
        dayPaint.setColor(color);
//        if (animationStatus == ANIMATE_INDICATORS) {
//            float maxRadius = circleScale * bigCircleIndicatorRadius * 1.4f;
//            drawCircle(canvas, growfactorIndicator > maxRadius ? maxRadius : growfactorIndicator, x, y - (textHeight / 6));
//        } else {
//            drawCircle(canvas, circleScale * bigCircleIndicatorRadius, x, y - (textHeight / 6));
//        }

        drawCircle(canvas, circleScale * bigCircleIndicatorRadius, x, y - (textHeight / 6));
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        canvas.drawCircle(x, y, radius, dayPaint);
    }

    private void drawEventsMonthly(Canvas canvas, Calendar currentMonthToDrawCalendar) {
        int currentMonth = currentMonthToDrawCalendar.get(Calendar.MONTH);
        List<Events> uniqEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, currentMonthToDrawCalendar.get(Calendar.YEAR));

        boolean shouldDrawCurrentDayCircle = currentMonth == todayCalendar.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentMonth == currentMonthToDrawCalendar.get(Calendar.MONTH);

        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDayOfMonth = currentMonthToDrawCalendar.get(Calendar.DAY_OF_MONTH);
        float indicatorOffset = bigCircleIndicatorRadius / 2;
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = EventsCalendarPageView.getDayOfWeek(eventsCalendar, shouldShowMondayAsFirstDay) - 1;

                int weekNumberForMonth = eventsCalendar.get(Calendar.WEEK_OF_MONTH);
                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + paddingRight;
                float yPosition = weekNumberForMonth * heightPerDay + paddingHeight;

                List<Event> eventsList = events.getEvents();
                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);

                if (!currentDayIndicator || shouldDrawIndicatorsBelowSelectedDays || (!shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay)) {
                    if (eventIndicatorStyle == FILL_LARGE_INDICATOR || eventIndicatorStyle == NO_FILL_LARGE_INDICATOR) {
                        Event event = eventsList.get(0);
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                    } else {
                        yPosition += indicatorOffset;
                        // offset event indicators to draw below selected day indicators
                        // this makes sure that they do no overlap
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
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
        List<Events> uniqEvents = eventsContainer.getEventsForWeekAndYear(currentWeek, currentWeekToDrawCalendar.get(Calendar.YEAR));

        boolean shouldDrawCurrentDayCircle = currentWeek == todayCalendar.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentWeek == currentWeekToDrawCalendar.get(Calendar.MONTH);

        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDayOfMonth = currentWeekToDrawCalendar.get(Calendar.DAY_OF_MONTH);
        float indicatorOffset = bigCircleIndicatorRadius / 2;
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = EventsCalendarPageView.getDayOfWeek(eventsCalendar, shouldShowMondayAsFirstDay) - 1;

                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + paddingRight;
                float yPosition = heightPerDay + paddingHeight;

                List<Event> eventsList = events.getEvents();
                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);

                if (shouldDrawIndicatorsBelowSelectedDays || (!shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay)) {
                    if (eventIndicatorStyle == FILL_LARGE_INDICATOR || eventIndicatorStyle == NO_FILL_LARGE_INDICATOR) {
                        Event event = eventsList.get(0);
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                    } else {
                        yPosition += indicatorOffset;
                        // offset event indicators to draw below selected day indicators
                        // this makes sure that they do no overlap
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
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
        dayPaint.setColor(color);
        if (eventIndicatorStyle == SMALL_INDICATOR) {
            dayPaint.setStyle(Paint.Style.FILL);
            drawCircle(canvas, smallIndicatorRadius, x, y);
        } else if (eventIndicatorStyle == NO_FILL_LARGE_INDICATOR) {
            dayPaint.setStyle(Paint.Style.STROKE);
            drawDayCircleIndicator(NO_FILL_LARGE_INDICATOR, canvas, x, y, color);
        } else if (eventIndicatorStyle == FILL_LARGE_INDICATOR) {
            drawDayCircleIndicator(FILL_LARGE_INDICATOR, canvas, x, y, color);
        }
    }

    private void drawSingleEvent(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        Event event = eventsList.get(0);
        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
    }

    private void drawTwoEvents(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        //draw fist event just left of center
        drawEventIndicatorCircle(canvas, xPosition + (xIndicatorOffset * -1), yPosition, eventsList.get(0).getColor());
        //draw second event just right of center
        drawEventIndicatorCircle(canvas, xPosition + (xIndicatorOffset * 1), yPosition, eventsList.get(1).getColor());
    }

    //draw 2 eventsByMonthAndYearMap followed by plus indicator to show there are more than 2 eventsByMonthAndYearMap
    private void drawEventsWithPlus(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        // k = size() - 1, but since we don't want to draw more than 2 indicators, we just stop after 2 iterations so we can just hard k = -2 instead
        // we can use the below loop to draw arbitrary eventsByMonthAndYearMap based on the current screen size, for example, larger screens should be able to
        // display more than 2 evens before displaying plus indicator, but don't draw more than 3 indicators for now
        for (int j = 0, k = -2; j < 3; j++, k += 2) {
            Event event = eventsList.get(j);
            float xStartPosition = xPosition + (xIndicatorOffset * k);
            if (j == 2) {
                dayPaint.setColor(multiEventIndicatorColor);
                dayPaint.setStrokeWidth(multiDayIndicatorStrokeWidth);
                canvas.drawLine(xStartPosition - smallIndicatorRadius, yPosition, xStartPosition + smallIndicatorRadius, yPosition, dayPaint);
                canvas.drawLine(xStartPosition, yPosition - smallIndicatorRadius, xStartPosition, yPosition + smallIndicatorRadius, dayPaint);
                dayPaint.setStrokeWidth(0);
            } else {
                drawEventIndicatorCircle(canvas, xStartPosition, yPosition, event.getColor());
            }
        }
    }

    public void drawCalendarBackground(Canvas canvas, int calendarBackgroundColor, int width, int height) {
        dayPaint.setColor(calendarBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calendarTextColor);
    }
}
