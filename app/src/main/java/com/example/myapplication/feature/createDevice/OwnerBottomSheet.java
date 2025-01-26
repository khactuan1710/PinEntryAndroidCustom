package com.example.myapplication.feature.createDevice;

import static com.example.myapplication.util.CurrencyUtils.normalizeString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;
import com.example.myapplication.model.UserResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class OwnerBottomSheet  extends BottomSheetDialogFragment {
    OnSelect onChoose;
    AppCompatImageView ivClose;

    RecyclerView rcv_data;
    private SelectUserAdapter selectUserAdapter;
    private List<UserResponse.User> userList;
    private Activity activity;
    private CustomEditText edtSearch;
    private List<UserResponse.User> originalList;
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
        edtSearch = view.findViewById(R.id.edt_search);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        originalList = new ArrayList<>(userList);

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

        edtSearch.setOnTextChangeListener(new CustomEditText.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if (text != null && !text.isEmpty()) {
                    // Lọc danh sách dựa trên shortName
                    List<UserResponse.User> filteredList = new ArrayList<>();

                    String normalizedSearchText = normalizeString(text);

                    for (UserResponse.User user : originalList) {
                        String normalizedFullName = normalizeString(user.getFullName());
                        String normalizedPhoneNumber = normalizeString(user.getPhoneNumber());

                        // Kiểm tra xem normalizedSearchText có xuất hiện trong fullName hoặc phoneNumber
                        if ((normalizedFullName != null && normalizedFullName.contains(normalizedSearchText)) ||
                                (normalizedPhoneNumber != null && normalizedPhoneNumber.contains(normalizedSearchText))) {
                            filteredList.add(user);
                        }
                    }

                    // Cập nhật lại danh sách và thông báo cho adapter
                    userList.clear();
                    userList.addAll(filteredList);
                    selectUserAdapter.notifyDataSetChanged();

                }else {
                    // Nếu không có văn bản tìm kiếm, hiển thị danh sách ban đầu
                    userList.clear();
                    userList.addAll(originalList);
                    selectUserAdapter.notifyDataSetChanged();

                }
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return bottomSheetDialog;
    }




    public interface OnSelect {
        void onChoose(UserResponse.User user);
    }
}