package com.example.myapplication.feature.createDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DeviceDetailActivity;
import com.example.myapplication.adapter.EditServiceDialogFragment;
import com.example.myapplication.feature.userManage.EditUserDialogFragment;
import com.example.myapplication.model.Service;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.CurrencyUtils;

import java.util.List;

public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.ViewHolder> {

    private Context context;
    private List<Service> serviceList;

    private OnServiceListEmptyListener onServiceListEmptyListener; // Callback

    public interface OnServiceListEmptyListener {
        void onServiceListEmpty( List<Service> list);
    }
    public void setOnServiceListEmptyListener(OnServiceListEmptyListener listener) {
        this.onServiceListEmptyListener = listener;
    }

    public SelectServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    private boolean isAdmin;

    public SelectServiceAdapter(Context context, List<Service> serviceList, boolean isAdmin) {
        this.context = context;
        this.serviceList = serviceList;
        this.isAdmin = isAdmin;
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

        if (isAdmin) {
            holder.ic_delete.setVisibility(View.VISIBLE);
            holder.ic_edit.setVisibility(View.VISIBLE);
        } else {
            holder.ic_delete.setVisibility(View.GONE);
            holder.ic_edit.setVisibility(View.GONE);
        }

        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
                if (adapterPosition != RecyclerView.NO_POSITION) { // Kiểm tra hợp lệ
                    // Xóa item khỏi danh sách bên trong adapter và danh sách bên ngoài
                    Service removedService = serviceList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition); // Cập nhật RecyclerView

                    // Đồng bộ danh sách bên ngoài
                    if (context instanceof CreateDeviceActivity) {
                        ((CreateDeviceActivity) context).updateServiceList(removedService);
                    } else if(context instanceof DeviceDetailActivity) {
                        ((DeviceDetailActivity) context).updateServiceList(removedService);
                    }

                    // Kiểm tra nếu danh sách rỗng và gọi callback
                    if (onServiceListEmptyListener != null && serviceList.isEmpty()) {
                        onServiceListEmptyListener.onServiceListEmpty(serviceList);
                    }
                }
            }
        });

        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditServiceDialogFragment dialogFragment = EditServiceDialogFragment.newInstance(service);
                dialogFragment.setOnUserUpdatedListener(updatedService -> {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        serviceList.set(adapterPosition, updatedService); // Cập nhật dữ liệu adapter
                        notifyItemChanged(adapterPosition); // Cập nhật giao diện của item

                        // Đồng bộ danh sách bên ngoài
                        if (context instanceof DeviceDetailActivity) {
                            ((DeviceDetailActivity) context).updateEditedService(updatedService);
                        }else if (context instanceof CreateDeviceActivity) {
                            ((CreateDeviceActivity) context).updateEditedService(updatedService);
                        }
                    }
                });
                if (context instanceof DeviceDetailActivity) {
                    dialogFragment.show(((DeviceDetailActivity) context).getSupportFragmentManager(), "EditServiceDialogFragment");
                }else if (context instanceof CreateDeviceActivity) {
                    dialogFragment.show(((CreateDeviceActivity) context).getSupportFragmentManager(), "EditServiceDialogFragment");
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvServiceName, tvPrice, tvTime;
        AppCompatImageView ic_delete, ic_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTime = itemView.findViewById(R.id.tv_time);
            ic_delete = itemView.findViewById(R.id.ic_delete);
            ic_edit = itemView.findViewById(R.id.ic_edit);

        }
    }
}