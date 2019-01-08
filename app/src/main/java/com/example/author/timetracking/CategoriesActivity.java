package com.example.author.timetracking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.fragment.CategoryFragment;


public class CategoriesActivity extends AppCompatActivity
        implements CategoryFragment.OnCategoriesFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
    }

    @Override
    public void onCategoriesFragmentInteraction(Category item) {

    }
}
