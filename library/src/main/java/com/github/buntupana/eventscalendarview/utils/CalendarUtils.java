package com.github.buntupana.eventscalendarview.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

    public static Calendar initCalendar(boolean shouldShowMondayAsFirstDay, TimeZone timeZone, Locale locale){
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

    public static boolean isInactiveDate(Calendar calendar, Calendar minDateCalendar, Calendar maxDateCalendar, List<Integer> inactiveDays) {
        boolean conditionalActiveDay = inactiveDays.contains(calendar.get(Calendar.DAY_OF_WEEK));
        boolean conditionalMinDate = minDateCalendar != null && calendar.getTimeInMillis() < minDateCalendar.getTimeInMillis();
        boolean conditionalMaxDate = maxDateCalendar != null && maxDateCalendar.getTimeInMillis() < calendar.getTimeInMillis();

        return conditionalActiveDay || conditionalMinDate || conditionalMaxDate;
    }

    public static Calendar getCalendar(Date date, boolean shouldShowMondayAsFirstDay, TimeZone timeZone, Locale locale){
        Calendar calendar;
        if(date == null){
            calendar = null;
        } else {
            calendar = Calendar.getInstance(timeZone, locale);
            calendar.setMinimalDaysInFirstWeek(1);
            if(shouldShowMondayAsFirstDay){
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
            } else {
                calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            }
            calendar.setTime(date);
        }
        return calendar;
    }
}
