package com.github.buntupana.eventscalendarview.listeners;


import java.util.Date;

public interface EventsCalendarViewListener extends EventsCalendarPageViewListener {
    void onPageScroll(Date firstDayOfNewPage, int weekOfYear, String month, String year);
}
