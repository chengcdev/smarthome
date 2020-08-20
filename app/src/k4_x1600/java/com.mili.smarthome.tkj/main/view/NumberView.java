package com.mili.smarthome.tkj.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ResUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberView extends FrameLayout{

    public static final int MODE_CALL = 0;
    public static final int MODE_PASSWORD = 1;
    public static final int MODE_PASSWORD_SENIOR = 2;

    private static final String KeyImage = "image";

    private GridView mGridView;
    private TextView mTvNumber;
    private View mContentView;

    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList;
    private String mText;

    private int mMode = MODE_CALL;  //0-call mode 1-password mode 2-high password mode
    private int mMaxLen;
    private int mNumLen;


    public NumberView(Context context) {
        this(context, null);
    }

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (mContentView == null) {
            mContentView = LayoutInflater.from(context).inflate(R.layout.numberview, this);
        }
        mGridView = (GridView) mContentView.findViewById(R.id.gv_pwd);
        mTvNumber = (TextView)findViewById(R.id.tv_number);

        String[] from = {KeyImage};
        int[] to = {R.id.gvItem_image};
        mDataList = new ArrayList<>();
        mAdapter = new SimpleAdapter(context, mDataList, R.layout.numberview_item, from, to);
        mGridView.setAdapter(mAdapter);

        mMode = MODE_CALL;
        mText = "";
        mTvNumber.setText("");
    }

    /**
     * 设置控件长度
     * @param numLen    数字长度
     * @param iconLen   图标长度
     */
    public void setLen(int numLen, int iconLen) {
        mMaxLen = numLen + iconLen;
        mNumLen = numLen;
        if (mGridView != null) {
            mGridView.setNumColumns(iconLen);
        }
        if (mNumLen > 6) {
            mTvNumber.setTextSize(getResources().getDimension(R.dimen.sp_24));
        } else {
            mTvNumber.setTextSize(getResources().getDimension(R.dimen.sp_28));
        }
        LogUtils.d("numlen is " + numLen + ", iconlen is " + iconLen);
    }

    /**
     * 设置控件模式
     * @param mode  0-number mode 1-password mode 2-high password mode
     */
    public void setMode(int mode) {
        mMode = mode;
        if (mode == MODE_PASSWORD || mode == MODE_CALL) {
            mTvNumber.setText("");
            mNumLen = 0;
        }
    }

    public void setText(String text) {
        if (mGridView != null && mAdapter != null) {
            if (text.length() > mMaxLen) {
                mText = text.substring(0, mMaxLen);
            } else {
                mText = text;
            }
            int iconLen = mText.length();

            //高级密码模式，输入房号解析
            if (mMode == MODE_PASSWORD_SENIOR) {
                if (mText.length() <= mNumLen) {
                    mTvNumber.setText(mText);
                    mDataList.clear();
                    mAdapter.notifyDataSetChanged();
                    return;
                } else {
                    mTvNumber.setText(mText.substring(0, mNumLen));
                    iconLen = mText.length() - mNumLen;
                }
            }

            //图标显示解析
            mDataList.clear();
            for (int i=0; i<iconLen; i++) {
                Map<String, Object> map = new HashMap<>();
                switch (mMode) {
                    case MODE_CALL:
                        int index = mText.getBytes()[i] - '0';
                        int resId = ResUtils.getImageId(mContext, "call_" + index);
                        map.put(KeyImage, resId);
                        break;

                    case MODE_PASSWORD:
                    case MODE_PASSWORD_SENIOR:
                        map.put(KeyImage, R.drawable.pwd_star);
                        break;
                }
                mDataList.add(map);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public String getText() {
        return mText;
    }

    public void clear() {
        if (mGridView != null && mAdapter != null) {
            mText = "";
            mTvNumber.setText("");
            mDataList.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setVisibility(int visibility) {
        if (mGridView != null) {
            mTvNumber.setVisibility(visibility);
            mGridView.setVisibility(visibility);
        }
    }

//    private class GridViewAdapter extends BaseAdapter {
//
//        private Context mContext;
//        private List<Map<String, Object>> mItemList;
//
//        public GridViewAdapter(Context context, List<Map<String, Object>> dataList) {
//            this.mContext = context;
//            this.mItemList = dataList;
//        }
//
//        @Override
//        public int getCount() {
//            return mItemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            if (view == null) {
//                view = LayoutInflater.from(mContext).inflate(R.layout.numberview_item, null);
//            }
//            ImageView imageView = (ImageView) view.findViewById(R.id.gvItem_image);
//
//            Map<String, Object> map = mItemList.get(position);
//            imageView.setImageResource((Integer) map.get("image"));
//            return view;
//        }
//    }
}
