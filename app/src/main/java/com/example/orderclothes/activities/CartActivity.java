package com.example.orderclothes.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.adapters.CartAdapter;
import com.example.orderclothes.database.dao.CartDAO;
import com.example.orderclothes.models.CartItem;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemActionListener {
    private RecyclerView rvCartItems;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private CartDAO cartDAO;
    private SessionManager sessionManager;
    private User currentUser;
    private List<CartItem> cartItems;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();
        cartDAO = new CartDAO(this);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartItemActionListener(this);
        rvCartItems.setAdapter(cartAdapter);

        loadCart();

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đặt hàng sẽ bổ sung sau!", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish(); // Quay về trang trước (home)
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Đang ở trang này
                return true;
            } else if (itemId == R.id.nav_voucher) {
                Toast.makeText(this, "Voucher - Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_orders) {
                Toast.makeText(this, "Orders - Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile - Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadCart() {
        if (currentUser == null) {
            Toast.makeText(this, "currentUser == null!", Toast.LENGTH_LONG).show();
            Log.e("CartActivity", "currentUser == null!");
            return;
        }
        int userId = currentUser.getUserId();
        Toast.makeText(this, "Cart userId: " + userId, Toast.LENGTH_SHORT).show();
        Log.d("CartActivity", "Cart userId: " + userId);
        cartItems = cartDAO.getCartItems(userId);
        Toast.makeText(this, "Cart items count: " + cartItems.size(), Toast.LENGTH_SHORT).show();
        Log.d("CartActivity", "Cart items count: " + cartItems.size());
        cartAdapter.setCartItems(cartItems);
        updateTotalPrice();
        tvEmptyCart.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText("Tổng: " + formatter.format(total) + "đ");
    }

    @Override
    public void onRemoveCartItem(CartItem item) {
        cartDAO.removeCartItem(item.getCartItemId());
        loadCart();
    }

    @Override
    public void onUpdateQuantity(CartItem item, int newQuantity) {
        cartDAO.updateCartItemQuantity(item.getCartItemId(), newQuantity);
        loadCart();
    }
} 