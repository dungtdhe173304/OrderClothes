package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.orderclothes.R;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.database.dao.ProductDAO;
import com.example.orderclothes.database.dao.UserDAO;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvTotalProducts, tvTotalOrders, tvTotalUsers, tvRevenue;
    private CardView cardManageProducts, cardManageOrders, cardManageUsers, cardReports;

    private SessionManager sessionManager;
    private User currentUser;
    private UserDAO userDAO;
    private ProductDAO productDAO;

    private final ActivityResultLauncher<Intent> userManageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDashboardData(); // Cập nhật lại tổng số user
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        initObjects();
        setupToolbar();
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
        userDAO = new UserDAO(this);
        productDAO = new ProductDAO(this);

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


    private void loadDashboardData() {
        int totalUsers = userDAO.getAllUsers().size();
        tvTotalUsers.setText(String.valueOf(totalUsers));

        int totalProducts = productDAO.getTotalProducts();
        tvTotalProducts.setText(String.valueOf(totalProducts));

        OrderDAO orderDAO = new OrderDAO(this);
        int totalOrders = orderDAO.getAllOrders().size();
        double totalRevenue = orderDAO.getAllOrders().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvRevenue.setText(String.format("%,.0f VNĐ", totalRevenue));
    }

    private void setClickListeners() {
        cardManageProducts.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminManageProductsActivity.class));
        });

        cardManageOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageOrdersActivity.class));
        });

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUserActivity.class);
            userManageLauncher.launch(intent);
        });

        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ReportActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
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
