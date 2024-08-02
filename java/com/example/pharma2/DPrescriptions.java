package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DPrescriptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescriptions); // Link to prescriptions.xml

        CardView ViewPres = findViewById(R.id.viewPresDoc);
        CardView AddPres = findViewById(R.id.addPresDoc);

        ViewPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DPrescriptions.this, DocView.class);
                startActivity(intent);
            }
        });

        AddPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DPrescriptions.this, AddPrescriptions.class);
                startActivity(intent);
            }
        });
    }
}
