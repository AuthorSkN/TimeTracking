package com.example.author.timetracking;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.DataRepository;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.data.viewmodel.CategoryListViewModel;
import com.google.android.flexbox.FlexboxLayout;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RecordActivity  extends AppCompatActivity {

    public static final int RESULT_LOAD_IMG = 1;
    public static final int RESULT_CANCEL = 0;
    public static final String CURRENT_PHOTO = "currentPhoto";
    public static final int RESULT_PHOTO = 5;
    public static final String RECORD_MODEL = "record-model";

    private Record record;
    private Spinner spinner;
    private AppDatabase appDatabase;
    private DataRepository repository;
    private Button addImageButton;
    private FlexboxLayout imageLayout;
    private SwitchDateTimeDialogFragment dateTimeFragmentStart;
    private SwitchDateTimeDialogFragment dateTimeFragmentEnd;
    private static final String TAG_DATETIME_FRAGMENT_START = "TAG_DATETIME_FRAGMENT_START";
    private static final String TAG_DATETIME_FRAGMENT_END = "TAG_DATETIME_FRAGMENT_END";
    private TextView startDateView;
    private TextView endDateView;
    private Date start;
    private Date end;
    private Category choosenCategory;
    private Button saveButton;
    private List<Category> allCategories;
    private ArrayList<Photo> addedPhotos;
    private ArrayList<Photo> recentlyAddedPhotos = new ArrayList<>();

    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getInstance(this);
        repository = ((TrackingApplication) getApplication()).getRepository();
        setContentView(R.layout.activity_record);
        spinner = findViewById(R.id.cats_spinner);
        final CategoryListViewModel categoriesViewModel = ViewModelProviders.of(this)
                .get(CategoryListViewModel.class);
        allCategories = new ArrayList<>();
        categoriesViewModel.getCategories().observe(this, categories -> {
            if (categories == null) {
                allCategories = new ArrayList<>();
            } else {
                allCategories = categories;
                changeSpinnerItems();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choosenCategory = allCategories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageLayout = findViewById(R.id.imagesLayout);
        addedPhotos = new ArrayList<>();
        addImageButton = findViewById(R.id.addImage);
        addImageButton.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });

        dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        startDateView = findViewById(R.id.view_stdate);
        endDateView = findViewById(R.id.view_edate);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(event -> saveRecord());

        configureDateTimeFragments();

        getIncomeData();
    }

    private void changeSpinnerItems() {
        List<String> titles = new ArrayList<>();
        for (Category category : allCategories) {
            titles.add(category.getTitle());
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titles);
        spinner.setAdapter(spinnerAdapter);
    }

    private void saveRecord() {
        record.setCatId(choosenCategory.getCategoryId());
        record.setEndTime(end);
        record.setStartTime(start);
        EditText title = findViewById(R.id.edit_title);
        record.setTitle(title.getText().toString());
        long recId = appDatabase.getRecordDAO().insert(record);
        Photo[] photosToUpdate = new Photo[recentlyAddedPhotos.size()];
        Photo[] photosToInsert = new Photo[addedPhotos.size() - recentlyAddedPhotos.size()];
        int indexU = 0;
        int indexI = 0;
        for (Photo photo : addedPhotos) {
            Photo temp = new Photo();
            temp.setImageUri(photo.getImageUri());
            temp.setRecordId(recId);
            temp.setDescription(photo.getDescription());
            if (recentlyAddedPhotos.contains(photo)) {
                photosToUpdate[indexU] = temp;
                indexU++;
            } else {
                photosToInsert[indexI] = temp;
                indexI++;
            }
        }

        appDatabase.getPhotoDAO().update(photosToUpdate);
        appDatabase.getPhotoDAO().insert(photosToInsert);
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCEL) {
            if (resultCode == RESULT_PHOTO) {
                Photo intentPhoto = (Photo) data.getSerializableExtra(CURRENT_PHOTO);
                for (Photo photo : addedPhotos) {
                    if (photo.getPhId() == intentPhoto.getPhId()) {
                        photo.setDescription(intentPhoto.getDescription());
                    }
                }
            } else {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageBitmap(selectedImage);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                    Photo photo = new Photo(imageUri.toString());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                            intent.putExtra(CURRENT_PHOTO, photo);
                            startActivityForResult(intent, 1);
                        }
                    });
                    imageLayout.addView(imageView);

                    addedPhotos.add(photo);
                } catch (FileNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getIncomeData() {
        if (getIntent().hasExtra(RECORD_MODEL)) {
            record = (Record) getIntent().getSerializableExtra(RECORD_MODEL);
            getIntent().removeExtra(RECORD_MODEL);

            updateView();
        } else {
            record = new Record();
        }
    }

    private void updateView() {
        if (record.getStartTime() != null) {
            startDateView.setText(dateFormat.format(record.getStartTime().toString()));
        }
        if (record.getEndTime() != null) {
            endDateView.setText(dateFormat.format(record.getEndTime().toString()));
        }
        ((EditText) findViewById(R.id.edit_title)).setText(record.getTitle());
        repository.getPhotosByRecord(record.getRecordId())
                .observe(this, photos -> {
            if (photos != null) {
                try {
                    for (Photo photo : photos) {
                        addedPhotos.add(photo);
                        recentlyAddedPhotos.add(photo);
                        final Uri imageUri = Uri.parse(photo.getImageUri());
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                                intent.putExtra(CURRENT_PHOTO, photo);
                                startActivityForResult(intent, 1);
                            }
                        });
                        imageView.setImageBitmap(selectedImage);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                        imageLayout.addView(imageView);
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error on record photos showing", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void configureDateTimeFragments() {
        startDateView.setOnClickListener(v -> {
            dateTimeFragmentStart.startAtCalendarView();
            dateTimeFragmentStart.setDefaultDateTime(new GregorianCalendar().getTime());
            dateTimeFragmentStart.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT_START);
        });

        endDateView.setOnClickListener(v -> {
            dateTimeFragmentEnd.startAtCalendarView();
            dateTimeFragmentEnd.setDefaultDateTime(new GregorianCalendar().getTime());
            dateTimeFragmentEnd.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT_END);
        });

        dateTimeFragmentStart = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_START);
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

        dateTimeFragmentEnd = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_END);
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

}
