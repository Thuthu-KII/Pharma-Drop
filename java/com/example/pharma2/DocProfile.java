package com.example.pharma2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
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

public class DocProfile extends AppCompatActivity {
    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_profile); // Link to prescriptions.xml

        TextView NameField = findViewById(R.id.name);
        TextView LastnameField = findViewById(R.id.surname);
        TextView UsernameField = findViewById(R.id.username);
        TextView PasswordField = findViewById(R.id.password);
        TextView EmailField = findViewById(R.id.email);


        Button logout = findViewById(R.id.button);
        Button edit = findViewById(R.id.edit_profile_button);

        final SharedPreferences mySH = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String uname = (mySH.getString("dUsername", "Default_Value"));
        String token = (mySH.getString("dToken", "Default_Value"));
        String pass = (mySH.getString("Doc Password", "Default_Value"));

        client = new OkHttpClient();

        RequestBody postBody = new FormBody.Builder()
                .add("dToken", token)
                .build();

        Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index2.php").post(postBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(DocProfile.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(DocProfile.this, "Unexpected response", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    assert response.body() != null;
                    final String responseData = response.body().string();
                    JSONArray jsonArr = new JSONArray(responseData);
                    JSONObject jsonResponse = jsonArr.getJSONObject(0);


                    runOnUiThread(() -> {
                        try {
                            NameField.setText(jsonResponse.getString("Name"));
                            LastnameField.setText(jsonResponse.getString("Surname"));
                            UsernameField.setText(jsonResponse.getString("Username"));
                            PasswordField.setText(pass);
                            EmailField.setText(jsonResponse.getString("Email"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(DocProfile.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                }

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocProfile.this, DocEditProfile.class);
                startActivity(intent);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new OkHttpClient();

                RequestBody postBody = new FormBody.Builder()
                        .add("dUsername", uname)
                        .add("dToken", token)
                        .build();

                Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index.php")
                        .post(postBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DocProfile.this, "Network error", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(DocProfile.this, "Unexpected response", Toast.LENGTH_SHORT).show());
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
                                    Toast.makeText(DocProfile.this, message, Toast.LENGTH_SHORT).show();
                                    //store the api key(session token so we can use it anywhere
                                    Intent intent = new Intent(DocProfile.this, MainActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(DocProfile.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(DocProfile.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        });
    }

}
