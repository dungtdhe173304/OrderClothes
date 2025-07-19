package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public ProductDAO(Context context) {
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

    // Lấy tổng số sản phẩm đang hoạt động
    public int getTotalProducts() {
        int count = 0;
        open();
        Cursor cursor = null;

        try {
            cursor = database.rawQuery("SELECT COUNT(*) FROM products WHERE is_active = 1", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return count;
    }

    // Lấy tất cả sản phẩm active
    public List<Product> getAllActiveProducts() {
        List<Product> products = new ArrayList<>();
        open();
        Cursor cursor = null;

        try {
            cursor = database.query("products", null, "is_active = 1", null, null, null, "created_at DESC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return products;
    }

    // Lấy tất cả sản phẩm (bao gồm cả không active)
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        open();
        Cursor cursor = null;

        try {
            cursor = database.query("products", null, null, null, null, null, "created_at DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return products;
    }

    // Lấy sản phẩm theo category
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        open();
        Cursor cursor = null;

        try {
            cursor = database.query("products", null,
                    "category_id = ? AND is_active = 1",
                    new String[]{String.valueOf(categoryId)},
                    null, null, "created_at DESC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return products;
    }

    // Tìm kiếm sản phẩm
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        open();
        Cursor cursor = null;

        try {
            String selection = "(product_name LIKE ? OR brand LIKE ?) AND is_active = 1";
            String[] selectionArgs = {"%" + keyword + "%", "%" + keyword + "%"};

            cursor = database.query("products", null, selection, selectionArgs, null, null, "created_at DESC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return products;
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int productId) {
        Product product = null;
        open();
        Cursor cursor = null;

        try {
            cursor = database.query("products", null, "product_id = ?",
                    new String[]{String.valueOf(productId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                product = createProductFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }

        return product;
    }

    // Thêm sản phẩm mới
    public long insertProduct(Product product) {
        long result = -1;
        open();

        try {
            ContentValues values = new ContentValues();
            values.put("product_name", product.getProductName());
            values.put("description", product.getDescription());
            values.put("price", product.getPrice());
            values.put("category_id", product.getCategoryId());
            values.put("brand", product.getBrand());
            values.put("material", product.getMaterial());
            values.put("image_url", product.getImageUrl());
            values.put("stock_quantity", product.getStockQuantity());
            values.put("is_active", product.isActive() ? 1 : 0);

            result = database.insert("products", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    // Cập nhật sản phẩm
    public int updateProduct(Product product) {
        int result = 0;
        open();

        try {
            ContentValues values = new ContentValues();
            values.put("product_name", product.getProductName());
            values.put("description", product.getDescription());
            values.put("price", product.getPrice());
            values.put("category_id", product.getCategoryId());
            values.put("brand", product.getBrand());
            values.put("material", product.getMaterial());
            values.put("image_url", product.getImageUrl());
            values.put("stock_quantity", product.getStockQuantity());
            values.put("is_active", product.isActive() ? 1 : 0);

            result = database.update("products", values, "product_id = ?",
                    new String[]{String.valueOf(product.getProductId())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    // Xoá sản phẩm theo ID
    public int deleteProduct(int productId) {
        int result = 0;
        open();

        try {
            result = database.delete("products", "product_id = ?", new String[]{String.valueOf(productId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    // Helper: Tạo đối tượng Product từ Cursor
    private Product createProductFromCursor(Cursor cursor) {
        Product product = new Product();
        product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
        product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
        product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
        product.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
        product.setMaterial(cursor.getString(cursor.getColumnIndexOrThrow("material")));
        product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
        product.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("stock_quantity")));
        product.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
        product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        product.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return product;
    }
}
