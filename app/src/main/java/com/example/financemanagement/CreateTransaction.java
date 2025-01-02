package com.example.financemanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTransaction extends AppCompatActivity {
    private void loadCategoryNames(String categoryTypeName) {
        String url= "";
        if (categoryTypeName.equals("Thu")) {
            url = "http://10.0.2.2:8000/categories/1/names/";
        } else {
            url = "http://10.0.2.2:8000/categories/2/names/";
        }
        RequestQueue requestQueue = Volley.newRequestQueue(CreateTransaction.this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<String> categoryNames = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            categoryNames.add(response.getString(i));
                        }
                        showCategoryDialog(categoryNames);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CreateTransaction.this, "Lỗi khi lấy danh sách danh mục", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(CreateTransaction.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
    private void showCategoryDialog(List<String> categoryNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateTransaction.this, android.R.layout.simple_list_item_1, categoryNames);

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTransaction.this);
        builder.setTitle("Chọn danh mục");
        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedCategory = categoryNames.get(which);
            EditText etCategoryName = findViewById(R.id.etCategoryNameCreate);
            etCategoryName.setText(selectedCategory);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_transaction);

        EditText etAmount = findViewById(R.id.etAmountCreate);
        EditText etDescription = findViewById(R.id.etDescriptionCreate);
        EditText etTransactionDate = findViewById(R.id.etTransactionDateCreate);
        EditText etCategoryName = findViewById(R.id.etCategoryNameCreate);
        EditText etCategoryTypeName = findViewById(R.id.etCategoryTypeNameCreate);

        String categoryTypeNameCreat = etCategoryTypeName.getText().toString();
        etTransactionDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateTransaction.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Cập nhật ngày được chọn vào EditText
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;

                        etTransactionDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        etCategoryName.setOnClickListener(v -> {
            loadCategoryNames(categoryTypeNameCreat);
        });

        etCategoryTypeName.setOnClickListener(v -> {
            String[] categoryTypeNames = {"Thu", "Chi"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateTransaction.this, android.R.layout.simple_list_item_1, categoryTypeNames);

            AlertDialog.Builder builder = new AlertDialog.Builder(CreateTransaction.this);
            builder.setTitle("Chọn loại giao dịch");
            builder.setAdapter(adapter, (dialog, which) -> {
                String selectedCategoryType = categoryTypeNames[which];
                etCategoryTypeName.setText(selectedCategoryType);
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        Button btnCancelTransaction = findViewById(R.id.btnCancelTransaction);
        btnCancelTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateTransaction.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnCreateTransaction = findViewById(R.id.btnCreatTransaction);
        btnCreateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://10.0.2.2:8000/transactions/";
                String amount = etAmount.getText().toString();
                String description = etDescription.getText().toString();
                String transactionDate = etTransactionDate.getText().toString();
                String categoryName = etCategoryName.getText().toString();

                // Validate đầu vào
                if (amount.isEmpty() || !amount.matches("-?[0-9]+")) {
                    etAmount.setError("Số tiền không hợp lệ");
                    return;
                }

                if (transactionDate.isEmpty()) {
                    etTransactionDate.setError("Ngày giao dịch không được để trống");
                    return;
                }

                if (categoryName.isEmpty()) {
                    etCategoryName.setError("Danh mục không được để trống");
                    return;
                }

                // Tạo request body
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("amount", Integer.parseInt(amount));
                    jsonBody.put("description", description);
                    jsonBody.put("transactionDate", transactionDate);
                    jsonBody.put("categoryName", categoryName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateTransaction.this, "Lỗi khi tạo dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy access token
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", null);

                // Tạo request
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        response -> {
                            Toast.makeText(CreateTransaction.this, "Tạo giao dịch thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateTransaction.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        },
                        error -> {
                            String errorMessage = "Tạo giao dịch thất bại";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String errorResponse = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject errorJson = new JSONObject(errorResponse);
                                    errorMessage = errorJson.optString("error", errorMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(CreateTransaction.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Bearer " + accessToken);
                        return headers;
                    }
                };

                // Thêm request vào queue
                RequestQueue requestQueue = Volley.newRequestQueue(CreateTransaction.this);
                requestQueue.add(request);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.creatTransaction), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}