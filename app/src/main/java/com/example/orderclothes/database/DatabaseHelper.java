package com.example.orderclothes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "order_clothes.db";
    private static final int DATABASE_VERSION = 1;

    // Singleton pattern
    private static DatabaseHelper instance;

    // Thay đổi từ private constructor thành public
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        // Tạo bảng users
        db.execSQL(CREATE_TABLE_USERS);

        // Tạo bảng categories
        db.execSQL(CREATE_TABLE_CATEGORIES);

        // Tạo bảng products
        db.execSQL(CREATE_TABLE_PRODUCTS);

        // Tạo bảng product_sizes
        db.execSQL(CREATE_TABLE_PRODUCT_SIZES);

        // Tạo bảng cart
        db.execSQL(CREATE_TABLE_CART);

        // Tạo bảng cart_items
        db.execSQL(CREATE_TABLE_CART_ITEMS);

        // Tạo bảng orders
        db.execSQL(CREATE_TABLE_ORDERS);

        // Tạo bảng order_items
        db.execSQL(CREATE_TABLE_ORDER_ITEMS);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS product_sizes");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS users");
    }

    // Thêm dữ liệu mặc định
    private void insertDefaultData(SQLiteDatabase db) {
        // Tạo admin mặc định
        insertDefaultAdmin(db);

        // Tạo user test
        insertTestUser(db);

        // Tạo categories mẫu
        insertSampleCategories(db);

        // Tạo products mẫu
        insertSampleProducts(db);
    }

    // Tạo admin mặc định
    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put("username", "admin");
        adminValues.put("email", "admin@orderclothes.com");
        adminValues.put("password", hashPassword("admin123"));
        adminValues.put("full_name", "Administrator");
        adminValues.put("phone", "0123456789");
        adminValues.put("address", "Admin Address");
        adminValues.put("role", "admin");

        db.insert("users", null, adminValues);
    }

    // Tạo user test
    private void insertTestUser(SQLiteDatabase db) {
        ContentValues userValues = new ContentValues();
        userValues.put("username", "user123");
        userValues.put("email", "user@test.com");
        userValues.put("password", hashPassword("123456"));
        userValues.put("full_name", "Test User");
        userValues.put("phone", "0987654321");
        userValues.put("address", "123 Test Street");
        userValues.put("role", "user");

        db.insert("users", null, userValues);
    }

    // Thêm categories mẫu
    private void insertSampleCategories(SQLiteDatabase db) {
        insertCategory(db, "Áo thun", "Các loại áo thun nam nữ", "https://cdn-icons-png.flaticon.com/512/892/892458.png");
        insertCategory(db, "Áo sơ mi", "Các loại áo sơ mi", "https://cdn-icons-png.flaticon.com/512/892/892458.png");
        insertCategory(db, "Quần jean", "Các loại quần jean", "https://cdn-icons-png.flaticon.com/512/892/892458.png");
        insertCategory(db, "Váy đầm", "Các loại váy đầm", "https://cdn-icons-png.flaticon.com/512/892/892458.png");
        insertCategory(db, "Phụ kiện", "Phụ kiện thời trang", "https://cdn-icons-png.flaticon.com/512/892/892458.png");
    }

    // Helper method để insert category
    private void insertCategory(SQLiteDatabase db, String name, String description, String imageUrl) {
        ContentValues values = new ContentValues();
        values.put("category_name", name);
        values.put("description", description);
        values.put("image_url", imageUrl);
        db.insert("categories", null, values);
    }

    // Tạo products mẫu
    private void insertSampleProducts(SQLiteDatabase db) {
        // Áo thun
        insertProduct(db, "Áo thun nam basic", "Áo thun cotton 100% thoáng mát", 199000, 1, "Uniqlo", "Cotton", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 50);
        insertProduct(db, "Áo thun nữ oversize", "Áo thun form rộng phong cách Hàn Quốc", 229000, 1, "Zara", "Cotton blend", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 30);

        // Áo sơ mi
        insertProduct(db, "Áo sơ mi trắng công sở", "Áo sơ mi nam trắng cổ điển", 399000, 2, "Aristino", "Cotton", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 25);
        insertProduct(db, "Áo sơ mi nữ họa tiết", "Áo sơ mi nữ họa tiết hoa nhỏ", 359000, 2, "H&M", "Polyester", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 20);

        // Quần jean
        insertProduct(db, "Quần jean nam slim fit", "Quần jean nam ôm vừa phải", 599000, 3, "Levi's", "Denim", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 40);
        insertProduct(db, "Quần jean nữ skinny", "Quần jean nữ ôm sát chân", 549000, 3, "Zara", "Denim stretch", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 35);

        // Váy đầm
        insertProduct(db, "Váy đầm maxi hoa", "Váy đầm dài họa tiết hoa", 699000, 5, "Mango", "Chiffon", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 15);
        insertProduct(db, "Váy đầm công sở", "Váy đầm ngắn thanh lịch", 459000, 5, "Zara", "Polyester", "https://kenh14cdn.com/203336854389633024/2025/6/29/48427799012203294996640565770072566130465660n-1751213367694-1751213367933736583458.jpg", 20);
    }

    // Helper method để insert product
    private void insertProduct(SQLiteDatabase db, String name, String description, double price,
                               int categoryId, String brand, String material, String imageUrl, int stock) {
        ContentValues productValues = new ContentValues();
        productValues.put("product_name", name);
        productValues.put("description", description);
        productValues.put("price", price);
        productValues.put("category_id", categoryId);
        productValues.put("brand", brand);
        productValues.put("material", material);
        productValues.put("image_url", imageUrl);
        productValues.put("stock_quantity", stock);
        productValues.put("is_active", 1);

        long productId = db.insert("products", null, productValues);

        // Thêm sizes cho mỗi sản phẩm
        insertProductSizes(db, productId);
    }

    // Thêm sizes cho sản phẩm
    private void insertProductSizes(SQLiteDatabase db, long productId) {
        String[] sizes = {"S", "M", "L", "XL"};
        int[] stocks = {10, 15, 20, 10}; // Stock cho mỗi size

        for (int i = 0; i < sizes.length; i++) {
            ContentValues sizeValues = new ContentValues();
            sizeValues.put("product_id", productId);
            sizeValues.put("size_name", sizes[i]);
            sizeValues.put("stock_quantity", stocks[i]);

            db.insert("product_sizes", null, sizeValues);
        }
    }

    // Mã hóa password
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
        } catch (Exception e) {
            return password;
        }
    }

    // SQL tạo bảng users
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "full_name TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "address TEXT, " +
                    "role TEXT DEFAULT 'user', " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL tạo bảng categories
    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE categories (" +
                    "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "image_url TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL tạo bảng products
    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE products (" +
                    "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "category_id INTEGER, " +
                    "brand TEXT, " +
                    "material TEXT, " +
                    "image_url TEXT, " +
                    "stock_quantity INTEGER DEFAULT 0, " +
                    "is_active INTEGER DEFAULT 1, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (category_id) REFERENCES categories(category_id))";

    // SQL tạo bảng product_sizes
    private static final String CREATE_TABLE_PRODUCT_SIZES =
            "CREATE TABLE product_sizes (" +
                    "size_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "size_name TEXT NOT NULL, " +
                    "stock_quantity INTEGER DEFAULT 0, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id))";

    // SQL tạo bảng cart
    private static final String CREATE_TABLE_CART =
            "CREATE TABLE cart (" +
                    "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER UNIQUE, " +
                    "subtotal REAL DEFAULT 0.0, " +
                    "shipping_fee REAL DEFAULT 0.0, " +
                    "total_amount REAL DEFAULT 0.0, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))";

    // SQL tạo bảng cart_items
    private static final String CREATE_TABLE_CART_ITEMS =
            "CREATE TABLE cart_items (" +
                    "cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cart_id INTEGER, " +
                    "product_id INTEGER, " +
                    "size_id INTEGER, " +
                    "quantity INTEGER NOT NULL DEFAULT 1, " +
                    "unit_price REAL NOT NULL, " +
                    "added_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id), " +
                    "FOREIGN KEY (size_id) REFERENCES product_sizes(size_id), " +
                    "UNIQUE(cart_id, product_id, size_id))";

    // SQL tạo bảng orders
    private static final String CREATE_TABLE_ORDERS =
            "CREATE TABLE orders (" +
                    "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_number TEXT UNIQUE NOT NULL, " +
                    "user_id INTEGER, " +
                    "order_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "subtotal REAL NOT NULL, " +
                    "shipping_fee REAL DEFAULT 0.0, " +
                    "total_amount REAL NOT NULL, " +
                    "customer_name TEXT NOT NULL, " +
                    "customer_phone TEXT NOT NULL, " +
                    "shipping_address TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'pending', " +
                    "notes TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))";

    // SQL tạo bảng order_items
    private static final String CREATE_TABLE_ORDER_ITEMS =
            "CREATE TABLE order_items (" +
                    "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER, " +
                    "product_id INTEGER, " +
                    "product_name TEXT NOT NULL, " +
                    "size_id INTEGER, " +
                    "size_name TEXT NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "unit_price REAL NOT NULL, " +
                    "total_price REAL NOT NULL, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id), " +
                    "FOREIGN KEY (size_id) REFERENCES product_sizes(size_id))";
}