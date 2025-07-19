package com.example.orderclothes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.database.sqlite.SQLiteDatabase;
import com.example.orderclothes.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    TextView txtUsername, txtEmail;
    EditText edtUsername, edtEmail, edtPhone, edtAddress;
    Button btnEditInfo, btnSaveChanges, btnLogout, btnViewOrderHistory;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewOrderHistory = findViewById(R.id.btnViewOrderHistory);

        // Khởi tạo session và lấy thông tin user
        session = new SessionManager(this);
        User user = session.getCurrentUser();

        if (user != null) {
            txtUsername.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            edtUsername.setText(user.getFullName());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }

        // Mở các trường chỉnh sửa khi nhấn nút "Chỉnh sửa thông tin"
        btnEditInfo.setOnClickListener(v -> {
            edtUsername.setEnabled(true);
            edtEmail.setEnabled(true);
            edtPhone.setEnabled(true);
            edtAddress.setEnabled(true);
            txtUsername.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            edtUsername.setVisibility(View.VISIBLE);
            edtEmail.setVisibility(View.VISIBLE);
            edtPhone.setVisibility(View.VISIBLE);
            edtAddress.setVisibility(View.VISIBLE);
            btnEditInfo.setVisibility(View.GONE);
            btnSaveChanges.setVisibility(View.VISIBLE);
        });

        // Lưu thông tin thay đổi
        btnSaveChanges.setOnClickListener(v -> {
            String newUsername = edtUsername.getText().toString();
            String newEmail = edtEmail.getText().toString();
            String newPhone = edtPhone.getText().toString();
            String newAddress = edtAddress.getText().toString();
            updateUserInfo(newUsername, newEmail, newPhone, newAddress);
            Toast.makeText(this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
            txtUsername.setText(newUsername);
            txtEmail.setText(newEmail);
            txtUsername.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            edtUsername.setVisibility(View.GONE);
            edtEmail.setVisibility(View.GONE);
            edtPhone.setVisibility(View.GONE);
            edtAddress.setVisibility(View.GONE);
            btnEditInfo.setVisibility(View.VISIBLE);
            btnSaveChanges.setVisibility(View.GONE);
            edtUsername.setEnabled(false);
            edtEmail.setEnabled(false);
            edtPhone.setEnabled(false);
            edtAddress.setEnabled(false);
        });

        // Xử lý nút Xem lịch sử giao dịch
        btnViewOrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
        });

        // Xử lý đăng xuất
        btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            if (session != null) {
                session.logout();
                Log.d(TAG, "Session cleared, isLoggedIn: " + session.isLoggedIn());
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Log.d(TAG, "Redirected to LoginActivity");
            } else {
                Log.e(TAG, "SessionManager is null");
            }
        });

        // Thanh chuyển mục dưới
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, UserHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void updateUserInfo(String username, String email, String phone, String address) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
        db.update("users", values, "user_id = ?", new String[]{String.valueOf(session.getCurrentUser().getUserId())});
        db.close();
    }
}