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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Staff extends AppCompatActivity implements StaffInterface{

    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FrameLayout frameLayout;
    String uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> info;
    View headerView;
    TextView headerText;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        uid = getIntent().getExtras().getString("uid");
        toolbar = findViewById(R.id.staff_toolbar);
        drawerLayout = findViewById(R.id.staff_drawer_layout);
        navigationView = findViewById(R.id.staff_navigation_view);
        headerView = navigationView.getHeaderView(0);
        headerText = headerView.findViewById(R.id.header_text);
        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        frameLayout = findViewById(R.id.staff_framelayout);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id){
                case R.id.staff_dashboard:
                    toolbar.setTitle("Dashboard");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffDashboardFragment(), null).commit();
                    return true;
                case R.id.staff_home:
                    toolbar.setTitle("Staff profile");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffProfileFragment(), null).commit();
                    Toast.makeText(Staff.this, "Home", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.staff_view:
                    toolbar.setTitle("Student info");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffViewFragment(), null).commit();
                    Toast.makeText(Staff.this, "View", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.staff_send:
                    toolbar.setTitle("Broadcast message");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffSendFragment(), null).commit();
                    Toast.makeText(Staff.this, "Send Broadcast message to your student", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    logout();
                    return true;
            }
        });
        if (savedInstanceState == null) {
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
        data.put("which", "staff");
        Log.e("getStaffDocument", data.toString());
        Utility.showDialog(Staff.this);
        getUserDocument(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.cancelDialog();
                info = task.getResult();
                headerText.setText((String) info.get("name"));
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffDashboardFragment(), null).commit();
                Log.d("GET TAG", "DocumentSnapshot data: " + task.getResult());
                time = new Date().getTime() - time;
//                Toast.makeText(Staff.this, "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
            }else {
                Exception e = task.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                }
                Log.d("GET TAG", "No such document");
                Utility.cancelDialog();
            }
        });
    }

    @Override
    public void getStaffInfo(TextView name, TextView staff_id, TextView office_address, TextView dept) {
        name.setText((CharSequence) info.get("name"));
        staff_id.setText((CharSequence) info.get("staff_id"));
        office_address.setText((CharSequence) info.get("office_address"));
        dept.setText((CharSequence) info.get("dept"));
    }

    @Override
    public void navigate(String where) {
        if (where.equalsIgnoreCase("send")){
            toolbar.setTitle("Broadcast message");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffSendFragment(), null).commit();
        } else if (where.equalsIgnoreCase("view")) {
            toolbar.setTitle("Student info");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffViewFragment(), null).commit();
        } else {
            toolbar.setTitle("Staff profile");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffProfileFragment(), null).commit();
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
    public void onBackPressed() {

    }
}