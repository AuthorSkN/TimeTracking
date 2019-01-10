package com.example.author.timetracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.fragment.CategoryFragment;


public class CategoriesListActivity extends AppCompatActivity
        implements CategoryFragment.OnCategoriesFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        FloatingActionButton addRecordButton = findViewById(R.id.fab);
        addRecordButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onCategoriesFragmentInteraction(Category item) {

    }
}
