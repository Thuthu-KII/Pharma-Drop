package com.example.pharma2;

import android.content.Intent;
import android.os.Bundle;
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

public class DoctorSignUpPageActivity extends AppCompatActivity {
    OkHttpClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sign_up_page);

        client = new OkHttpClient();

        EditText NameField = findViewById(R.id.name_field);
        EditText LastnameField = findViewById(R.id.lastname_field);
        EditText IDField = findViewById(R.id.id_field);
        EditText CredentialsField = findViewById(R.id.credentials_field);
        EditText UsernameField = findViewById(R.id.username_field);
        EditText PasswordField = findViewById(R.id.password_field);
        EditText ConfirmPasswordField = findViewById(R.id.confirmpassword_field);
        EditText EmailField = findViewById(R.id.email_field);

        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = NameField.getText().toString().trim();
                String Lastname = LastnameField.getText().toString().trim();
                String ID = IDField.getText().toString().trim();
                String Credentials = CredentialsField.getText().toString().trim();
                String Username = UsernameField.getText().toString().trim();
                String Password = PasswordField.getText().toString().trim();
                String ConfirmPassword = ConfirmPasswordField.getText().toString().trim();
                String Email = EmailField.getText().toString().trim();

                boolean isValid = true;

                if (Name.isEmpty() || ConfirmPassword.isEmpty() || Lastname.isEmpty() || ID.isEmpty() || Credentials.isEmpty() || Username.isEmpty() || Password.isEmpty() || Email.isEmpty()) {
                    Toast.makeText(DoctorSignUpPageActivity.this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidID(ID)) {
                    Toast.makeText(DoctorSignUpPageActivity.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidEmail(Email)) {
                    Toast.makeText(DoctorSignUpPageActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidPassword(Password)) {
                    Toast.makeText(DoctorSignUpPageActivity.this, "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one digit, and one special character", Toast.LENGTH_LONG).show();
                    isValid = false;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(DoctorSignUpPageActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid) {
                    RequestBody postBody = new FormBody.Builder()
                            .add("dUsername", Username)
                            .add("Password", Password)
                            .add("Email", Email)
                            .add("dName", Name)
                            .add("dSurname", Lastname)
                            .add("dID", ID)
                            .add("Medic_Credentials", Credentials)
                            .build();

                    Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index.php").post(postBody).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(DoctorSignUpPageActivity.this, "Registration failed", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(DoctorSignUpPageActivity.this, "Unexpected error: " + response.message(), Toast.LENGTH_SHORT).show());
                                return;
                            }

                            //String responseData = response.body().string();
                            try {
                                assert response.body() != null;
                                final String responseData = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseData);
                                JSONObject json = new JSONObject(responseData);
                                int success = json.getInt("success");
                                String message = json.getString("message");

                                runOnUiThread(() -> {
                                    if (success == 1) {
                                        Toast.makeText(DoctorSignUpPageActivity.this, "Registration successful: " + message, Toast.LENGTH_SHORT).show();
                                        //next screen intent
                                        Intent intent = new Intent(DoctorSignUpPageActivity.this, LoginActivity.class);
                                        intent.putExtra("role", "Doctor");
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(DoctorSignUpPageActivity.this, "Registration failed: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(DoctorSignUpPageActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });

                }

            }
        });
    }

    private boolean isValidID(String id) {
        return id.matches("\\d{13}");
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".[A-Z].") && // At least one uppercase letter
                password.matches(".[a-z].") && // At least one lowercase letter
                password.matches(".\\d.") &&   // At least one digit
                password.matches(".[@#\\$%^&+=!]."); // At least one special character
    }
}