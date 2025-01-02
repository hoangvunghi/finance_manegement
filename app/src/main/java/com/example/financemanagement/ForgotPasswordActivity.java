package com.example.financemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotPasswordEmail, etOTP, etNewPassword;
    private Button btnSendOTP, btnVerifyOTP, btnChangePassword;
    private LinearLayout step1, step2;
    private boolean isSendingOTP = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Button btnBackLogin = findViewById(R.id.btnBackLogin);
        Button btnBackLogin2 = findViewById(R.id.btnBackLogin2);
        btnBackLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
            }
        });
        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
            }
        });
        // Khởi tạo các view
        etForgotPasswordEmail = findViewById(R.id.etForgotPasswordEmailForgot);
        etOTP = findViewById(R.id.etOTPForgot);
        etNewPassword = findViewById(R.id.etNewPasswordForgot);
        btnSendOTP = findViewById(R.id.btnSendOTPForgot);
        btnChangePassword = findViewById(R.id.btnChangePasswordForgot);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void sendOTP() {
        if (isSendingOTP) {
            return;
        }
        isSendingOTP = true;
        btnSendOTP.setEnabled(false);

        String email = etForgotPasswordEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email của bạn", Toast.LENGTH_SHORT).show();
            resetSendOTPButton();
            return;
        }

        String url = "http://10.0.2.2:8000/send-otp/";
        JSONObject emailData = new JSONObject();
        try {
            emailData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Đang gửi OTP...", Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, emailData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        step1.setVisibility(View.GONE);
                        step2.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    resetSendOTPButton();
                },
                error -> {
                    Log.e("SendOTPError", error.toString());
                    Toast.makeText(this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                    resetSendOTPButton();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void resetSendOTPButton() {
        isSendingOTP = false;
        btnSendOTP.setEnabled(true);
    }

    private void verifyOTP() {
        String email = etForgotPasswordEmail.getText().toString();
        String otp = etOTP.getText().toString();

        if (email.isEmpty() || otp.isEmpty()) {
            Toast.makeText(this, "Please enter email and OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/verify-otp/";
        JSONObject otpData = new JSONObject();
        try {
            otpData.put("email", email);
            otpData.put("code", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, otpData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("VerifyOTPError", error.toString());
                    Toast.makeText(this, "Failed to verify OTP", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void changePassword() {
        String email = etForgotPasswordEmail.getText().toString();
        String otp = etOTP.getText().toString();
        String newPassword = etNewPassword.getText().toString();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/change-password/";
        JSONObject passwordData = new JSONObject();
        try {
            passwordData.put("email", email);
            passwordData.put("otp_code", otp);
            passwordData.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, passwordData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, LoginRegisterActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("ChangePasswordError", error.toString());
                    Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}