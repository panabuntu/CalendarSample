package com.github.buntupana.calendarsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.buntupana.eventscalendarview.EventsCalendarPageView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EventsCalendarPageView month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        month = (EventsCalendarPageView) findViewById(R.id.month);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);

        month.setCurrentDate(calendar.getTime());
    }
}
