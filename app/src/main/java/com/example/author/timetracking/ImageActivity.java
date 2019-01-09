package com.example.author.timetracking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.author.timetracking.data.entity.Photo;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    public static final String CURRENT_PHOTO = "currentPhoto";
    public static final int RESULT_PHOTO = 5;

    private EditText text;
    private ImageView imageView;
    private Photo photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imageView);
        text = findViewById(R.id.editText);
        photo = (Photo) getIntent().getSerializableExtra(CURRENT_PHOTO);
        try {
            final InputStream imageStream = getContentResolver().openInputStream(Uri.parse(photo.getImageUri()));
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(selectedImage);
        } catch (FileNotFoundException err) {
            Toast.makeText(getApplicationContext(), "Can't load image", Toast.LENGTH_LONG).show();
            finish();
        }
        if (photo.getDescription() != null) {
            text.setText(photo.getDescription());
        }
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(event -> {
            photo.setDescription(text.getText().toString());
            getIntent().putExtra(CURRENT_PHOTO, photo);
            setResult(RESULT_PHOTO, getIntent());
            finish();
        });
    }

}
