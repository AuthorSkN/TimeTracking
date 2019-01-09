package com.example.author.timetracking.fragment.statistic;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.author.timetracking.R;
import com.example.author.timetracking.adapter.CategoriesRecyclerViewAdapter;
import com.example.author.timetracking.data.viewmodel.CategoryListViewModel;
import com.example.author.timetracking.fragment.CategoryFragment;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.Locale;
import java.util.TimeZone;

public class GlobalDurationFragment extends Fragment {
    private static final String TAG_DATETIME_FRAGMENT_START = "TAG_DATETIME_FRAGMENT_START";
    private static final String TAG_DATETIME_FRAGMENT_END = "TAG_DATETIME_FRAGMENT_END";
    private static final String DATE_FORMAT_PATTERN = "d MMM yyyy HH:mm";
    private static final String DATE_FORMAT_PATTERN_MINI = "MMMM dd";

    private CategoryFragment.OnCategoriesFragmentInteractionListener mListener;
    private SwitchDateTimeDialogFragment dateTimeFragmentStart;
    private SwitchDateTimeDialogFragment dateTimeFragmentEnd;
    private Date start;
    private Date end;
    private SimpleDateFormat dateFormat;
    private TextView startDateView;
    private TextView endDateView;
    private RecyclerView recyclerView;
    private CategoryListViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global_dur, container, false);
        dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, java.util.Locale.getDefault());
        startDateView = view.findViewById(R.id.startDateView);
        endDateView = view.findViewById(R.id.endDateView);
        configureDateTimeFragments();
        Context context = view.getContext();
        recyclerView =  view.findViewById(R.id.most_sum_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        view.findViewById(R.id.search_button).setOnClickListener(event -> new Find().doInBackground(start, end));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoryFragment.OnCategoriesFragmentInteractionListener) {
            mListener = (CategoryFragment.OnCategoriesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void configureDateTimeFragments() {
        startDateView.setOnClickListener(event -> {
            dateTimeFragmentStart.startAtCalendarView();
            dateTimeFragmentStart.setDefaultDateTime(new GregorianCalendar().getTime());
            dateTimeFragmentStart.show(getFragmentManager(), TAG_DATETIME_FRAGMENT_START);
        });

        endDateView.setOnClickListener(event -> {
            dateTimeFragmentEnd.startAtCalendarView();
            dateTimeFragmentEnd.setDefaultDateTime(new GregorianCalendar().getTime());
            dateTimeFragmentEnd.show(getFragmentManager(), TAG_DATETIME_FRAGMENT_END);
        });

        dateTimeFragmentStart = (SwitchDateTimeDialogFragment) getFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_START);
        if (dateTimeFragmentStart == null) {
            dateTimeFragmentStart = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        dateTimeFragmentStart.setTimeZone(TimeZone.getDefault());
        dateTimeFragmentStart.set24HoursMode(false);
        dateTimeFragmentStart.setHighlightAMPMSelection(false);
        dateTimeFragmentStart.setMinimumDateTime(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime());
        dateTimeFragmentStart.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragmentStart.setSimpleDateMonthAndDayFormat(new SimpleDateFormat(DATE_FORMAT_PATTERN_MINI, Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Stat date fragment", e.getMessage());
        }
        dateTimeFragmentStart.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                startDateView.setText(dateFormat.format(date));
                start = date;
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                startDateView.setText("");
            }
        });

        dateTimeFragmentEnd = (SwitchDateTimeDialogFragment) getFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_END);
        if (dateTimeFragmentEnd == null) {
            dateTimeFragmentEnd = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        dateTimeFragmentEnd.setTimeZone(TimeZone.getDefault());
        dateTimeFragmentEnd.set24HoursMode(false);
        dateTimeFragmentEnd.setHighlightAMPMSelection(false);
        dateTimeFragmentEnd.setMinimumDateTime(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime());
        dateTimeFragmentEnd.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragmentEnd.setSimpleDateMonthAndDayFormat(new SimpleDateFormat(DATE_FORMAT_PATTERN_MINI, Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("End date fragment", e.getMessage());
        }
        dateTimeFragmentEnd.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                endDateView.setText(dateFormat.format(date));
                end = date;
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                endDateView.setText("");
            }
        });
    }

    private class Find extends AsyncTask<Date, Void, Void> {
        @Override
        protected Void doInBackground(Date... objects) {
            if (objects[0] != null && objects[1] != null) {
                recyclerView.setAdapter(new CategoriesRecyclerViewAdapter(viewModel.getMostSum(objects[0], objects[1]), mListener, getContext(), GlobalDurationFragment.this));
            } else {
                recyclerView.setAdapter(new CategoriesRecyclerViewAdapter(viewModel.getMostSum(), mListener, getContext(), GlobalDurationFragment.this));
            }
            return null;
        }
    }
}
