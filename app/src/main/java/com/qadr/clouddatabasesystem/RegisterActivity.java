package com.qadr.clouddatabasesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button student , admin, staff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_summary);
        student = findViewById(R.id.student);
        admin = findViewById(R.id.admin);
        staff = findViewById(R.id.staff);
        student.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Register.class);
            intent.putExtra("which", "student");
            startActivity(intent);
        });
        staff.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Register.class);
            intent.putExtra("which", "staff");
            startActivity(intent);
        });
        admin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Register.class);
            intent.putExtra("which", "admin");
            startActivity(intent);
        });
    }
}