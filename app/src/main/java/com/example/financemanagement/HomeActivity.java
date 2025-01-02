package com.example.financemanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private TransactionAdapter transactionAdapter;
    private ArrayList<Object> listTransaction;
//    private EditText etHome;
    private EditText etStartDate, etEndDate,etYear;
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
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }

    public static String convertDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("d/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_edit_profile) {
                // Chuyển đến màn hình chỉnh sửa thông tin cá nhân
                Intent intent = new Intent(HomeActivity.this, EditProfile.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.menu_logout) {
                // Xử lý đăng xuất
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Đăng xuất");
                builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
                builder.setPositiveButton("Có", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("access_token");
                    editor.remove("refresh_token");
                    editor.remove("first_name");
                    editor.remove("last_name");
                    editor.remove("email");
                    editor.remove("username");
                    editor.apply();
                    Intent intent = new Intent(HomeActivity.this, LoginRegisterActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
                builder.show();
                return true;
            } else if (id == R.id.menu_statistic) {
                // Chuyển đến màn hình thống kê
                Intent intent = new Intent(HomeActivity.this, StatisticsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.menu_create_transaction) {
                //Chuyển đến màn hình tạo giao dịch
                Intent intent = new Intent(HomeActivity.this, CreateTransaction.class);
                startActivity(intent);
            }
            return false;
        });

        popupMenu.show();
    }
    private void showYearPickerDialog(EditText editText) {
        // Tạo một AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn năm");

        // Tạo một NumberPicker để chọn năm
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1900); // Năm tối thiểu
        numberPicker.setMaxValue(2100); // Năm tối đa
        numberPicker.setValue(Calendar.getInstance().get(Calendar.YEAR)); // Năm hiện tại

        // Thêm NumberPicker vào AlertDialog
        builder.setView(numberPicker);

        // Xử lý sự kiện khi người dùng bấm "OK"
        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedYear = numberPicker.getValue();
            String selectedDate = selectedYear + "";
            editText.setText(selectedDate);
        });

        // Xử lý sự kiện khi người dùng bấm "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Hiển thị AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
//        etHome = findViewById(R.id.etHome);
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));
        initializeViews();
//        setupLogoutButton();
        String accessToken = getAccessToken();
        getInformation(accessToken);

        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showPopupMenu(v));
        etYear = findViewById(R.id.etYear);
        etYear.setOnClickListener(v -> showYearPickerDialog(etYear));
        RadioGroup radioGroupDateType = findViewById(R.id.radioGroupDateType);

        // Xử lý sự kiện khi người dùng chọn radio button
        radioGroupDateType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioByDateHome) {
                // Hiển thị ô nhập ngày, ẩn ô nhập năm
                findViewById(R.id.dateRangeLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.yearLayoutHome).setVisibility(View.GONE);
            } else if (checkedId == R.id.radioByYearHome) {
                // Hiển thị ô nhập năm, ẩn ô nhập ngày
                findViewById(R.id.dateRangeLayout).setVisibility(View.GONE);
                findViewById(R.id.yearLayoutHome).setVisibility(View.VISIBLE);
            }
        });

