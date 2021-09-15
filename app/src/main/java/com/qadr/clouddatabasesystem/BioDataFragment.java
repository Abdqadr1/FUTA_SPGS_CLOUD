package com.qadr.clouddatabasesystem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BioDataFragment extends Fragment {
    TextView name, matric, email, phone, dob, address, program, year_of_award;
    StudentInterface studentInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.biodata_layout, container, false);
        name = view.findViewById(R.id.student_name_text);
        matric = view.findViewById(R.id.student_matric_text);
        email = view.findViewById(R.id.student_email_text);
        phone = view.findViewById(R.id.student_phone_text);
        dob = view.findViewById(R.id.student_dob_text);
        address = view.findViewById(R.id.student_address_text);
        program = view.findViewById(R.id.student_program_text);
        year_of_award = view.findViewById(R.id.student_award_text);
        studentInterface.getBioData(name, matric, email, phone, dob, address, program, year_of_award);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        studentInterface = (StudentInterface) activity;
    }
}
