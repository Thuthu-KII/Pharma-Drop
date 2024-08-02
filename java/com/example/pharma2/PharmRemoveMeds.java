package com.example.pharma2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class PharmRemoveMeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_meds); // Link to prescriptions.xml

        EditText IDField = findViewById(R.id.p1);
        EditText PasswordField = findViewById(R.id.password0);

        OkHttpClient client = new OkHttpClient();

        Button remove = findViewById(R.id.add);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String MedID = IDField.getText().toString().trim();
                String Password = PasswordField.getText().toString().trim();


                boolean isValid = true;

                if (MedID.isEmpty() || Password.isEmpty()) {
                    Toast.makeText(PharmRemoveMeds.this, "All Fields Required", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid) {
                    final SharedPreferences mySH = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String psswd = (mySH.getString("Pharm Password", "Default_Value"));

                    if (!Password.equals(psswd)){
                        Toast.makeText(PharmRemoveMeds.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RequestBody postBody = new FormBody.Builder()
                            .add("medID", MedID)
                            .add("Password", Password)
                            .build();

                    Request request = new Request.Builder().url("https://lamp.ms.wits.ac.za/home/s2543444/index2.php").post(postBody).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(PharmRemoveMeds.this, "Failed", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(PharmRemoveMeds.this, "Unexpected error: " + response.message(), Toast.LENGTH_SHORT).show());
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
                                        Toast.makeText(PharmRemoveMeds.this, message, Toast.LENGTH_SHORT).show();
                                        //next screen intent
                                        Intent intent = new Intent(PharmRemoveMeds.this, PharmAvailableMeds.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(PharmRemoveMeds.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(PharmRemoveMeds.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });

                }
            }
        });
    }

}
