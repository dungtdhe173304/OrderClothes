package com.example.orderclothes.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.adapters.CartAdapter;
import com.example.orderclothes.database.dao.CartDAO;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.database.dao.UserDAO;
import com.example.orderclothes.models.CartItem;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.OrderItem;
import com.example.orderclothes.models.User;
import com.example.orderclothes.utils.SessionManager;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemActionListener {
    private RecyclerView rvCartItems;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private CartDAO cartDAO;
    private UserDAO userDAO;
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
        userDAO = new UserDAO(this);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartItemActionListener(this);
        rvCartItems.setAdapter(cartAdapter);

        loadCart();

        btnCheckout.setOnClickListener(v -> showOrderConfirmation());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_orders) {
                Toast.makeText(this, "Orders - Coming soon", Toast.LENGTH_SHORT).show();
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
        cartItems = cartDAO.getCartItems(userId);
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

    private void showOrderConfirmation() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = calculateCartTotal();
        String address = currentUser.getAddress();
        String fullName = currentUser.getFullName();
        String phone = currentUser.getPhone();

        if (address == null || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng cập nhật địa chỉ trong hồ sơ!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_confirmation, null);
        builder.setView(dialogView);

        TextView tvOrderTotal = dialogView.findViewById(R.id.tvOrderTotal);
        TextView tvRecipientName = dialogView.findViewById(R.id.tvRecipientName);
        TextView tvRecipientAddress = dialogView.findViewById(R.id.tvRecipientAddress);
        TextView tvRecipientPhone = dialogView.findViewById(R.id.tvRecipientPhone);
        TextView tvOrderNote = dialogView.findViewById(R.id.tvOrderNote);
        Button btnConfirmOrder = dialogView.findViewById(R.id.btnConfirmOrder);
        Button btnCancelOrder = dialogView.findViewById(R.id.btnCancelOrder);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvOrderTotal.setText("Tổng tiền: " + formatter.format(total) + "đ");
        tvRecipientName.setText("Người nhận: " + fullName);
        tvRecipientAddress.setText("Địa chỉ: " + address);
        tvRecipientPhone.setText("Số điện thoại: " + phone);
        tvOrderNote.setText("Vui lòng để ý điện thoại để nhận sản phẩm!");

        AlertDialog dialog = builder.create();

        btnConfirmOrder.setOnClickListener(v -> {
            if (placeOrder()) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                startActivity(new Intent(CartActivity.this, OrderHistoryActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelOrder.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean placeOrder() {
        OrderDAO orderDAO = new OrderDAO(this);
        SessionManager session = new SessionManager(this);
        User user = session.getCurrentUser();

        if (user != null && cartItems != null && !cartItems.isEmpty()) {
            Order orderActivity = new Order();
            orderActivity.setUserId(user.getUserId());
            orderActivity.setOrderNumber("ORD" + System.currentTimeMillis()); // Tạo số đơn hàng duy nhất
            orderActivity.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            orderActivity.setSubtotal(calculateSubtotal());
            orderActivity.setTotalAmount(calculateTotal());
            orderActivity.setCustomerName(user.getFullName());
            orderActivity.setCustomerPhone(user.getPhone());
            orderActivity.setShippingAddress(user.getAddress());
            orderActivity.setStatus("pending");

            long orderId = orderDAO.insertOrder(orderActivity);

            if (orderId > 0) {
                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem item : cartItems) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId((int) orderId);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProductName(item.getProductName());
                    orderItem.setSizeId(item.getSizeId());
                    orderItem.setSizeName(item.getSizeName());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(item.getUnitPrice());
                    orderItem.setTotalPrice(item.getQuantity() * item.getUnitPrice());
                    orderItems.add(orderItem);
                }
                orderDAO.insertOrderItems(orderItems);
                cartDAO.clearCart(user.getUserId()); // Xóa giỏ hàng sau khi đặt
                loadCart(); // Cập nhật giao diện
                return true;
            }
        }
        return false;
    }

    private double calculateCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        return total;
    }

    private double calculateSubtotal() {
        return calculateCartTotal(); // Không có phí ship, có thể thêm logic nếu cần
    }

    private double calculateTotal() {
        return calculateCartTotal(); // Không có phí ship, có thể thêm logic nếu cần
    }

    private List<CartItem> getCartItems() {
        return cartItems; // Trả về danh sách hiện tại
    }

    private void clearCart() {
        if (currentUser != null) {
            cartDAO.clearCart(currentUser.getUserId());
        }
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