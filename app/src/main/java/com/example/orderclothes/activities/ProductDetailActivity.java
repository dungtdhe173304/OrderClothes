package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.orderclothes.R;
import com.example.orderclothes.database.dao.CategoryDAO;
import com.example.orderclothes.database.dao.CartDAO;
import com.example.orderclothes.database.dao.ProductDAO;
import com.example.orderclothes.models.Category;
import com.example.orderclothes.models.Product;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductBrand, tvProductMaterial, tvProductStock, tvProductDescription, tvProductCategory;
    private BottomNavigationView bottomNavigationView;
    private Button btnAddToCart;
    private SessionManager sessionManager;
    private User currentUser;
    private Product currentProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductBrand = findViewById(R.id.tvProductBrand);
        tvProductMaterial = findViewById(R.id.tvProductMaterial);
        tvProductStock = findViewById(R.id.tvProductStock);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductCategory = findViewById(R.id.tvProductCategory);

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            ProductDAO productDAO = new ProductDAO(this);
            Product product = productDAO.getProductById(productId);
            if (product != null) {
                currentProduct = product;
                tvProductName.setText(product.getProductName());
                tvProductPrice.setText(String.format("%,.0fđ", product.getPrice()));
                tvProductBrand.setText("Thương hiệu: " + product.getBrand());
                tvProductMaterial.setText("Chất liệu: " + product.getMaterial());
                tvProductStock.setText("Kho: " + product.getStockQuantity());
                tvProductDescription.setText(product.getDescription());
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(this).load(product.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(ivProductImage);
                } else {
                    ivProductImage.setImageResource(R.drawable.ic_shopping_bag);
                }
                CategoryDAO categoryDAO = new CategoryDAO(this);
                Category category = categoryDAO.getCategoryById(product.getCategoryId());
                if (category != null) {
                    tvProductCategory.setText("Danh mục: " + category.getCategoryName());
                } else {
                    tvProductCategory.setText("Danh mục: Không xác định");
                }
            }
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish(); // Quay về trang trước (home)
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_voucher) {
                // TODO: Mở trang voucher
                return true;
            } else if (itemId == R.id.nav_orders) {
                // TODO: Mở trang đơn hàng
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Mở trang tài khoản
                return true;
            }
            return false;
        });

        btnAddToCart = findViewById(R.id.btnAddToCart);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ProductDetailActivity.this, "userId: " + currentUser.getUserId(), Toast.LENGTH_SHORT).show();
                CartDAO cartDAO = new CartDAO(ProductDetailActivity.this);
                cartDAO.addToCart(currentUser.getUserId(), currentProduct, 1);
                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
} 