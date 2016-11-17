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
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.buntupana.eventscalendarview.listeners.EventsCalendarPageViewListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.R.attr.screenDensity;
import static com.github.buntupana.eventscalendarview.EventsCalendarView.MONTHLY;


public class EventsCalendarPageView extends View {

    private final String TAG = EventsCalendarPageView.class.getSimpleName();

    private Context mContext;

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
    private int calenderTextColor;
    private int currentSelectedDayBackgroundColor;
    private int calenderBackgroundColor = Color.WHITE;
    private boolean shouldScroll = true;
    private TimeZone timeZone = TimeZone.getDefault();
    private Locale locale = Locale.getDefault();

    // Calendars
    private Calendar todayCalendar;
    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar minDateCalendar = null;
    private Calendar maxDateCalendar = null;

    private Paint dayPaint = new Paint();
    private Paint background = new Paint();

    private List<Integer> inactiveDays = new ArrayList<>();

    private EventsCalendarPageViewListener listener;

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            int dayColumn = Math.round((paddingLeft + e.getX() - paddingWidth - paddingRight) / widthPerDay);
            int dayRow = Math.round((e.getY() - paddingHeight) / heightPerDay);

            long currentTime = currentCalendar.getTimeInMillis();
            setFirstDay(currentCalendar);

            //Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
            int firstDayOfMonth = getDayOfWeek(currentCalendar);

            int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

            if (dayOfMonth < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    && dayOfMonth >= 0) {
                currentCalendar.add(Calendar.DATE, dayOfMonth);
                if (!isInactiveDate(currentCalendar)) {
                    invalidate();
                    if(listener != null) {
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

    public EventsCalendarPageView(Context context, AttributeSet attrs, Calendar currentCalendar, Calendar minDateCalendar, Calendar maxDateCalendar, List<Integer> inactiveDays, TimeZone timeZone, Locale locale, EventsCalendarPageViewListener listener) {
        super(context, attrs);
        this.currentCalendar = currentCalendar;
        this.minDateCalendar = minDateCalendar;
        this.maxDateCalendar = maxDateCalendar;
        this.inactiveDays = inactiveDays;
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

        calenderBackgroundColor = ContextCompat.getColor(context, R.color.backgroundColor);
        currentSelectedDayBackgroundColor = ContextCompat.getColor(context, R.color.currentSelectedDayColor);
        currentDayBackgroundColor = ContextCompat.getColor(context, R.color.currentDayColor);
        calenderTextColor = ContextCompat.getColor(context, R.color.textColor);
        multiEventIndicatorColor = ContextCompat.getColor(context, R.color.multiIndicatorColor);

        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EventsCalendarView, 0, 0);
            try {
                currentDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
                calenderTextColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarTextColor, calenderTextColor);
                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor);
                calenderBackgroundColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarBackgroundColor, calenderBackgroundColor);
                multiEventIndicatorColor = typedArray.getColor(R.styleable.EventsCalendarView_eventsCalendarMultiEventIndicatorColor, multiEventIndicatorColor);
                textSize = typedArray.getDimensionPixelSize(R.styleable.EventsCalendarView_eventsCalendarTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
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

        todayCalendar = Calendar.getInstance();

        setUseWeekDayAbbreviation(false);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(Typeface.SANS_SERIF);
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calenderTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), textSizeRect);
        textHeight = textSizeRect.height() * 3;
        textWidth = textSizeRect.width() * 2;

