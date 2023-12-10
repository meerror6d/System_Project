package com.example.basic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLogin;
    TextView mCreateLogin;
    ProgressBar mProgressBar;
    CheckBox mAdminCheckBox;
    CheckBox mUserCheckBox;
    boolean passwordVisibility;

    DatabaseReference userReference;    // Reference to the "User" node in the Realtime Database
    DatabaseReference adminReference;    // Reference to the "Admin" node in the Realtime Database


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        mEmail = findViewById(R.id.email2);
        mPassword = findViewById(R.id.password2);
        mProgressBar = findViewById(R.id.progressbar2);
        mLogin = findViewById(R.id.Login);
        mCreateLogin = findViewById(R.id.press2);
        mAdminCheckBox = findViewById(R.id.checkadmin2);
        mUserCheckBox = findViewById(R.id.checkuser2);

        // Initialize the DatabaseReference
        userReference = FirebaseDatabase.getInstance().getReference("User");
        adminReference = FirebaseDatabase.getInstance().getReference("Admin");

        // Add a listener to the admin checkbox
        mAdminCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Admin checkbox is checked, uncheck the user checkbox
                    mUserCheckBox.setChecked(false);
                }
            }
        });

        // Add a listener to the user checkbox
        mUserCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // User checkbox is checked, uncheck the admin checkbox
                    mAdminCheckBox.setChecked(false);
                }
            }
        });

        mCreateLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("E-mail is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must be > than 6 characters");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                if (mAdminCheckBox.isChecked()) {
                    // Check if the entered email is an admin's email (e.g., ends with @stud.kuet.ac.bd or @cse.kuet.ac.bd)
                    if (email.endsWith("@stud.kuet.ac.bd") || email.endsWith("@cse.kuet.ac.bd")) {

                        adminReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    DataSnapshot adminSnapshot = snapshot.getChildren().iterator().next();
                                    String adminPasswordFromDatabase = adminSnapshot.child("passwordad").getValue(String.class);

                                    if (adminPasswordFromDatabase != null && adminPasswordFromDatabase.equals(password)) {
                                        // Admin login successful
                                        Toast.makeText(LoginActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, datashow_admin.class));
                                        saveEmailToSharedPreferences(email);
                                    } else {
                                        // Invalid admin password
                                        Toast.makeText(LoginActivity.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    // Admin email not found in the database
                                    Toast.makeText(LoginActivity.this, "Admin not found", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle database error
                                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                } else {

                    userReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                                if (userSnapshot.hasChild("password")) {
                                    String userPassword = userSnapshot.child("password").getValue(String.class);

                                    if (userPassword != null && userPassword.equals(password)) {
                                        // User login successful
                                        Toast.makeText(LoginActivity.this, "User login successful", Toast.LENGTH_SHORT).show();


                                        Intent intent = new Intent(LoginActivity.this, datashow_user.class);
                                        startActivity(intent);

                                        // Assuming you have the user's entered email in a variable called enteredEmail
                                        saveEmailToSharedPreferences(email);

                                    } else {
                                        // Invalid user password
                                        Toast.makeText(LoginActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    // "password" field not found in the user data
                                    Toast.makeText(LoginActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            } else {
                                // User email not found in the database
                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                            Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= mPassword.getRight() - mPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = mPassword.getSelectionEnd();
                        if (passwordVisibility) {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password_icon, 0, R.drawable.eye_icon, 0);
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisibility = false;
                        } else {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_icon, 0, R.drawable.password_icon, 0);
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisibility = true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void saveEmailToSharedPreferences(String email) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("current_email", email);
        editor.apply();
    }

}