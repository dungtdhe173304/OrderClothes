package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.OrderActivity;
import com.example.orderclothes.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Insert Order
    public long insertOrder(OrderActivity orderActivity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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

        long id = db.insert("orders", null, values);
        db.close();
        return id;
    }

    // Insert Order Items
    public void insertOrderItems(List<OrderItem> orderItems) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        db.close();
    }

    // Get all orders (Admin/Staff use)
    public List<OrderActivity> getAllOrders() {
        List<OrderActivity> orderActivities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY order_date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                OrderActivity orderActivity = new OrderActivity();
                orderActivity.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                orderActivity.setOrderNumber(cursor.getString(cursor.getColumnIndexOrThrow("order_number")));
                orderActivity.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                orderActivity.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                orderActivity.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
                orderActivity.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                orderActivity.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
                orderActivity.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
                orderActivity.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                orderActivity.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                orderActivity.setOrderItems(getOrderItemsByOrderId(orderActivity.getOrderId()));
                orderActivities.add(orderActivity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderActivities;
    }

    // Get orders by user ID (for customer)
    public List<OrderActivity> getOrdersByUserId(int userId) {
        List<OrderActivity> orderActivities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            OrderActivity orderActivity = new OrderActivity();
            orderActivity.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
            orderActivity.setOrderNumber(cursor.getString(cursor.getColumnIndexOrThrow("order_number")));
            orderActivity.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            orderActivity.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
            orderActivity.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
            orderActivity.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
            orderActivity.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
            orderActivity.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
            orderActivity.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
            orderActivity.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            orderActivity.setOrderItems(getOrderItemsByOrderId(orderActivity.getOrderId()));
            orderActivities.add(orderActivity);
        }
        cursor.close();
        db.close();
        return orderActivities;
    }

    // Get order items by order ID
    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM order_items WHERE order_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

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
            orderItems.add(item);
        }
        cursor.close();
        return orderItems;
    }

    // Update order status
    public void updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update("orders", values, "order_id = ?", new String[]{String.valueOf(orderId)});
        db.close();
    }
}
