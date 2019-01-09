package com.example.author.timetracking;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.author.timetracking.adapter.TabsFragmentAdapter;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.fragment.CategoryFragment;
import com.example.author.timetracking.fragment.RecordsFragment;
import com.example.author.timetracking.fragment.statistic.MostOftenFragment;
import com.example.author.timetracking.fragment.statistic.MostSumFragment;
import com.example.author.timetracking.fragment.statistic.PieFragment;
import com.example.author.timetracking.fragment.statistic.SumByCatFragment;


public class StatisticActivity extends AppCompatActivity
        implements RecordsFragment.OnRecordsListFragmentInteractionListener,
        CategoryFragment.OnCategoriesFragmentInteractionListener{

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new MostOftenFragment(), getString(R.string.most_often));
        adapter.addFragment(new MostSumFragment(), getString(R.string.most_sum));
        adapter.addFragment(new SumByCatFragment(), getString(R.string.sum));
        adapter.addFragment(new PieFragment(), getString(R.string.pie));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCategoriesFragmentInteraction(Category item) {

    }

    @Override
    public void OnRecordsListFragmentInteractionListener(Record item) {

    }
}
