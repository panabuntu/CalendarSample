package com.github.buntupana.eventscalendarview.listeners;

import java.util.Date;


public interface EventsCalendarPageViewListener {
    void onDayClick(Date dateClicked, int day, String month, String year);
}
