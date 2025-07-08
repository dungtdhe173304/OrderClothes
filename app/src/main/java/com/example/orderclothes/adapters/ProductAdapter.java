package com.example.orderclothes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderclothes.R;
import com.example.orderclothes.models.Product;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct, ivAddToCart;
        private TextView tvProductName, tvProductPrice, tvProductBrand, tvStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            ivAddToCart = itemView.findViewById(R.id.ivAddToCart);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvStock = itemView.findViewById(R.id.tvStock);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductClick(products.get(position));
                    }
                }
            });

            ivAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAddToCartClick(products.get(position));
                    }
                }
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getProductName());
            tvProductBrand.setText(product.getBrand());
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(formatter.format(product.getPrice()) + "đ");
            tvStock.setText("Còn " + product.getStockQuantity() + " sản phẩm");
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(context).load(product.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_shopping_bag);
            }
            if (product.getStockQuantity() > 0) {
                ivAddToCart.setVisibility(View.VISIBLE);
                ivAddToCart.setImageResource(R.drawable.ic_add_shopping_cart);
            } else {
                ivAddToCart.setVisibility(View.GONE);
            }
        }
    }
}
