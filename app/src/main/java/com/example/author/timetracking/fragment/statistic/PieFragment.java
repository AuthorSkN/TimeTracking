package com.example.author.timetracking.fragment.statistic;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.author.timetracking.R;
import com.example.author.timetracking.data.viewmodel.CategoryListViewModel;
import com.example.author.timetracking.data.entity.*;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieFragment extends Fragment {
    private SwitchDateTimeDialogFragment dateTimeFragmentStart;
    private SwitchDateTimeDialogFragment dateTimeFragmentEnd;
    private static final String TAG_DATETIME_FRAGMENT_START = "TAG_DATETIME_FRAGMENT_START";
    private static final String TAG_DATETIME_FRAGMENT_END = "TAG_DATETIME_FRAGMENT_END";
    private Date start;
    private Date end;
    private SimpleDateFormat myDateFormat;
    private TextView startDateView;
    private TextView endDateView;
    private CategoryListViewModel viewModel;
    private PieChartView pieChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pie, container, false);
        myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        startDateView = view.findViewById(R.id.startDateView);
        endDateView = view.findViewById(R.id.endDateView);
        configureDateTimeFragments();
        pieChartView = view.findViewById(R.id.chart);
        viewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        view.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PieFragment.Find().doInBackground(start, end);
            }
        });
        return view;
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
        dateTimeFragmentStart.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragmentStart.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragmentStart.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Stat date fragment", e.getMessage());
        }
        dateTimeFragmentStart.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                startDateView.setText(myDateFormat.format(date));
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
        dateTimeFragmentEnd.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragmentEnd.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragmentEnd.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("End date fragment", e.getMessage());
        }
        dateTimeFragmentEnd.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                endDateView.setText(myDateFormat.format(date));
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
            List<SliceValue> pieData = new ArrayList<>();
            List<Category> categories;
            if (objects[0] != null && objects[1] != null) {
                categories = viewModel.getSum(objects[0], objects[1]);
            } else {
                categories = viewModel.getSum();
            }
            for (Category category : categories) {
                SliceValue sliceValue = new SliceValue(category.getSum(), Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                sliceValue.setLabel(category.getTitle());
                pieData.add(sliceValue);
            }
            PieChartData pieChartData = new PieChartData(pieData);
            pieChartData.setHasLabels(true);
            pieChartView.setPieChartData(pieChartData);
            return null;
        }
    }
}
