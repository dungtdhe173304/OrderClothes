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
    private static final String KEY_ROLE = "role";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // ✅ Lưu thông tin khi đăng nhập
    public void createLoginSession(User user) {
        if (user == null) return;

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_ROLE, user.getRole() != null ? user.getRole() : "user");
        editor.apply();
    }

    // ✅ Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ✅ Lấy thông tin người dùng hiện tại
    public User getCurrentUser() {
        if (!isLoggedIn()) return null;

        User user = new User();
        user.setUserId(pref.getInt(KEY_USER_ID, 0));
        user.setUsername(pref.getString(KEY_USERNAME, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setFullName(pref.getString(KEY_FULL_NAME, ""));
        user.setRole(pref.getString(KEY_ROLE, "user"));
        return user;
    }

    // ✅ Lấy email hiện tại
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    // ✅ Lấy vai trò hiện tại
    public String getRole() {
        return pref.getString(KEY_ROLE, "user");
    }

    // ✅ Kiểm tra có phải Admin
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(pref.getString(KEY_ROLE, "user"));
    }

    // ✅ Đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
