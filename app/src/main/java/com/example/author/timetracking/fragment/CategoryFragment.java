package com.example.author.timetracking.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.author.timetracking.R;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.viewmodel.CategoryListViewModel;
import com.example.author.timetracking.adapter.CategoriesRecyclerViewAdapter;

import java.util.List;


public class CategoryFragment extends Fragment {
    private OnCategoriesFragmentInteractionListener mListener;


    public CategoryFragment() {
    }

    @SuppressWarnings("unused")
    public static CategoryFragment newInstance(int columnCount) {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final CategoryListViewModel viewModel =
                    ViewModelProviders.of(this).get(CategoryListViewModel.class);
            viewModel.getCategories().observe(this, new Observer<List<Category>>() {
                @Override
                public void onChanged(@Nullable List<Category> categories) {
                    recyclerView.setAdapter(new CategoriesRecyclerViewAdapter(categories, mListener, getContext(), CategoryFragment.this));

                }
            });
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoriesFragmentInteractionListener) {
            mListener = (OnCategoriesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoriesFragmentInteractionListener {
        void onCategoriesFragmentInteraction(Category item);
    }
}
