package com.qadr.clouddatabasesystem;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterThesisFragment extends Fragment {
    private static final int REQUEST_CODE = 101;
    Button uploadBtn, registerBtn;
    TextInputEditText author_name, author_matric, thesis_title, file_text;
    TextInputLayout authorNameLayout, authorMatricLayout;
    Uri file;
    String fileExtension, uid, which, name, matric;
    private long time;

    public RegisterThesisFragment (String uid, String which) {
        this.uid = uid;
        this.which = which;
    }

    public RegisterThesisFragment(String uid, String which, String name, String matric) {
        this.uid = uid;
        this.which = which;
        this.name = name;
        this.matric = matric;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_thesis, container, false);
        authorNameLayout = view.findViewById(R.id.author_name);
        authorMatricLayout = view.findViewById(R.id.author_matric);
        author_name = view.findViewById(R.id.author_name_text);
        author_matric = view.findViewById(R.id.author_matric_text);
        thesis_title = view.findViewById(R.id.thesis_title_text);
        file_text = view.findViewById(R.id.file_text);
        uploadBtn = view.findViewById(R.id.uploadBtn);
        registerBtn = view.findViewById(R.id.register_btn);
        if (which.equals("student")) {
            authorMatricLayout.setVisibility(View.GONE);
            authorNameLayout.setVisibility(View.GONE);
        }
        registerBtn.setOnClickListener(v -> register());
        uploadBtn.setOnClickListener(v -> filePicker());
        return view;
    }

    private void filePicker(){
        Intent intent = new Intent(getContext(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowFiles(true)
                .setShowAudios(false)
                .setShowVideos(false)
                .setShowImages(false)
                .setMaxSelection(1)
                .setSuffixes("text","pdf", "doc", "docx").setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if successful
        if (resultCode == Activity.RESULT_OK && data != null){
            ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            String path = mediaFiles.get(0).getName();
            if(requestCode == REQUEST_CODE){
                //Toast first
                Log.i("file", path);
                file_text.setText(path);
                file = mediaFiles.get(0).getUri();
                fileExtension = path.substring(path.lastIndexOf("."));
            }
        }
    }

    private void register () {
        String name, matric, title, fileText;
        if (which.equals("student")) {
            name = this.name;
            matric = this.matric;
        } else {
            name = author_name.getText().toString().trim();
            matric = author_matric.getText().toString().trim();
        }
        title = thesis_title.getText().toString().trim();
        fileText = file_text.getText().toString();
        int okay = 1;
        if (name.isEmpty() || name.length() > 40) {
            author_name.setError("cannot  be empty or > 40");
            okay = -1;
        }
        if (matric.isEmpty() || matric.length() > 11) {
            author_matric.setError("cannot be empty or > 11");
            okay = -1;
        }
        if (title.isEmpty() || title.length() > 100) {
            thesis_title.setError("cannot be empty or > 100");
            okay = -1;
        }
        if (file == null || fileText.isEmpty()) {
            file_text.setError("select a document file");
            okay = -1;
        }
        if (okay == 1) {
            Map<String, Object> thesisData = new HashMap<>();
            thesisData.put("author_name", name);
            thesisData.put("author_matric", matric);
            thesisData.put("title", title);
            thesisData.put("which", which);
            thesisData.put("uploaded_by", uid);
            if (Utility.uploadFile(getContext(), file, fileExtension, thesisData)){
                author_name.setText("");
                author_matric.setText("");
                thesis_title.setText("");
                file_text.setText("");
                file = null; fileExtension = null;
            }
        }
    }
}
