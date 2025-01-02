package com.example.financemanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private void getInformation(String accessToken) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:8000/get-information";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("first_name", response.getString("first_name"));
                        editor.putString("last_name", response.getString("last_name"));
                        editor.putString("email", response.getString("email"));
                        editor.putString("username", response.getString("username"));
                        editor.apply();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", null);
        getInformation(accessToken);
        Button btnBackHome = findViewById(R.id.btnBackHome);
        Button btnEditProfile = findViewById(R.id.btnUpdateProfile);
        EditText etFirstNameEdit = findViewById(R.id.etFirstNameEdit);
        EditText etLastNameEdit = findViewById(R.id.etLastNameEdit);
        EditText etEmailEdit = findViewById(R.id.etEmailEdit);
        EditText etPasswordEdit = findViewById(R.id.etPasswordEdit);


        String firstName = sharedPreferences.getString("first_name", null);
        String lastName = sharedPreferences.getString("last_name", null);
        String email = sharedPreferences.getString("email", null);
        String usernameLogin = sharedPreferences.getString("username", null);

        etFirstNameEdit.setText(firstName);
        etLastNameEdit.setText(lastName);
        etEmailEdit.setText(email);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create password verification dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                View dialogView = LayoutInflater.from(EditProfile.this)
                        .inflate(R.layout.dialog_enter_password, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                EditText etPasswordVerify = dialogView.findViewById(R.id.etPasswordVerify);
                Button btnVerify = dialogView.findViewById(R.id.btnVerify);
                Button btnCancel = dialogView.findViewById(R.id.btnCancel);

                btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String enteredPassword = etPasswordVerify.getText().toString();
                        String new_password = etPasswordEdit.getText().toString();
                        if (!enteredPassword.isEmpty()) {
                            String url = "http://10.0.2.2:8000/update-profile";
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("first_name", etFirstNameEdit.getText().toString());
                                jsonBody.put("last_name", etLastNameEdit.getText().toString());
                                jsonBody.put("email", etEmailEdit.getText().toString());
//                                jsonBody.put("username", usernameLogin);
                                jsonBody.put("password", enteredPassword);
                                jsonBody.put("new_password", new_password);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH,url,jsonBody,
                                    response -> {
                                        Toast.makeText(EditProfile.this, "Profile updated successfully",
                                                Toast.LENGTH_SHORT).show();
                                        getInformation(accessToken);
                                        Intent intent = new Intent(EditProfile.this, HomeActivity.class);
                                        startActivity(intent);
                                    },
                                    error -> {
                                        Toast.makeText(EditProfile.this, "Incorrect password",
                                                Toast.LENGTH_SHORT).show();
                                    })
                            {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Authorization", "Bearer " + accessToken);
                                    return headers;
                                }
                            };
                            requestQueue.add(request);

                        } else {
                            Toast.makeText(EditProfile.this, "Incorrect password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}