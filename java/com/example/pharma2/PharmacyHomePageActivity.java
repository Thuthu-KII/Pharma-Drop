package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PharmacyHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pharm_home); // Link to doc_home.xml

        // Add your code here to handle Doctor home page specific functionality
        CardView orders = findViewById(R.id.orders);
        CardView AvailMeds = findViewById(R.id.availableMeds6);
        CardView Profile = findViewById(R.id.profileInfo6);


        AvailMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyHomePageActivity.this, PharmAvailableMeds.class);
                startActivity(intent);
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyHomePageActivity.this, Orders.class);
                startActivity(intent);
            }
        });

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyHomePageActivity.this, PharmProfile.class);
                startActivity(intent);
            }
        });

    }
}
