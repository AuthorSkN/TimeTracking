package com.example.author.timetracking;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.author.timetracking.adapter.TabsFragmentAdapter;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.fragment.CategoryFragment;
import com.example.author.timetracking.fragment.RecordsListFragment;
import com.example.author.timetracking.fragment.statistic.OftenListFragment;
import com.example.author.timetracking.fragment.statistic.SumDurationListFragment;
import com.example.author.timetracking.fragment.statistic.DiagramFragment;
import com.example.author.timetracking.fragment.statistic.SumDurationBySelectCatsListFragment;


public class StatisticActivity extends AppCompatActivity
        implements RecordsListFragment.OnRecordsListFragmentInteractionListener,
        CategoryFragment.OnCategoriesFragmentInteractionListener{

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new OftenListFragment(), getString(R.string.often));
        adapter.addFragment(new SumDurationListFragment(), getString(R.string.sum_in_all));
        adapter.addFragment(new SumDurationBySelectCatsListFragment(), getString(R.string.sum));
        adapter.addFragment(new DiagramFragment(), getString(R.string.diagram));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCategoriesFragmentInteraction(Category item) {

    }

    @Override
    public void OnRecordsListFragmentInteractionListener(Record item) {

    }
}
