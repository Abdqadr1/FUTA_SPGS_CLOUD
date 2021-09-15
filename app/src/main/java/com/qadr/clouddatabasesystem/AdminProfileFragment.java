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

public class AdminProfileFragment extends Fragment {
    TextView name, staff_id, office_address, dept;
    AdminInterface adminInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_layout, container, false);
        name = view.findViewById(R.id.admin_name_text);
        staff_id = view.findViewById(R.id.admin_id_text);
        office_address = view.findViewById(R.id.admin_office_text);
        adminInterface.getAdminData(name, staff_id, office_address);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        adminInterface = (AdminInterface) activity;
    }
}
