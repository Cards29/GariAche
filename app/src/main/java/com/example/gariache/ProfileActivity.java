package com.example.gariache;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView nameText, emailText, phoneText, studentIdText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupClickListeners();
        loadUserProfile();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        studentIdText = findViewById(R.id.student_id_text);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Load user profile information from database
     */
    private void loadUserProfile() {
        // Get current user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GariAche", MODE_PRIVATE);
        int currentUserId = prefs.getInt("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Query database for user information
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                DatabaseHelper.COLUMN_USER_NAME,
                DatabaseHelper.COLUMN_USER_EMAIL,
                DatabaseHelper.COLUMN_USER_PHONE,
                DatabaseHelper.COLUMN_USER_STUDENT_ID
        };

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(currentUserId)};

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                // Extract user data from cursor
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PHONE));
                String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_STUDENT_ID));

                // Display user information
                displayUserInfo(name, email, phone, studentId);
            } else {
                Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Display user information in the UI
     */
    private void displayUserInfo(String name, String email, String phone, String studentId) {
        nameText.setText(name != null ? name : "Not provided");
        emailText.setText(email != null ? email : "Not provided");
        phoneText.setText(phone != null ? phone : "Not provided");
        studentIdText.setText(studentId != null ? studentId : "Not provided");
    }
}
