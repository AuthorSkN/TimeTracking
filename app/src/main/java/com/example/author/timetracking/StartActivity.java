package com.example.author.timetracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.fragment.RecordsListFragment;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecordsListFragment.OnRecordsListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FloatingActionButton addRecordButton = findViewById(R.id.fab);
        addRecordButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
            startActivity(intent);
        });

        NavigationView menuView = findViewById(R.id.menu_view);
        menuView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.to_records) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);

        } else if (id == R.id.to_categories) {
            Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
            startActivity(intent);

        } else if (id == R.id.to_statistic) {
            Intent intent = new Intent(getApplicationContext(), com.example.author.timetracking.StatisticActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnRecordsListFragmentInteractionListener(Record item) {

    }
}
