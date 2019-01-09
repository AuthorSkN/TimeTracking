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
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.data.viewmodel.RecordsListViewModel;
import com.example.author.timetracking.adapter.RecordsRecyclerViewAdapter;

import java.util.List;

public class RecordsListFragment extends Fragment {

    private OnRecordsListFragmentInteractionListener mListener;


    public RecordsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final RecordsListViewModel viewModel =
                    ViewModelProviders.of(this).get(RecordsListViewModel.class);
            viewModel.getRecords().observe(this, new Observer<List<Record>>() {
                @Override
                public void onChanged(@Nullable List<Record> records) {
                    recyclerView.setAdapter(new RecordsRecyclerViewAdapter(records, mListener, getContext(), RecordsListFragment.this));

                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecordsListFragmentInteractionListener) {
            mListener = (OnRecordsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordsListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRecordsListFragmentInteractionListener {

        void OnRecordsListFragmentInteractionListener(Record item);

    }
}
