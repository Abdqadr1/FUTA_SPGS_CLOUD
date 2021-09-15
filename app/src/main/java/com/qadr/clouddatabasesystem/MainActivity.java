package com.qadr.clouddatabasesystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button button;
    TextView signupBtn, error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button = findViewById(R.id.loginbtn);
        signupBtn = findViewById(R.id.changeBtn);
        error_msg = findViewById(R.id.error_msg);
        button.setOnClickListener(v -> {
            String email_text, pass_text;
            email_text = email.getText().toString().trim();
            pass_text = password.getText().toString().trim();
            int okay = 1;
            if (email_text.length() < 10 || email_text.length() > 40) {
                okay = -1;
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("email cannot be less than 10 or greater than 40 characters");
            }

            if (pass_text.length() < 6) {
                okay = -1;
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("password cannot be less than 6 characters");
            }
            if (okay == 1) {
                signIn(email_text, pass_text);
            }
        });

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
    private void signIn(String email_text, String pass_text){
        if (!email_text.isEmpty() && !pass_text.isEmpty()){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Utility.showDialog(MainActivity.this);
            auth.signInWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Authenticated", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    user.getIdToken(true).addOnSuccessListener(result -> {
                        Map<String, Object> claims = result.getClaims();
                        boolean hasAdmin = claims.containsKey("admin");
                        boolean hasStaff = claims.containsKey("staff");
                        Utility.cancelDialog();
                        if(hasAdmin){
                            Log.e("hasAdmin", "onCreate: ");
                            if((boolean)claims.get("admin")){
                                Intent intent = new Intent(MainActivity.this, Admin.class);
                                intent.putExtra("uid", user.getUid());
                                startActivity(intent);
                            }
                        }else if(hasStaff){
                            Log.e("hasStaff", "onCreate: ");
                            if((boolean)claims.get("staff")){
                                Intent intent = new Intent(MainActivity.this, Staff.class);
                                intent.putExtra("uid", user.getUid());
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(MainActivity.this, Student.class);
                            intent.putExtra("uid", user.getUid());
                            startActivity(intent);
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        error_msg.setText("Invalid email address");
                        error_msg.setVisibility(View.VISIBLE);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error_msg.setText("Invalid password");
                        error_msg.setVisibility(View.VISIBLE);
                    } catch (FirebaseNetworkException e) {
                        error_msg.setText("Failed to sign in, no internet connection");
                        error_msg.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.e("LOG_TAG", e.getMessage());
                        error_msg.setText(e.getMessage().substring(0, 20));
                        error_msg.setVisibility(View.VISIBLE);
                    }
                    Log.w("LOG_TAG", "signInWithEmail:failed", task.getException());
                    Utility.cancelDialog();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }
}