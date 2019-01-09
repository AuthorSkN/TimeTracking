package com.example.author.timetracking.fragment.statistic;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.author.timetracking.R;
import com.example.author.timetracking.adapter.RecordsRecyclerViewAdapter;
import com.example.author.timetracking.data.viewmodel.RecordsListViewModel;
import com.example.author.timetracking.fragment.RecordsListFragment;


public class FrequentListFragment extends Fragment {
    private RecordsListFragment.OnRecordsListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frqt_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final RecordsListViewModel viewModel =
                    ViewModelProviders.of(this).get(RecordsListViewModel.class);
            viewModel.getRecords().observe(this, records -> {
                recyclerView.setAdapter(new RecordsRecyclerViewAdapter(records, mListener, getContext(), FrequentListFragment.this));
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecordsListFragment.OnRecordsListFragmentInteractionListener) {
            mListener = (RecordsListFragment.OnRecordsListFragmentInteractionListener) context;
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

}
