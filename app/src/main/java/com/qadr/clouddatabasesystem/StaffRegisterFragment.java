package com.qadr.clouddatabasesystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StaffRegisterFragment extends Fragment {

    TextInputEditText staff_id, name, email, phone, title, address, password, dept;
    Button registerBtn;
    private long time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_register_fragment_layout, container, false);
        registerBtn = view.findViewById(R.id.register_btn);
        staff_id = view.findViewById(R.id.staff_id_text);
        name = view.findViewById(R.id.name_text);
        email = view.findViewById(R.id.email_text);
        phone = view.findViewById(R.id.phone_text);
        title = view.findViewById(R.id.title_text);
        address = view.findViewById(R.id.address_text);
        password = view.findViewById(R.id.password_text);
        dept = view.findViewById(R.id.dept_text);

        registerBtn.setOnClickListener(v -> {
            String id = staff_id.getText().toString().trim(),
                fullname = name.getText().toString().trim(),
                email_text = email.getText().toString().trim(),
                phone_no = phone.getText().toString().trim(),
                title_text = title.getText().toString().trim(),
                office_address = address.getText().toString().trim(),
                pass_text = password.getText().toString().trim(),
                dept_text = dept.getText().toString().trim();

            int okay = 1;
            if (id.length() > 7 || id.length() < 2) {
                okay = -1;
                staff_id.setError("cannot be empty or greater than 7");
            }
            if (fullname.length() > 40 || fullname.length() < 10) {
                okay = -1;
                name.setError("cannot be empty or greater than 40");
            }
            if (email_text.length() > 40 || email_text.length() < 10) {
                okay = -1;
                email.setError("cannot be empty or greater than 40 characters");
            }
            if (title_text.length() > 3 || title_text.length() < 2) {
                okay = -1;
                title.setError("cannot be empty or greater than 3 characters");
            }
            if (office_address.length() > 40 || office_address.length() < 3) {
                okay = -1;
                address.setError("cannot be empty or greater than 40 characters");
            }
            if (phone_no.length() != 11) {
                okay = -1;
                phone.setError("cannot be less or greater than 11 characters");
            }
            if (pass_text.length() < 6 || pass_text.length() > 20) {
                okay = -1;
                password.setError("cannot be < 6 and > 20 characters");
            }

            if (dept_text.length() < 3 || dept_text.length() > 20) {
                okay = -1;
                password.setError("cannot be < 3 and > 20 characters");
            }

            if (okay == 1) {
                time = new Date().getTime();
                register(pass_text, email_text, id, fullname, phone_no, title_text, office_address, dept_text);
            }

        });
        return view;
    }

    private Task<String> createUserRecord (Map<String, Object> data){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions.getHttpsCallable("createUserRecord").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    Map<String, Object> returnData = (Map<String, Object>) task.getResult().getData();
                    String message = (String) returnData.get("message");
                    return message;
                });
    }

    private void register(String pass_text, String email_text, String id, String fullname, String phone_no, String title_text,
                          String office_address, String dept_text){
        Map<String, Object> data = new HashMap<>();
        data.put("staff_id", id);
        data.put("name", fullname);
        data.put("email", email_text);
        data.put("phone_no", phone_no);
        data.put("title", title_text);
        data.put("office_address", office_address);
        data.put("dept", dept_text);
        data.put("which", "staff");

        if(!id.isEmpty() && !email_text.isEmpty() && !pass_text.isEmpty() && !fullname.isEmpty()){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Utility.showDialog(getContext());
            auth.createUserWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    //save data to FireStore
                    FirebaseUser user = auth.getCurrentUser();
                    data.put("uid", user.getUid());
                    createUserRecord(data).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Utility.cancelDialog();
                            Log.d("staffRegistration", task1.getResult());
                            Intent intent = new Intent(getContext(), Staff.class);
                            intent.putExtra("uid", user.getUid());
                            time = new Date().getTime() - time;
//                            Toast.makeText(getContext(), "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }else {
                            Exception e = task1.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            // If function fails, display a message to the user.
                            Utility.cancelDialog();
                            Log.w("staff", "createUserRecord:failure", task1.getException());
                            Log.e("staff error", data.toString() );
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Utility.cancelDialog();
                    Toast.makeText(getActivity(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }


}
