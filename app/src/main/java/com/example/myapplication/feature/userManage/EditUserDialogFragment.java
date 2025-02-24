package com.example.myapplication.feature.userManage;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;
import com.example.myapplication.feature.createAccount.AddressAdapter;
import com.example.myapplication.feature.createAccount.InputAddressDialogFragment;
import com.example.myapplication.model.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class EditUserDialogFragment extends DialogFragment {
    private CustomEditText edtPhoneNumber, edtAddress, edtUsername;
    private AppCompatButton btnUpdate;
    private ImageView ivClose;
    private Switch switchIsActive;
    private UserResponse.User user;
    private OnUserUpdatedListener onUserUpdatedListener;
    private List<String> listAddress;
    TextView tvAddAddress, tvCount;
    RecyclerView rcvAddress;
    private AddressAdapter addressAdapter;

    public static EditUserDialogFragment newInstance(UserResponse.User user) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_user, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTTC);

        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtUsername = view.findViewById(R.id.edtUsername);
        switchIsActive = view.findViewById(R.id.switchIsActive);
        btnUpdate = view.findViewById(R.id.btn_update);
        ivClose = view.findViewById(R.id.iv_close);

        tvAddAddress = view.findViewById(R.id.tv_add_address);
        rcvAddress = view.findViewById(R.id.rcv_data);
        tvCount = view.findViewById(R.id.tv_count);

        listAddress = new ArrayList<>();
        if (getArguments() != null) {
            user = (UserResponse.User) getArguments().getSerializable("user");
            if (user != null) {
                edtPhoneNumber.setText(user.getPhoneNumber());
                edtAddress.setText(user.getAddress());
                edtUsername.setText(user.getFullName());
                switchIsActive.setChecked(user.isActive());
                listAddress = user.getAddressNew();
            }
        }

        addressAdapter = new AddressAdapter(getContext(), listAddress);
        rcvAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvAddress.setAdapter(addressAdapter);
        addressAdapter.setOnDelete(new AddressAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String address) {
                listAddress.remove(address);
                tvCount.setText("Địa chỉ: " + listAddress.size());
                addressAdapter.notifyDataSetChanged();
            }
        });

        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputAddressDialogFragment dialogFragment = InputAddressDialogFragment.newInstance();
                dialogFragment.setOnAddListener(address -> {
                    listAddress.add(address);
                    addressAdapter.notifyDataSetChanged();
                });
                dialogFragment.show(getActivity().getSupportFragmentManager(), "InputAddressDialogFragment");
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if(edtPhoneNumber.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            if(listAddress == null || listAddress.isEmpty() || listAddress.size() == 0) {
                Toast.makeText(getContext(), "Vui lòng thêm địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            if(edtUsername.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (onUserUpdatedListener != null) {
                user.setPhoneNumber(edtPhoneNumber.getText().toString());
                user.setAddress(edtAddress.getText().toString());
                user.setFullName(edtUsername.getText().toString());
                user.setActive(switchIsActive.isChecked());
                user.setAddressNew(listAddress);
                onUserUpdatedListener.onUserUpdated(user);
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
        void onUserUpdated(UserResponse.User updatedUser);
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
