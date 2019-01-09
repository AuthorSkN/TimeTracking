package com.example.author.timetracking.adapter;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.author.timetracking.R;
import com.example.author.timetracking.RecordActivity;
import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.dao.CategoryDAO;
import com.example.author.timetracking.data.dao.PhotoDAO;
import com.example.author.timetracking.data.dao.RecordDAO;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.fragment.RecordsFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyRecordsRecyclerViewAdapter extends RecyclerView.Adapter<MyRecordsRecyclerViewAdapter.ViewHolder> {
    public static final String RECORD_MODEL = "record-model";

    private List<Record> mValues;
    private final RecordsFragment.OnRecordsListFragmentInteractionListener mListener;
    private final CategoryDAO categoryDAO;
    private final PhotoDAO photoDAO;
    private final RecordDAO recordDAO;
    private Fragment owner;
    private Category recordCategory;

    public MyRecordsRecyclerViewAdapter(List<Record> records, RecordsFragment.OnRecordsListFragmentInteractionListener listener, Context context, Fragment owner) {
        this.owner = owner;
        if (records == null) {
            mValues = new ArrayList<>();
        } else {
            mValues = records;
        }
        mListener = listener;
        categoryDAO = AppDatabase.getInstance(context).getCategoryDAO();
        photoDAO = AppDatabase.getInstance(context).getPhotoDAO();
        recordDAO = AppDatabase.getInstance(context).getRecordDAO();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        categoryDAO.findById(mValues.get(position).getCatId()).observe(owner, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category category) {
                if (category == null) {
                    recordCategory = new Category();
                } else {
                    recordCategory = category;
                    long phId = recordCategory.getPhId();
                    try {
                        Photo photo = photoDAO.findById(phId);
                        final Uri imageUri = Uri.parse(photo.getImageUri());
                        final InputStream imageStream = owner.getContext().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        holder.mCategoryIcon.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException err) {
                        Toast.makeText(owner.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });
        holder.mRecordTitle.setText(mValues.get(position).getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Intent intent = new Intent(owner.getContext(), RecordActivity.class);
                    intent.putExtra(RECORD_MODEL, mValues.get(position));
                    owner.startActivity(intent);
                    mListener.OnRecordsListFragmentInteractionListener(holder.mItem);
                }
            }
        });
        if (owner instanceof RecordsFragment) {
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(owner.getContext())
                            .setTitle("Confirm")
                            .setMessage("Do you really want to delete this record?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    recordDAO.delete(mValues.get(position));
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCategoryIcon;
        public final TextView mRecordTitle;
        public Record mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCategoryIcon = (ImageView) view.findViewById(R.id.category_icon);
            mRecordTitle = (TextView) view.findViewById(R.id.record_id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecordTitle.getText() + "'";
        }
    }
}


