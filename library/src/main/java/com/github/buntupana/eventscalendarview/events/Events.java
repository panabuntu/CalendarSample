package com.github.buntupana.eventscalendarview.events;

import com.github.buntupana.eventscalendarview.domain.Event;

import java.util.List;



public class Events {
    private final List<Event> events;
    private final long timeInMillis;

    Events(long timeInMillis, List<Event> events) {
        this.timeInMillis = timeInMillis;
        this.events = events;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Events event = (Events) o;

        if (timeInMillis != event.timeInMillis) return false;
        if (events != null ? !events.equals(event.events) : event.events != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = events != null ? events.hashCode() : 0;
        result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Events{" +
                "events=" + events +
                ", timeInMillis=" + timeInMillis +
                '}';
    }
}
