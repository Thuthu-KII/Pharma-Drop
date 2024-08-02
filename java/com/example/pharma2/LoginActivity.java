package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_all);

        Button patientButton = findViewById(R.id.patient_button1);
        Button doctorButton = findViewById(R.id.doctor_button1);
        Button pharmacyButton = findViewById(R.id.pharmacy_button1);

        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle patient button click
                Intent intent = new Intent(LoginActivity.this, PatLogin.class);
                startActivity(intent);
            }
        });

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle doctor button click
                Intent intent = new Intent(LoginActivity.this, DocLogin.class);
                startActivity(intent);
            }
        });

        pharmacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle pharmacy button click
                Intent intent = new Intent(LoginActivity.this, PharmLogin.class);
                startActivity(intent);
            }
        });
    }
}