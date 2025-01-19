package com.example.myapplication.feature.createAccount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.BankCodeResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BankCodeBottomSheet extends BottomSheetDialogFragment {
    OnBankCodeSelect onBankCodeSelect;
    AppCompatImageView ivClose;

    RecyclerView rcvBankCode;
    private BankCodeAdapter bankCodeAdapter;
    private List<BankCodeResponse.BankCode> bankCodeList;
    private Activity activity;
    public BankCodeBottomSheet(Activity activity, List<BankCodeResponse.BankCode> bankCodeList) {
        this.activity = activity;
        this.bankCodeList = bankCodeList;
    }
    public void setOnBankCodeSelected(OnBankCodeSelect onBankCodeSelect) {
        this.onBankCodeSelect = onBankCodeSelect;
    }


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogThemeV6);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.botom_sheet_bank_code, null);
        ivClose = view.findViewById(R.id.iv_close);
        rcvBankCode = view.findViewById(R.id.rcv_bank_code);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        bankCodeAdapter = new BankCodeAdapter(activity, bankCodeList);
        bankCodeAdapter.setItemClick(new BankCodeAdapter.ItemClick() {
            @Override
            public void onClick(BankCodeResponse.BankCode bankCode) {
                if(onBankCodeSelect != null) {
                    onBankCodeSelect.onChoose(bankCode);
                    dismiss();
                }
            }
        });


        rcvBankCode.setLayoutManager(new LinearLayoutManager(activity));
        rcvBankCode.setAdapter(bankCodeAdapter);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }



    public interface OnBankCodeSelect {
        void onChoose(BankCodeResponse.BankCode bankCode);
    }
}

