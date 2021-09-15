package com.qadr.clouddatabasesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.appbar.MaterialToolbar;

public class Register extends AppCompatActivity{

    FrameLayout frameLayout;
    public static FragmentManager fragmentManager;
    String which;
    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        which = getIntent().getExtras().getString("which");
        frameLayout = findViewById(R.id.register_framelayout);
        toolbar = findViewById(R.id.gen_toolbar);
        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null){
            if (which.equals("student")) {
                toolbar.setTitle("Student Registration");
                StudentRegisterFragment studentRegisterFragment = new StudentRegisterFragment();
                fragmentManager.beginTransaction().replace(frameLayout.getId(), studentRegisterFragment, null).commit();
            } else if (which.equals("staff")) {
                toolbar.setTitle("Staff Registration");
                StaffRegisterFragment staffRegisterFragment = new StaffRegisterFragment();
                fragmentManager.beginTransaction().replace(frameLayout.getId(), staffRegisterFragment, null).commit();
            } else {
                toolbar.setTitle("Admin Registration");
                AdminRegistrationFragment adminRegistrationFragment = new AdminRegistrationFragment();
                fragmentManager.beginTransaction().replace(frameLayout.getId(), adminRegistrationFragment, null).commit();
            }
        }
    }


}