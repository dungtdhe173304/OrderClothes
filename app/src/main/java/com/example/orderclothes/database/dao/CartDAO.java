package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Cart;
import com.example.orderclothes.models.CartItem;
import com.example.orderclothes.models.Product;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public CartDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // Lấy cart theo userId
    public Cart getCartByUserId(int userId) {
        Cart cart = null;
        open();
        try {
            Cursor cursor = database.query("cart", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cart = new Cart();
                cart.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_id")));
                cart.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                cart.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
                cart.setDiscountAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("discount_amount")));
                cart.setShippingFee(cursor.getDouble(cursor.getColumnIndexOrThrow("shipping_fee")));
                cart.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                cart.setVoucherId(cursor.getInt(cursor.getColumnIndexOrThrow("voucher_id")));
                cursor.close();
            }
        } finally {
            //close(); // BỎ DÒNG NÀY ĐI
        }
        return cart;
    }

    // Tạo cart mới cho user
    public long createCart(int userId) {
        open();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            result = database.insert("cart", null, values);
        } finally {
            close();
        }
        return result;
    }

    // Thêm hoặc cập nhật sản phẩm vào cart
    public void addToCart(int userId, Product product, int quantity) {
        open();
        try {
            Cart cart = getCartByUserId(userId);
            int cartId;
            if (cart == null) {
                cartId = (int) createCart(userId);
            } else {
                cartId = cart.getCartId();
            }
            // Kiểm tra sản phẩm đã có trong cart chưa
            Cursor cursor = database.query("cart_items", null, "cart_id = ? AND product_id = ?", new String[]{String.valueOf(cartId), String.valueOf(product.getProductId())}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Đã có, cập nhật số lượng
                int oldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int newQuantity = oldQuantity + quantity;
                ContentValues values = new ContentValues();
                values.put("quantity", newQuantity);
                database.update("cart_items", values, "cart_id = ? AND product_id = ?", new String[]{String.valueOf(cartId), String.valueOf(product.getProductId())});
                cursor.close();
            } else {
                // Chưa có, thêm mới
                ContentValues values = new ContentValues();
                values.put("cart_id", cartId);
                values.put("product_id", product.getProductId());
                values.put("quantity", quantity);
                values.put("unit_price", product.getPrice());
                values.put("size_id", 0);
                long result = database.insert("cart_items", null, values);
                if (result == -1) {
                    android.util.Log.e("CartDAO", "Insert cart_items failed: " + values.toString());
                }
            }
        } finally {
            close();
        }
    }

    // Lấy danh sách sản phẩm trong cart
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        open();
        try {
            Cart cart = getCartByUserId(userId);
            if (cart == null) return items;
            int cartId = cart.getCartId();
            String query = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.quantity, ci.unit_price, p.product_name, p.image_url " +
                    "FROM cart_items ci JOIN products p ON ci.product_id = p.product_id WHERE ci.cart_id = ?";
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(cartId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    CartItem item = new CartItem();
                    item.setCartItemId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_item_id")));
                    item.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_id")));
                    item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                    item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    item.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")));
                    item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                    item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                    items.add(item);
                }
                cursor.close();
            }
        } finally {
            //close(); // BỎ DÒNG NÀY ĐI
        }
        return items;
    }

    // Xóa sản phẩm khỏi cart
    public void removeCartItem(int cartItemId) {
        open();
        try {
            database.delete("cart_items", "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
        } finally {
            close();
        }
    }

    // Cập nhật số lượng sản phẩm trong cart
    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("quantity", newQuantity);
            database.update("cart_items", values, "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
        } finally {
            close();
        }
    }

    // Xóa toàn bộ cart của user (sau khi đặt hàng thành công)
    public void clearCart(int userId) {
        open();
        try {
            Cart cart = getCartByUserId(userId);
            if (cart != null) {
                int cartId = cart.getCartId();
                database.delete("cart_items", "cart_id = ?", new String[]{String.valueOf(cartId)});
            }
        } finally {
            close();
        }
    }
} 