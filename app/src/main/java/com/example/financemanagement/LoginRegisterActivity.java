package com.example.financemanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRegisterActivity extends AppCompatActivity {

    private LinearLayout loginForm, registerForm, forgotPasswordForm;
    private Button btnToggleForm, btnLogin, btnRegister, btnForgotPassword, btnSubmitForgotPassword;
    private EditText etLoginUsername, etLoginPassword;
    private EditText etRegisterUsername, etRegisterPassword, etRegisterEmail, etRegisterFirstName, etRegisterLastName;
    private EditText etForgotPasswordEmail;

    private boolean isLoginFormVisible = true;
    private boolean isRegistering = false; // Biến cờ để kiểm soát việc đăng ký

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Edge-to-Edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các view
        loginForm = findViewById(R.id.loginForm);
        registerForm = findViewById(R.id.registerForm);
        forgotPasswordForm = findViewById(R.id.forgotPasswordForm);
        btnToggleForm = findViewById(R.id.btnToggleForm);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnSubmitForgotPassword = findViewById(R.id.btnSubmitForgotPassword);

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterFirstName = findViewById(R.id.etRegisterFirstName);
        etRegisterLastName = findViewById(R.id.etRegisterLastName);

        etForgotPasswordEmail = findViewById(R.id.emailFormForgotPassword);

        // Xử lý chuyển đổi giữa form đăng nhập và đăng ký
        btnToggleForm.setOnClickListener(v -> toggleForm());

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());

        // Xử lý sự kiện đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Xử lý sự kiện quên mật khẩu
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginRegisterActivity.this, ForgotPasswordActivity.class);
                startActivity(it);
            }
        });

        // Xử lý sự kiện gửi form quên mật khẩu
        btnSubmitForgotPassword.setOnClickListener(v -> submitForgotPassword());
    }

    private void toggleForgotPasswordForm() {
        loginForm.setVisibility(View.GONE);
        registerForm.setVisibility(View.GONE);
        forgotPasswordForm.setVisibility(View.VISIBLE);
        btnToggleForm.setText("Chuyển sang đăng nhâp");
        isLoginFormVisible = false;
    }

    private void submitForgotPassword() {
        String email = etForgotPasswordEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "http://10.0.2.2:8000/forgot/forgot-password";
        JSONObject emailData = new JSONObject();
        try {
            emailData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, emailData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        loginForm.setVisibility(View.VISIBLE);
                        registerForm.setVisibility(View.GONE);
                        forgotPasswordForm.setVisibility(View.GONE);
                        btnToggleForm.setText("Chuyển sang đăng ký");
                        isLoginFormVisible = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("ForgotPasswordError", error.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Quên mật khẩu thất bại");
                    builder.setMessage(error.toString());
                    builder.setPositiveButton("OK", null);
                    builder.show();
                });
        requestQueue.add(request);
    }

    private void toggleForm() {
        if (isLoginFormVisible) {
            loginForm.setVisibility(View.GONE);
            registerForm.setVisibility(View.VISIBLE);
            forgotPasswordForm.setVisibility(View.GONE);
            btnToggleForm.setText("Chuyển sang đăng nhập");
        } else {
            loginForm.setVisibility(View.VISIBLE);
            registerForm.setVisibility(View.GONE);
            forgotPasswordForm.setVisibility(View.GONE);
            btnToggleForm.setText("Chuyển sang đăng ký");
        }
        isLoginFormVisible = !isLoginFormVisible;
    }

    private void loginUser() {
        String username = etLoginUsername.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/login";
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, loginData,
                response -> {
                    try {
                        String message = response.getString("response");
                        JSONObject tokenObject = response.getJSONObject("token");
                        String accessToken = tokenObject.getString("access");
                        String refreshToken = tokenObject.getString("refresh");
                        Toast.makeText(this, "Đăng nhập thành công: " + message, Toast.LENGTH_SHORT).show();

                        // Lưu access token và refresh token vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.apply();

                        // Điều hướng đến HomeActivity
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Đăng nhập thất bại", error.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Đăng nhập thất bại");
                    builder.setMessage(error.toString());
                    builder.setPositiveButton("OK", null);
                    builder.show();
                });
        requestQueue.add(request);
    }
    private void registerUser() {
        if (isRegistering) {
            return;
        }
        isRegistering = true;
        btnRegister.setEnabled(false);

        String username = etRegisterUsername.getText().toString();
        String password = etRegisterPassword.getText().toString();
        String email = etRegisterEmail.getText().toString();
        String firstName = etRegisterFirstName.getText().toString();
        String lastName = etRegisterLastName.getText().toString();

        if (username.isEmpty()) {
            etRegisterUsername.setError("Bạn cần nhập tên đăng nhập");
            etRegisterUsername.requestFocus();
            resetRegisterButton();
            return;
        }

        if (password.isEmpty()) {
            etRegisterPassword.setError("Bạn cần nhập mật khẩu");
            etRegisterPassword.requestFocus();
            resetRegisterButton();
            return;
        }

        if (email.isEmpty()) {
            etRegisterEmail.setError("Bạn cần nhập email");
            etRegisterEmail.requestFocus();
            resetRegisterButton();
            return;
        }

        if (firstName.isEmpty()) {
            etRegisterFirstName.setError("Bạn cần nhập tên");
            etRegisterFirstName.requestFocus();
            resetRegisterButton();
            return;
        }

        if (lastName.isEmpty()) {
            etRegisterLastName.setError("Bạn cần nhập họ");
            etRegisterLastName.requestFocus();
            resetRegisterButton();
            return;
        }

        if (password.length() < 8) {
            etRegisterPassword.setError("Mât khẩu phải có ít nhất 8 ký tự");
            etRegisterPassword.requestFocus();
            resetRegisterButton();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Sai định dạng email");
            etRegisterEmail.requestFocus();
            resetRegisterButton();
            return;
        }

        String url = "http://10.0.2.2:8000/register";
        JSONObject registerData = new JSONObject();
        try {
            registerData.put("username", username);
            registerData.put("password", password);
            registerData.put("email", email);
            registerData.put("first_name", firstName);
            registerData.put("last_name", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, registerData,
                response -> {
                    try {
                        String message = response.getString("response");
                        Toast.makeText(this, "Đăng ký thành công: " + message, Toast.LENGTH_SHORT).show();

                        autoLoginAfterRegistration(username, password);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Không thể đăng ký", Toast.LENGTH_SHORT).show();
                    }
                    resetRegisterButton();
                },
                error -> {
                    Log.e("RegisterError", error.toString());
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                            String errorMessage = errorResponse.getString("message");
                            if (errorMessage.contains("Tên đăng nhập đã tồn tại")) {
                                etRegisterUsername.setError(errorMessage);
                                etRegisterUsername.requestFocus();
                            } else if (errorMessage.contains("Email đã tồn tại")) {
                                etRegisterEmail.setError(errorMessage);
                                etRegisterEmail.requestFocus();
                            } else {
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                    }
                    resetRegisterButton();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void resetRegisterButton() {
        isRegistering = false;
        btnRegister.setEnabled(true);
    }

    private void autoLoginAfterRegistration(String username, String password) {
        etLoginUsername.setText(username);
        etLoginPassword.setText(password);

        loginUser();
    }


}