package com.example.orderclothes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.orderclothes.database.DatabaseHelper;
import com.example.orderclothes.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public CategoryDAO(Context context) {
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

    // Lấy tất cả categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        open();

        try {
            Cursor cursor = database.query("categories", null, null, null, null, null, "category_name ASC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Category category = new Category();
                    category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                    category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                    category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    category.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                    category.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

                    categories.add(category);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return categories;
    }

    // Lấy category theo ID
    public Category getCategoryById(int categoryId) {
        Category category = null;
        open();

        try {
            Cursor cursor = database.query("categories", null, "category_id = ?",
                    new String[]{String.valueOf(categoryId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                category.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                category.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return category;
    }

    // Thêm category mới
    public long insertCategory(Category category) {
        long result = -1;
        open();

        try {
            ContentValues values = new ContentValues();
            values.put("category_name", category.getCategoryName());
            values.put("description", category.getDescription());
            values.put("image_url", category.getImageUrl());

            result = database.insert("categories", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    // Cập nhật category
    public int updateCategory(Category category) {
        int result = 0;
        open();

        try {
            ContentValues values = new ContentValues();
            values.put("category_name", category.getCategoryName());
            values.put("description", category.getDescription());
            values.put("image_url", category.getImageUrl());

            result = database.update("categories", values, "category_id = ?",
                    new String[]{String.valueOf(category.getCategoryId())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    // Xóa category
    public int deleteCategory(int categoryId) {
        int result = 0;
        open();

        try {
            result = database.delete("categories", "category_id = ?",
                    new String[]{String.valueOf(categoryId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }
}
