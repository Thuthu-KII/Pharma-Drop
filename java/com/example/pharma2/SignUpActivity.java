package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button patientButton = findViewById(R.id.patient_button);
        Button doctorButton = findViewById(R.id.doctor_button);
        Button pharmacyButton = findViewById(R.id.pharmacy_button);

        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle patient button click
                Intent intent = new Intent(SignUpActivity.this, PatientSignUpPageActivity.class);
                startActivity(intent);
            }
        });

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle doctor button click
                Intent intent = new Intent(SignUpActivity.this,DoctorSignUpPageActivity.class);
                startActivity(intent);
            }
        });

        pharmacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle pharmacy button click
                Intent intent = new Intent(SignUpActivity.this, PharmacySignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}