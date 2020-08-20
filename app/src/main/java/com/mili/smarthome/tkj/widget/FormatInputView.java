package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 格式化输入控件
 */
public class FormatInputView extends InputView {

    public static final int FORMAT_IP = 1;
    public static final int FORMAT_DATE = 2;
    public static final int FORMAT_TIME = 3;

    @IntDef({FORMAT_IP, FORMAT_DATE, FORMAT_TIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FormatMode {}

    private int mFormat = FORMAT_IP;

    public FormatInputView(Context context) {
        this(context, null, 0);
    }

    public FormatInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FormatInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FormatInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.FormatInputView, defStyleAttr, defStyleRes);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.FormatInputView_format:
                        mFormat = a.getInt(attr, FORMAT_IP);
                        break;
                }
            }
            a.recycle();
        }
        bindFormat();
    }

    public void setInputFormat(@FormatMode int format) {
        mFormat = format;
        bindFormat();
    }

    private void bindFormat() {
        switch (mFormat) {
            case FORMAT_IP:
                mText = new StringBuilder("000.000.000.000");
                mMaxLength = 15;
                mCursorStart = 0;
                break;
            case FORMAT_DATE:
                SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                mText = new StringBuilder(dateF.format(System.currentTimeMillis()));
                mMaxLength = 10;
                mCursorStart = 2;
                break;
            case FORMAT_TIME:
                SimpleDateFormat timeF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                mText = new StringBuilder(timeF.format(System.currentTimeMillis()));
                mMaxLength = 8;
                mCursorStart = 0;
                break;
        }
        mCursorIndex = mCursorStart;
    }

    @Override
    public void setMaxLength(int maxLength) {
        String message = FormatInputView.class.getName() + "不支持自定义最大长度！";
        throw new UnsupportedOperationException(message);
    }

    @Override
    public void setText(CharSequence text) {
        if (mFormat == FORMAT_IP) {
            super.setText(ipPadding(text.toString()));
        }
    }

    public CharSequence getTrimText() {
        if (mFormat == FORMAT_IP) {
            return ipTrim(super.getText().toString());
        } else {
            return super.getText();
        }
    }

    @Override
    public void clearText() {
        if (mFormat == FORMAT_IP) {
            super.setText("000.000.000.000");
        }
    }

    @Override
    public void input(int num) {
        char c = (char) ((num % 10) + 48);
        if (!inputCheck(c, mCursorIndex)) {
            return;
        }
        mText.setCharAt(mCursorIndex, c);
        if (mCursorIndex == mText.length() - 1) {
            focusNext();
        } else {
            mCursorIndex++;
            while (!Character.isDigit(mText.charAt(mCursorIndex))) {
                mCursorIndex++;
            }
        }
        invalidate();
    }

    @Override
    public boolean backspace() {
        mText.setCharAt(mCursorIndex, '0');
        if (mCursorIndex == mCursorStart) {
            return focusPrevious();
        } else {
            mCursorIndex--;
            while (!Character.isDigit(mText.charAt(mCursorIndex))) {
                mCursorIndex--;
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.getSaveCount();
        canvas.save();

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        int color = mTextColor.getColorForState(getDrawableState(), Color.WHITE);
        mPaint.setColor(color);
        mPaint.setTextSize(mTextSize);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseline = paddingTop - fontMetrics.top;

        // 绘制文本
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mText, 0, mText.length(), paddingLeft, baseline, mPaint);

        // 绘制光标
        if (isFocused() && mDrawCursor && mCursorIndex < mText.length()) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dip2px(1.5f));

            float linePadding = dip2px(1f);
            float offestY = paddingTop + fontMetrics.descent - fontMetrics.top;
            float startX = paddingLeft + mPaint.measureText(mText.substring(0, mCursorIndex)) + linePadding;
            float stopX = paddingLeft + mPaint.measureText(mText.substring(0, mCursorIndex+1)) - linePadding;
            canvas.drawLine(startX, offestY, stopX, offestY, mPaint);
        }

        canvas.restoreToCount(saveCount);
    }

    private CharSequence ipPadding(String ip) {
        String[] temp = ip.split("\\.");
        StringBuilder newIp = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            if (i != 0) {
                newIp.append(".");
            }
            String seq = temp[i];
            if (seq.length() == 1) {
                newIp.append("00");
            } else if (seq.length() == 2) {
                newIp.append("0");
            }
            newIp.append(seq);
        }
        return newIp;
    }

    private CharSequence ipTrim(String ip) {
        String[] temp = ip.split("\\.");
        StringBuilder newIp = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            if (i != 0) {
                newIp.append(".");
            }
            newIp.append(Integer.valueOf(temp[i]));
        }
        return newIp;
    }

    private boolean inputCheck(char input, int index) {
        if (mFormat == FORMAT_IP) {
            return inputIpCheck(input, index);
        } else if (mFormat == FORMAT_DATE) {
            return inputDateCheck(input, index);
        } else if (mFormat == FORMAT_TIME) {
            return inputTimeCheck(input, index);
        }
        return false;
    }

    private boolean inputIpCheck(char input, int index) {
        final char CHAR2 = '2';
        final char CHAR5 = '5';
        if (index == 0 || index == 4 || index == 8 || index == 12) {
            if (input > CHAR2) {
                return false;
            } else if (input == CHAR2) {
                char nextC = mText.charAt(index + 1);
                if (nextC > CHAR5) {
                    nextC = CHAR5;
                    mText.setCharAt(index + 1, CHAR5);
                }
                if (nextC == CHAR5 && mText.charAt(index + 2) > CHAR5) {
                    mText.setCharAt(index + 2, CHAR5);
                }
            }
        } else if (index == 1 || index == 5 || index == 9 || index == 13) {
            if (mText.charAt(index - 1) == CHAR2 && input > CHAR5) {
                return false;
            } else if (input == CHAR5) {
                if (mText.charAt(index + 1) > CHAR5) {
                    mText.setCharAt(index + 1, CHAR5);
                }
            }
        } else if (index == 2 || index == 6 || index == 10 || index == 14) {
            String s = mText.substring(index - 2, index);
            if (Integer.valueOf(s) == 25 && input > CHAR5)
                return false;
        }
        return true;
    }

    private boolean inputDateCheck(char input, int index) {
        final char CHAR1 = '1';
        final char CHAR2 = '2';
        final char CHAR3 = '3';
        if (index == 5) {
            // 月十位
            if (input > CHAR1) {
                return false;
            } else if (input == CHAR1) {
                if (mText.charAt(index + 1) > CHAR2) {
                    mText.setCharAt(index + 1, CHAR2);
                }
            }
        } else if (index == 6) {
            // 月个位
            if (mText.charAt(5) == CHAR1 && input > CHAR2)
                return false;
        } else if (index == 8) {
            // 日十位
            if (input > CHAR3) {
                return false;
            } else if (input == CHAR3) {
                if (mText.charAt(index + 1) > CHAR1) {
                    mText.setCharAt(index + 1, CHAR1);
                }
            }
        } else if (index == 9) {
            // 日个位
            if (mText.charAt(8) == CHAR3 && input > CHAR1)
                return false;
        }
        return true;
    }

    private boolean inputTimeCheck(char input, int index) {
        final char CHAR2 = '2';
        final char CHAR3 = '3';
        final char CHAR5 = '5';
        if (index == 0) {
            // 小时十位
            if (input > CHAR2) {
                return false;
            } else if (input == CHAR2) {
                if (mText.charAt(1) > CHAR3) {
                    mText.setCharAt(1, CHAR3);
                }
            }
        } else if (index == 1) {
            // 小时个位
            if (mText.charAt(0) == CHAR2 && input > CHAR3)
                return false;
        } else if (index == 3 || mCursorIndex == 6) {
            // 分或者秒的十位
            if (input > CHAR5)
                return false;
        }
        return true;
    }
}
