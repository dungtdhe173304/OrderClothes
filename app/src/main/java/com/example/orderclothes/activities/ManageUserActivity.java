package com.example.orderclothes.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private UserDAO userDAO;

    private LinearLayout userFormLayout;
    private EditText etFullName, etEmail, etPassword;
    private Button btnSaveUser;

    private boolean isEditing = false;
    private User editingUser;
    private SessionManager sessionManager;
    private String loggedInEmail;

    private boolean isDataChanged = false; // ✅ Theo dõi thay đổi dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        // ✅ Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý người dùng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ✅ Session
        sessionManager = new SessionManager(this);
        loggedInEmail = sessionManager.getEmail();

        // ✅ DAO + RecyclerView
        userDAO = new UserDAO(this);
        recyclerView = findViewById(R.id.recyclerUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(
                this,
                userList,
                sessionManager.getCurrentUser().getEmail(),
                user -> showUserForm(user),
                user -> {
                    boolean deleted = userDAO.deleteUserById(user.getUserId());
                    if (deleted) {
                        Toast.makeText(this, "Đã xoá người dùng", Toast.LENGTH_SHORT).show();
                        isDataChanged = true; // ✅ Có thay đổi
                        reloadUserList();
                    } else {
                        Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
        recyclerView.setAdapter(userAdapter);

        // ✅ Form
        userFormLayout = findViewById(R.id.userFormLayout);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSaveUser = findViewById(R.id.btnSaveUser);

        // ✅ Nút lưu
        btnSaveUser.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || (!isEditing && password.isEmpty())) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (isEditing && editingUser != null) {
                editingUser.setFullName(fullName);
                editingUser.setEmail(email);
                if (!password.isEmpty()) {
                    editingUser.setPassword(password);
                }
                success = userDAO.updateUser(editingUser);
                Toast.makeText(this, success ? "Đã cập nhật" : "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
            } else {
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setUsername(email.split("@")[0]);
                newUser.setRole("user");
                success = userDAO.insertUser(newUser);
                Toast.makeText(this, success ? "Đã thêm người dùng" : "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            }

            if (success) {
                isDataChanged = true; // ✅ Có thay đổi thì đánh dấu
            }

            userFormLayout.setVisibility(View.GONE);
            reloadUserList();
        });

        reloadUserList();
    }

    private void reloadUserList() {
        userList.clear();
        userList.addAll(userDAO.getAllUsers());
        userAdapter.notifyDataSetChanged();
    }

    private void showUserForm(User user) {
        userFormLayout.setVisibility(View.VISIBLE);
        if (user != null) {
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPassword.setText("");
            isEditing = true;
            editingUser = user;
        } else {
            etFullName.setText("");
            etEmail.setText("");
            etPassword.setText("");
            isEditing = false;
        }
    }

    // ✅ Menu Thêm người dùng
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            isEditing = false;
            editingUser = null;
            showUserForm(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ✅ Khi bấm Back: trả về RESULT_OK nếu có thay đổi
    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
