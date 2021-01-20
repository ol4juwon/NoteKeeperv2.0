package com.ol4juwon.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseInfo course= mCourses.get(position);
        holder.mTextCourse.setText(course.getTitle());
       // holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition = position;

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextCourse;
       // public final TextView mTextTitle;
        public int mCurrentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
           // mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view ,mCourses.get(mCurrentPosition).getTitle(),Snackbar.LENGTH_LONG).show();
                }
            });


        }
    }
}
