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

public class StudentRegisterFragment extends Fragment {

    TextInputEditText matric, name, email, phone, address, program, dob, password, award;
    Button registerBtn;
    long time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.student_register_fragment_layout, container, false);
         registerBtn = view.findViewById(R.id.register_btn);
         matric = view.findViewById(R.id.matric_text);
         name = view.findViewById(R.id.name_text);
         email = view.findViewById(R.id.email_text);
         phone = view.findViewById(R.id.phone_text);
         address = view.findViewById(R.id.address_text);
         program = view.findViewById(R.id.program_text);
         dob = view.findViewById(R.id.dob_text);
         password = view.findViewById(R.id.password_text);
         award = view.findViewById(R.id.award_text);

         registerBtn.setOnClickListener(v -> {
             String matric_no = matric.getText().toString().trim(),
                     fullname = name.getText().toString().trim(),
                     email_text = email.getText().toString().trim(),
                     phone_no = phone.getText().toString().trim(),
                     address_text = address.getText().toString().trim(),
                     program_text = program.getText().toString().trim(),
                     dob_text = dob.getText().toString().trim(),
                     pass_text = password.getText().toString().trim(),
                     award_text = award.getText().toString().trim();

             int okay = 1;
             if (matric_no.length() != 11) {
                 okay = -1;
                 matric.setError("cannot be less or greater than 11");
             }
             if (dob_text.length() != 10) {
                 okay = -1;
                 dob.setError("must be in the format MM/DD/YYY");
             }
             if (award_text.length() != 4) {
                 okay = -1;
                 award.setError("cannot be less or greater than 4");
             }
             if (fullname.length() > 40 || fullname.length() < 10) {
                 okay = -1;
                 name.setError("cannot be empty or greater than 40");
             }
             if (email_text.length() > 40 || email_text.length() < 10) {
                 okay = -1;
                 email.setError("cannot be empty or greater than 40 characters");
             }
             if (program_text.length() > 40 || program_text.length() < 5) {
                 okay = -1;
                 program.setError("cannot be empty or greater than 40");
             }
             if (address_text.length() > 40 || address_text.length() < 3) {
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

             if (okay == 1) {
                 time = new Date().getTime();
                 register(pass_text, email_text, matric_no, fullname, phone_no, address_text, program_text, dob_text, award_text);
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

    private void register(String pass_text, String email_text, String matric_no, String fullname, String phone_no, String address_text,
                          String program_text, String dob_text, String award_text) {
        if(!email_text.isEmpty() && !pass_text.isEmpty() && !matric_no.isEmpty() && !fullname.isEmpty()){


            Map<String, Object> data = new HashMap<>();
            data.put("matric", matric_no.toUpperCase());
            data.put("name", fullname);
            data.put("email", email_text);
            data.put("phone", phone_no);
            data.put("address", address_text);
            data.put("program", program_text);
            data.put("dob", dob_text);
            data.put("year_of_award", award_text);
            data.put("which", "student");


            FirebaseAuth auth = FirebaseAuth.getInstance();
            Utility.showDialog(getContext());
            auth.createUserWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    data.put("uid", user.getUid());
                    createUserRecord(data).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Utility.cancelDialog();
                            Log.d("studentRegistration", task1.getResult());
                            Intent intent = new Intent(getContext(), Student.class);
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
                            Utility.cancelDialog();
                            // If function fails, display a message to the user.
                            Log.w("student", "createUserRecord:failure", task1.getException());
                            Log.e("student error", data.toString());
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

        } else {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
