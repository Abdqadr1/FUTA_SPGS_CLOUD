package com.qadr.clouddatabasesystem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StaffViewFragment extends Fragment {
    TextView name, dept, matric, thesis_title, no_result, result;

    TextInputEditText search_text;
    Button button, downloadBtn;
    TextInputLayout search_layout;
    Map<String, Object> info, thesis;
    LinearLayout result_layout, thesis_linear;
    String downloadURL;
    long time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_view_layout, container, false);
        result_layout = view.findViewById(R.id.result_layout);
        thesis_linear = view.findViewById(R.id.thesis_linear);
        name = view.findViewById(R.id.student_info_name);
        dept = view.findViewById(R.id.student_dept_text);
        matric = view.findViewById(R.id.student_info_matric);
        thesis_title = view.findViewById(R.id.student_info_thesis);
        no_result = view.findViewById(R.id.no_result);
//        result = view.findViewById(R.id.student_info_result);
        search_layout = view.findViewById(R.id.search_layout);
        search_text = view.findViewById(R.id.search_text);
        button = view.findViewById(R.id.search_btn);
        downloadBtn = view.findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(v -> {
            if (thesis != null) {
                // TODO: 8/26/2021 download thesis
                downloadURL = "thesis_documents/"+downloadURL;
                Log.d("DOWNLOAD URL", downloadURL);
                Utility.downloadFile(getContext(), downloadURL);
            }
        });
        button.setOnClickListener(v -> {
            v.setEnabled(false);
            String text = search_text.getText().toString().trim();
            if (!text.isEmpty()){
                if (text.length() != 11) {
                    search_text.setError("Matric number must be 11 characters");
                } else  {
                    getStudent(text.toUpperCase());
                }
            }else{
                search_layout.setError("Enter student's matric no");
            }
        });
//        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Log.e("user", user.toString() );
        return view;
    }

    private void getStudent(String text){
        Map<String, Object> data = new HashMap<>();
        data.put("matric", text);
        time = new Date().getTime();
        Utility.showDialog(getContext());
        getStudentDocument(data);
    }
    private void getStudentDocument (Map<String, Object> data){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("students").whereEqualTo("matric",data.get("matric")).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("GETTING STUDENT", document.getId() + " => " + document.getData());
                            data.put("uid", document.getId());
                            data.put("which", "student");
                            break;
                        }
                        FirebaseFunctions functions = FirebaseFunctions.getInstance();
                        if(data.get("uid") != null){
                            functions.getHttpsCallable("getUserDocument").call(data)
                                    .continueWith(task1 -> {

                                        Map<String, Object> returnData = (Map<String, Object>) task1.getResult().getData();
                                        return returnData;
                                    }).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    info = task1.getResult();
                                    no_result.setVisibility(View.GONE);
                                    result_layout.setVisibility(View.VISIBLE);
                                    name.setText((CharSequence) info.get("name"));
                                    dept.setText((CharSequence) info.get("program"));
                                    matric.setText((CharSequence) info.get("matric"));
                                    search_text.setText("");
                                    Log.d("student_info", info.toString());
                                    data.put("uid", data.get("matric"));
                                    Utility.getThesis(data).addOnCompleteListener(task2 -> {
                                       if(task2.isSuccessful()){
                                           ArrayList<Map<String, Object>> thesis_list = (ArrayList<Map<String, Object>>) task2.getResult().get("thesis");
                                           if (!thesis_list.isEmpty()) {
                                               thesis_linear.setVisibility(View.VISIBLE);
                                               thesis = thesis_list.get(0);
                                               thesis_title.setText((CharSequence) thesis.get("title"));
                                               downloadURL = (String) thesis.get("uri");
                                           } else {
                                               thesis_linear.setVisibility(View.GONE);
                                           }

                                           Log.d("GETTING_THESIS", task2.getResult().toString());
                                       } else {
                                           Log.e("ERROR", "error getting thesis: ", task2.getException());
                                       }
                                        time = new Date().getTime() - time;
                                        Toast.makeText(getContext(), "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
                                    });
                                }else {
                                    Log.e("Error", "getStudent: ", task.getException());
                                    result_layout.setVisibility(View.GONE);
                                    no_result.setVisibility(View.VISIBLE);
                                    no_result.setText(data.get("matric") + " " + "is not registered");
                                }
                                button.setEnabled(true);
                                Utility.cancelDialog();
                            });
                        }else {
                            Log.e("NULL", "getUserDocument: " + "document not found");
                        }

                    } else {
                        Log.d("GETTING STUDENT", "Error getting documents: ", task.getException());
                    }
                });
    }
}
