package com.qadr.clouddatabasesystem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class StudentDashboardFragment extends Fragment {
    private StudentInterface studentInterface;
    ConstraintLayout profile, results, thesis, payments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_dashboard, container, false);
        profile = view.findViewById(R.id.account_layout);
        results = view.findViewById(R.id.result_layout);
        thesis = view.findViewById(R.id.thesis_layout);
        payments = view.findViewById(R.id.payment_layout);
        profile.setOnClickListener(v -> {
            studentInterface.navigate("profile");
        });
        results.setOnClickListener(v -> {
            studentInterface.navigate("results");
        });
        thesis.setOnClickListener(v -> {
            studentInterface.navigate("thesis");
        });
        payments.setOnClickListener(v -> {
            studentInterface.navigate("payments");
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        studentInterface = (StudentInterface) activity;
    }
}
