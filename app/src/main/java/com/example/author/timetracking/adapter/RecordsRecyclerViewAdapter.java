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

import com.example.author.timetracking.R;
import com.example.author.timetracking.RecordActivity;
import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.dao.CategoryDAO;
import com.example.author.timetracking.data.dao.PhotoDAO;
import com.example.author.timetracking.data.dao.RecordDAO;
import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.data.entity.Record;
import com.example.author.timetracking.fragment.RecordsListFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordsRecyclerViewAdapter extends RecyclerView.Adapter<RecordsRecyclerViewAdapter.ViewHolder> {

    public static final String RECORD_MODEL = "record-model";

    private List<Record> mValues;
    private final RecordsListFragment.OnRecordsListFragmentInteractionListener mListener;
    private final CategoryDAO categoryDAO;
    private final PhotoDAO photoDAO;
    private final RecordDAO recordDAO;
    private Fragment owner;
    private Category recordCategory;

    public RecordsRecyclerViewAdapter(List<Record> records, RecordsListFragment.OnRecordsListFragmentInteractionListener listener, Context context, Fragment owner) {
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
        holder.item = mValues.get(position);
        categoryDAO.findById(mValues.get(position).getCatId()).observe(owner, category -> {
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
                    holder.categoryIcon.setImageBitmap(selectedImage);
                } catch (FileNotFoundException err) {
                    Toast.makeText(owner.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();

                }

            }
        });
        holder.recordTitle.setText(mValues.get(position).getTitle());
        holder.view.setOnClickListener(event -> {
            if (null != mListener) {
                Intent intent = new Intent(owner.getContext(), RecordActivity.class);
                intent.putExtra(RECORD_MODEL, mValues.get(position));
                owner.startActivity(intent);
                mListener.OnRecordsListFragmentInteractionListener(holder.item);
            }
        });
        if (owner instanceof RecordsListFragment) {
            holder.view.setOnLongClickListener(v -> {
                new AlertDialog.Builder(owner.getContext())
                        .setTitle("Confirm")
                        .setMessage("Do you really want to delete this record?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes,
                                (dialog, whichButton) -> recordDAO.delete(mValues.get(position)))
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView categoryIcon;
        public final TextView recordTitle;
        public Record item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            categoryIcon =  view.findViewById(R.id.category_icon);
            recordTitle =  view.findViewById(R.id.record_id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + recordTitle.getText() + "'";
        }
    }
}


