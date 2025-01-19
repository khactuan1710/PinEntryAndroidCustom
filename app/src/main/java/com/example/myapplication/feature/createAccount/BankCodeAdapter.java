package com.example.myapplication.feature.createAccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.BankCodeResponse;

import java.time.Instant;
import java.util.List;

public class BankCodeAdapter extends RecyclerView.Adapter<BankCodeAdapter.BankCodeViewHolder> {

    private Context context;
    private List<BankCodeResponse.BankCode> bankCodeList;
    private ItemClick itemClick;

    public BankCodeAdapter(Context context, List<BankCodeResponse.BankCode> bankCodeList) {
        this.context = context;
        this.bankCodeList = bankCodeList;
    }
    public interface ItemClick{
        void onClick(BankCodeResponse.BankCode bankCode);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public BankCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bank_code, parent, false);
        return new BankCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankCodeViewHolder holder, int position) {
        BankCodeResponse.BankCode bankCode = bankCodeList.get(position);
        holder.tvFullName.setText(bankCode.getName());
        holder.tvBankCode.setText(bankCode.getShort_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null) {
                    itemClick.onClick(bankCode);
                }
            }
        });

        Glide.with(holder.ivLogo.getContext())
                .load(bankCode.getLogo())
                .into(holder.ivLogo);

    }

    @Override
    public int getItemCount() {
        return bankCodeList.size();
    }

    public static class BankCodeViewHolder extends RecyclerView.ViewHolder {
        TextView tvBankCode;
        TextView tvFullName;
        ImageView ivLogo;

        public BankCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankCode = itemView.findViewById(R.id.tvBankCode);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            ivLogo = itemView.findViewById(R.id.ivLogo);
        }
    }
}