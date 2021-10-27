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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RemoveUserFragment extends Fragment {
    TextView user_name, user_id, user_level, no_result, user_address;
    TextInputEditText search_editText;
    TextInputLayout search_layout;
    Button removeBtn, search_btn;
    LinearLayout info_layout;
    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    FirebaseFunctions functions = FirebaseFunctions.getInstance();
    Map<String, Object> user_info;
    String uid;
    private long time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remove_user_layout, container, false);
        info_layout = view.findViewById(R.id.info_layout);
        removeBtn = view.findViewById(R.id.remove_btn);
        search_layout = view.findViewById(R.id.search_layout);
        search_editText  = view.findViewById(R.id.search_text);
        user_id = view.findViewById(R.id.user_info_id);
        user_name = view.findViewById(R.id.user_info_name);
        user_level = view.findViewById(R.id.user_level_text);
        user_address = view.findViewById(R.id.user_info_address);
        no_result = view.findViewById(R.id.no_result);
        search_btn = view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(v -> {
            String text = search_editText.getText().toString().trim();
            if(!text.isEmpty()){
                if (text.length() < 3 || text.length() > 11) {
                    search_layout.setError("user id cannot be < than 3 or > 11 characters");
                }else {
                    v.setEnabled(false);
                    removeBtn.setEnabled(false);
                    String finalText = text.toUpperCase();
                    Utility.showDialog(getContext());
                    getUser(finalText);
                }
            } else {
                search_layout.setError("Enter matric or staff id");
            }
        });

        removeBtn.setOnClickListener(v -> {
            if (!uid.isEmpty()){
                time = new Date().getTime();
                v.setEnabled(false);
                Map<String, Object> data = new HashMap<>();
                data.put("uid", uid);
                Utility.showDialog(getContext());
                removeUser(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        user_info = null;
                        uid = null;
                        Toast.makeText(getContext(), (task.getResult()), Toast.LENGTH_SHORT).show();
                        no_result.setVisibility(View.GONE);
                        info_layout.setVisibility(View.GONE);
                        time = new Date().getTime() - time;
                        Toast.makeText(getContext(), "User removed successfully", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getContext(), "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("ERROR", "error deleting user: ", task.getException());
                    }
                    v.setEnabled(true);
                    v.setVisibility(View.GONE);
                    Utility.cancelDialog();
                });
            }
        });

        return view;
    }

    private void getUser(String finalText){
        user_info = null;
        uid = null;
        db.collection("staffs").whereEqualTo("staff_id", finalText).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    user_info = document.getData();
                    uid = document.getId();
                    break;
                }
                if (uid == null){
                    db.collection("students").whereEqualTo("matric", finalText).limit(1).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for (QueryDocumentSnapshot document1 : Objects.requireNonNull(task1.getResult())){
                                user_info = document1.getData();
                                uid = document1.getId();
                                break;
                            }
                            if(uid != null){
                                Map<String, Object> dataToSend = new HashMap<>();
                                dataToSend.put("which", "student");
                                dataToSend.put("uid", uid);
                                getUserDocument(dataToSend).addOnCompleteListener(task2 -> {
                                    if(task2.isSuccessful()){
                                        user_info = task2.getResult();
                                        getInfo(user_info, "student");
                                    }else {
                                        Log.e("ERROR","error decrypting data", task2.getException() );
                                    }
                                });
                            } else {
                                info_layout.setVisibility(View.GONE);
                                removeBtn.setVisibility(View.GONE);
                                no_result.setVisibility(View.VISIBLE);
                                no_result.setText("No user found with that id");
                            }
                            search_btn.setEnabled(true);
                            removeBtn.setEnabled(true);
                            Utility.cancelDialog();
                        } else {
                            Log.d("finding user", "Error getting documents: ", task1.getException());
                        }
                    });
                } else {
                        Map<String, Object> dataToSend = new HashMap<>();
                        dataToSend.put("which", "staff");
                        dataToSend.put("uid", uid);
                        getUserDocument(dataToSend).addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()){
                                user_info = task2.getResult();
                                getInfo(user_info, "staff");
                            }else {
                                Log.e("ERROR","error decrypting staff data", task2.getException() );
                            }
                        });

                }

            } else {
                Log.e("Finding User", "error getting documents: ", task.getException());
            }
            search_btn.setEnabled(true);
            removeBtn.setEnabled(true);
            Utility.cancelDialog();
        });

    }
    private void getInfo(Map<String, Object> user_info, String which){
        Log.d("record", user_info.toString());
        no_result.setVisibility(View.GONE);
        info_layout.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.VISIBLE);
        user_name.setText((CharSequence) user_info.get("name"));
        if (which.equals("student")) {
            user_address.setText((CharSequence) user_info.get("address"));
            user_id.setText((CharSequence) user_info.get("matric"));
        } else {
            user_address.setText((CharSequence) user_info.get("office_address"));
            user_id.setText((CharSequence) user_info.get("staff_id"));
        }
        user_level.setText(which);
    }

    private Task<Map<String, Object>> getUserDocument(Map<String, Object> data){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions.getHttpsCallable("getUserDocument").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    Map<String, Object> returnData = (Map<String, Object>) task.getResult().getData();
                    return returnData;
                });
    }
    private Task<String> removeUser(Map<String, Object> data){
        return functions.getHttpsCallable("deleteUser").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    Map<String, Object> returnData = (Map<String, Object>) task.getResult().getData();
                    String message = (String) returnData.get("message");
                    return message;
                });
    }
}
