package com.example.orderclothes.database.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.OrderItem;
import com.example.orderclothes.models.ReportItem;


import java.util.ArrayList;
import java.util.List;


public class OrderDAO {
    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase db;


    public OrderDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }


    // ======================== INSERT ========================
    public long insertOrder(Order orderActivity) {
        ContentValues values = new ContentValues();
        values.put("order_number", orderActivity.getOrderNumber());
        values.put("user_id", orderActivity.getUserId());
        values.put("order_date", orderActivity.getOrderDate());
        values.put("subtotal", orderActivity.getSubtotal());
        values.put("total_amount", orderActivity.getTotalAmount());
        values.put("customer_name", orderActivity.getCustomerName());
        values.put("customer_phone", orderActivity.getCustomerPhone());
        values.put("shipping_address", orderActivity.getShippingAddress());
        values.put("status", orderActivity.getStatus());
        return db.insert("orders", null, values);
    }


    public void insertOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            ContentValues values = new ContentValues();
            values.put("order_id", item.getOrderId());
            values.put("product_id", item.getProductId());
            values.put("product_name", item.getProductName());
            values.put("size_id", item.getSizeId());
            values.put("size_name", item.getSizeName());
            values.put("quantity", item.getQuantity());
            values.put("unit_price", item.getUnitPrice());
            values.put("total_price", item.getTotalPrice());
            db.insert("order_items", null, values);
        }
    }


    // ======================== GET ========================
    public List<Order> getAllOrderActivities() {
        List<Order> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY order_date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                order.setOrderNumber(cursor.getString(cursor.getColumnIndexOrThrow("order_number")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                order.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
                order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                order.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
                order.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC", new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
            order.setOrderNumber(cursor.getString(cursor.getColumnIndexOrThrow("order_number")));
            order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
            order.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
            order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
            order.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
            order.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
            order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
            order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
            list.add(order);
        }
        cursor.close();
        return list;
    }


    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM order_items WHERE order_id = ?", new String[]{String.valueOf(orderId)});
        while (cursor.moveToNext()) {
            OrderItem item = new OrderItem();
            item.setItemId(cursor.getInt(cursor.getColumnIndexOrThrow("item_id")));
            item.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
            item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
            item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            item.setSizeId(cursor.getInt(cursor.getColumnIndexOrThrow("size_id")));
            item.setSizeName(cursor.getString(cursor.getColumnIndexOrThrow("size_name")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
            item.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")));
            item.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
            list.add(item);
        }
        cursor.close();
        return list;
    }


    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY order_date DESC", null);
        while (cursor.moveToNext()) {
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
            list.add(order);
        }
        cursor.close();
        return list;
    }


    // ======================== UPDATE ========================
    public void updateOrderStatus(int orderId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update("orders", values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }


    // ======================== REPORT ========================
    public double getTotalByDate(String date) {
        Cursor cursor = db.rawQuery("SELECT SUM(total_amount) FROM orders WHERE DATE(order_date) = ?", new String[]{date});
        double total = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        cursor.close();
        return total;
    }


    public double getTotalByMonth(String month) {
        Cursor cursor = db.rawQuery("SELECT SUM(total_amount) FROM orders WHERE strftime('%Y-%m', order_date) = ?", new String[]{month});
        double total = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        cursor.close();
        return total;
    }


    public double getTotalByYear(String year) {
        Cursor cursor = db.rawQuery("SELECT SUM(total_amount) FROM orders WHERE strftime('%Y', order_date) = ?", new String[]{year});
        double total = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        cursor.close();
        return total;
    }


    public int getTotalOrdersByDay(String date) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM orders WHERE DATE(order_date) = ?", new String[]{date});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }


    public int getTotalOrdersByMonth(String month) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM orders WHERE strftime('%Y-%m', order_date) = ?", new String[]{month});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }


    public int getTotalOrdersByYear(String year) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM orders WHERE strftime('%Y', order_date) = ?", new String[]{year});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }


    public List<ReportItem> getReportItemByDay() {
        return getReport("DATE(order_date)", "report_date");
    }


    public List<ReportItem> getReportItemByMonth() {
        return getReport("strftime('%Y-%m', order_date)", "report_month");
    }


    public List<ReportItem> getReportItemByYear() {
        return getReport("strftime('%Y', order_date)", "report_year");
    }


    private List<ReportItem> getReport(String groupBy, String alias) {
        List<ReportItem> list = new ArrayList<>();
        String sql = "SELECT " + groupBy + " as " + alias + ", COUNT(*) as total_orders, SUM(total_amount) as total_amount " +
                "FROM orders GROUP BY " + alias + " ORDER BY " + alias + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String time = cursor.getString(0);
            int count = cursor.getInt(1);
            double total = cursor.getDouble(2);
            list.add(new ReportItem(time, count, total));
        }
        cursor.close();
        return list;
    }
}





