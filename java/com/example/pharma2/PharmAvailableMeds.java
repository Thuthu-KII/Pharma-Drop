package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PharmAvailableMeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pharm_avail_meds); // Link to prescriptions.xml

        CardView RemoveMeds = findViewById(R.id.remove);
        CardView AddMeds = findViewById(R.id.add);
        CardView ViewMeds = findViewById(R.id.view);

        ViewMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmAvailableMeds.this, PharmViewMeds.class);
                startActivity(intent);
            }
        });

        AddMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmAvailableMeds.this, PharmAddMeds.class);
                startActivity(intent);
            }
        });

        RemoveMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmAvailableMeds.this, PharmRemoveMeds.class);
                startActivity(intent);
            }
        });
    }

}
