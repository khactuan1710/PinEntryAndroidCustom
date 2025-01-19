package com.example.myapplication.feature.createDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Service;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.CurrencyUtils;

import java.util.List;

public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.ViewHolder> {

    private Context context;
    private List<Service> serviceList;

    public SelectServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_selected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.tvServiceName.setText("Tên dịch vụ: " + service.getServiceName());
        holder.tvPrice.setText("Giá: " + CurrencyUtils.formatCurrency(service.getPrice()));
        holder.tvTime.setText("Thời gian hoạt động: " + service.getTotalMinutes() + " phút");


    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvServiceName, tvPrice, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTime = itemView.findViewById(R.id.tv_time);

        }
    }
}