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
import android.widget.ImageView;
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

public class Admin extends AppCompatActivity implements AdminInterface, ThesisChange{

    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    String uid, staff_id;
    Map<String, Object> info;
    View headerView;
    TextView headerText;
    long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        uid = getIntent().getExtras().getString("uid");
        toolbar = findViewById(R.id.admin_toolbar);
        toolbar.setTitle("Admin");
        drawerLayout = findViewById(R.id.admin_drawer_layout);
        navigationView = findViewById(R.id.admin_navigation_view);
        headerView = navigationView.getHeaderView(0);
        headerText = headerView.findViewById(R.id.header_text);
        frameLayout = findViewById(R.id.admin_fragment);
        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        fragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id){
                case R.id.admin_dasboard:
                    toolbar.setTitle("Dashboard");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new AdminDashboardFragment(), null).commit();
                    Toast.makeText(Admin.this, "Admin dashboard", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.admin_home:
                    toolbar.setTitle("Admin profile");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new AdminProfileFragment(), null).commit();
                    Toast.makeText(Admin.this, "Admin profile", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.admin_delete:
                    toolbar.setTitle("Remove User");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new RemoveUserFragment(), null).commit();
                    Toast.makeText(Admin.this, "Remove user", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.view_student:
                    toolbar.setTitle("View Student");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffViewFragment(), null).commit();
                    Toast.makeText(Admin.this, "View student info", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.admin_thesis:
                    toolbar.setTitle("Thesis Registration");
                    fragmentManager.beginTransaction().replace(frameLayout.getId(), new ThesisFragment(uid, "admin", staff_id), null).commit();
                    Toast.makeText(Admin.this, "Register Thesis", Toast.LENGTH_SHORT).show();
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
        data.put("which", "admin");
        Log.e("getAdminDocument", data.toString());
        Utility.showDialog(Admin.this);
        getUserDocument(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.cancelDialog();
                info = task.getResult();
                staff_id = (String) info.get("staff_id");
                headerText.setText((String) info.get("name"));
                fragmentManager.beginTransaction().replace(frameLayout.getId(), new AdminDashboardFragment(), null).commit();
                Log.d("GET TAG", "DocumentSnapshot data: " + task.getResult());
                time = new Date().getTime() - time;
//                Toast.makeText(Admin.this, "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();
            }else {
                    Log.e("ERROR", "error getting admin document", task.getException());
                Utility.cancelDialog();
            }
        });
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
    public void getAdminData(TextView name, TextView id, TextView address) {
        name.setText((CharSequence) info.get("name"));
        id.setText((CharSequence) info.get("staff_id"));
        address.setText((CharSequence) info.get("office_address"));
    }

    @Override
    public void navigate(String which) {
        if (which.equals("profile")) {
            toolbar.setTitle("Admin profile");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new AdminProfileFragment(), null).commit();
            Toast.makeText(Admin.this, "Admin profile", Toast.LENGTH_SHORT).show();
        } else if (which.equals("remove")) {
            toolbar.setTitle("Remove User");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new RemoveUserFragment(), null).commit();
            Toast.makeText(Admin.this, "Remove user", Toast.LENGTH_SHORT).show();
        } else if (which.equals("register")) {
            toolbar.setTitle("Thesis Registration");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new ThesisFragment(uid, "admin", staff_id), null).commit();
            Toast.makeText(Admin.this, "Register Thesis", Toast.LENGTH_SHORT).show();
        } else {
            toolbar.setTitle("View Student");
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new StaffViewFragment(), null).commit();
            Toast.makeText(Admin.this, "View student info", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void changeTo(String where) {
        if (where.equals("register")) {
            fragmentManager.beginTransaction().replace(frameLayout.getId(), new RegisterThesisFragment(uid, "admin")).addToBackStack("register").commit();
        }
    }

    @Override
    public void onBackPressed() {

    }
}