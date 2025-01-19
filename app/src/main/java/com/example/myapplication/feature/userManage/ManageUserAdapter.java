package com.example.myapplication.feature.userManage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.UserResponse;

import java.util.List;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.DeviceViewHolder> {

    private Context context;
    private List<UserResponse.User> userList;
    private ItemClick itemClick;

    public ManageUserAdapter(Context context, List<UserResponse.User> userList) {
        this.context = context;
        this.userList = userList;
    }
    public interface ItemClick{
        void onClick(UserResponse.User user);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        UserResponse.User user = userList.get(position);
        holder.tvUsername.setText(user.getFullName());
        holder.tvPhoneNumber.setText(user.getPhoneNumber());
        holder.tvAddress.setText(user.getAddress());

        if(user.isActive()) {
            holder.tvStatusActive.setText("Hoạt động");
            holder.tvStatusActive.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvStatusActive.setText("Đang khoá");
            holder.tvStatusActive.setTextColor(Color.parseColor("#ff4b19"));
        }
        holder.ivEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvPhoneNumber;
        TextView tvStatusActive;
        TextView tvAddress;
        ImageView ivEditUser;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvStatusActive = itemView.findViewById(R.id.tv_status_active);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ivEditUser = itemView.findViewById(R.id.tv_edit_user);
        }
    }
}
