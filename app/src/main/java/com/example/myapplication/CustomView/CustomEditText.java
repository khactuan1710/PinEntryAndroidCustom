package com.example.myapplication.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputLayout;

public class CustomEditText extends ConstraintLayout {


    private EditText editText;
    private ImageView ivClear;
    private ImageView ivPassword;
    private ImageView ivEnd;
    private FrameLayout frmClick;
    private TextView tvEnd;
    private TextInputLayout textInputLayout;
    private View line;
    private ConstraintLayout ctr_container;

    private OnTextChangeListener onTextChangeListener;
    private OnTextFocusChangeListener onTextFocusChangeListener;
    private OnTextEndClickListener onTextEndClickListener;
    private OnIconEndClickListener onIconEndClickListener;
    private OnActionDoneListener onActionDoneListener;
    private OnClickViewMaskListener onClickViewMaskListener;

    final long DISABLE_DELAY = 500;
    final Handler handler = new Handler();
    private int inputType = 0;
    private boolean showClearButton = true;
    private boolean multiLine = false;
    private boolean showBgRadius = false;
    private int maxLength = 200;
    private AttributeSet attrs;

    private int typeInput = 0;

    public void setOnTextFocusChangeListener(OnTextFocusChangeListener onTextFocusChangeListener) {
        this.onTextFocusChangeListener = onTextFocusChangeListener;
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        initView();
        initListeners();
    }

