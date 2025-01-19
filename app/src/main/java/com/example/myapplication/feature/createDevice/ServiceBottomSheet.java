package com.example.myapplication.feature.createDevice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;
import com.example.myapplication.model.Service;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ServiceBottomSheet extends BottomSheetDialogFragment {
    OnTypeSelect onTypeSelect;
    AppCompatImageView ivClose;

    CustomEditText edtName, edtPrice, edtTime;
    AppCompatButton btnConfirm, btnAddService;
    AppCompatTextView tvCount;

    List<Service> serviceList;
    public ServiceBottomSheet() {
    }
    public void setOnSelected(OnTypeSelect onTypeSelect) {
        this.onTypeSelect = onTypeSelect;
    }


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogThemeV6);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.botom_sheet_service, null);
        ivClose = view.findViewById(R.id.iv_close);
        edtName = view.findViewById(R.id.edtName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtTime = view.findViewById(R.id.edtTime);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnAddService = view.findViewById(R.id.btn_add_service);
        tvCount = view.findViewById(R.id.tv_count);
        serviceList = new ArrayList<>();


        ivClose.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           dismiss();
                                       }
                                   });

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (validate()) {
                    Service service = new Service(edtName.getText().toString(), Integer.parseInt(edtPrice.getText().toString()), Integer.parseInt(edtTime.getText().toString()));
                    serviceList.add(service);
                    tvCount.setText("Số dịch vụ đã thêm: " + serviceList.size());
                    Toast.makeText(getContext(), "Thêm dịch vụ thành công", Toast.LENGTH_SHORT).show();
                    edtName.setText("");
                    edtPrice.setText("");
                    edtTime.setText("");
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!serviceList.isEmpty()) {
                    onTypeSelect.onChoose(serviceList);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Vui lòng thêm dịch vụ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }

    private boolean validate() {
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Vui lòng nhập tên dịch vụ");
            return false;
        }
        if (edtPrice.getText().toString().isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá dịch vụ");
            return false;
        }
        if (edtTime.getText().toString().isEmpty()) {
            edtTime.setError("Vui lòng nhập thời gian");
            return false;
        }
        return true;
    }


    public interface OnTypeSelect {
        void onChoose(List<Service> listService);
    }
}

