package com.example.myapplication.feature.createAccount;

import android.annotation.SuppressLint;
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

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TypeAccountBottomSheet extends BottomSheetDialogFragment {
    OnTypeSelect onTypeSelect;
    AppCompatImageView ivClose;
    AppCompatButton btnAdmin, btnOwner;

    public TypeAccountBottomSheet() {

    }
    public void setOnAddressSelected(OnTypeSelect onTypeSelect) {
        this.onTypeSelect = onTypeSelect;
    }


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogThemeV6);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.botom_sheet_type_account, null);
        ivClose = view.findViewById(R.id.iv_close);
        btnAdmin = view.findViewById(R.id.btn_admin);
        btnOwner = view.findViewById(R.id.btn_owner);
        ivClose.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           dismiss();
                                       }
                                   });
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTypeSelect.onChoose("Admin");
                dismiss();
            }
        });
        btnOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTypeSelect.onChoose("Chủ nhà");
                dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }



    public interface OnTypeSelect {
        void onChoose(String type);
    }
}

