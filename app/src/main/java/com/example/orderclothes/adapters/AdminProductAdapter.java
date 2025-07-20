package com.example.orderclothes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderclothes.R;
import com.example.orderclothes.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
        void onToggleActive(Product product);
    }

    public AdminProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newList) {
        this.products.clear();
        this.products.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvName, tvBrand, tvPrice, tvStock, tvStatus;
        Button btnEdit, btnDelete;
        ImageButton btnToggle;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnToggle = itemView.findViewById(R.id.btnToggle); // sửa kiểu đúng
        }

        public void bind(Product product) {
            tvName.setText(product.getProductName());
            tvBrand.setText("Thương hiệu: " + product.getBrand());
            tvStock.setText("Kho: " + product.getStockQuantity());
            tvStatus.setText(product.isActive() ? "Đang hiển thị" : "Đã ẩn");
            tvStatus.setTextColor(product.isActive()
                    ? android.graphics.Color.parseColor("#008000") // Xanh lá
                    : android.graphics.Color.parseColor("#FF0000")); // Đỏ


            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvPrice.setText(nf.format(product.getPrice()) + "đ");

            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_shopping_bag)
                    .into(ivProductImage);

            // Đổi icon theo trạng thái
            btnToggle.setImageResource(product.isActive() ? R.drawable.ic_eye : R.drawable.ic_eye_off);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(product);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(product);
            });

            btnToggle.setOnClickListener(v -> {
                if (listener != null) listener.onToggleActive(product);
            });
        }
    }

}
