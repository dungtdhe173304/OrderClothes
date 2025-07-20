package com.example.orderclothes.activities;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderclothes.R;
import com.example.orderclothes.adapters.ReportAdapter;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.models.ReportItem;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView recyclerReport;
    private OrderDAO orderDAO;
    private ReportAdapter reportAdapter;
    private RadioGroup radioGroupReportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // DAO
        orderDAO = new OrderDAO(this);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Báo cáo thống kê");
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView setup
        recyclerReport = findViewById(R.id.recyclerReport);
        recyclerReport.setLayoutManager(new LinearLayoutManager(this));

        // RadioGroup xử lý loại thống kê
        radioGroupReportType = findViewById(R.id.radioGroupReportType);
        radioGroupReportType.setOnCheckedChangeListener((group, checkedId) -> {
            List<ReportItem> data = null;

            if (checkedId == R.id.radioDay) {
                data = orderDAO.getReportItemByDay();
            } else if (checkedId == R.id.radioMonth) {
                data = orderDAO.getReportItemByMonth();
            } else if (checkedId == R.id.radioYear) {
                data = orderDAO.getReportItemByYear();
            }

            if (data != null && !data.isEmpty()) {
                reportAdapter = new ReportAdapter(data);
                recyclerReport.setAdapter(reportAdapter);
            } else {
                Toast.makeText(this, "Không có dữ liệu thống kê", Toast.LENGTH_SHORT).show();
                recyclerReport.setAdapter(null);
            }
        });

        // Mặc định chọn loại thống kê theo ngày (radioDay đã checked sẵn trong XML)
        loadReportByDay();
    }

    private void loadReportByDay() {
        List<ReportItem> data = orderDAO.getReportItemByDay();
        if (data != null && !data.isEmpty()) {
            reportAdapter = new ReportAdapter(data);
            recyclerReport.setAdapter(reportAdapter);
        } else {
            Toast.makeText(this, "Không có dữ liệu thống kê theo ngày", Toast.LENGTH_SHORT).show();
        }
    }
}
