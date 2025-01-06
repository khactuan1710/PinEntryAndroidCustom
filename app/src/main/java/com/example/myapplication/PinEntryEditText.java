package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
@SuppressLint("AppCompatCustomView")
public class PinEntryEditText extends EditText {
    public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

    private float mSpace = 24; //24 dp by default, space between the lines
    private float mCharSize;
    private float mNumChars = 6;
    private float mLineSpacing = 8; //8dp by default, height of the text from our lines
    private int mMaxLength = 6;

    private OnClickListener mClickListener;

    private float mLineStroke = 1; //1dp by default
    private float mLineStrokeSelected = 2; //2dp by default
    private Paint mLinesPaint;
    private Paint mCursorPaint;
    int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_selected}, // selected
            new int[]{android.R.attr.state_focused}, // focused
            new int[]{-android.R.attr.state_focused}, // unfocused
    };

    int[] mColors = new int[]{
            Color.GREEN,
            Color.BLACK,
            Color.GRAY
    };

    ColorStateList mColorStates = new ColorStateList(mStates, mColors);

    private int mCurrentIndex = 0; // The index of the current character being edited
    private boolean mShowCursor = false;
    private final Handler mHandler = new Handler();
    private final Runnable mCursorBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            mShowCursor = !mShowCursor;
            invalidate();
            mHandler.postDelayed(this, 500); // Blink every 500 ms
        }
    };

    public PinEntryEditText(Context context) {
        super(context);
    }

    public PinEntryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinEntryEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinEntryEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Paint mCirclePaint;
    private void init(Context context, AttributeSet attrs) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLineStrokeSelected = multi * mLineStrokeSelected;
        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);

        mCursorPaint = new Paint();
        mCursorPaint.setColor(Color.BLACK); // Color for the cursor line
        mCursorPaint.setStrokeWidth(2); // Width of the cursor line



        // Khởi tạo Paint cho dấu chấm tròn
        mCirclePaint = new Paint(getPaint());
        mCirclePaint.setColor(Color.parseColor("#E5E5E5")); // Đặt màu cho dấu chấm tròn, bạn có thể thay đổi màu sắc


        if (!isInEditMode()) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorControlActivated,
                    outValue, true);
            final int colorActivated = outValue.data;
            mColors[0] = colorActivated;

            context.getTheme().resolveAttribute(android.R.attr.colorPrimary,
                    outValue, true);
            final int colorDark = outValue.data;
            mColors[1] = colorDark;

            context.getTheme().resolveAttribute(android.R.attr.colorControlHighlight,
                    outValue, true);
            final int colorHighlight = outValue.data;
            mColors[2] = colorHighlight;
        }
        setBackgroundResource(0);
        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels for our density
        mMaxLength = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 6);
        mNumChars = mMaxLength;

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // When tapped, move cursor to end of text.
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
//        // Start cursor blinking animation
//        mHandler.post(mCursorBlinkRunnable);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();

        // Đặt khoảng cách giữa các ô
        mSpace = 8 * getResources().getDisplayMetrics().density;

        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int top = getPaddingTop(); // Thay đổi từ bottom thành top
        int bottom = getHeight() - getPaddingBottom(); // Đặt bottom để khớp với chiều cao của view

        // Text Width
        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);

        for (int i = 0; i < mNumChars; i++) {
            updateColorForLines(i == textLength);

            // Vẽ hình chữ nhật bo tròn với chỉ viền
            float rectLeft = startX;
            float rectRight = startX + mCharSize;
            float rectTop = top; // Đặt rectTop bằng với top của view
            float rectBottom = rectTop + mCharSize; // Đặt rectBottom để ô có chiều cao bằng mCharSize

            // Bo tròn 4 góc
            float cornerRadius = 8 * getResources().getDisplayMetrics().density;

            // Thiết lập để chỉ vẽ viền, không có nền
            mLinesPaint.setStyle(Paint.Style.STROKE);

            canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, cornerRadius, cornerRadius, mLinesPaint);

            // Custom ký tự nhập vào
            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, rectTop + mCharSize - (mLineSpacing * 2), getPaint());
            }

            // Draw cursor line if this is the current index
            if (i == mCurrentIndex && mShowCursor) {
                float cursorX = startX + mCharSize / 2;
                float cursorTop = rectTop + 20; // Adjust cursor position
                float cursorBottom = rectBottom - 20;
                canvas.drawLine(cursorX, cursorTop, cursorX, cursorBottom, mCursorPaint);
            }

            // Di chuyển startX để vẽ ký tự tiếp theo
            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }





    private int getColorForState(int... states) {
        return mColorStates.getColorForState(states, Color.GRAY);
    }

    /**
     * @param next Is the current char the next character to be input?
     */
    private void updateColorForLines(boolean next) {
        if (isFocused()) {
            mLinesPaint.setStrokeWidth(mLineStrokeSelected);
            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
            if (next) {
                mLinesPaint.setColor(getColorForState(android.R.attr.state_selected));
            }
        } else {
            mLinesPaint.setStrokeWidth(mLineStroke);
            mLinesPaint.setColor(getColorForState(-android.R.attr.state_focused));
        }
    }
    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mCurrentIndex = text.length(); // Update the current index
        invalidate(); // Redraw the view
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused) {
            mCurrentIndex = getText().length(); // Update the current index
            invalidate(); // Redraw the view
            // Start cursor blinking animation
            mHandler.post(mCursorBlinkRunnable);
        }else {
            mShowCursor = false; // Hide the cursor when losing focus
            mHandler.removeCallbacks(mCursorBlinkRunnable); // Stop blinking
        }

    }

    public void removeCr() {
        mShowCursor = false; // Hide the cursor when losing focus
        mHandler.removeCallbacks(mCursorBlinkRunnable); // Stop blinking
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mCursorBlinkRunnable); // Stop blinking when detached
    }
}