package com.example.orderclothes.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderclothes.R;
import com.example.orderclothes.adapters.OrderAdapter;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.models.OrderActivity;
import com.google.android.material.appbar.MaterialToolbar; // ✅ Đúng import
import androidx.appcompat.app.ActionBar;

import java.util.List;

public class ManageOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderDAO = new OrderDAO(this);
        List<OrderActivity> orderActivityList = orderDAO.getAllOrderActivities();
        orderAdapter = new OrderAdapter(this, orderActivityList); // Use the class field
        recyclerView.setAdapter(orderAdapter);

        // Thiết lập MaterialToolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        // Bắt sự kiện nút back
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
