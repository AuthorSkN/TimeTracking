package com.example.author.timetracking.rvadapter;

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
import com.example.author.timetracking.data.dao.PhotoDAO;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.fragment.CategoryFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<MyCategoriesRecyclerViewAdapter.ViewHolder>{
    public static final String CATEGORY_MODEL = "currentCategory";

    private List<Category> mValues;
    private final CategoryFragment.OnCategoriesFragmentInteractionListener mListener;
    private final PhotoDAO photoDAO;
    private Fragment owner;

    public MyCategoriesRecyclerViewAdapter(List<Category> categories, CategoryFragment.OnCategoriesFragmentInteractionListener listener, Context context, Fragment owner) {
        this.owner = owner;
        if (categories == null) {
            mValues = new ArrayList<>();
        } else {
            mValues = categories;
        }
        mListener = listener;
        photoDAO = AppDatabase.getInstance(context).getPhotoDAO();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCategoryTitle.setText(mValues.get(position).getTitle());
        try {
            Photo photo = photoDAO.findById(mValues.get(position).getPhId());
            final Uri imageUri = Uri.parse(photo.getImageUri());
            final InputStream imageStream = owner.getContext().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            holder.mCategoryIcon.setImageBitmap(selectedImage);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        Intent intent = new Intent(owner.getContext(), CategoryActivity.class);
                        intent.putExtra(CATEGORY_MODEL, mValues.get(position));
                        owner.startActivity(intent);
                        mListener.onCategoriesFragmentInteraction(holder.mItem);
                    }
                }
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
        public final View mView;
        public final ImageView mCategoryIcon;
        public final TextView mCategoryTitle;
        public Category mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCategoryIcon = (ImageView) view.findViewById(R.id.category_icon);
            mCategoryTitle = (TextView) view.findViewById(R.id.category_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCategoryTitle.getText() + "'";
        }
    }

}
