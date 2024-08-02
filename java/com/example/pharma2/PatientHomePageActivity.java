package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PatientHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pat_home); // Link to doc_home.xml

        // Add your code here to handle Doctor home page specific functionality

        CardView pres = findViewById(R.id.prescriptions1);
        CardView Profile = findViewById(R.id.profileInfo1);
        CardView history = findViewById(R.id.order);
        CardView search = findViewById(R.id.search_bar1);

        pres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientHomePageActivity.this, PatPrescriptions.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientHomePageActivity.this, search.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientHomePageActivity.this, PatOrderHistory.class);
                startActivity(intent);
            }
        });


        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientHomePageActivity.this, PatProfile.class);
                startActivity(intent);
            }
        });
    }
}
