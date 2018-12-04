package com.example.flickrsearchclient;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectablePhotoViewHolder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView personName;
    TextView personAge;
    ImageView personPhoto;

    SelectablePhoto photo;
    OnItemSelectedListener onItemSelectedListener;

    SelectablePhotoViewHolder(View itemView, OnItemSelectedListener listener) {
        super(itemView);
        onItemSelectedListener = listener;
        cv = itemView.findViewById(R.id.card_view);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        personName = itemView.findViewById(R.id.person_name);
        personAge = itemView.findViewById(R.id.person_age);
        personPhoto = itemView.findViewById(R.id.person_photo);
    }

    public interface OnItemSelectedListener {

        void onItemSelected(SelectablePhoto item);
    }
}
