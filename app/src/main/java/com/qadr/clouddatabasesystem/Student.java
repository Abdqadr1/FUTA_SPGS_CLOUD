package com.qadr.clouddatabasesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Student extends AppCompatActivity implements StudentInterface, ThesisChange{
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FrameLayout frameLayout;
    String uid, matric, name;
    Map<String, Object> info;
    View headerView;
    TextView headerText;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile);
        uid = getIntent().getExtras().getString("uid");
        toolbar = findViewById(R.id.student_toolbar);
        drawerLayout = findViewById(R.id.student_drawer_layout);
        navigationView = findViewById(R.id.student_navigation_view);
        headerView = navigationView.getHeaderView(0);
        headerText = headerView.findViewById(R.id.header_text);
        frameLayout = findViewById(R.id.student_framelayout);
        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id){
                case R.id.student_dashboard:
                    toolbar.setTitle("Dashboard");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StudentDashboardFragment(), null).commit();
                    return true;
                case R.id.student_thesis:
                    toolbar.setTitle("Student Thesis");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new ThesisFragment(uid, "student", matric), null).commit();
                    Toast.makeText(Student.this, "Thesis", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.student_result:
                    toolbar.setTitle("Student Result");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new Result(), null).commit();
                    Toast.makeText(Student.this, "Result", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.student_info:
                    toolbar.setTitle("Student Bio data");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new BioDataFragment(), null).commit();
                    Toast.makeText(Student.this, "Bio data", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.student_payment:
                    toolbar.setTitle("Payments");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new Payments(), null).commit();
                    Toast.makeText(Student.this, "Payment", Toast.LENGTH_SHORT).show();
                default:
                    logout();
                    return true;
            }
        });
        if(savedInstanceState == null){
            time = new Date().getTime();
            getDocument();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void getDocument(){
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("which", "student");
        Log.e("getDocument", data.toString());
        Utility.showDialog(Student.this);
        getUserDocument(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                info = task.getResult();
                matric = (String) info.get("matric");
                name = (String) info.get("name");
                headerText.setText(name);
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new StudentDashboardFragment(), null).commit();
                time = new Date().getTime() - time;
                Toast.makeText(Student.this, "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
            }else {
                Log.e("ERROR", "error getting student document", task.getException());
            }
            Utility.cancelDialog();
        });
    }

    public void getBioData(TextView name, TextView matric, TextView email, TextView phone,
                           TextView dob, TextView address, TextView program, TextView year_of_award){

        Log.e("TAG", "getBioData: yeah ");
        name.setText((CharSequence) info.get("name"));
        matric.setText((CharSequence) info.get("matric"));
        email.setText((CharSequence) info.get("email"));
        phone.setText((CharSequence) info.get("phone"));
        dob.setText((CharSequence) info.get("dob"));
        address.setText((CharSequence) info.get("address"));
        program.setText((CharSequence) info.get("program"));
        year_of_award.setText((CharSequence) info.get("year_of_award"));
    }
    public void getResults(){

    }
    public void getPayments(){

    }
    public void getThesis(TextView author, TextView title, TextView supervisor, TextView content){
//        author.setText();
    }

    @Override
    public void navigate(String where) {
        switch (where){
            case "results":
                toolbar.setTitle("Student Result");
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new Result(), null).commit();
                break;
            case "payments":
                toolbar.setTitle("Payments");
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new Payments(), null).commit();
                break;
            case "profile":
                toolbar.setTitle("Student Bio data");
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new BioDataFragment(), null).commit();
                break;
            case "thesis":
                toolbar.setTitle("Student Thesis");
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new ThesisFragment(uid, "student", matric), null).commit();
                break;
        }
    }

    private Task<Map<String, Object>> getUserDocument (Map<String, Object> data){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions.getHttpsCallable("getUserDocument").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    Map<String, Object> returnData = (Map<String, Object>) task.getResult().getData();
                    return returnData;
                });
    }

    @Override
    public void changeTo(String where) {
        if (where.equals("register")) {
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new RegisterThesisFragment(uid, "student", name, matric)).addToBackStack("register").commit();
        }
    }

    @Override
    public void onBackPressed() {

    }
}