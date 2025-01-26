package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;
import com.example.myapplication.model.Service;
import com.example.myapplication.model.UserResponse;

public class EditServiceDialogFragment extends DialogFragment {
    private CustomEditText edtName, edtPrice, edtTime;
    private AppCompatButton btnUpdate;
    private ImageView ivClose;
    private Service service;
    private OnUpdatedListener onUpdatedListener;

    public static EditServiceDialogFragment newInstance(Service service) {
        EditServiceDialogFragment fragment = new EditServiceDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("service", service);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final LinearLayout root = new LinearLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTTC);
        return dialog;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_service, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTTC);

        edtName = view.findViewById(R.id.edtName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtTime = view.findViewById(R.id.edtTime);
        btnUpdate = view.findViewById(R.id.btn_update);
        ivClose = view.findViewById(R.id.iv_close);

        if (getArguments() != null) {
            service = (Service) getArguments().getSerializable("service");
            if (service != null) {
                edtName.setText(service.getServiceName());
                edtPrice.setText(String.valueOf(service.getPrice()));
                edtTime.setText(String.valueOf(service.getTotalMinutes()));
            }
        }

        btnUpdate.setOnClickListener(v -> {
            if(edtName.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên thiết bị", Toast.LENGTH_SHORT).show();
                return;
            }
            if(edtPrice.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập giá thiết bị", Toast.LENGTH_SHORT).show();
                return;
            }

            if(edtTime.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số phút", Toast.LENGTH_SHORT).show();
                return;
            }
            if (onUpdatedListener != null) {
                service.setServiceName(edtName.getText().toString());
                service.setPrice(Integer.parseInt(edtPrice.getText().toString()));
                service.setTotalMinutes(Integer.parseInt(edtTime.getText().toString()));
                onUpdatedListener.onUpdated(service);
            }
            dismiss();
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return view;
    }

    public void setOnUserUpdatedListener(OnUpdatedListener listener) {
        this.onUpdatedListener = listener;
    }

    public interface OnUpdatedListener {
        void onUpdated(Service service);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Thiết lập lại kích thước dialog
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% chiều rộng màn hình
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }
}
