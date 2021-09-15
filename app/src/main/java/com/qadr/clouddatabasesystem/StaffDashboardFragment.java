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

public class StaffDashboardFragment extends Fragment {
    ConstraintLayout profile, send, view_layout;
    StaffInterface staffInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_dashboard, container, false);
        profile = view.findViewById(R.id.account_layout);
        send = view.findViewById(R.id.send_layout);
        view_layout = view.findViewById(R.id.view_layout);
        profile.setOnClickListener(v -> {
            staffInterface.navigate("profile");
        });
        send.setOnClickListener(v -> {
            staffInterface.navigate("send");
        });
        view_layout.setOnClickListener(v -> {
            staffInterface.navigate("view");
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        staffInterface = (StaffInterface) activity;
    }
}
