package com.example.myapplication.feature.createAccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DeviceDetailActivity;
import com.example.myapplication.adapter.EditServiceDialogFragment;
import com.example.myapplication.feature.createDevice.CreateDeviceActivity;
import com.example.myapplication.feature.createDevice.SelectServiceAdapter;
import com.example.myapplication.model.Service;
import com.example.myapplication.util.CurrencyUtils;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<String> addressList;


    public AddressAdapter(Context context, List<String> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    private OnDeleteListener onDeleteListener; // Callback

    public interface OnDeleteListener {
        void onDelete(String address);
    }
    public void setOnDelete(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String address = addressList.get(position);
        holder.tvAddress.setText("Địa chỉ "+ (holder.getAdapterPosition() + 1) +":" + address);

        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
                if (adapterPosition != RecyclerView.NO_POSITION) { // Kiểm tra hợp lệ
                    // Xóa item khỏi danh sách bên trong adapter và danh sách bên ngoài
                    String removedAddress = addressList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition); // Cập nhật RecyclerView

                    // Đồng bộ danh sách bên ngoài
                    if (context instanceof CreateAccountActivity) {
                        ((CreateAccountActivity) context).updateAddressList(removedAddress);
                    }

                    if(onDeleteListener != null) {
                        onDeleteListener.onDelete(removedAddress);
                    }
                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvAddress;
        AppCompatImageView ic_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAddress = itemView.findViewById(R.id.tv_address);
            ic_delete = itemView.findViewById(R.id.ic_delete);

        }
    }
}