package com.example.author.timetracking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.dao.PhotoDAO;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CategoryActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMG = 1;
    public static final String CATEGORY_MODEL = "currentCategory";
    public static final int RESULT_CANCEL = 0;
    public static final String CURRENT_PHOTO = "currentPhoto";
    public static final int RESULT_PHOTO = 5;
    public static final String RECORD_MODEL = "record-model";

    private TextView title;
    private TextView description;
    private ImageView image;
    private PhotoDAO photoDAO;
    private Category category;
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        title = findViewById(R.id.editTextTitle);
        description = findViewById(R.id.editTextDescription);
        image = findViewById(R.id.imageCatView);
        photoDAO = AppDatabase.getInstance(getApplicationContext()).getPhotoDAO();
        updateView();
        findViewById(R.id.button).setOnClickListener(event -> {
            category.setDescription(description.getText().toString());
            category.setTitle(title.getText().toString());
            if (photo != null) {
                int size = photoDAO.update(photo);
                if (size == 0) {
                    long[] phId = photoDAO.insert(photo);
                    category.setPhId(phId[0]);
                }
            }
            AppDatabase.getInstance(getApplicationContext()).getCategoryDAO().update(category);
            finish();
        });
        image.setOnClickListener(event -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });
        image.setOnLongClickListener(event -> {
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.putExtra(CURRENT_PHOTO, photo);
            startActivityForResult(intent, 1);
            return true;
        });
    }

    private void updateView() {
        category = (Category) getIntent().getSerializableExtra(CATEGORY_MODEL);
        if (category != null) {
            title.setText(category.getTitle());
            if (category.getDescription() != null) {
                description.setText(category.getDescription());
            }
            photo = photoDAO.findById(category.getPhId());
            updateImage();

        }
    }

    private void updateImage() {
        try {
            final Uri imageUri = Uri.parse(photo.getImageUri());
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            image.setImageBitmap(selectedImage);
        } catch (FileNotFoundException err) {
            Toast.makeText(getApplicationContext(), "Can't load image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCEL) {
            if (resultCode == RESULT_PHOTO) {
                Photo intentPhoto = (Photo) data.getSerializableExtra(CURRENT_PHOTO);
                this.photo = intentPhoto;
                updateImage();
            } else {
                final Uri imageUri = data.getData();
                photo.setImageUri(imageUri.toString());
                updateImage();
            }
        }
    }



}
