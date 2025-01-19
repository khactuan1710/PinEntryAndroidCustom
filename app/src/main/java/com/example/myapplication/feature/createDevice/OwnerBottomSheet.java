package com.example.myapplication.feature.createDevice;

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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.UserResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class OwnerBottomSheet  extends BottomSheetDialogFragment {
    OnSelect onChoose;
    AppCompatImageView ivClose;

    RecyclerView rcv_data;
    private SelectUserAdapter selectUserAdapter;
    private List<UserResponse.User> userList;
    private Activity activity;
    public OwnerBottomSheet(Activity activity, List<UserResponse.User> userList) {
        this.activity = activity;
        this.userList = userList;
    }
    public void setOnSelected(OnSelect onChoose) {
        this.onChoose = onChoose;
    }


    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogThemeV6);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.botom_sheet_select_user, null);
        ivClose = view.findViewById(R.id.iv_close);
        rcv_data = view.findViewById(R.id.rcv_data);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        selectUserAdapter = new SelectUserAdapter(activity, userList);
        selectUserAdapter.setItemClick(new SelectUserAdapter.ItemClick() {
            @Override
            public void onClick(UserResponse.User user) {
                if(onChoose != null) {
                    onChoose.onChoose(user);
                    dismiss();
                }
            }
        });


        rcv_data.setLayoutManager(new LinearLayoutManager(activity));
        rcv_data.setAdapter(selectUserAdapter);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }



    public interface OnSelect {
        void onChoose(UserResponse.User user);
    }
}