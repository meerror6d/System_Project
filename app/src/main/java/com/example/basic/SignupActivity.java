package com.example.basic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText mUsername, unique, mEmail, mPassword, mNid;
    Button mRegisterButton;
    TextView mLogin;
    ProgressBar mProgressBar;
    boolean passwordVisibility;
    CheckBox mAdminCheckBox;
    CheckBox mUserCheckBox;
    long a = 0, b = 0;
    private int uniqueIdCounter = 1;


    DatabaseReference userReference;
    DatabaseReference adminReference;
    DatabaseReference findReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mUsername = findViewById(R.id.username);
        unique = findViewById(R.id.username_unique);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mNid = findViewById(R.id.nid);
        mRegisterButton = findViewById(R.id.Signup);
        mLogin = findViewById(R.id.press);
        mProgressBar = findViewById(R.id.progressbar);
        mAdminCheckBox = findViewById(R.id.checkadmin);
        mUserCheckBox = findViewById(R.id.checkuser);

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

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String enteredUsername = charSequence.toString();
                String uniquename = generateUniqueUsername(enteredUsername, String.valueOf(mEmail)) ;
                unique.setText(uniquename);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });


        mLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= mPassword.getRight() - mPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = mPassword.getSelectionEnd();
                        if (passwordVisibility) {
                            mPassword.setTransformationMethod(new PasswordTransformationMethod());
                            passwordVisibility = false;
                        } else {
                            mPassword.setTransformationMethod(null);
                            passwordVisibility = true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString();
                final String nid = mNid.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("E-mail is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(nid)) {
                    mPassword.setError("NID is required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password must be > than 6 characters");
                    return;
                }

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Enter a valid email address");
                    return;
                }

                // Validate NID format (10 digits)
                if (nid.length() != 10) {
                    mNid.setError("NID must be 10 digits");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                if (mAdminCheckBox.isChecked() && (email.endsWith("@stud.kuet.ac.bd") || email.endsWith("@cse.kuet.ac.bd"))) {
                    // Create a separate node in the database for admin users
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    } else {

                        adminReference = FirebaseDatabase.getInstance().getReference("Admin");

                        // Check if the email or NID already exists
                        adminReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(SignupActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                } else {
                                    // Check if the NID already exists
                                    adminReference.orderByChild("nid").equalTo(nid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Toast.makeText(SignupActivity.this, "NID already registered", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                            } else {
                                                // Continue with the registration process
                                                String adminId = adminReference.push().getKey();

                                                String uniquename = generateUniqueUsername(username,email);
                                                Admin admin = new Admin(adminId, username, email, password, nid);

                                                adminReference.child(username).setValue(admin);
                                                adminReference.child(username).child("uniquename").setValue(uniquename);

                                                Toast.makeText(SignupActivity.this, "Admin registration successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                //generateAndCheckUniqueId(username, email, nid, password, true);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(SignupActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SignupActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (mUserCheckBox.isChecked()) {
                    // Regular user registration
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    } else {

                        userReference = FirebaseDatabase.getInstance().getReference("User");
                        // Check if the email or NID already exists
                        userReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(SignupActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                } else {
                                    // Check if the NID already exists
                                    userReference.orderByChild("nid").equalTo(nid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Toast.makeText(SignupActivity.this, "NID already registered", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                            } else {
                                                // Continue with the registration process
                                                String userId = userReference.push().getKey();

                                                String  uniquename = generateUniqueUsername(username, email) ;
                                                //Toast.makeText(SignupActivity.this, "unique name "+uniquename, Toast.LENGTH_SHORT).show();

                                                User user = new User(userId, username, email, password, nid);

                                                userReference.child(uniquename).setValue(user);
                                                userReference.child(uniquename).child("uniquename").setValue(uniquename);

                                                Toast.makeText(SignupActivity.this, "User registration successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                //generateAndCheckUniqueId(username, email, nid, password, false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(SignupActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SignupActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    Toast.makeText(SignupActivity.this, "Please select user or admin", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private String generateUniqueUsername(String username, String email) {
        StringBuilder uniqueId = new StringBuilder();

        // Extract odd-positioned letters from the username
        for (int i = 0; i < username.length(); i += 2) {
            uniqueId.append(username.charAt(i));
        }
        String generatedUniqueName = uniqueId.toString();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");

        String userId = userReference.push().getKey();

        // Get the last 4 digits from the generated key
        if (userId != null && userId.length() > 4) {
            userId = userId.substring(userId.length() - 4);
        }
        generatedUniqueName += "-" + userId;

        if (isUniqueNameExists(generatedUniqueName)) {
            generatedUniqueName = modifyGeneratedUniqueName(generatedUniqueName);
        }
        return generatedUniqueName;
    }

    // Method to check if a unique name already exists in the database
    private boolean isUniqueNameExists(String uniqueName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        final boolean[] isExists = {false};
        reference.child(uniqueName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isExists[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

        return isExists[0];
    }

    private String modifyGeneratedUniqueName(String existingUniqueName) {
        int lastIndex = existingUniqueName.length() - 1;
        char lastChar = existingUniqueName.charAt(lastIndex);

        if (Character.isDigit(lastChar)) {
            // If the last character is a digit, increment it by 1
            int lastNumber = Character.getNumericValue(lastChar);
            lastNumber++;

            return existingUniqueName.substring(0, lastIndex) + lastNumber;
        } else {
            // If the last character is not a digit, append "_1" to the unique name
            return existingUniqueName + "1";
        }
    }

}