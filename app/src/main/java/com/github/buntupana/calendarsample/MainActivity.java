package com.github.buntupana.calendarsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.buntupana.eventscalendarview.EventsCalendarView;
import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarViewListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.github.buntupana.calendarsample.R.id.calendar;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private EventsCalendarView calendarView;
    private EventsCalendarView calendarWeeklyView;
    private TextView textDate;
    private TextView textDay;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = (EventsCalendarView) findViewById(calendar);
        calendarWeeklyView = (EventsCalendarView) findViewById(R.id.calendarWeekly);
        textDate = (TextView) findViewById(R.id.textDate);
        textDay = (TextView) findViewById(R.id.textDay);

        Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.set(Calendar.DAY_OF_MONTH, 3);
        maxDate.set(Calendar.DAY_OF_MONTH, 24);

//        calendarView.add(Calendar.MONTH, 1);
//        calendarView.setMinDate(minDate.getTime());
        calendar.setMinimalDaysInFirstWeek(1);
        maxDate.setMinimalDaysInFirstWeek(1);

        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        addEvents(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        calendarView.setMinDate(minDate.getTime());
        calendarWeeklyView.setMinDate(minDate.getTime());
        calendarView.setMaxDate(maxDate.getTime());
        calendarWeeklyView.setMaxDate(maxDate.getTime());

        calendarView.setListener(new EventsCalendarViewListener() {
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textDate.setText(sdf.format(firstDayOfNewMonth));
            }

            @Override
            public void onDayClick(Date dateClicked) {
                Log.d(TAG, "onDayClick: " + dateClicked);
                textDay.setText("onDayClick: " + sdf2.format(dateClicked));
            }
        });

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.refresh();
            }
        });
    }

    private void addEvents(int month, int year) {
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
//        currentCalender.setFirstDayOfWeek(Calendar.MONDAY);
        Date firstDayOfMonth = currentCalender.getTime();
        List<Event> events = new ArrayList<>();
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

            events.addAll(getEvents(timeInMillis, i));

        }
        //            mCompactCalendarViewMonthly.addEvents(events);
        calendarView.addEvents(events);
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if (day > 2 && day <= 6) {
            return Arrays.asList(
                    new Event(ContextCompat.getColor(this, R.color.status_missing), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(ContextCompat.getColor(this, R.color.status_fully_approved), timeInMillis, "Event at " + new Date(timeInMillis)));
        }
    }
}
