package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.adapters.CategoryAdapter;
import com.example.orderclothes.adapters.ProductAdapter;
import com.example.orderclothes.database.dao.CategoryDAO;
import com.example.orderclothes.database.dao.ProductDAO;
import com.example.orderclothes.models.Category;
import com.example.orderclothes.models.Product;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.example.orderclothes.activities.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity implements
        CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener {

    private TextView tvWelcome, tvSeeAllCategories, tvSeeAllProducts;
    private EditText etSearch;
    private RecyclerView rvCategories, rvProducts;
    private BottomNavigationView bottomNavigationView;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    private CategoryDAO categoryDAO;
    private ProductDAO productDAO;
    private SessionManager sessionManager;
    private User currentUser;

    private List<Category> categories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private Category selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        initViews();
        initObjects();
        setupRecyclerViews();
        loadData();
        setupListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvSeeAllCategories = findViewById(R.id.tvSeeAllCategories);
        tvSeeAllProducts = findViewById(R.id.tvSeeAllProducts);
        etSearch = findViewById(R.id.etSearch);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void initObjects() {
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        categoryDAO = new CategoryDAO(this);
        productDAO = new ProductDAO(this);
    }

    private void setupRecyclerViews() {
        // Setup Categories RecyclerView
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(categoryLayoutManager);
        categoryAdapter = new CategoryAdapter(this, categories);
        categoryAdapter.setOnCategoryClickListener(this);
        rvCategories.setAdapter(categoryAdapter);

        // Setup Products RecyclerView
        LinearLayoutManager productLayoutManager = new LinearLayoutManager(this);
        rvProducts.setLayoutManager(productLayoutManager);
        productAdapter = new ProductAdapter(this, products);
        productAdapter.setOnProductClickListener(this);
        rvProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        new Thread(() -> {
            // Load categories
            List<Category> loadedCategories = categoryDAO.getAllCategories();
            // Load products
            List<Product> loadedProducts = productDAO.getAllActiveProducts();
            runOnUiThread(() -> {
                categories.clear();
                categories.addAll(loadedCategories);
                categoryAdapter.notifyDataSetChanged();

                allProducts.clear();
                allProducts.addAll(loadedProducts);
                // Hiển thị toàn bộ sản phẩm
                products.clear();
                products.addAll(allProducts);
                productAdapter.notifyDataSetChanged();

                if (currentUser != null) {
                    tvWelcome.setText("Hello " + currentUser.getFullName());
                }
            });
        }).start();
    }

    private void setupListeners() {
        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // See all categories
        tvSeeAllCategories.setOnClickListener(v -> {
            Toast.makeText(this, "Show all categories - Coming soon", Toast.LENGTH_SHORT).show();
        });

        // See all products
        tvSeeAllProducts.setOnClickListener(v -> {
            showAllProducts();
        });
    }

    private void filterProducts(String query) {
        if (query.trim().isEmpty()) {
            if (selectedCategory != null) {
                filterProductsByCategory(selectedCategory);
            } else {
                // Hiển thị lại toàn bộ sản phẩm
                products.clear();
                products.addAll(allProducts);
                productAdapter.notifyDataSetChanged();
            }
        } else {
            new Thread(() -> {
                List<Product> searchResults = productDAO.searchProducts(query);
                runOnUiThread(() -> {
                    products.clear();
                    products.addAll(searchResults);
                    productAdapter.notifyDataSetChanged();
                });
            }).start();
        }
    }

    private void filterProductsByCategory(Category category) {
        selectedCategory = category;
        new Thread(() -> {
            List<Product> categoryProducts = productDAO.getProductsByCategory(category.getCategoryId());
            runOnUiThread(() -> {
                products.clear();
                products.addAll(categoryProducts);
                productAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void showAllProducts() {
        new Thread(() -> {
            List<Product> loadedProducts = productDAO.getAllActiveProducts();
            runOnUiThread(() -> {
                products.clear();
                products.addAll(loadedProducts);
                productAdapter.notifyDataSetChanged();
                selectedCategory = null;
                etSearch.setText("");
                Toast.makeText(this, "Showing all products", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Toast.makeText(UserHomeActivity.this, "Cart - Coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_voucher) {


                    Toast.makeText(UserHomeActivity.this, "Voucher - Coming soon", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_voucher) {
                    Toast.makeText(UserHomeActivity.this, "Voucher - Coming soon", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (itemId == R.id.nav_orders) {
                    Toast.makeText(UserHomeActivity.this, "Orders - Coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    openProfile();
                    return true;
                }
                return false;
            }
        });
    }

    private void openProfile() {
        Intent intent = new Intent(UserHomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCategoryClick(Category category) {
        if (selectedCategory == null || selectedCategory.getCategoryId() != category.getCategoryId()) {
            filterProductsByCategory(category);
            Toast.makeText(this, "Showing " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Open ProductDetailActivity
        Toast.makeText(this, "Product: " + product.getProductName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCartClick(Product product) {
        if (product.getStockQuantity() <= 0) {
            Toast.makeText(this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Add to cart functionality
        Toast.makeText(this, "Đã thêm " + product.getProductName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Clear search and category filter when back pressed
        if (!etSearch.getText().toString().isEmpty() || selectedCategory != null) {
            etSearch.setText("");
            selectedCategory = null;
            // Hiển thị lại toàn bộ sản phẩm
            products.clear();
            products.addAll(allProducts);
            productAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}