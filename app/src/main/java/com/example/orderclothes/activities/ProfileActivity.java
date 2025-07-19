package com.example.orderclothes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.database.dao.UserDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView txtUsername, txtEmail, txtPhone, txtAddress;
    private EditText edtUsername, edtEmail, edtPhone, edtAddress;
    private Button btnEditInfo, btnSave, btnLogout, btnViewOrderHistory, btnChangePassword;
    private SessionManager session;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewOrderHistory = findViewById(R.id.btnViewOrderHistory);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Khởi tạo session và lấy thông tin user
        session = new SessionManager(this);
        userDAO = new UserDAO(this);
        currentUser = session.getCurrentUser();

        if (currentUser != null) {
            displayUserInfo(currentUser);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        // Mở các trường chỉnh sửa khi nhấn nút "Chỉnh sửa thông tin"
        btnEditInfo.setOnClickListener(v -> {
            // Hiển thị EditText với giá trị hiện tại và ẩn TextView
            enableEditFields(true);
            txtUsername.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            txtPhone.setVisibility(View.GONE);
            txtAddress.setVisibility(View.GONE);
            btnEditInfo.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            // Điền giá trị hiện tại vào EditText
            edtUsername.setText(currentUser.getFullName());
            edtEmail.setText(currentUser.getEmail());
            edtPhone.setText(currentUser.getPhone());
            edtAddress.setText(currentUser.getAddress());
        });

        // Lưu thông tin thay đổi khi nhấn nút "Lưu"
        btnSave.setOnClickListener(v -> {
            String newUsername = edtUsername.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newAddress = edtAddress.getText().toString().trim();

            if (validateUserInfo(newUsername, newEmail, newPhone, newAddress)) {
                updateUserInfo(newUsername, newEmail, newPhone, newAddress);
                Toast.makeText(this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
                // Cập nhật giao diện ngay lập tức với thông tin mới
                displayUserInfo(currentUser);
                enableEditFields(false); // Tắt chỉnh sửa
                txtUsername.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtPhone.setVisibility(View.VISIBLE);
                txtAddress.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnEditInfo.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

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

    private void displayUserInfo(User user) {
        txtUsername.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
    }

    private void enableEditFields(boolean enabled) {
        edtUsername.setEnabled(enabled);
        edtEmail.setEnabled(enabled);
        edtPhone.setEnabled(enabled);
        edtAddress.setEnabled(enabled);
        edtUsername.setVisibility(enabled ? View.VISIBLE : View.GONE);
        edtEmail.setVisibility(enabled ? View.VISIBLE : View.GONE);
        edtPhone.setVisibility(enabled ? View.VISIBLE : View.GONE);
        edtAddress.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private boolean validateUserInfo(String username, String email, String phone, String address) {
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUserInfo(String username, String email, String phone, String address) {
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", username);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);

        int rowsAffected = db.update("users", values, "user_id = ?", new String[]{String.valueOf(currentUser.getUserId())});
        db.close();

        if (rowsAffected > 0) {
            currentUser.setFullName(username);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            session.createLoginSession(currentUser); // Cập nhật session
        } else {
            Toast.makeText(this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        final EditText oldPasswordEditText = dialogView.findViewById(R.id.old_password_edittext);
        final EditText newPasswordEditText = dialogView.findViewById(R.id.new_password_edittext);
        final EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password_edittext);

        builder.setTitle("Đổi mật khẩu")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String oldPassword = oldPasswordEditText.getText().toString().trim();
                    String newPassword = newPasswordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                    if (validatePasswords(oldPassword, newPassword, confirmPassword)) {
                        updatePassword(oldPassword, newPassword);
                    }
                })
                .setNeutralButton("Quay lại", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private boolean validatePasswords(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updatePassword(String oldPassword, String newPassword) {
        if (currentUser != null) {
            String hashedOldPassword = userDAO.hashPassword(oldPassword);
            String hashedNewPassword = userDAO.hashPassword(newPassword);

            SQLiteDatabase db = DatabaseHelper.getInstance(this).getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT password FROM users WHERE user_id = ?", new String[]{String.valueOf(currentUser.getUserId())});
            String dbPassword = null;
            if (cursor.moveToFirst()) {
                dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }
            cursor.close();
            db.close();

            if (hashedOldPassword.equals(dbPassword)) {
                ContentValues values = new ContentValues();
                values.put("password", hashedNewPassword);

                SQLiteDatabase dbUpdate = DatabaseHelper.getInstance(this).getWritableDatabase();
                int rowsAffected = dbUpdate.update("users", values, "user_id = ?", new String[]{String.valueOf(currentUser.getUserId())});
                dbUpdate.close();

                if (rowsAffected > 0) {
                    currentUser.setPassword(hashedNewPassword);
                    session.createLoginSession(currentUser);
                    Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}