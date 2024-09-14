package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppointmentListActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private List<String> appointmentList;
    private Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        listView = findViewById(R.id.listView);

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(v -> {
            startActivity(new Intent(AppointmentListActivity.this, AddAppointmentActivity.class));
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not logged in
            // You might want to navigate to the login screen or handle this case according to your app logic
            return;
        }
        // Get current user's ID
        String userId = currentUser.getUid();
        // Database reference to "appointments" node
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        listView.setAdapter(adapter);

        // Add ValueEventListener to retrieve appointment data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                // Iterate through each child node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Deserialize Appointment object from dataSnapshot
                    Map<String, Object> appointmentData = (Map<String, Object>) snapshot.getValue();
                    if (appointmentData != null && appointmentData.containsKey("userId") && appointmentData.get("userId").equals(userId)) {
                        // Format appointment data as desired
                        String appointmentInfo = "\nDate: " + appointmentData.get("date") +
                                "\nTitle: " + appointmentData.get("title") +
                                "\nDescription: " + appointmentData.get("description")+"\n";
                        // Add formatted data to the list
                        appointmentList.add(appointmentInfo);
                    }
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                // You might want to log the error or display a message to the user
            }
        });
    }
}
