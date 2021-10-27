package com.qadr.clouddatabasesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThesisFragment extends Fragment {

    ThesisChange thesisChange;
    String uid, which, id;
    Button registerThesisBtn;
    TextView no_thesis_result;
    ListView listView;
    LinearLayout registerThesisLayout;
    long time;
    public ThesisFragment (String uid, String which, String id) {
        this.uid = uid;
        this.id = id;
        this.which = which;
        Log.d("ThesisFragment: ", "id=="+id);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thesis_layout, container, false);
        no_thesis_result = view.findViewById(R.id.no_thesis_result);
        registerThesisBtn = view.findViewById(R.id.register_thesis_btn);
        listView = view.findViewById(R.id.list_view);
        registerThesisBtn.setOnClickListener(v -> thesisChange.changeTo("register"));
        registerThesisLayout = view.findViewById(R.id.thesis_linearLayout);
        Map<String, Object> data = new HashMap<>();
        data.put("uid", id);
        data.put("which", which);
        time = new Date().getTime();
        Utility.showDialog(getContext());
        Utility.getThesis(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //getting thesis
                ArrayList<Map<String, Object>> thesis_list = (ArrayList<Map<String, Object>>) task.getResult().get("thesis");
                if (!thesis_list.isEmpty()) {
                    if (which.equals("student")) {
                        registerThesisLayout.setVisibility(View.GONE);
                    } else {
                        registerThesisLayout.setVisibility(View.VISIBLE);
                    }
                    no_thesis_result.setVisibility(View.GONE);
                    ArrayList<ThesisObject> list = new ArrayList<>();
                    for (int i = 0; i < thesis_list.size(); i++) {
                        Map<String, Object> thesis = thesis_list.get(i);
                        ThesisObject object = new ThesisObject((String) thesis.get("uri"), (String) thesis.get("title"), (String) thesis.get("author_matric"));
                        list.add(object);
                        Log.d("THESIS", object.toString());
                    }
                    ThesisListAdapter adapter = new ThesisListAdapter(getContext(), list);
                    listView.setVisibility(View.VISIBLE);
                    no_thesis_result.setVisibility(View.GONE);
                    listView.setAdapter(adapter);
                    time = new Date().getTime() - time;
//                    Toast.makeText(getContext(), "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
                } else {
                    registerThesisLayout.setVisibility(View.VISIBLE);
                    no_thesis_result.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    no_thesis_result.setText("No Thesis Found");
                }
            } else {
                Log.e("ERROR", "error getting thesis: ", task.getException());

            }
            Utility.cancelDialog();
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        thesisChange = (ThesisChange) activity;
    }


}
