package com.github.buntupana.eventscalendarview.utils;

import android.support.annotation.NonNull;

import com.github.buntupana.eventscalendarview.CalendarAttr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

    public static Calendar initCalendar(boolean shouldShowMondayAsFirstDay, TimeZone timeZone, Locale locale) {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        // make setMinimalDaysInFirstWeek same across android versions
        calendar.setMinimalDaysInFirstWeek(1);
        if (shouldShowMondayAsFirstDay) {
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

    public static Calendar getCalendar(Date date, boolean shouldShowMondayAsFirstDay, TimeZone timeZone, Locale locale) {
        Calendar calendar;
        if (date == null) {
            calendar = null;
        } else {
            calendar = Calendar.getInstance(timeZone, locale);
            calendar.setMinimalDaysInFirstWeek(1);
            if (shouldShowMondayAsFirstDay) {
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
            } else {
                calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            }
            calendar.setTime(date);
        }
        return calendar;
    }

    public static String getDate(@NonNull Calendar calendar, int calendarFormat, Locale locale){
        return getMonth(calendar, calendarFormat, locale) + " " + getYear(calendar, calendarFormat, locale);
    }

    public static String getMonth(Calendar calendar, int calendarFormat, Locale locale) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM", locale);

        String month;

        if (calendarFormat == CalendarAttr.MONTHLY) {
            month = sdf.format(calendar.getTime());
        } else {

            Calendar firstDayWeekCalendar = getCalendar(calendar.getTime(), calendar.getFirstDayOfWeek() == Calendar.MONDAY, calendar.getTimeZone(), locale);
            Calendar lastDayWeekCalendar = getCalendar(calendar.getTime(), calendar.getFirstDayOfWeek() == Calendar.MONDAY, calendar.getTimeZone(), locale);

            firstDayWeekCalendar.setMinimalDaysInFirstWeek(1);
            lastDayWeekCalendar.setMinimalDaysInFirstWeek(1);

            int firstDayWeek = calendar.getFirstDayOfWeek();
            int lastDayWeek;
            if (calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
                lastDayWeek = Calendar.SUNDAY;
            } else {
                lastDayWeek = Calendar.SATURDAY;
            }

            firstDayWeekCalendar.set(Calendar.DAY_OF_WEEK, firstDayWeek);
            lastDayWeekCalendar.set(Calendar.DAY_OF_WEEK, lastDayWeek);

            if (firstDayWeekCalendar.get(Calendar.MONTH) != lastDayWeekCalendar.get(Calendar.MONTH)) {
                month = sdf.format(firstDayWeekCalendar.getTime()) + "/" + sdf.format(lastDayWeekCalendar.getTime());
            } else {
                month = sdf.format(calendar.getTime());
            }
        }
        return month;
    }

    public static String getYear(Calendar calendar, int calendarFormat, Locale locale) {

        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy", locale);

        String year;

        if (calendarFormat == CalendarAttr.MONTHLY) {
            year = sdfFull.format(calendar.getTime());
        } else {
            Calendar lastDayWeekCalendar = getCalendar(calendar.getTime(), calendar.getFirstDayOfWeek() == Calendar.MONDAY, calendar.getTimeZone(), locale);

            lastDayWeekCalendar.setMinimalDaysInFirstWeek(1);

            int lastDayWeek;
            if (calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
                lastDayWeek = Calendar.SUNDAY;
            } else {
                lastDayWeek = Calendar.SATURDAY;
            }

            lastDayWeekCalendar.set(Calendar.DAY_OF_WEEK, lastDayWeek);

            year = sdfFull.format(lastDayWeekCalendar.getTime());
        }
        return year;
    }
}
