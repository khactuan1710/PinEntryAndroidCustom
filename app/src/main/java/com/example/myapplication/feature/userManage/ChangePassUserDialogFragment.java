package com.example.myapplication.feature.userManage;

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
import com.example.myapplication.model.UserResponse;

public class ChangePassUserDialogFragment extends DialogFragment {
    private CustomEditText edtPass, edtRepass;
    private AppCompatButton btnUpdate;
    private ImageView ivClose;
    private UserResponse.User user;
    private OnUserUpdatedListener onUserUpdatedListener;

    public static ChangePassUserDialogFragment newInstance(UserResponse.User user) {
        ChangePassUserDialogFragment fragment = new ChangePassUserDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
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
        View view = inflater.inflate(R.layout.dialog_change_pass_user, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTTC);

        edtPass = view.findViewById(R.id.edt_pass);
        edtRepass = view.findViewById(R.id.edt_repass);
        btnUpdate = view.findViewById(R.id.btn_update);
        ivClose = view.findViewById(R.id.iv_close);


        btnUpdate.setOnClickListener(v -> {
            if(edtPass.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }else if(edtPass.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Mật khẩu phải dài hơn 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if(edtRepass.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }else if (!edtRepass.getText().toString().equals(edtPass.getText().toString())) {
                Toast.makeText(getContext(), "Nhập lại chưa đúng mật khẩu", Toast.LENGTH_SHORT).show();
            }


            if (onUserUpdatedListener != null) {
                onUserUpdatedListener.onUserUpdated(edtRepass.getText().toString());
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

    public void setOnUserUpdatedListener(OnUserUpdatedListener listener) {
        this.onUserUpdatedListener = listener;
    }

    public interface OnUserUpdatedListener {
        void onUserUpdated(String newpass);
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
