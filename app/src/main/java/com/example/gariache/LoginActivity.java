package com.example.gariache;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout loginForm, signupForm;
    private TextView titleText, subtitleText, toggleText;
    private Button loginButton, signupButton;
    private EditText emailInput, passwordInput;
    private EditText nameInput, signupEmailInput, phoneInput, studentIdInput, signupPasswordInput;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        loginForm = findViewById(R.id.login_form);
        signupForm = findViewById(R.id.signup_form);
        titleText = findViewById(R.id.title_text);
        subtitleText = findViewById(R.id.subtitle_text);
        toggleText = findViewById(R.id.toggle_text);

        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        nameInput = findViewById(R.id.name_input);
        signupEmailInput = findViewById(R.id.signup_email_input);
        phoneInput = findViewById(R.id.phone_input);
        studentIdInput = findViewById(R.id.student_id_input);
        signupPasswordInput = findViewById(R.id.signup_password_input);
    }

    private void setupClickListeners() {
        toggleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLoginSignupMode();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });
    }

    private void toggleLoginSignupMode() {
        if (isLoginMode) {
            // Switch to signup mode
            titleText.setText("Create Account");
            subtitleText.setText("Sign up to get started");
            toggleText.setText("Already have an account? Login");
            loginForm.setVisibility(View.GONE);
            signupForm.setVisibility(View.VISIBLE);
            isLoginMode = false;
        } else {
            // Switch to login mode
            titleText.setText("Welcome Back");
            subtitleText.setText("Sign in to continue");
            toggleText.setText("Don't have an account? Sign Up");
            loginForm.setVisibility(View.VISIBLE);
            signupForm.setVisibility(View.GONE);
            isLoginMode = true;
        }
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Hardcoded login for testing
        if (email.equals("student@iut-dhaka.edu") && password.equals("123456")) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }



    private void handleSignup() {
        String name = nameInput.getText().toString().trim();
        String email = signupEmailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String studentId = studentIdInput.getText().toString().trim();
        String password = signupPasswordInput.getText().toString().trim();

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@iut-dhaka.edu")) {
            Toast.makeText(this, "Please use your IUT email", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Add Firebase authentication here later
        Toast.makeText(this, "Signup functionality will be added with Firebase", Toast.LENGTH_SHORT).show();
    }
}
