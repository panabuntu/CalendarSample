package com.github.buntupana.eventscalendarview.listeners;


import java.util.Date;

public interface EventsCalendarViewListener extends EventsCalendarPageViewListener {
    void onPageScroll(Date firstDayOfNewPage, int day, String month, String year);
}
