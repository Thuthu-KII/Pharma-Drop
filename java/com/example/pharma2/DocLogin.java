package com.example.pharma2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class DocLogin extends AppCompatActivity {

    OkHttpClient client;
    SharedPreferences sh ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_login);// Link to doc_login.xml

        EditText usernameField = (EditText) findViewById(R.id.username2);
        EditText passwordField = findViewById(R.id.password2);
        Button loginButton = findViewById(R.id.login_button2);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //getApplicationContext().getSharedPreferences("DocLogin", MODE_PRIVATE);
        /*
        //if user is already loggin in, no need to logout
        if (sh.getString("Logged In", "false").equals("true")){
            Intent intent = new Intent(DocLogin.this, DoctorHomePageActivity.class);
            intent.putExtra("role", "Doctor");
            startActivity(intent);
            finish();
        }*/
        client = new OkHttpClient();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                boolean isValid = true;

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(DocLogin.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid) {
                    RequestBody postBody = new FormBody.Builder()
                            .add("dUsername", username)
                            .add("Password", password)
                            .build();

                    Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index.php").post(postBody).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(DocLogin.this, "Network error", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(DocLogin.this, "Unexpected response", Toast.LENGTH_SHORT).show());
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
                                        Toast.makeText(DocLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        //store the api key(session token so we can use it anywhere
                                        String token = null;
                                        try {
                                            token = jsonResponse.getString("apiKey");
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        SharedPreferences.Editor editor = sh.edit();
                                        editor.putString("Logged In", "true");
                                        editor.putString("dUsername",username );
                                        editor.putString("dToken", token);
                                        editor.putString("Doc Password", password);
                                        editor.commit();


                                        Intent intent = new Intent(DocLogin.this, DoctorHomePageActivity.class);
                                        intent.putExtra("role", "Doctor");
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(DocLogin.this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(DocLogin.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
                }
            }
        });
    }

}