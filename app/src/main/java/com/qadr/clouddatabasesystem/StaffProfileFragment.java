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

import org.w3c.dom.Text;

public class StaffProfileFragment extends Fragment {
    TextView name, staff_id, office_address, dept;

    StaffInterface staffInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_profile_layout, container, false);
        name = view.findViewById(R.id.staff_name_text);
        staff_id = view.findViewById(R.id.staff_id_text);
        office_address = view.findViewById(R.id.staff_office_text);
        dept = view.findViewById(R.id.staff_dept_text);
        staffInterface.getStaffInfo(name, staff_id, office_address, dept);
         return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        staffInterface = (StaffInterface) activity;
    }
}
