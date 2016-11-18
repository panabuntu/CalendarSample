package com.github.buntupana.calendarsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.buntupana.eventscalendarview.EventsCalendarPageView;
import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarPageViewListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private EventsCalendarPageView monthPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthPage = (EventsCalendarPageView) findViewById(R.id.month);

        Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.set(Calendar.DAY_OF_MONTH, 3);
        maxDate.set(Calendar.DAY_OF_MONTH, 24);

//        calendar.add(Calendar.MONTH, 1);

        monthPage.setCurrentDate(calendar.getTime());
        monthPage.addInactiveDays(Calendar.SUNDAY);
        monthPage.setMinDateCalendar(minDate.getTime());
        monthPage.setMaxDateCalendar(maxDate.getTime());

        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        monthPage.setOnDayClickListener(new EventsCalendarPageViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Log.d(TAG, "onDayClick: ");
            }
        });

    }

    private void addEvents(int month, int year) {
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
//        currentCalender.setFirstDayOfWeek(Calendar.MONDAY);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 20; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
//            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            monthPage.addEvents(events);
//            mCompactCalendarViewMonthly.addEvents(events);
        }
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 6) {
            return Arrays.asList(
                    new Event(ContextCompat.getColor(this, R.color.status_missing), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(ContextCompat.getColor(this, R.color.status_fully_approved), timeInMillis, "Event at " + new Date(timeInMillis)));
        }
    }
}
