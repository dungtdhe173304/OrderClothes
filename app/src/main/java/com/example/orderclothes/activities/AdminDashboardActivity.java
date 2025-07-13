package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {
//a
    private TextView tvWelcome, tvTotalProducts, tvTotalOrders, tvTotalUsers, tvRevenue;
    private CardView cardManageProducts, cardManageOrders, cardManageUsers, cardReports;

    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        initObjects();
        setupToolbar();
        setupAdminInfo();
        loadDashboardData();
        setClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvRevenue = findViewById(R.id.tvRevenue);

        cardManageProducts = findViewById(R.id.cardManageProducts);
        cardManageOrders = findViewById(R.id.cardManageOrders);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardReports = findViewById(R.id.cardReports);
    }

    private void initObjects() {
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        // Kiểm tra session và quyền admin
        if (currentUser == null || !currentUser.isAdmin()) {
            redirectToLogin();
            return;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản trị hệ thống");
        }
    }

    private void setupAdminInfo() {
        if (currentUser != null) {
            tvWelcome.setText("Chào Admin " + currentUser.getFullName() + "!");
        }
    }

    private void loadDashboardData() {
        // TODO: Load thống kê từ database
        // Tạm thời hiển thị dữ liệu mẫu
        tvTotalProducts.setText("8");
        tvTotalOrders.setText("0");
        tvTotalUsers.setText("2");
        tvRevenue.setText("0 VNĐ");
    }

    private void setClickListeners() {
        cardManageProducts.setOnClickListener(v -> {
            // Chuyển đến quản lý sản phẩm
            Toast.makeText(this, "Đang phát triển - Quản lý sản phẩm", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ManageProductsActivity.class);
            // startActivity(intent);
        });

        cardManageOrders.setOnClickListener(v -> {
            // Chuyển đến quản lý đơn hàng
            Toast.makeText(this, "Đang phát triển - Quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ManageOrdersActivity.class);
            // startActivity(intent);
        });

        cardManageUsers.setOnClickListener(v -> {
            // Chuyển đến quản lý người dùng
            Toast.makeText(this, "Đang phát triển - Quản lý người dùng", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ManageUsersActivity.class);
            // startActivity(intent);
        });

        cardReports.setOnClickListener(v -> {
            // Chuyển đến báo cáo thống kê
            Toast.makeText(this, "Đang phát triển - Báo cáo thống kê", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ReportsActivity.class);
            // startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            performLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
