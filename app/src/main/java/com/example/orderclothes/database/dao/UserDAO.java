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

    // Thêm user từ Admin (khác với đăng ký)
    public boolean insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("password", hashPassword(user.getPassword()));
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("role", user.getRole());

            long result = db.insert("users", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("role", user.getRole());

            // Nếu có password mới thì cập nhật luôn
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                values.put("password", hashPassword(user.getPassword()));
            }

            int rowsAffected = db.update("users", values, "user_id = ?", new String[]{String.valueOf(user.getUserId())});
            return rowsAffected > 0;
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

    // Mã hóa password bằng SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    // Lấy danh sách toàn bộ người dùng
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }

    //xoá user
    public boolean deleteUserById(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int rows = db.delete("users", "user_id = ?", new String[]{String.valueOf(userId)});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Lấy tổng số người dùng
    public int getTotalUsers() {
        int total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }
}
