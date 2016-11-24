package com.github.buntupana.calendarsample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class NavBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private FrameLayout mainContainer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_nav_base);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mainContainer = (FrameLayout) findViewById(R.id.main_container);

        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(layoutResID, mainContainer, true);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpNavView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setUpNavView() {
        mNavigationView.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout) mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        ((DrawerLayout) mDrawerLayout).addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        if (useDrawerToggle() && this instanceof SummaryActivity) { // use the hamburger menu
//            drawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout) mNavigationBinding.getRoot(), mBaseBinding.toolbar,
//                    R.string.navigation_drawer_open,
//                    R.string.navigation_drawer_close);
//
//            ((DrawerLayout) mNavigationBinding.getRoot()).addDrawerListener(drawerToggle);
//            drawerToggle.syncState();
//        } else if (useToolbar() && getSupportActionBar() != null) {
//            // Use home/back button instead
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        setCheckedItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
