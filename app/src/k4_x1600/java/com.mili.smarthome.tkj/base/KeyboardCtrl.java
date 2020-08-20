package com.mili.smarthome.tkj.base;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyboardCtrl extends KeyboardView{

    public static final int KEYMODE_PASSWORD = 0;
    public static final int KEYMODE_CALL = 1;
    public static final int KEYMODE_SET = 2;
    public static final int KEYMODE_EDIT = 3;

    private static final String Tag = "MyKeyboardView";
    private EditText mEditText;
    private IKeyboardListener mListener = null;
    private boolean mClickable = true;
    private int mMaxLen = 4;

    // 0-9 的数字
//    private final List<Character> keyCodes = Arrays.asList(
//            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
    private final List<Character> temp = Arrays.asList(
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0');

    private List<Character> keyCodes;

    private Integer[] keyIcons = {R.drawable.key_0, R.drawable.key_1,
            R.drawable.key_2, R.drawable.key_3, R.drawable.key_4, R.drawable.key_5,
            R.drawable.key_6, R.drawable.key_7, R.drawable.key_8, R.drawable.key_9};

    public interface IKeyboardListener {
        boolean onKeyCancel();
        boolean onKeyConfirm();
        boolean onKey(int code);
        boolean onKeyText(String text);
        boolean onTextChanged(String text);
    }

    public KeyboardCtrl(Context context) {
        this(context, null);
    }

    public KeyboardCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        keyCodes = new ArrayList<>(temp);
        Keyboard keyboard = new Keyboard(context, R.xml.keyboard);
        setKeyboard(keyboard);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(mKeyboardListener);

        mEditText = new EditText(context);
        mEditText.addTextChangedListener(mTextViewListener);
        setTextMaxLen(mMaxLen);
    }

    public void setKeyboardListener(IKeyboardListener listener) {
        this.mListener = listener;
        Log.d(Tag, "setKeyboardListener");
    }

    public void setTextMaxLen(int len) {
        mMaxLen = len;
        if (mEditText != null) {
            mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(len)});
        }
    }

    public void setText(String text) {
        if (mEditText != null) {
            if (text.length() > mMaxLen) {
                text = text.substring(0, mMaxLen);
            }
            mEditText.setText(text);
        }
    }

    public String getText() {
        if (mEditText == null)
            return null;
        return mEditText.getText().toString();
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    /**
     * 设置键盘模式
     * @param mode 0-password mode  1-call mode 2-set mode 3 edit mode
     */
    public void setMode(int mode) {
        int resId = 0;
        switch (mode) {
            case KEYMODE_PASSWORD:
                resId = R.drawable.key_lock;
                break;
            case KEYMODE_CALL:
                resId = R.drawable.key_answer;
                break;
            case KEYMODE_SET:
                resId = R.drawable.key_ok;
                break;
            case KEYMODE_EDIT:
                resId = R.drawable.key_edit;
                break;
        }
        getKeyboard().getKeys().get(11).icon = getResources().getDrawable(resId, null);
        invalidateKey(11);
    }

    /**
     * 随机打乱数字键盘上显示的数字顺序。
     */
    public void shuffleKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null
                && keyboard.getKeys().size() > 0) {
            // 随机排序数字
            Collections.shuffle(keyCodes);

            // 遍历所有的按键
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                // 如果按键是数字
                if (key.codes[0] != Keyboard.KEYCODE_CANCEL
                        && key.codes[0] != Keyboard.KEYCODE_DONE) {
                    char code = keyCodes.get(index++);
                    int iconId = keyIcons[code-'0'];
                    key.codes[0] = code;
                    key.icon = getResources().getDrawable(iconId, null);
                }
            }
            // 更新键盘
            invalidateAllKeys();
        }
    }

    public void resetKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null
                && keyboard.getKeys().size() > 0) {

            List<Keyboard.Key> keys = keyboard.getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                index++;
                if (index > 9) {
                    break;
                }
                //1-9按键
                int iconId = keyIcons[index];
                key.codes[0] = index + '0';
                key.icon = getResources().getDrawable(iconId, null);
            }

            //0键
            keys.get(10).codes[0] = '0';
            keys.get(10).icon = getResources().getDrawable(R.drawable.key_0, null);

            // 更新键盘
            invalidateAllKeys();
        }
    }

    private KeyboardView.OnKeyboardActionListener mKeyboardListener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int i) {

        }

        @Override
        public void onRelease(int i) {

        }

        @Override
        public void onKey(int code, int[] keyCodes) {
            //按键音处理
            if (AppConfig.getInstance().getKeyVolume() == 1) {
                SinglechipClientProxy.getInstance().playKeyClick();
            }

            if (!mClickable) {
                return;
            }
            Editable editable = mEditText.getText();
            int start = mEditText.length();//mEditText.getSelectionStart();

            switch (code) {
                case Keyboard.KEYCODE_CANCEL:
                    if (mListener != null) {
                        mListener.onKeyCancel();
                    }
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start-1, start);
                            if (mListener != null) {
                                mListener.onKeyText(editable.toString());
                            }
                        }
                    }
                    break;

                case Keyboard.KEYCODE_DONE:
                    if (mListener != null) {
                        mListener.onKeyConfirm();
                    }
                    break;

                default:
                    if (code <'0' || code > '9') {
                        break;
                    }
                    if (mListener != null) {
                        mListener.onKey(code-'0');
                    }
                    if (editable.length() >= mMaxLen) {
                        break;
                    }
                    editable.insert(start, Character.toString((char)code));
                    if (mListener != null) {
                        mListener.onKeyText(editable.toString());
                    }
                    break;
            }
        }

        @Override
        public void onText(CharSequence charSequence) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    private TextWatcher mTextViewListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mClickable && mListener != null && mEditText != null) {
                String text = mEditText.getText().toString();
                mListener.onTextChanged(text);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