    public void enableEditText(boolean isEnable) {
        editText.setEnabled(isEnable);
        if (!isEnable) {
            showClearButton = false;
            editText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_9099A0));
        } else {
            editText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_44494D));
        }
        ivClear.setVisibility(isEnable && !TextUtils.isEmpty(editText.getText())
                ? View.VISIBLE
                : View.GONE);
    }

    public void enableEditText2(boolean isEnable) {
        editText.setEnabled(isEnable);
        if (!isEnable) {
            showClearButton = false;
        }
        ivClear.setVisibility(isEnable && !TextUtils.isEmpty(editText.getText())
                ? View.VISIBLE
                : View.GONE);
    }

    public String getText() {
        return (editText.getText() != null ? editText.getText().toString().trim() : "");
    }

    public void setText(String input, boolean moveCursorLast) {
        editText.setText(input);
        if (moveCursorLast) {
            editText.setSelection(editText.length());
        }
    }

    public void setText(String input) {
        editText.setText(input);
    }

    public void setTextEnd(String input) {
        if (TextUtils.isEmpty(input)) {
            tvEnd.setText("");
            tvEnd.setVisibility(View.GONE);
        } else {
            tvEnd.setText(input);
            tvEnd.setVisibility(View.VISIBLE);
        }
    }

    public void setHint(String hint) {
        textInputLayout.setHint(hint);
    }

    public void setImeOptions(int action) {
        editText.setImeOptions(action);
    }

    public void setTypeface(Typeface typeface) {
        editText.setTypeface(typeface);
    }

    public void setError(String error) {
        if (!TextUtils.isEmpty(error)) {
            textInputLayout.setError(error);
        }
    }

    public void setBorderCustom(int bg) {
        ctr_container.setBackgroundResource(bg);
    }

    public void clearFocus() {
        editText.clearFocus();
        if (showBgRadius) {
            if (editText.length() == 0) {
                editText.setTranslationY(-12);
            } else {
                editText.setTranslationY(0);
            }
        }
    }


    public void setEndIcon(int icon) {
        ivEnd.setImageResource(icon);
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_custom_editext, this, true);

        editText = view.findViewById(R.id.edt_input);
        ivClear = view.findViewById(R.id.iv_clear);
        ivPassword = view.findViewById(R.id.iv_password);
        ivEnd = view.findViewById(R.id.iv_end);
        frmClick = view.findViewById(R.id.frm_click);
        tvEnd = view.findViewById(R.id.tv_end);
        textInputLayout = view.findViewById(R.id.text_input_layout);
        line = view.findViewById(R.id.line);
        ctr_container = view.findViewById(R.id.ctr_container);

        TypedArray attrArr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int editTextId = View.generateViewId();
            int textInputLayoutId = View.generateViewId();
            editText.setId(editTextId);
            textInputLayout.setId(textInputLayoutId);
        }

        Drawable endIcon = attrArr.getDrawable(R.styleable.CustomEditText_endIcon);
        if (endIcon != null) {
            ivEnd.setImageDrawable(endIcon);
            ivEnd.setVisibility(View.VISIBLE);
        } else {
            ivEnd.setVisibility(View.GONE);
        }

        boolean clickViewEnable = attrArr.getBoolean(R.styleable.CustomEditText_clickViewEnable, false);
        frmClick.setVisibility(clickViewEnable ? View.VISIBLE : View.GONE);

        boolean isMultiLine = attrArr.getBoolean(R.styleable.CustomEditText_isMultiLine, false);
        if (isMultiLine) {
            editText.setSingleLine(false);
            editText.setMaxLines(Integer.MAX_VALUE);
            editText.setEllipsize(null);
            editText.setHorizontallyScrolling(false);
        }


        showClearButton = attrArr.getBoolean(R.styleable.CustomEditText_showClearButton, true);
        showClearButton = attrArr.getBoolean(R.styleable.CustomEditText_showClearButton, true);

        showBgRadius = attrArr.getBoolean(R.styleable.CustomEditText_showBgRadius, false);

        multiLine = attrArr.getBoolean(R.styleable.CustomEditText_multiLine, false);

        maxLength = attrArr.getInt(R.styleable.CustomEditText_maxLength, 200);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

        String hint = attrArr.getString(R.styleable.CustomEditText_hint);
        if (hint != null) {
            textInputLayout.setHint(hint);
        }


        String endText = attrArr.getString(R.styleable.CustomEditText_endText);
        if (endText != null) {
            tvEnd.setText(endText);
            tvEnd.setVisibility(View.VISIBLE);
        }

        boolean enable = attrArr.getBoolean(R.styleable.CustomEditText_enable, true);
        enableEditText(enable);

        boolean editable = attrArr.getBoolean(R.styleable.CustomEditText_enable, true);
        editText.setFocusable(editable);

        editText.setTextColor(
                attrArr.getColor(
                        R.styleable.CustomEditText_textColorCustom,
                        getResources().getColor(R.color.color_44494D)
                )
        );

        editText.setHintTextColor(
                attrArr.getColor(
                        R.styleable.CustomEditText_titleColor,
                        Color.parseColor("#6C757D")
                )
        );

        setInputType(attrArr.getInt(R.styleable.CustomEditText_inputType, 0));
        typeInput = attrArr.getInt(R.styleable.CustomEditText_inputType, 0);
        int imeOptions = attrArr.getInteger(R.styleable.CustomEditText_imeOptions, 0);

        switch (imeOptions) {
            case 0:
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                break;
            case 1:
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
        }

        attrArr.recycle();
    }

    private void setInputType(int inputType) {
        switch (inputType) {
            case 1: // InputType.TYPE_CLASS_NUMBER
                editText.setInputType(multiLine ?
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_NUMBER :
                        InputType.TYPE_CLASS_NUMBER);
                break;
            case 2: // InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 3: // InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 4: // InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
            // Add more cases for other input types as needed
            default:
                // InputType.TYPE_CLASS_TEXT
                editText.setInputType(multiLine ?
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT :
                        InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private void initListeners() {
        ivClear.setOnClickListener(v -> {
            editText.setText("");
            if (onTextChangeListener != null) {
                onTextChangeListener.onTextChange("");
            }
        });

        ivPassword.setOnClickListener(v -> {
            if (onTextEndClickListener != null) {
                onTextEndClickListener.onTextEndClick();
            }
            boolean isPasswordVisible = editText.getTransformationMethod() instanceof PasswordTransformationMethod;

            showPasswordToggle(isPasswordVisible);
        });

        tvEnd.setOnClickListener(v -> {
            if (onTextEndClickListener != null) {
                onTextEndClickListener.onTextEndClick();
            }
        });

        frmClick.setOnClickListener(v -> {
            if (!editText.isEnabled()) {
                return;
            }
            if (onClickViewMaskListener != null) {
                onClickViewMaskListener.onClickViewMask();
            }
            frmClick.setEnabled(false);
            frmClick.setClickable(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    frmClick.setEnabled(true);
                    frmClick.setClickable(true);
                }
            }, DISABLE_DELAY);
        });

        ivEnd.setOnClickListener(v -> {
            if (onIconEndClickListener != null) {
                onIconEndClickListener.onIconEndClick();
            }
        });

        if (showBgRadius) {
            line.setVisibility(GONE);
            ctr_container.setBackgroundResource(R.drawable.bg_boder_defaut_edittext);
            if (editText.length() == 0) {
                editText.setTranslationY(-12);
            }
            editText.setOnFocusChangeListener((view, b) -> {
                new Handler().postDelayed(() -> {
                    String text = editText.getText().toString();
                    if (b) {
                        ctr_container.setBackgroundResource(R.drawable.bg_boder_defaut);
                        editText.setTranslationY(0);
                    } else {
                        ctr_container.setBackgroundResource(R.drawable.bg_boder_defaut_edittext);
                        if (text.length() == 0) {
                            editText.setTranslationY(-12);
                        } else {
                            editText.setTranslationY(0);
                        }
                    }
                    if (onTextFocusChangeListener != null) {
                        onTextFocusChangeListener.onTextFocusChange(b);
                    }
                }, 200);
            });
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                ivClear.setVisibility(!TextUtils.isEmpty(text) && showClearButton ? View.VISIBLE : View.GONE);
                ivPassword.setVisibility(!TextUtils.isEmpty(text) && typeInput == 2 ? View.VISIBLE : View.GONE);
                if (onTextChangeListener != null) {
                    onTextChangeListener.onTextChange(text);
                }
                if (showBgRadius) {
                    if (text.length() != 0 || editText.hasFocus()) {
                        editText.setTranslationY(0);
                    } else {
                        editText.setTranslationY(-12);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showPasswordToggle(boolean show) {
        if (show) {
            editText.setTransformationMethod(null);
            ivPassword.setImageResource(R.drawable.ic_eye_show_new);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivPassword.setImageResource(R.drawable.ic_eye_hide_new);
        }
        editText.setSelection(editText.length());
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.onTextChangeListener = listener;
    }

    public void setOnTextEndClickListener(OnTextEndClickListener listener) {
        this.onTextEndClickListener = listener;
    }

    public void setOnIconEndClickListener(OnIconEndClickListener listener) {
        this.onIconEndClickListener = listener;
    }

    public void setOnActionDoneListener(OnActionDoneListener listener) {
        this.onActionDoneListener = listener;
    }

    public void setOnClickViewMaskListener(OnClickViewMaskListener listener) {
        this.onClickViewMaskListener = listener;
    }

    public interface OnTextChangeListener {
        void onTextChange(String text);
    }

    public interface OnTextFocusChangeListener {
        void onTextFocusChange(boolean hasFocus);
    }

    public interface OnTextEndClickListener {
        void onTextEndClick();
    }

    public interface OnIconEndClickListener {
        void onIconEndClick();
    }

    public interface OnActionDoneListener {
        void onActionDone();
    }

    public interface OnClickViewMaskListener {
        void onClickViewMask();
    }
}
