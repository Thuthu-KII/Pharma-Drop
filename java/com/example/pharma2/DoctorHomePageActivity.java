package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DoctorHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_home); // Link to doc_home.xml

        // Add your code here to handle Doctor home page specific functionality
        CardView pres = findViewById(R.id.prescriptions);
        CardView Profile = findViewById(R.id.profileInfo);
        CardView availableMeds = findViewById(R.id.availableMeds);

        pres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomePageActivity.this, DPrescriptions.class);
                startActivity(intent);
            }
        });

        availableMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomePageActivity.this, AvailableMedications.class);
                startActivity(intent);
            }
        });


        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomePageActivity.this, DocProfile.class);
                startActivity(intent);
            }
        });
    }
}
