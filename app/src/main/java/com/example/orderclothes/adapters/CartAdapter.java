package com.example.orderclothes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderclothes.R;
import com.example.orderclothes.models.CartItem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onRemoveCartItem(CartItem item);
        void onUpdateQuantity(CartItem item, int newQuantity);
    }

    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems.clear();
        this.cartItems.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvProductName.setText(item.getProductName() + " (Size: " + (item.getSizeName() != null ? item.getSizeName() : "N/A") + ")");
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(item.getUnitPrice()) + "đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_shopping_bag);
        }
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveCartItem(item);
        });
        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) listener.onUpdateQuantity(item, item.getQuantity() - 1);
        });
        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateQuantity(item, item.getQuantity() + 1);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice, tvQuantity;
        private Button btnRemove, btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }

        public void bind(CartItem item) {
            tvProductName.setText(item.getProductName());
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(formatter.format(item.getUnitPrice()) + "đ");
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext()).load(item.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_shopping_bag);
            }
            btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveCartItem(item);
            });
            btnMinus.setOnClickListener(v -> {
                if (listener != null && item.getQuantity() > 1) listener.onUpdateQuantity(item, item.getQuantity() - 1);
            });
            btnPlus.setOnClickListener(v -> {
                if (listener != null) listener.onUpdateQuantity(item, item.getQuantity() + 1);
            });
        }
    }
} 