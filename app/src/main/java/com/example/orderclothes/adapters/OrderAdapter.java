package com.example.orderclothes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderclothes.R;
import com.example.orderclothes.database.dao.OrderDAO;
import com.example.orderclothes.models.Order;


import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private final List<Order> orderActivityList;
    private final OrderDAO orderDAO;

    public OrderAdapter(Context context, List<Order> orderActivityList) {
        this.context = context;
        this.orderActivityList = orderActivityList;
        this.orderDAO = new OrderDAO(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order orderActivity = orderActivityList.get(position);

        holder.tvOrderNumber.setText("Mã đơn: " + orderActivity.getOrderNumber());
        holder.tvCustomerName.setText("Khách hàng: " + orderActivity.getCustomerName());
        holder.tvTotal.setText("Tổng tiền: " + formatCurrency(orderActivity.getTotalAmount()));

        // Danh sách trạng thái
        String[] statusList = {"pending", "processing", "completed", "cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Chọn trạng thái hiện tại
        int selectedIndex = 0;
        for (int i = 0; i < statusList.length; i++) {
            if (statusList[i].equalsIgnoreCase(orderActivity.getStatus())) {
                selectedIndex = i;
                break;
            }
        }

        // Tránh trigger sự kiện onItemSelected khi setSelection
        holder.spinnerStatus.setOnItemSelectedListener(null);
        holder.spinnerStatus.setSelection(selectedIndex);

        // Gắn lại listener
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelection = true; // để tránh gọi khi mới khởi tạo

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String newStatus = statusList[pos];
                if (firstSelection) {
                    firstSelection = false;
                    return;
                }

                if (!newStatus.equalsIgnoreCase(orderActivity.getStatus())) {
                    orderDAO.updateOrderStatus(orderActivity.getOrderId(), newStatus);
                    orderActivity.setStatus(newStatus);
                    Toast.makeText(context, "Đã cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        holder.btnDetails.setOnClickListener(v -> {
            String message = "Mã đơn: " + orderActivity.getOrderNumber() + "\n"
                    + "Khách hàng: " + orderActivity.getCustomerName() + "\n"
                    + "Số điện thoại: " + orderActivity.getCustomerPhone() + "\n"
                    + "Địa chỉ giao hàng: " + orderActivity.getShippingAddress() + "\n"
                    + "Trạng thái: " + orderActivity.getStatus() + "\n"
                    + "Tổng tiền: " + formatCurrency(orderActivity.getTotalAmount());

            new AlertDialog.Builder(context)
                    .setTitle("Chi tiết đơn hàng")
                    .setMessage(message)
                    .setPositiveButton("Đóng", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return orderActivityList.size();
    }

    // ✅ Hàm định dạng tiền theo chuẩn VNĐ
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount); // ví dụ: 250.000 ₫
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvCustomerName, tvTotal;
        Spinner spinnerStatus;
        Button btnDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