        gestureDetector = new GestureDetectorCompat(context, gestureListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCalenderBackground(canvas);
        if(calendarFormat == MONTHLY ){
            drawMonth(canvas, currentCalendar);
        } else {
            drawWeek(canvas, currentCalendar);
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

    void drawMonth(Canvas canvas, Calendar monthToDrawCalender) {
//        drawEventsMonthly(canvas, monthToDrawCalender, offset);

        Calendar aux = Calendar.getInstance();
        aux.setTimeInMillis(monthToDrawCalender.getTimeInMillis());
        aux.setFirstDayOfWeek(monthToDrawCalender.getFirstDayOfWeek());
        aux.set(Calendar.DAY_OF_MONTH, 1);

        //offset by one because of 0 index based calculations
        boolean isSameMonthAsToday = monthToDrawCalender.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH);
        boolean isSameYearAsToday = monthToDrawCalender.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
        boolean isSameMonthAsCurrentCalendar = monthToDrawCalender.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);

        boolean selectedDay = false;
        int row = 0;
        int day_month = aux.get(Calendar.DAY_OF_MONTH);
        int day_week;
        do {

            float yPosition = row * heightPerDay + paddingHeight;

            for (int column = 0; column < dayColumnNames.length; column++) {
                float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + paddingRight);
//                float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight);
//                if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor) {
//                    // don't draw days if animating expose or indicators
//                    continue;
//                }

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrawDaysHeader) {
                        dayPaint.setColor(calenderTextColor);
                        dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(calenderTextColor);
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
                        if (currentCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !inactiveDays.contains(currentCalendar.get(Calendar.DAY_OF_WEEK)) && isSameMonthAsCurrentCalendar) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        } else if (inactiveDays.contains(currentCalendar.get(Calendar.DAY_OF_WEEK)) && !inactiveDays.contains(aux.get(Calendar.DAY_OF_WEEK))) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        }
                    }

                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(calenderTextColor);
                    if (isInactiveDate(aux)) {
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


    void drawWeek(Canvas canvas, Calendar weekToDrawCalender) {
//        drawEventsWeekly(canvas, weekToDrawCalender, offset);

        Calendar aux = Calendar.getInstance();
        aux.setFirstDayOfWeek(weekToDrawCalender.getFirstDayOfWeek());
        aux.setTimeInMillis(weekToDrawCalender.getTimeInMillis());
        //offset by one because we want to start from Monday

        //offset by one because of 0 index based calculations
        boolean isSameWeekAsToday = weekToDrawCalender.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR);
        boolean isSameYearAsToday = weekToDrawCalender.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
        boolean isSameWeekAsCurrentCalendar = weekToDrawCalender.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
//        boolean isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION;

        boolean selectedDay = false;
        for (int column = 0; column < dayColumnNames.length; column++) {

//            float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight);
            float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + paddingRight);

            for (int row = 0; row < 2; row++) {

                float yPosition = row * (heightPerDay + paddingHeight);
//                if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor) {
//                    // don't draw days if animating expose or indicators
//                    continue;
//                }

                if (row == 0) {
                    // first row, so draw the first letter of the day
                    if (shouldDrawDaysHeader) {
                        dayPaint.setColor(calenderTextColor);
                        dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(calenderTextColor);
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
                        if (currentCalendar.get(Calendar.DAY_OF_MONTH) == day_month && !inactiveDays.contains(currentCalendar.get(Calendar.DAY_OF_WEEK)) && isSameWeekAsCurrentCalendar) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        } else if (inactiveDays.contains(currentCalendar.get(Calendar.DAY_OF_WEEK)) && !inactiveDays.contains(aux.get(Calendar.DAY_OF_WEEK))) {
                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                            selectedDay = true;
                        }
                    }

                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(calenderTextColor);
                    if (isInactiveDate(aux)) {
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

    private boolean isInactiveDate(Calendar calendar) {
        boolean conditionalActiveDay = inactiveDays.contains(calendar.get(Calendar.DAY_OF_WEEK));
        boolean conditionalMinDate = minDateCalendar != null && calendar.getTimeInMillis() < minDateCalendar.getTimeInMillis();
        boolean conditionalMaxDate = maxDateCalendar != null && maxDateCalendar.getTimeInMillis() < calendar.getTimeInMillis();

        return conditionalActiveDay || conditionalMinDate || conditionalMaxDate;
    }

    private void drawCalenderBackground(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
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

    public void setCurrentDate(Date currentDate){
        if(currentDate == null){
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

    private int getDayOfWeek(Calendar calendar) {

        int dayOfWeek;
        if (!shouldShowMondayAsFirstDay) {
            return calendar.get(Calendar.DAY_OF_WEEK);
        } else {
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            dayOfWeek = dayOfWeek <= 0 ? 7 : dayOfWeek;
        }
        return dayOfWeek;
    }
}
