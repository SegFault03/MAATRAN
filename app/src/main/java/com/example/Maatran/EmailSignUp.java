package com.example.Maatran;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

//Called from RegistrationActivity
//email based sign-up
//xml file: screen-3
public class EmailSignUp extends Activity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.screen_3);
        ImageButton create = findViewById(R.id.create_acc);
        create.setOnClickListener(view -> {
            String email =  ((EditText)findViewById(R.id.email)).getText().toString();
            String password = ((EditText)findViewById(R.id.password)).getText().toString();
            String confirmPass =  ((EditText)findViewById(R.id.confirm_password)).getText().toString();
            String user_name = ((EditText)findViewById(R.id.user_name)).getText().toString();
            if(password.equals(confirmPass))
            createAccount(email, password, user_name);
            else
                Toast.makeText(EmailSignUp.this, "Passwords don't match.",
                        Toast.LENGTH_SHORT).show();
        });
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // [END on_start_check_user]



    private void createAccount(String email, String password, String user_name) {
        // [START create_user_with_email]
        Map<String, String> userName = new HashMap<>();
        userName.put("user_name", user_name);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("UserDetails").document(user.getEmail())
                                .set(userName, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(EmailSignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    updateUI();
                });
        // [END create_user_with_email]
    }


    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }

    public void signInOptions(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    private void reload() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(intent);
    }

    private void updateUI() {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.putExtra("isPatient", true);
        startActivity(intent);
    }
}
