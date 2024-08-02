package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PharmacySignUpActivity extends AppCompatActivity {

    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_sign_up);

        EditText BusinessRegNumberField = findViewById(R.id.business_reg_number);
        EditText EmailField = findViewById(R.id.email);
        EditText PharmacyNameField = findViewById(R.id.pharmacy_name);
        EditText UsernameField = findViewById(R.id.username);
        EditText PasswordField = findViewById(R.id.password);
        EditText ConfirmPasswordField = findViewById(R.id.confirm_password_pharma);

        client = new OkHttpClient();

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BusinessRegNumber = BusinessRegNumberField.getText().toString().trim();
                String Email  = EmailField.getText().toString().trim();
                String Pharmacy_Name = PharmacyNameField.getText().toString().trim();
                String Username = UsernameField.getText().toString().trim();
                String Password = PasswordField.getText().toString().trim();
                String ConfirmPassword = ConfirmPasswordField.getText().toString().trim();

                boolean isValid = true;

                if (BusinessRegNumber.isEmpty() || ConfirmPassword.isEmpty() || Pharmacy_Name.isEmpty() || Username.isEmpty() || Password.isEmpty() || Email.isEmpty()) {
                    Toast.makeText(PharmacySignUpActivity.this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidEmail(Email)) {
                    Toast.makeText(PharmacySignUpActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidPassword(Password)) {
                    Toast.makeText(PharmacySignUpActivity.this, "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one digit, and one special character", Toast.LENGTH_LONG).show();
                    isValid = false;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(PharmacySignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid){

                    RequestBody postBody = new FormBody.Builder()
                            .add("phUsername", Username)
                            .add("Password", Password)
                            .add("Email", Email)
                            .add("phName", Pharmacy_Name)
                            .add("Bus_Reg_Num", BusinessRegNumber)
                            .build();

                    Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index.php").post(postBody).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(PharmacySignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(PharmacySignUpActivity.this, "Unexpected error: " + response.message(), Toast.LENGTH_SHORT).show());
                                return;
                            }

                            //String responseData = response.body().string();
                            try {
                                assert response.body() != null;
                                final String responseData = response.body().string();
                                // JSONObject jsonResponse = new JSONObject(responseData);
                                JSONObject json = new JSONObject(responseData);
                                int success = json.getInt("success");
                                String message = json.getString("message");

                                runOnUiThread(() -> {
                                    if (success == 1) {
                                        Toast.makeText(PharmacySignUpActivity.this, "Registration successful: " + message, Toast.LENGTH_SHORT).show();
                                        //next screen intent
                                        Intent intent = new Intent(PharmacySignUpActivity.this, PharmLogin.class);
                                        intent.putExtra("role", "Pharmacy");
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(PharmacySignUpActivity.this, "Registration failed: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }catch (JSONException e){
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(PharmacySignUpActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });

                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // Check if the password meets the criteria
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") && // At least one uppercase letter
                password.matches(".*[a-z].*") && // At least one lowercase letter
                password.matches(".*\\d.*") &&   // At least one digit
                password.matches(".*[@#\\$%^&+=!?].*"); // At least one special character
    }


}