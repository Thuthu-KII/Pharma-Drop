package com.example.pharma2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PharmViewMeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.available_meds);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.containerMed), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout c = findViewById(R.id.containerMed);
        LayoutInflater inflator = (LayoutInflater) getLayoutInflater();
        final SharedPreferences mySH = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = (mySH.getString("Pharm Token", "Default_Value"));

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("phToken", token)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2543444/index.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, @NonNull final Response response) throws IOException {
                //try to convert data into a JSON array and do the rest if cant (catch)
                try {
                    //getting the relevant data from the sever and decoding it by converting it into a JSON Array
                    assert response.body() != null;
                    String responseData = response.body().string();
                    JSONArray jsonArr = new JSONArray(responseData);

                    //I WAS GETTING THAT HIERARCHY MAIN THREAD ERROR, CAUSE if you try to access or update views from outside the main thread in the Callback, you will probably receive an exception:
                    //SO THIS IS THE FIX I PERFORMED, i put all my cod ein the run() method and that fixed everything

                    PharmViewMeds.this.runOnUiThread(new Runnable() {
                        @Override

                        public void run() {
                            try {
                                //Putting it onto the screen
                                for (int i = 0; i < jsonArr.length(); ++i){
                                    JSONObject temp = jsonArr.getJSONObject(i) ;
                                    View myView = inflator.inflate(R.layout.doc_avail_meds_table, null, false);

                                    TextView presId = myView.findViewById(R.id.Prescrip_ID_row2);
                                    presId.setText(temp.getString("Med_ID") );

                                    TextView patAC = myView.findViewById(R.id.PatAcc_row2);
                                    patAC.setText(temp.getString("MedName"));

                                    TextView docAc = myView.findViewById(R.id.DocAcc_row2);
                                    docAc.setText(temp.getString("Description") );

                                    TextView medID = myView.findViewById(R.id.MedID_row2);
                                    medID.setText(temp.getString("StockCount"));

                                    c.addView(myView);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    //some relevant error message
                }
            }
        });





    }
}