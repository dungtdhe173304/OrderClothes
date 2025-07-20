package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.ReportItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final SQLiteDatabase db;

    public OrderDAO(Context context) {
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY order_date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(cursor.getColumnIndexOrThrow("order_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("order_number")),
                        cursor.getString(cursor.getColumnIndexOrThrow("customer_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("shipping_fee")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status"))
                );
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update("orders", values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }

    public double getTotalByDate(String date) {
        Cursor cursor = db.rawQuery(
                "SELECT SUM(total_amount) FROM orders WHERE DATE(order_date) = ?",
                new String[]{date});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getTotalByMonth(String month) {
        Cursor cursor = db.rawQuery(
                "SELECT SUM(total_amount) FROM orders WHERE strftime('%Y-%m', order_date) = ?",
                new String[]{month});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getTotalByYear(String year) {
        Cursor cursor = db.rawQuery(
                "SELECT SUM(total_amount) FROM orders WHERE strftime('%Y', order_date) = ?",
                new String[]{year});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public int getTotalOrdersByDay(String date) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = ?",
                new String[]{date});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalOrdersByMonth(String month) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders WHERE strftime('%Y-%m', order_date) = ?",
                new String[]{month});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalOrdersByYear(String year) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM orders WHERE strftime('%Y', order_date) = ?",
                new String[]{year});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ====== Các phương thức trả về danh sách String (giữ nguyên) ======

    public List<String> getReportByDay() {
        List<String> report = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as report_date, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY DATE(order_date) ORDER BY DATE(order_date) DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("report_date"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                report.add(date + ": " + count + " đơn hàng - " + String.format("%,.0f", total) + "đ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return report;
    }

    public List<String> getReportByMonth() {
        List<String> report = new ArrayList<>();
        String sql = "SELECT strftime('%Y-%m', order_date) as report_month, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY report_month ORDER BY report_month DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String month = cursor.getString(cursor.getColumnIndexOrThrow("report_month"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                report.add(month + ": " + count + " đơn hàng - " + String.format("%,.0f", total) + "đ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return report;
    }

    public List<String> getReportByYear() {
        List<String> report = new ArrayList<>();
        String sql = "SELECT strftime('%Y', order_date) as report_year, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY report_year ORDER BY report_year DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String year = cursor.getString(cursor.getColumnIndexOrThrow("report_year"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                report.add(year + ": " + count + " đơn hàng - " + String.format("%,.0f", total) + "đ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return report;
    }

    // ====== Các phương thức mới trả về ReportItem (chuyên nghiệp hơn) ======

    public List<ReportItem> getReportItemByDay() {
        List<ReportItem> reportList = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as report_date, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY DATE(order_date) ORDER BY DATE(order_date) DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("report_date"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                reportList.add(new ReportItem(date, count, total));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reportList;
    }

    public List<ReportItem> getReportItemByMonth() {
        List<ReportItem> reportList = new ArrayList<>();
        String sql = "SELECT strftime('%Y-%m', order_date) as report_month, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY report_month ORDER BY report_month DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String month = cursor.getString(cursor.getColumnIndexOrThrow("report_month"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                reportList.add(new ReportItem(month, count, total));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reportList;
    }

    public List<ReportItem> getReportItemByYear() {
        List<ReportItem> reportList = new ArrayList<>();
        String sql = "SELECT strftime('%Y', order_date) as report_year, COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY report_year ORDER BY report_year DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String year = cursor.getString(cursor.getColumnIndexOrThrow("report_year"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("total_orders"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                reportList.add(new ReportItem(year, count, total));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reportList;
    }
}
