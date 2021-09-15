package com.qadr.clouddatabasesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ThesisListAdapter extends ArrayAdapter<ThesisObject> {
    public ThesisListAdapter(@NonNull Context context, @NonNull ArrayList<ThesisObject> objects) {
        super(context, R.layout.list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ThesisObject thesis = getItem(position);
        String url = thesis.getUrl();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,false);
        }

        TextView title = convertView.findViewById(R.id.title_text);
        TextView author = convertView.findViewById(R.id.author_matric);
        Button button = convertView.findViewById(R.id.button);
        title.setText(thesis.getTitle());
        author.setText("Author: " + thesis.getAuthor());
        button.setOnClickListener(v -> {
            Utility.downloadFile(getContext(), "thesis_documents/"+url);
        });
        return convertView;
    }
}
