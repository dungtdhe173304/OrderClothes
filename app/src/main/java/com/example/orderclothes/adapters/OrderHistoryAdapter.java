package com.example.orderclothes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.models.Order;
import com.example.orderclothes.models.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderActivities;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order orderActivity);
    }

    public OrderHistoryAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orderActivities) {
        this.orderActivities = orderActivities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order orderActivity = orderActivities.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(orderActivity);
        });
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvOrderDate.setText("Đơn hàng #" + orderActivity.getOrderNumber() + " - " + orderActivity.getOrderDate());
        holder.tvOrderTotal.setText("Tổng: " + formatter.format(orderActivity.getTotalAmount()) + "đ");
    }

    @Override
    public int getItemCount() {
        return orderActivities != null ? orderActivities.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}