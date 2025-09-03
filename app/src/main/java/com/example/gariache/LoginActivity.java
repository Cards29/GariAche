package com.example.gariache;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

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
        toggleText.setOnClickListener(v -> toggleLoginSignupMode());

        loginButton.setOnClickListener(v -> handleLogin());

        signupButton.setOnClickListener(v -> handleSignup());
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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USER_PASSWORD}; // Add ID column
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD));
                if (password.equals(storedPassword)) {

                    // Save user ID to SharedPreferences for session management
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    SharedPreferences sharedPref = getSharedPreferences("GariAche", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("current_user_id", userId);
                    editor.apply();

                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to Home page
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
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
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@iut-dhaka.edu")) {
            Toast.makeText(this, "Please use your IUT email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email or student ID already exists
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {DatabaseHelper.COLUMN_USER_EMAIL, DatabaseHelper.COLUMN_USER_STUDENT_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ? OR " + DatabaseHelper.COLUMN_USER_STUDENT_ID + " = ?";
        String[] selectionArgs = {email, studentId};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Account with this email or student ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Insert new user
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, name);
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_USER_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_USER_STUDENT_ID, studentId);
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, password);

        long newUserId = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        if (newUserId == -1) {
            Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show();

            // Switch to login mode for user to login
            toggleLoginSignupMode();
        }
    }
}

