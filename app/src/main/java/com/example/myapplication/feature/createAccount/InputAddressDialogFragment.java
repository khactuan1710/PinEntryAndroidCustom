package com.example.myapplication.feature.createAccount;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;

public class InputAddressDialogFragment extends DialogFragment {
    private CustomEditText  edtAddress;
    private AppCompatButton btnAdd;
    private ImageView ivClose;
    private String address;
    private OnAddListener onAddListener;

    public static InputAddressDialogFragment newInstance() {
        InputAddressDialogFragment fragment = new InputAddressDialogFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input_address, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTTC);

        edtAddress = view.findViewById(R.id.edtAddress);
        ivClose = view.findViewById(R.id.iv_close);
        btnAdd = view.findViewById(R.id.btn_add);


        btnAdd.setOnClickListener(v -> {
            if(edtAddress.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (onAddListener != null) {
                onAddListener.onAdd(edtAddress.getText());
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

    public void setOnAddListener(OnAddListener listener) {
        this.onAddListener = listener;
    }

    public interface OnAddListener {
        void onAdd(String address);
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
