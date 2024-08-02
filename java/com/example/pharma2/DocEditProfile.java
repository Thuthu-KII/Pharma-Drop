package com.example.pharma2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DocEditProfile extends AppCompatActivity {
    OkHttpClient client ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_doc_profile); // Link to prescriptions.xml

        EditText NameField = findViewById(R.id.name_field);
        EditText LastnameField = findViewById(R.id.lastname_field);
        EditText UsernameField = findViewById(R.id.username_field);
        EditText PasswordField = findViewById(R.id.password_field);
        EditText ConfirmPasswordField = findViewById(R.id.confirmpassword_field);
        EditText EmailField = findViewById(R.id.email_field);

        Button edits = (Button) findViewById(R.id.edit);

        edits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new OkHttpClient();

                String Name = NameField.getText().toString().trim();
                String Lastname = LastnameField.getText().toString().trim();
                String Username = UsernameField.getText().toString().trim();
                String Password = PasswordField.getText().toString().trim();
                String ConfirmPassword = ConfirmPasswordField.getText().toString().trim();
                String Email = EmailField.getText().toString().trim();

                boolean isValid = true;

                if (Name.isEmpty() || ConfirmPassword.isEmpty() || Lastname.isEmpty() ||Username.isEmpty() || Password.isEmpty() || Email.isEmpty()) {
                    Toast.makeText(DocEditProfile.this, "All fields must be filled out. If not updating, input current info", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidEmail(Email)) {
                    Toast.makeText(DocEditProfile.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (!isValidPassword(Password)) {
                    Toast.makeText(DocEditProfile.this, "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one digit, and one special character", Toast.LENGTH_LONG).show();
                    isValid = false;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(DocEditProfile.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid){
                final SharedPreferences mySH = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String uname = (mySH.getString("dUsername", "Default_Value"));
                String token = (mySH.getString("dToken", "Default_Value"));

                RequestBody postBody = new FormBody.Builder()
                        .add("dUsername", Username)
                        .add("dSurname", Lastname )
                        .add("dName", Name)
                        .add("Password", Password)
                        .add("Email", Email )
                        .add("dToken", token)
                        .build();

                Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index2.php").post(postBody).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DocEditProfile.this, "Network error", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(DocEditProfile.this, "Unexpected response", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        try {
                            assert response.body() != null;
                            final String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int success = jsonResponse.getInt("success");
                            String message = jsonResponse.getString("message");

                            runOnUiThread(() -> {
                                if (success == 1) {
                                    Toast.makeText(DocEditProfile.this, message, Toast.LENGTH_SHORT).show();
                                    //store the api key(session token so we can use it anywhere
                                    Intent intent = new Intent(DocEditProfile.this, DocProfile.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(DocEditProfile.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(DocEditProfile.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
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
