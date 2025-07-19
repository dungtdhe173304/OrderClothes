package com.example.orderclothes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.orderclothes.models.User;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ROLE = "role";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Tạo session đăng nhập
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_PHONE, user.getPhone()); // Lưu số điện thoại
        editor.putString(KEY_ADDRESS, user.getAddress()); // Lưu địa chỉ
        editor.putString(KEY_ROLE, user.getRole());
        editor.commit();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin user hiện tại
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setUserId(pref.getInt(KEY_USER_ID, 0));
        user.setUsername(pref.getString(KEY_USERNAME, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setFullName(pref.getString(KEY_FULL_NAME, ""));
        user.setPhone(pref.getString(KEY_PHONE, "")); // Lấy số điện thoại
        user.setAddress(pref.getString(KEY_ADDRESS, "")); // Lấy địa chỉ
        user.setRole(pref.getString(KEY_ROLE, "user"));

        return user;
    }

    // Kiểm tra user có phải admin không
    public boolean isAdmin() {
        return "admin".equals(pref.getString(KEY_ROLE, "user"));
    }

    // Đăng xuất
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