//        tạo 2 biến startDate và endDate để lưu ngày bắt đầu và kết thúc
        Button btnGetData = findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(view -> {
            int selectedId = radioGroupDateType.getCheckedRadioButtonId();
            if (selectedId == R.id.radioByDateHome) {
                // Lấy dữ liệu theo ngày
                String startDate = convertDate(etStartDate.getText().toString());
                String endDate = convertDate(etEndDate.getText().toString());
                if (startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    loadTransactions("http://10.0.2.2:8000/transactions/?start_date=" + startDate + "&end_date=" + endDate);
                }
            } else if (selectedId == R.id.radioByYearHome) {
                // Lấy dữ liệu theo năm
                String year = etYear.getText().toString();
                if (year.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập năm", Toast.LENGTH_SHORT).show();
                } else {
                    loadTransactions("http://10.0.2.2:8000/transactions/?year=" + year);
                }
            }
        });
        loadTransactions("http://10.0.2.2:8000/transactions/");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {

        ListView lvHome = findViewById(R.id.lvHome);

        listTransaction = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this, listTransaction);
        lvHome.setAdapter(transactionAdapter);
        lvHome.setOnItemClickListener((parent, view, position, id) -> {
            Object item = listTransaction.get(position);

            if (item instanceof TransactionModel) {
                TransactionModel transaction = (TransactionModel) item;

                // Chuyển sang màn hình chi tiết giao dịch hoặc hiển thị thông tin
                Intent intent = new Intent(HomeActivity.this, TransactionDetail.class);
                intent.putExtra("amount", transaction.getAmount());
                intent.putExtra("id_trans", transaction.getIdTransaction());
                intent.putExtra("description", transaction.getDescription());
                intent.putExtra("transaction_date", transaction.getTransactionDate());
                intent.putExtra("category_name", transaction.getCategoryName());
                intent.putExtra("category_image", transaction.getCategoryImage());
                intent.putExtra("category_type_name", transaction.getCategoryTypeName());
                startActivity(intent);
            } else if (item instanceof HeaderModel) {
                HeaderModel header = (HeaderModel) item;
                Toast.makeText(this, "Ngày: " + header.getDate(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTransactions(String url) {
//        String url = "http://10.0.2.2:8000/transactions/";
        String accessToken = getAccessToken();

        if (accessToken == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        Map<String, List<TransactionModel>> groupedTransactions = groupTransactionsByDate(dataArray);
                        updateTransactionList(groupedTransactions);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateTransactionList(Map<String, List<TransactionModel>> groupedTransactions) {
        ArrayList<Object> newListTransaction = new ArrayList<>();

        for (Map.Entry<String, List<TransactionModel>> entry : groupedTransactions.entrySet()) {
            String date = entry.getKey();
            List<TransactionModel> transactions = entry.getValue();

            double totalIncome = 0;
            double totalSpending = 0;

            for (TransactionModel transaction : transactions) {
                double amount = Double.parseDouble(transaction.getAmount());
                if (amount > 0) {
                    totalIncome += amount;
                } else {
                    totalSpending += amount;
                }
            }

            newListTransaction.add(new HeaderModel(date, totalSpending, totalIncome));
            newListTransaction.addAll(transactions);
        }

        listTransaction.clear();
        listTransaction.addAll(newListTransaction);
        transactionAdapter.notifyDataSetChanged();
    }

//    private void setupLogoutButton() {
//        Button btnLogOut = findViewById(R.id.btnLogOut);
//        btnLogOut.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Đăng xuất");
//            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
//            builder.setPositiveButton("Có", (dialog, which) -> {
//                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.remove("access_token");
//                editor.remove("refresh_token");
//                editor.remove("first_name");
//                editor.remove("last_name");
//                editor.remove("email");
//                editor.remove("username");
//                editor.apply();
//                Intent intent = new Intent(HomeActivity.this, LoginRegisterActivity.class);
//                startActivity(intent);
//            });
//            builder.setNegativeButton("Không", (dialog, which) -> {
//                dialog.dismiss();
//            });
//            builder.show();
//        });
//    }

    private Map<String, List<TransactionModel>> groupTransactionsByDate(JSONArray dataArray) throws JSONException {
        Map<String, List<TransactionModel>> groupedTransactions = new LinkedHashMap<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject transactionObject = dataArray.getJSONObject(i);
            TransactionModel transaction = new TransactionModel(
                    transactionObject.getString("amount"),
                    transactionObject.getString("description"),
                    transactionObject.getString("transaction_date"),
                    transactionObject.getString("category_name"),
                    transactionObject.getString("category_image"),
                    transactionObject.getString("category_type_name"),
                    transactionObject.getInt("category"),
                    transactionObject.getInt("category_id"),
                    transactionObject.getInt("category_type_id"),
                    transactionObject.getInt("id")
            );

            String transactionDate = transaction.getTransactionDate();
            if (!groupedTransactions.containsKey(transactionDate)) {
                groupedTransactions.put(transactionDate, new ArrayList<>());
            }
            groupedTransactions.get(transactionDate).add(transaction);
        }
        return groupedTransactions;
    }
}