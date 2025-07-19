package com.example.orderclothes.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor; // Added import for Cursor

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.database.dao.UserDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.database.sqlite.SQLiteDatabase;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    TextView txtUsername, txtEmail;
    EditText edtUsername, edtEmail, edtPhone, edtAddress;
    Button btnEditInfo, btnSaveChanges, btnLogout, btnViewOrderHistory, btnChangePassword;
    SessionManager session;
    UserDAO userDAO;

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
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Khởi tạo session và lấy thông tin user
        session = new SessionManager(this);
        userDAO = new UserDAO(this);
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
                .setNeutralButton("Quay lại", (dialog, which) -> {
                    // Do nothing, just dismiss the dialog to return to ProfileActivity
                    dialog.dismiss();
                })
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
        User currentUser = session.getCurrentUser();
        if (currentUser != null) {
            String hashedOldPassword = userDAO.hashPassword(oldPassword);
            String hashedNewPassword = userDAO.hashPassword(newPassword);
            Log.d(TAG, "Input Old Password: " + oldPassword);
            Log.d(TAG, "Hashed Old Password: " + hashedOldPassword);
            Log.d(TAG, "Stored Password (from Session): " + currentUser.getPassword());

            // Fetch password directly from database to compare
            SQLiteDatabase db = DatabaseHelper.getInstance(this).getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT password FROM users WHERE user_id = ?", new String[]{String.valueOf(currentUser.getUserId())});
            String dbPassword = null;
            if (cursor.moveToFirst()) {
                dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }
            cursor.close();
            db.close();
            Log.d(TAG, "Stored Password (from Database): " + dbPassword);

            if (hashedOldPassword.equals(currentUser.getPassword())) {
                ContentValues values = new ContentValues();
                values.put("password", hashedNewPassword);

                SQLiteDatabase dbUpdate = DatabaseHelper.getInstance(this).getWritableDatabase();
                int rowsAffected = dbUpdate.update("users", values, "user_id = ?", new String[]{String.valueOf(currentUser.getUserId())});
                dbUpdate.close();

                if (rowsAffected > 0) {
                    Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    currentUser.setPassword(hashedNewPassword);
                    session.createLoginSession(currentUser); // Cập nhật session
                } else {
                    Toast.makeText(this, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show();
            }
        }
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

        // Cập nhật session
        User updatedUser = session.getCurrentUser();
        updatedUser.setFullName(username);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);
        updatedUser.setAddress(address);
        session.createLoginSession(updatedUser);
    }
}