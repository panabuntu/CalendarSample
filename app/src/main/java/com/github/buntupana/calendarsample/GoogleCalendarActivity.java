package com.github.buntupana.calendarsample;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.buntupana.eventscalendarview.EventsCalendarView;
import com.github.buntupana.eventscalendarview.domain.Event;
import com.github.buntupana.eventscalendarview.listeners.EventsCalendarViewListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GoogleCalendarActivity extends NavBaseActivity {

    private final String TAG = GoogleCalendarActivity.class.getSimpleName();

    private boolean isExpanded = false;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mDatePikerButton;
    private View mDatePikerArrow;
    private TextView mTitle;
    private EventsCalendarView mCalendarView;
    private TextView mTextDate;

    private Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_calendar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDatePikerButton = findViewById(R.id.date_picker_button);
        mDatePikerArrow = findViewById(R.id.date_picker_arrow);
        mTitle = (TextView) findViewById(R.id.title);
        mCalendarView = (EventsCalendarView) findViewById(R.id.calendarView);
        mTextDate = (TextView) findViewById(R.id.textDate);

        setSupportActionBar(mToolbar);



        mCalendarView.setListener(new EventsCalendarViewListener() {
            @Override
            public void onPageScroll(Date dateOfNewPage, int weekOfYear, String month, String year) {
                mTitle.setText(month + " " + year);
            }

            @Override
            public void onDayClick(Date dateClicked, int day, String month, String year) {
                mTextDate.setText(day + " " + month + " " + year);
            }
        });

        mDatePikerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppBarLayout.setExpanded(!isExpanded, true);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxVerticalOffset = Math.abs(appBarLayout.getTotalScrollRange());
                float angle = (Math.abs(verticalOffset) * 180 / maxVerticalOffset) + 180;
                mDatePikerArrow.setRotation(angle);
                if (angle == 180) {
                    isExpanded = true;
                    appBarLayout.setActivated(false);
                } else if (angle == 360) {
                    isExpanded = false;

                }
            }
        });
        addEvents();

        mTitle.setText(mCalendarView.getPageTitle());
        mTextDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(mCalendarView.getCurrentDate().getTime()));
    }

    private void addEvents() {
        mCalendar.setTime(mCalendarView.getCurrentDate());
        mCalendar.set(Calendar.DAY_OF_MONTH, 3);
        mCalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.eventColor1), mCalendar.getTimeInMillis()), true);
        mCalendar.set(Calendar.DAY_OF_MONTH, 3);
        mCalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.eventColor2), mCalendar.getTimeInMillis()), true);
        mCalendar.set(Calendar.DAY_OF_MONTH, 3);
        mCalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.eventColor2), mCalendar.getTimeInMillis()), true);
        mCalendar.set(Calendar.DAY_OF_MONTH, 25);
        mCalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.eventColor3), mCalendar.getTimeInMillis()), true);
        mCalendar.set(Calendar.DAY_OF_MONTH, 15);
        mCalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.eventColor1), mCalendar.getTimeInMillis()), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        } else if (id == R.id.action_current_day) {
            mCalendarView.setSelectedDate(new Date(), true);
        }

        return super.onOptionsItemSelected(item);
    }
}
