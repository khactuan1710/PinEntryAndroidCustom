package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.webview.WebViewActivity;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Context context;
    private List<DeviceResponse.Device> deviceList;
    private String token;
    private ItemClick itemClick;

    public DeviceAdapter(Context context, String token, List<DeviceResponse.Device> deviceList) {
        this.context = context;
        this.token = token;
        this.deviceList = deviceList;
    }
    public interface ItemClick{
        void onClick(String deviceId, int isOnOff);

        void onDetail(DeviceResponse.Device device);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceResponse.Device device = deviceList.get(position);
        holder.tvDeviceName.setText(device.getDeviceFullName());

// Tắt tạm thời listener để tránh gọi itemClick khi setChecked
        holder.switchDevice.setOnCheckedChangeListener(null);
        holder.switchDevice.setChecked(device.getCurrentStatus().equals("on"));

        holder.tvHistory.setPaintFlags(holder.tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.tvReport.setPaintFlags(holder.tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.tvHistory.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/history?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Lịch sử giặt");
            intent.putExtra("token", token);
            context.startActivity(intent);
        });
        holder.tvReport.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/report?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Báo cáo/Thống kê");
            intent.putExtra("token", token);
            context.startActivity(intent);
        });


        final CompoundButton.OnCheckedChangeListener[] switchListener = new CompoundButton.OnCheckedChangeListener[1];

        switchListener[0] = (buttonView, isChecked) -> {
            if (!isChecked) {  // Nếu người dùng TẮT switch
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn tắt máy giặt không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            itemClick.onClick(device.getDeviceId(), 0);
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            buttonView.setOnCheckedChangeListener(null); // Tắt listener tạm thời
                            buttonView.setChecked(true); // Giữ trạng thái bật
                            buttonView.setOnCheckedChangeListener(switchListener[0]); // Gán lại listener
                        })
                        .setCancelable(false)
                        .show();
            } else { // Nếu người dùng BẬT switch
                itemClick.onClick(device.getDeviceId(), 1);
            }
        };

        holder.switchDevice.setOnCheckedChangeListener(switchListener[0]);




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onDetail(device);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName, tvHistory, tvReport;
        Switch switchDevice;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            switchDevice = itemView.findViewById(R.id.switch_device);
            tvHistory = itemView.findViewById(R.id.tv_history);
            tvReport = itemView.findViewById(R.id.tv_report);
        }
    }
}
