package com.example.financemanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private PieChart pieChartIncome;
    private PieChart pieChartExpense;
    private BarChart barChart;
    private LineChart lineChart;
    private Button btnStatistic;
    private EditText txtStartDateStatistic, txtEndDateStatistic,txtYearStatistic;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        pieChartIncome = findViewById(R.id.pieChartIncome);
        pieChartExpense = findViewById(R.id.pieChartExpense);
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        btnStatistic = findViewById(R.id.btnStatistic);
        txtStartDateStatistic = findViewById(R.id.txtStartDateStatistic);
        txtEndDateStatistic = findViewById(R.id.txtEndDateStatistic);
        txtYearStatistic = findViewById(R.id.txtYearStatistic);
        // Khởi tạo Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Lấy SharedPreferences để lấy access token
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Xử lý chọn ngày
        txtStartDateStatistic.setOnClickListener(v -> showDatePickerDialog(txtStartDateStatistic));
        txtEndDateStatistic.setOnClickListener(v -> showDatePickerDialog(txtEndDateStatistic));
        txtYearStatistic.setOnClickListener(v -> showYearPickerDialog(txtYearStatistic));
        // Xử lý nút thống kê

        RadioGroup radioGroupStatisticType = findViewById(R.id.radioGroupStatisticType);
        radioGroupStatisticType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioByDate) {
                // Hiển thị ô nhập ngày, ẩn ô nhập năm
                txtStartDateStatistic.setVisibility(View.VISIBLE);
                txtEndDateStatistic.setVisibility(View.VISIBLE);
                txtYearStatistic.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioByYear) {
                // Hiển thị ô nhập năm, ẩn ô nhập ngày
                txtStartDateStatistic.setVisibility(View.GONE);
                txtEndDateStatistic.setVisibility(View.GONE);
                txtYearStatistic.setVisibility(View.VISIBLE);
            }
        });

        btnStatistic.setOnClickListener(v -> {

            int selectedId = radioGroupStatisticType.getCheckedRadioButtonId();

            if (selectedId == R.id.radioByDate) {
                // Thống kê theo ngày
                String startDate = txtStartDateStatistic.getText().toString();
                String endDate = txtEndDateStatistic.getText().toString();
                if (startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    loadChartData(startDate, endDate, null);
                }
            } else if (selectedId == R.id.radioByYear) {
                // Thống kê theo năm
                String year = txtYearStatistic.getText().toString();
                if (year.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập năm", Toast.LENGTH_SHORT).show();
                } else {
                    loadChartData(null, null, year);
                }
            }
        });

        Button btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(StatisticsActivity.this, HomeActivity.class);
                startActivity(it);
            }
        });

        // Load dữ liệu mặc định
        loadChartData(null, null, null);
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editText.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showYearPickerDialog(EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn năm");

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1900);
        numberPicker.setMaxValue(2100);
        numberPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
        builder.setView(numberPicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedYear = numberPicker.getValue();
            String selectedDate = selectedYear + "";
            editText.setText(selectedDate);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void loadChartData(String startDate, String endDate, String year) {
        String url = "http://10.0.2.2:8000/analytics";
        if (startDate != null && endDate != null) {
            url += "?start_date=" + startDate + "&end_date=" + endDate;
        } else if (year != null && !year.isEmpty()) {
            url += "?year=" + year;
        }

        String accessToken = sharedPreferences.getString("access_token", null);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            updatePieChart(response.getJSONArray("pie_data"));
                            updateIncomePieChart(response.getJSONArray("income_categories"));
                            updateExpensePieChart(response.getJSONArray("expense_categories"));
                            updateBarChart(response.getJSONArray("monthly_data"));
                            updateLineChart(response.getJSONArray("daily_data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Lỗi khi tải dữ liệu";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorJson = new JSONObject(errorResponse);
                                errorMessage = errorJson.optString("error", errorMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // Hiển thị thông báo lỗi
                        Toast.makeText(StatisticsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (accessToken != null) {
                    headers.put("Authorization", "Bearer " + accessToken);
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }
    private void updateIncomePieChart(JSONArray incomeData) throws JSONException {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < incomeData.length(); i++) {
            JSONObject item = incomeData.getJSONObject(i);
            String category = item.getString("category__name");
            float total = (float) item.getDouble("total");
            entries.add(new PieEntry(total, category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Thu nhập");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChartIncome.setData(pieData);
        pieChartIncome.invalidate();
    }

    private void updateExpensePieChart(JSONArray expenseData) throws JSONException {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < expenseData.length(); i++) {
            JSONObject item = expenseData.getJSONObject(i);
            String category = item.getString("category__name");
            float total = (float) item.getDouble("total");
            entries.add(new PieEntry(total, category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChartExpense.setData(pieData);
        pieChartExpense.invalidate();
    }
    private void updatePieChart(JSONArray pieData) throws JSONException {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < pieData.length(); i++) {
            JSONObject item = pieData.getJSONObject(i);
            String type = item.getString("category__type__name");
            float total = (float) item.getDouble("total");
            entries.add(new PieEntry(Math.abs(total), type));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieDataObj = new PieData(dataSet);
        pieChart.setData(pieDataObj);
        pieChart.invalidate();
    }

    private void updateBarChart(JSONArray monthlyData) throws JSONException {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < monthlyData.length(); i++) {
            JSONObject item = monthlyData.getJSONObject(i);
            float thu = (float) item.getDouble("thu");
            float chi = (float) item.getDouble("chi");
            entries.add(new BarEntry(i, new float[]{thu, chi}));
            labels.add(item.getString("month"));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thu - Chi");
        dataSet.setColors(new int[]{ColorTemplate.COLORFUL_COLORS[0], ColorTemplate.COLORFUL_COLORS[1]});
        dataSet.setStackLabels(new String[]{"Thu", "Chi"});

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.invalidate(); // Refresh biểu đồ
    }

    private void updateLineChart(JSONArray dailyData) throws JSONException {
        ArrayList<Entry> thuEntries = new ArrayList<>();
        ArrayList<Entry> chiEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject item = dailyData.getJSONObject(i);
            float thu = (float) item.getDouble("thu");
            float chi = (float) item.getDouble("chi");
            thuEntries.add(new Entry(i, thu));
            chiEntries.add(new Entry(i, chi));
            labels.add(item.getString("date"));
        }

        LineDataSet thuDataSet = new LineDataSet(thuEntries, "Thu nhập");
        thuDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);

        LineDataSet chiDataSet = new LineDataSet(chiEntries, "Chi tiêu");
        chiDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]);

        LineData lineData = new LineData(thuDataSet, chiDataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.invalidate();
    }
}