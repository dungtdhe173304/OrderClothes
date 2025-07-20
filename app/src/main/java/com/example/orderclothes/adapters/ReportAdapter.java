package com.example.orderclothes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderclothes.R;
import com.example.orderclothes.models.ReportItem;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private final List<ReportItem> reportList;

    public ReportAdapter(List<ReportItem> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = reportList.get(position);
        holder.tvReportDate.setText("Ngày: " + item.getDateLabel());
        holder.tvOrderCount.setText("Số đơn hàng: " + item.getOrderCount());
        holder.tvTotalRevenue.setText(String.format("Tổng doanh thu: %,.0fđ", item.getTotalRevenue()));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvReportDate, tvOrderCount, tvTotalRevenue;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportDate = itemView.findViewById(R.id.tvReportDate);
            tvOrderCount = itemView.findViewById(R.id.tvOrderCount);
            tvTotalRevenue = itemView.findViewById(R.id.tvTotalRevenue);
        }
    }
}

