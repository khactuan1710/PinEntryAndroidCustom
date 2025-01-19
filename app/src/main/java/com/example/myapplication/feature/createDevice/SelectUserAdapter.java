package com.example.myapplication.feature.createDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.UserResponse;

import java.util.List;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.ViewHolder> {

    private Context context;
    private List<UserResponse.User> userList;
    private ItemClick itemClick;

    public SelectUserAdapter(Context context, List<UserResponse.User> userList) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_selected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse.User user = userList.get(position);
        holder.tvUserName.setText(user.getFullName());
        holder.tvAdress.setText(user.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null) {
                    itemClick.onClick(user);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvAdress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvAdress = itemView.findViewById(R.id.tv_address);
        }
    }
}