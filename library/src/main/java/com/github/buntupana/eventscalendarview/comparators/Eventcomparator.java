package com.github.buntupana.eventscalendarview.comparators;

import com.github.buntupana.eventscalendarview.domain.Event;

import java.util.Comparator;

public class Eventcomparator implements Comparator<Event> {
    @Override
    public int compare(Event lhs, Event rhs) {
        return lhs.getTimeInMillis() < rhs.getTimeInMillis() ? -1 : lhs.getTimeInMillis() == rhs.getTimeInMillis() ? 0 : 1;
    }
}
