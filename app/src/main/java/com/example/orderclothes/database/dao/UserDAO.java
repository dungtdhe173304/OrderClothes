package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.orderclothes.models.User;
import com.example.orderclothes.database.DatabaseHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Đăng nhập
    public User loginUser(String usernameOrEmail, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        String hashedPassword = hashPassword(password);

        String query = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail, hashedPassword});

        try {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return user;
    }

    // Đăng ký user mới
    public boolean registerUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("password", hashPassword(user.getPassword()));
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("role", user.getRole() != null ? user.getRole() : "user");

            long result = db.insert("users", null, values);
            return result != -1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Kiểm tra username đã tồn tại
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT 1 FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    // Kiểm tra email đã tồn tại
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT 1 FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    // Mã hóa password bằng SHA-256 với UTF-8 encoding
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8")); // Explicit UTF-8
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return password;
        }
    }
}