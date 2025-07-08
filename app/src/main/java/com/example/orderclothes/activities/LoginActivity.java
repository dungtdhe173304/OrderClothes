package com.example.orderclothes.activities;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import com.example.orderclothes.database.dao.UserDAO;
import com.example.orderclothes.utils.SessionManager;
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsernameOrEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initObjects();
        setClickListeners();

        // Kiểm tra nếu đã đăng nhập thì chuyển sang trang tương ứng
        if (sessionManager.isLoggedIn()) {
            redirectToHomePage();
        }
    }

    private void initViews() {
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void initObjects() {
        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);
    }

    private void setClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(usernameOrEmail)) {
            etUsernameOrEmail.setError("Vui lòng nhập username hoặc email");
            etUsernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Thực hiện đăng nhập
        User user = userDAO.loginUser(usernameOrEmail, password);

        if (user != null) {
            // Đăng nhập thành công
            sessionManager.createLoginSession(user);

            String welcomeMessage = "Chào mừng " + user.getFullName() + "!";
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();

            redirectToHomePage();
        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Thông tin đăng nhập không chính xác!", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToHomePage() {
        Intent intent;

        if (sessionManager.isAdmin()) {
            // Chuyển đến AdminDashboardActivity
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            // Chuyển đến UserHomeActivity
            intent = new Intent(LoginActivity.this, UserHomeActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
