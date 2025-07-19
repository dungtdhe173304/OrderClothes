package com.example.orderclothes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderclothes.R;
import com.example.orderclothes.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private final List<User> userList;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;
    private final String loggedInAdminEmail;

    public interface OnEditClickListener {
        void onEditClick(User user);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, String loggedInAdminEmail,
                       OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.userList = userList;
        this.editClickListener = editListener;
        this.deleteClickListener = deleteListener;
        this.loggedInAdminEmail = loggedInAdminEmail;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvEmail, tvRole;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvFullName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText("Vai trò: " + user.getRole());

        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) editClickListener.onEditClick(user);
        });

        if (user.getEmail().equals(loggedInAdminEmail)) {
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                // ❗ Hiển thị AlertDialog xác nhận xoá
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc chắn muốn xoá người dùng \"" + user.getFullName() + "\" không?")
                        .setPositiveButton("Xác nhận", (dialog, which) -> {
                            if (deleteClickListener != null) {
                                deleteClickListener.onDeleteClick(user);
                            }
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
