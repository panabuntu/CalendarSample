package com.github.buntupana.eventscalendarview;


public class EventsCalendarController {

//
//    private int paddingWidth = 40;
//    private int paddingHeight = 40;
//    private int textHeight;
//    private int textWidth;
//    private int widthPerDay;
//    private int heightPerDay;
//    private int textSize = 30;
//    private int width;
//    private int height;
//    private int paddingRight;
//    private int paddingLeft;
//    private Paint dayPaint = new Paint();
//    private Paint background = new Paint();
//    private Rect textSizeRect;
//
//
//    public EventsCalendarController(Paint dayPaint, OverScroller scroller, Rect textSizeRect, AttributeSet attrs,
//                                    Context context, int currentDayBackgroundColor, int calenderTextColor,
//                                    int currentSelectedDayBackgroundColor, VelocityTracker velocityTracker,
//                                    int multiEventIndicatorColor,
//                                    Locale locale, TimeZone timeZone) {
//
//        this.dayPaint = dayPaint;
////        this.scroller = scroller;
//        this.textSizeRect = textSizeRect;
////        this.currentDayBackgroundColor = currentDayBackgroundColor;
////        this.calenderTextColor = calenderTextColor;
////        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
////        this.velocityTracker = velocityTracker;
////        this.multiEventIndicatorColor = multiEventIndicatorColor;
////        this.locale = locale;
////        this.timeZone = timeZone;
////        loadAttributes(attrs, context);
////        this.eventsContainer = new EventsContainer(Calendar.getInstance(), calendarFormat);
//        init(context);
//    }
//
//    private void init(Context context){
//
//
//        dayPaint.setTextAlign(Paint.Align.CENTER);
//        dayPaint.setStyle(Paint.Style.STROKE);
//        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        dayPaint.setTypeface(Typeface.SANS_SERIF);
//        dayPaint.setTextSize(textSize);
//        dayPaint.setColor(calenderTextColor);
//        dayPaint.getTextBounds("31", 0, "31".length(), textSizeRect);
//        textHeight = textSizeRect.height() * 3;
//        textWidth = textSizeRect.width() * 2;
//
//    }
//
//    void drawMonth(Canvas canvas, Calendar monthToDrawCalender, int offset) {
////        drawEventsMonthly(canvas, monthToDrawCalender, offset);
//
//        Calendar aux = Calendar.getInstance();
//        aux.setTimeInMillis(monthToDrawCalender.getTimeInMillis());
//        aux.setFirstDayOfWeek(monthToDrawCalender.getFirstDayOfWeek());
//        aux.set(Calendar.DAY_OF_MONTH, 1);
//
//        //offset by one because of 0 index based calculations
//        boolean isSameMonthAsToday = monthToDrawCalender.get(Calendar.MONTH) == todayCalender.get(Calendar.MONTH);
//        boolean isSameYearAsToday = monthToDrawCalender.get(Calendar.YEAR) == todayCalender.get(Calendar.YEAR);
//        boolean isSameMonthAsCurrentCalendar = monthToDrawCalender.get(Calendar.MONTH) == currentCalender.get(Calendar.MONTH);
//        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);
//        boolean isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION;
//
//        boolean selectedDay = false;
//        int row = 0;
//        int day_month = aux.get(Calendar.DAY_OF_MONTH);
//        int day_week;
//        do {
//
//            float yPosition = row * heightPerDay + paddingHeight;
//
//            for (int column = 0; column < dayColumnNames.length; column++) {
//                float xPosition = widthPerDay * column + (paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight);
//                if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor) {
//                    // don't draw days if animating expose or indicators
//                    continue;
//                }
//
//                if (row == 0) {
//                    // first row, so draw the first letter of the day
//                    if (shouldDrawDaysHeader) {
//                        dayPaint.setColor(calenderTextColor);
//                        dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
//                        dayPaint.setStyle(Paint.Style.FILL);
//                        dayPaint.setColor(calenderTextColor);
//                        canvas.drawText(dayColumnNames[column], xPosition, paddingHeight, dayPaint);
//                        dayPaint.setTypeface(Typeface.DEFAULT);
//                    }
//                } else {
//
//                    day_month = aux.get(Calendar.DAY_OF_MONTH);
//                    day_week = aux.get(Calendar.DAY_OF_WEEK);
//
//                    if (aux.getFirstDayOfWeek() == Calendar.MONDAY) {
//                        if (day_week == Calendar.SUNDAY && column != 6) {
//                            continue;
//                        } else if (day_week != Calendar.SUNDAY && day_week - 1 != column + 1) {
//                            continue;
//                        }
//                    } else if (day_week != (column + 1)) {
//                        continue;
//                    }
//
//                    if (currentDayIndicator && isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day_month && !isAnimatingWithExpose) {
//                        drawDayCircleIndicator(currentDayIndicatorStyle, canvas, xPosition, yPosition, currentDayBackgroundColor);
//                    } else if (!selectedDay && inactiveDays.size() != 7) {
//                        if (currentCalender.get(Calendar.DAY_OF_MONTH) == day_month && !inactiveDays.contains(currentCalender.get(Calendar.DAY_OF_WEEK)) && isSameMonthAsCurrentCalendar && !isAnimatingWithExpose) {
//                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
//                            selectedDay = true;
//                        } else if (inactiveDays.contains(currentCalender.get(Calendar.DAY_OF_WEEK)) && !inactiveDays.contains(aux.get(Calendar.DAY_OF_WEEK))) {
//                            drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
//                            selectedDay = true;
//                        }
//                    }
//
//                    dayPaint.setStyle(Paint.Style.FILL);
//                    dayPaint.setColor(calenderTextColor);
//                    if (isInactiveDate(aux)) {
//                        dayPaint.setAlpha(127);
//                    }
//                    canvas.drawText(String.valueOf(day_month), xPosition, yPosition, dayPaint);
//
//                    if (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH)) {
//                        aux.add(Calendar.DATE, 1);
//                    }
//                }
//            }
//            row++;
//        } while (day_month != aux.getActualMaximum(Calendar.DAY_OF_MONTH));
//    }

}
