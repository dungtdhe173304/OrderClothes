package com.example.orderclothes.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderclothes.R;
import com.example.orderclothes.adapters.UserAdapter;
import com.example.orderclothes.database.dao.UserDAO;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private final List<User> userList = new ArrayList<>();
    private UserDAO userDAO;

    private MaterialCardView userFormLayout;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSaveUser;
    private Spinner spRole;

    private boolean isEditing = false;
    private User editingUser;
    private boolean isDataChanged = false;
    private MaterialButton btnCancelUser;


    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý người dùng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnCancelUser = findViewById(R.id.btnCancelUser);
        btnCancelUser.setOnClickListener(v -> {
            userFormLayout.setVisibility(View.GONE);
            isEditing = false;
            editingUser = null;
        });
        toolbar.setNavigationOnClickListener(v -> finish());

        // Session
        sessionManager = new SessionManager(this);

        // DAO & RecyclerView
        userDAO = new UserDAO(this);
        recyclerView = findViewById(R.id.recyclerUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(
                this,
                userList,
                sessionManager.getCurrentUser().getEmail(),
                this::showUserForm,
                user -> {
                    if (userDAO.deleteUserById(user.getUserId())) {
                        Toast.makeText(this, "Đã xoá người dùng", Toast.LENGTH_SHORT).show();
                        isDataChanged = true;
                        reloadUserList();
                    } else {
                        Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
        recyclerView.setAdapter(userAdapter);

        // Form
        userFormLayout    = findViewById(R.id.userFormLayout);
        etFullName        = findViewById(R.id.etFullName);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSaveUser       = findViewById(R.id.btnSaveUser);
        spRole            = findViewById(R.id.spRole);

        // Role Spinner setup
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"user", "admin"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        btnSaveUser.setOnClickListener(v -> saveUser());

        reloadUserList();
    }

    private void saveUser() {
        String fullName = etFullName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String pwd      = etPassword.getText().toString().trim();
        String rePwd    = etConfirmPassword.getText().toString().trim();
        String role     = spRole.getSelectedItem().toString();

        if (!isEditing) {
            if (fullName.isEmpty() || email.isEmpty() || pwd.isEmpty() || rePwd.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pwd.equals(rePwd)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (!pwd.isEmpty() && !pwd.equals(rePwd)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean success;
        if (isEditing && editingUser != null) {
            if (!pwd.isEmpty()) {
                editingUser.setPassword(pwd);
            }
            editingUser.setRole(role); // ✅ Cho phép cập nhật vai trò
            success = userDAO.updateUser(editingUser);
            Toast.makeText(this, success ? "Đã cập nhật" : "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
        } else {
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(pwd);
            newUser.setUsername(email.split("@")[0]);
            newUser.setRole("user"); // luôn tạo mới với role "user"
            success = userDAO.insertUser(newUser);
            Toast.makeText(this, success ? "Đã thêm người dùng" : "Email đã tồn tại", Toast.LENGTH_SHORT).show();
        }

        if (success) {
            isDataChanged = true;
            userFormLayout.setVisibility(View.GONE);
            reloadUserList();
        }
    }

    private void showUserForm(User user) {
        userFormLayout.setVisibility(View.VISIBLE);

        if (user != null) {
            isEditing = true;
            editingUser = user;

            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPassword.setText("");
            etConfirmPassword.setText("");
            spRole.setVisibility(View.VISIBLE);
            spRole.setSelection(user.getRole().equals("admin") ? 1 : 0);

            // ❌ Không cho chỉnh sửa email và họ tên
            etFullName.setEnabled(false);
            etEmail.setEnabled(false);
        } else {
            isEditing = false;
            editingUser = null;

            etFullName.setText("");
            etEmail.setText("");
            etPassword.setText("");
            etConfirmPassword.setText("");
            spRole.setVisibility(View.GONE);

            etFullName.setEnabled(true);
            etEmail.setEnabled(true);
        }
    }

    private void reloadUserList() {
        userList.clear();
        userList.addAll(userDAO.getAllUsers());
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            showUserForm(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if (isDataChanged) setResult(RESULT_OK);
        super.finish();
    }
}
