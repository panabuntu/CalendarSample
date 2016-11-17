package com.github.buntupana.eventscalendarview.listeners;


import java.util.Date;

public interface EventsCalendarViewListener extends EventsCalendarPageViewListener {
    void onMonthScroll(Date firstDayOfNewMonth);
}
