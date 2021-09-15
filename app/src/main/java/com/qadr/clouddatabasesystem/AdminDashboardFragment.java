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

public class AdminDashboardFragment extends Fragment {
    AdminInterface adminInterface;
    ConstraintLayout profile, view_layout, remove, register;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dashboard, container, false);
        profile = view.findViewById(R.id.account_layout);
        view_layout = view.findViewById(R.id.view_layout);
        remove = view.findViewById(R.id.remove_layout);
        register = view.findViewById(R.id.register_layout);
        profile.setOnClickListener(v -> adminInterface.navigate("profile"));
        view_layout.setOnClickListener(v -> adminInterface.navigate("view"));
        remove.setOnClickListener(v -> adminInterface.navigate("remove"));
        register.setOnClickListener(v ->adminInterface.navigate("register"));
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        adminInterface = (AdminInterface) activity;
    }
}
