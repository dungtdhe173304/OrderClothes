package com.example.orderclothes.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.orderclothes.database.DatabaseHelper;

public class AdminDAO {
    private DatabaseHelper dbHelper;

    public AdminDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Lấy tổng số người dùng (không bao gồm admin)
    public int getTotalUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM users WHERE role = 'user'";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    // Lấy tổng số đơn hàng
    public int getTotalOrders() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM orders";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    // Lấy tổng số sản phẩm
    public int getTotalProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM products WHERE is_active = 1";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    // Lấy tổng doanh thu
    public double getTotalRevenue() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(total_amount) FROM orders WHERE status != 'cancelled'";
        Cursor cursor = db.rawQuery(query, null);

        double revenue = 0.0;
        if (cursor.moveToFirst()) {
            revenue = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return revenue;
    }

    // Lấy số đơn hàng theo trạng thái
    public int getOrderCountByStatus(String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM orders WHERE status = ?";
        Cursor cursor = db.rawQuery(query, new String[]{status});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    // Lấy doanh thu theo tháng
    public double getRevenueByMonth(int year, int month) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(total_amount) FROM orders " +
                "WHERE strftime('%Y', order_date) = ? " +
                "AND strftime('%m', order_date) = ? " +
                "AND status != 'cancelled'";

        String yearStr = String.valueOf(year);
        String monthStr = String.format("%02d", month);

        Cursor cursor = db.rawQuery(query, new String[]{yearStr, monthStr});

        double revenue = 0.0;
        if (cursor.moveToFirst()) {
            revenue = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return revenue;
    }
}
