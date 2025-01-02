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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionDetail extends AppCompatActivity {
    private void loadCategoryNames(String categoryTypeName) {
        String url= "";
        if (categoryTypeName.equals("Thu")) {
            url = "http://10.0.2.2:8000/categories/1/names/";
        } else {
            url = "http://10.0.2.2:8000/categories/2/names/";
        }
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionDetail.this);
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
                        Toast.makeText(TransactionDetail.this, "Lỗi khi lấy danh sách danh mục", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(TransactionDetail.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
    private void showCategoryDialog(List<String> categoryNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TransactionDetail.this, android.R.layout.simple_list_item_1, categoryNames);

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetail.this);
        builder.setTitle("Chọn danh mục");
        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedCategory = categoryNames.get(which);
            EditText etCategoryName = findViewById(R.id.etCategoryName);
            etCategoryName.setText(selectedCategory);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        EditText etAmount = findViewById(R.id.etAmount);
        EditText etDescription = findViewById(R.id.etDescription);
        EditText etTransactionDate = findViewById(R.id.etTransactionDate);
        EditText etCategoryName = findViewById(R.id.etCategoryName);
        EditText etCategoryTypeName = findViewById(R.id.etCategoryTypeName);
        Button btnSaveTransaction = findViewById(R.id.btnSaveTransaction);
        Button btnDeleteTransaction = findViewById(R.id.btnDeleteTransaction);

        String categoryTypeNameEdit = getIntent().getStringExtra("category_type_name");

        etCategoryName.setOnClickListener(v -> {
            loadCategoryNames(categoryTypeNameEdit);
        });

        Button btnCancelTransaction = findViewById(R.id.btnCancelTransaction);
        btnCancelTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionDetail.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSaveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer idTransaction = getIntent().getIntExtra("id_trans", 0);
                String url = "http://10.0.2.2:8000/transactions/"+idTransaction;
                String amount = etAmount.getText().toString();
                String description = etDescription.getText().toString();
                String transactionDate = etTransactionDate.getText().toString();
                String categoryName = etCategoryName.getText().toString();
                String categoryTypeName = etCategoryTypeName.getText().toString();
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
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("amount", Integer.parseInt(amount));
                    jsonBody.put("description", description);
                    jsonBody.put("transactionDate", transactionDate);
                    jsonBody.put("categoryName", categoryName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TransactionDetail.this, "Lỗi khi tạo dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("access_token", null);

                // Tạo request
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, jsonBody,
                        response -> {
                            Toast.makeText(TransactionDetail.this, "Cập nhật giao dịch thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TransactionDetail.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        },
                        error -> {
                            String errorMessage = "Cập nhật thất bại";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String errorResponse = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject errorJson = new JSONObject(errorResponse);
                                    errorMessage = errorJson.optString("error", errorMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(TransactionDetail.this, errorMessage, Toast.LENGTH_LONG).show();
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
                RequestQueue requestQueue = Volley.newRequestQueue(TransactionDetail.this);
                requestQueue.add(request);
            }
        });

        btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer idTransaction = getIntent().getIntExtra("id_trans", 0);
                String url = "http://10.0.2.2:8000/transactions/"+idTransaction;

                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetail.this);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa giao dịch này không?");
                builder.setPositiveButton("Có", (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    String accessToken = sharedPreferences.getString("access_token", null);
                    RequestQueue requestQueue = Volley.newRequestQueue(TransactionDetail.this);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                            response -> {
                                Toast.makeText(TransactionDetail.this, "Xóa giao dịch thành công", Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                Toast.makeText(TransactionDetail.this, "Xóa giao dịch thành công", Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + accessToken);
                            return headers;
                        }
                    };

                    requestQueue.add(request);
                            Intent intent = new Intent(TransactionDetail.this, HomeActivity.class);
                            startActivity(intent);
                }
                );
                builder.setNegativeButton("Không", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                builder.show();

            }
        });

        etTransactionDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TransactionDetail.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Cập nhật ngày được chọn vào EditText
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;

                        etTransactionDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        String amount = getIntent().getStringExtra("amount");
        String description = getIntent().getStringExtra("description");
        String transactionDate = getIntent().getStringExtra("transaction_date");
        String categoryName = getIntent().getStringExtra("category_name");
        String categoryTypeName = getIntent().getStringExtra("category_type_name");
        String categoryImage = getIntent().getStringExtra("category_image");


        etAmount.setText(amount);
        etDescription.setText(description);
        etTransactionDate.setText(transactionDate);
        etCategoryName.setText(categoryName);
        etCategoryTypeName.setText(categoryTypeName);


    }
}
