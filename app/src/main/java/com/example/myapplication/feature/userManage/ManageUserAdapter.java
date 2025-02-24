package com.example.myapplication.feature.userManage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        void onEdit(UserResponse.User user);

        void onChangePass(UserResponse.User user);
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
//        holder.tvAddress.setText(user.getAddress());

        holder.layoutAddress.removeAllViews();
        List<String> addressList = user.getAddressNew();
        if (addressList.isEmpty()) {
            // Nếu không có địa chỉ, hiển thị TextView mặc định
            TextView noAddressView = new TextView(holder.itemView.getContext());
            noAddressView.setText("Chưa có địa chỉ");
            noAddressView.setTextSize(16);
            noAddressView.setTextColor(Color.parseColor("#737373"));
            holder.layoutAddress.addView(noAddressView);
        } else {
            for (int i = 0; i < addressList.size(); i++) {
                TextView addressView = new TextView(holder.itemView.getContext());
                addressView.setText(addressList.get(i));
                addressView.setTextSize(16);
                addressView.setTextColor(Color.parseColor("#282828"));

                // Áp dụng marginTop = 0 cho phần tử đầu tiên, còn lại là 8dp
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, (i == 0) ? 0 : 8, 0, 0);
                addressView.setLayoutParams(params);

                holder.layoutAddress.addView(addressView);
            }
        }

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
                itemClick.onEdit(user);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(user);
            }
        });
        holder.ivChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onChangePass(user);
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
        ImageView ivChangePass;
        LinearLayout layoutAddress;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvStatusActive = itemView.findViewById(R.id.tv_status_active);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ivEditUser = itemView.findViewById(R.id.tv_edit_user);
            ivChangePass = itemView.findViewById(R.id.tv_change_password);
            layoutAddress = itemView.findViewById(R.id.layout_address);
        }
    }
}
