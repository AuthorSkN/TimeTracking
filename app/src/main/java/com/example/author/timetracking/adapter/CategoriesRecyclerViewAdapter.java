package com.example.author.timetracking.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.author.timetracking.CategoryActivity;
import com.example.author.timetracking.R;
import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.dao.CategoryDAO;
import com.example.author.timetracking.data.dao.PhotoDAO;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.fragment.CategoryFragment;
import com.example.author.timetracking.fragment.RecordsListFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {
    public static final String CATEGORY_MODEL = "currentCategory";

    private List<Category> mValues;
    private final CategoryFragment.OnCategoriesFragmentInteractionListener mListener;
    private final PhotoDAO photoDAO;
    private final CategoryDAO categoryDAO;
    private Fragment owner;

    public CategoriesRecyclerViewAdapter(List<Category> categories, CategoryFragment.OnCategoriesFragmentInteractionListener listener, Context context, Fragment owner) {
        this.owner = owner;
        if (categories == null) {
            mValues = new ArrayList<>();
        } else {
            mValues = categories;
        }
        mListener = listener;
        photoDAO = AppDatabase.getInstance(context).getPhotoDAO();
        categoryDAO = AppDatabase.getInstance(context).getCategoryDAO();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mValues.get(position);
        holder.categoryTitle.setText(mValues.get(position).getTitle());
        try {
            Photo photo = photoDAO.findById(mValues.get(position).getPhId());
            final Uri imageUri = Uri.parse(photo.getImageUri());
            final InputStream imageStream = owner.getContext().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            holder.categoryIcon.setImageBitmap(selectedImage);
            holder.view.setOnClickListener(event -> {
                if (null != mListener) {
                    Intent intent = new Intent(owner.getContext(), CategoryActivity.class);
                    intent.putExtra(CATEGORY_MODEL, mValues.get(position));
                    owner.startActivity(intent);
                    mListener.onCategoriesFragmentInteraction(holder.item);
                }
            });

            holder.view.setOnLongClickListener(v -> {
                new AlertDialog.Builder(owner.getContext())
                        .setTitle("Confirm")
                        .setMessage("Do you really want to delete this category?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes,
                                (dialog, whichButton) -> categoryDAO.delete(mValues.get(position)))
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            });

        } catch (FileNotFoundException err) {
            Toast.makeText(owner.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView categoryIcon;
        public final TextView categoryTitle;
        public Category item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            categoryIcon = view.findViewById(R.id.category_icon);
            categoryTitle = view.findViewById(R.id.category_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + categoryTitle.getText() + "'";
        }
    }

}
