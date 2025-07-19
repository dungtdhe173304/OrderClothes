package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.adapters.OrderHistoryAdapter;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.OrderItem;
import com.example.orderclothes.utils.SessionManager;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private OrderDAO orderDAO;
    private SessionManager sessionManager;
    private Button btnBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        btnBackToProfile = findViewById(R.id.btnBackToProfile);
        sessionManager = new SessionManager(this);
        orderDAO = new OrderDAO(this);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryAdapter = new OrderHistoryAdapter(order -> showOrderDetails(order));
        rvOrderHistory.setAdapter(orderHistoryAdapter);

        btnBackToProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        if (sessionManager.getCurrentUser() != null) {
            List<Order> orders = orderDAO.getOrdersByUserId(sessionManager.getCurrentUser().getUserId());
            if (orders.isEmpty()) {
                rvOrderHistory.setVisibility(View.GONE);
                TextView tvEmpty = new TextView(this);
                tvEmpty.setText("Không có đơn hàng nào.");
                tvEmpty.setTextSize(16);
                tvEmpty.setGravity(View.TEXT_ALIGNMENT_CENTER);
                ((LinearLayout) findViewById(android.R.id.content).getRootView()).addView(tvEmpty);
            } else {
                orderHistoryAdapter.setOrders(orders);
                orderHistoryAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showOrderDetails(Order order) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_details, null);
        builder.setView(dialogView);

        TextView tvOrderDate = dialogView.findViewById(R.id.tvOrderDate);
        TextView tvOrderItems = dialogView.findViewById(R.id.tvOrderItems);
        TextView tvOrderTotal = dialogView.findViewById(R.id.tvOrderTotal);
        Button btnBack = dialogView.findViewById(R.id.btnBack);

        tvOrderDate.setText("Thời gian: " + (order.getOrderDate() != null ? order.getOrderDate() : "N/A"));
        StringBuilder products = new StringBuilder();
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                products.append(item.getProductName()).append(" (x").append(item.getQuantity()).append(" - ")
                        .append(String.format("%,.0f", item.getUnitPrice())).append("đ), ");
            }
            if (products.length() > 0) products.setLength(products.length() - 2);
        } else {
            products.append("Không có sản phẩm.");
        }
        tvOrderItems.setText("Sản phẩm: " + products.toString());
        tvOrderTotal.setText("Tổng tiền: " + String.format("%,.0f", order.getTotalAmount()) + "đ");

        android.app.AlertDialog dialog = builder.create();

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}