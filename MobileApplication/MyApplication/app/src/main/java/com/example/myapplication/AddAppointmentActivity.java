package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AddAppointmentActivity";

    private FirebaseAuth mAuth;
    private Button logoutButton;
    private CalendarView calendar;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDesc;
    private Button addCalendarButton;
    private DatabaseReference databaseReference;
    private String selectedDate;
    private Button viewallButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        calendar = findViewById(R.id.calendar);
        inputTitle = findViewById(R.id.inputTitle);
        inputDesc = findViewById(R.id.inputDesc);
        addCalendarButton = findViewById(R.id.addCalendarButton);

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = (dayOfMonth) + "/" + (month + 1) + "/" + year;
            Toast.makeText(AddAppointmentActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        viewallButton = findViewById(R.id.viewallButton);
        viewallButton.setOnClickListener(v -> {
            startActivity(new Intent(AddAppointmentActivity.this, AppointmentListActivity.class));
            finish();
        });

        logoutButton = findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(AddAppointmentActivity.this, LoginActivity.class));
            finish();
        });

        addCalendarButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Calendar Button clicked");

            String title = inputTitle.getText().toString().trim();
            String description = inputDesc.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || selectedDate == null) {
                Toast.makeText(AddAppointmentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user ID
            String userId = mAuth.getCurrentUser().getUid();

            // Create a new appointment object
            Appointment appointment = new Appointment(title, description, selectedDate, userId);

            // Save appointment to database
            databaseReference.push().setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Appointment added successfully");
                        Toast.makeText(AddAppointmentActivity.this, "Appointment added successfully", Toast.LENGTH_SHORT).show();
                        // Optionally clear the inputs or redirect the user
                        inputTitle.setText("");
                        inputDesc.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to add appointment: " + e.getMessage());
                        Toast.makeText(AddAppointmentActivity.this, "Failed to add appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

class Appointment {
    private String title;
    private String description;
    private String date;
    private String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }

    // Parameterized constructor to initialize the fields
    public Appointment(String title, String description, String date, String userId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.userId = userId;
    }

    // Getters and Setters for each field
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}