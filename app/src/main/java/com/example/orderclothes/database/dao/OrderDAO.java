package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final SQLiteDatabase db;

    public OrderDAO(Context context) {
        db = DatabaseHelper.getInstance(context).getWritableDatabase(); // Cần writable để update
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
}
