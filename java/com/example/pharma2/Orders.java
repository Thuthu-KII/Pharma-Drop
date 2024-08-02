package com.example.pharma2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Orders extends AppCompatActivity {
    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders); // Link to prescriptions.xml

        CardView Pendng = findViewById(R.id.Pending);
        CardView Deliverd = findViewById(R.id.Delivered);
        Button deliver = findViewById(R.id.button);

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2543444/Orders.php")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(Orders.this, "Network error", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(Orders.this, "Unexpected response", Toast.LENGTH_SHORT).show());
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
                                    Toast.makeText(Orders.this, message, Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(Orders.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(Orders.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        });

        Pendng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Orders.this, Pending.class);
                startActivity(intent);
            }
        });

        Deliverd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Orders.this, Delivered.class);
                startActivity(intent);
            }
        });
    }
}
