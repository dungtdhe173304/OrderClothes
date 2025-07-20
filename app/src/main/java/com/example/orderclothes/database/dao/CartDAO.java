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
import android.util.Log;

public class CartDAO {
    private DatabaseHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Lấy cart theo userId
    public Cart getCartByUserId(int userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cart cart = null;
        Cursor cursor = null;
        try {
            cursor = database.query("cart", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cart = new Cart();
                cart.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_id")));
                cart.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                cart.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
                cart.setDiscountAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("discount_amount")));
                cart.setShippingFee(cursor.getDouble(cursor.getColumnIndexOrThrow("shipping_fee")));
                cart.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                cart.setVoucherId(cursor.getInt(cursor.getColumnIndexOrThrow("voucher_id")));
            }
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in getCartByUserId: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cart;
    }

    // Tạo cart mới cho user
    public long createCart(int userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        try {
            return database.insert("cart", null, values);
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in createCart: " + e.getMessage());
            return -1;
        }
    }

    // Thêm hoặc cập nhật sản phẩm vào cart
    public void addToCart(int userId, Product product, int sizeId, int quantity) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            // Kiểm tra tồn kho của size
            Cursor cursor = database.rawQuery("SELECT stock_quantity FROM product_sizes WHERE size_id = ?", new String[]{String.valueOf(sizeId)});
            if (cursor.moveToFirst()) {
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock_quantity"));
                if (quantity > stock) {
                    throw new IllegalArgumentException("Số lượng vượt quá tồn kho cho kích thước này!");
                }
            } else {
                throw new IllegalArgumentException("Kích thước không tồn tại!");
            }
            cursor.close();

            database.beginTransaction();
            Cart cart = getCartByUserId(userId);
            int cartId;
            if (cart == null) {
                cartId = (int) createCart(userId);
                android.util.Log.d("CartDAO", "Created new cart with ID: " + cartId);
            } else {
                cartId = cart.getCartId();
                android.util.Log.d("CartDAO", "Using existing cart with ID: " + cartId);
            }

            // Kiểm tra sản phẩm đã có trong cart với size_id cụ thể chưa
            cursor = database.query("cart_items", null, "cart_id = ? AND product_id = ? AND size_id = ?",
                    new String[]{String.valueOf(cartId), String.valueOf(product.getProductId()), String.valueOf(sizeId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Đã có, cập nhật số lượng
                int oldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int newQuantity = oldQuantity + quantity;
                ContentValues values = new ContentValues();
                values.put("quantity", newQuantity);
                int rows = database.update("cart_items", values, "cart_id = ? AND product_id = ? AND size_id = ?",
                        new String[]{String.valueOf(cartId), String.valueOf(product.getProductId()), String.valueOf(sizeId)});
                android.util.Log.d("CartDAO", "Updated quantity for product " + product.getProductId() + ", size " + sizeId + ": " + newQuantity + ", rows affected: " + rows);
                cursor.close();
            } else {
                // Chưa có, thêm mới
                ContentValues values = new ContentValues();
                values.put("cart_id", cartId);
                values.put("product_id", product.getProductId());
                values.put("size_id", sizeId);
                values.put("quantity", quantity);
                values.put("unit_price", product.getPrice());
                long result = database.insert("cart_items", null, values);
                android.util.Log.d("CartDAO", "Inserted new cart item for product " + product.getProductId() + ", size " + sizeId + ", result: " + result);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in addToCart: " + e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }

    // Lấy danh sách sản phẩm trong cart
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            Cart cart = getCartByUserId(userId);
            if (cart == null) return items;
            int cartId = cart.getCartId();
            String query = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.size_id, ci.quantity, ci.unit_price, " +
                    "p.product_name, p.image_url, ps.size_name " +
                    "FROM cart_items ci " +
                    "JOIN products p ON ci.product_id = p.product_id " +
                    "LEFT JOIN product_sizes ps ON ci.size_id = ps.size_id " +
                    "WHERE ci.cart_id = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(cartId)});
            while (cursor.moveToNext()) {
                CartItem item = new CartItem();
                item.setCartItemId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_item_id")));
                item.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow("cart_id")));
                item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                item.setSizeId(cursor.getInt(cursor.getColumnIndexOrThrow("size_id")));
                item.setSizeName(cursor.getString(cursor.getColumnIndexOrThrow("size_name")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                item.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")));
                item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                items.add(item);
            }
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in getCartItems: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    // Xóa sản phẩm khỏi cart
    public void removeCartItem(int cartItemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.delete("cart_items", "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in removeCartItem: " + e.getMessage());
        }
    }

    // Cập nhật số lượng sản phẩm trong cart
    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("quantity", newQuantity);
            database.update("cart_items", values, "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in updateCartItemQuantity: " + e.getMessage());
        }
    }

    // Xóa toàn bộ cart của user (sau khi đặt hàng thành công)
    public void clearCart(int userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            Cart cart = getCartByUserId(userId);
            if (cart != null) {
                int cartId = cart.getCartId();
                database.delete("cart_items", "cart_id = ?", new String[]{String.valueOf(cartId)});
            }
        } catch (Exception e) {
            android.util.Log.e("CartDAO", "Error in clearCart: " + e.getMessage());
        }
    }
}