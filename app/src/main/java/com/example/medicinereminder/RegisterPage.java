package com.example.medicinereminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    private EditText nameEditText, contactNumberEditText, emailEditText, passwordEditText, confirmPasswordEditText,
            careTakerNameEditText, careTakerNumberEditText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("userData");

        nameEditText = findViewById(R.id.nameEditText);
        contactNumberEditText = findViewById(R.id.contactNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        careTakerNameEditText = findViewById(R.id.careTakerNameEditText);
        careTakerNumberEditText = findViewById(R.id.careTakerNumberEditText);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String name = nameEditText.getText().toString().trim();
        String contactNumber = contactNumberEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        final String careTakerName = careTakerNameEditText.getText().toString().trim();
        final String careTakerNumber = careTakerNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contactNumber) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(careTakerName) ||
                TextUtils.isEmpty(careTakerNumber)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = mDatabase.child(userId);

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("contactNumber", contactNumber);
                            userData.put("email", email);
                            userData.put("careTakerName", careTakerName);
                            userData.put("careTakerNumber", careTakerNumber);

                            currentUserDb.setValue(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterPage.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                finish(); // Finish the activity and go back to the login screen
                                            } else {
                                                Toast.makeText(RegisterPage.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterPage.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
