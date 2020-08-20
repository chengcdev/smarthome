package com.mili.smarthome.tkj.setting.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

import java.util.ArrayList;
import java.util.List;

public class SetListView extends ListView {

    private static final int PAGE_SIZE = 3;
    private ListViewAdapter mAdapter;
    private ImageButton mBtnUp, mBtnDown;
    private TextView mHeadText;
    private List<SettingFunc> mItemList = new ArrayList<>();

    public SetListView(Context context) {
        this(context, null);
    }

    public SetListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View headerView = LayoutInflater.from(context).inflate(R.layout.listview_header, this, false);
        addHeaderView(headerView);
        setHeaderDividersEnabled(false);
        mHeadText = (TextView) headerView.findViewById(R.id.tv_head);

        View footerView = LayoutInflater.from(context).inflate(R.layout.listview_footer, this, false);
        addFooterView(footerView, null, false);
        setFooterDividersEnabled(false);

        mBtnUp = (ImageButton) footerView.findViewById(R.id.ib_up);
        mBtnDown = (ImageButton) footerView.findViewById(R.id.ib_down);
        mBtnUp.setOnClickListener(mUpDownListener);
        mBtnDown.setOnClickListener(mUpDownListener);

        mAdapter = new ListViewAdapter(context);
        showFooter(null);
        setAdapter(mAdapter);
        setDividerHeight(0);

        Log.d("MyListView", "create listview");
    }

    public void resetData(SettingFunc func) {
        mItemList.clear();
        putItem(func);
    }

    public void putItem(SettingFunc func) {
        mItemList.add(func);
        if (mAdapter != null) {
            mAdapter.setData(func);
            showHeader(func);
            showFooter(func);
        }
    }

    public SettingFunc getItem() {
        int count = mItemList.size();
        if (count > 0) {
            SettingFunc func = mItemList.get(count-1);
            return func;
        }
        return null;
    }

    public SettingFunc getParentItem() {
        int count = mItemList.size();
        if (count > 1) {
            mItemList.remove(count-1);
            SettingFunc func = mItemList.get(count-2);
            mAdapter.setData(func);
            showHeader(func);
            showFooter(func);
            return func;
        }
        return null;
    }

    public void freshData() {
        if (mAdapter != null) {
            mAdapter.firstPage();
        }
    }

    private void showHeader(SettingFunc func) {
        if (func != null) {
            mHeadText.setText(func.getName());
        } else {
            mHeadText.setText("");
        }
    }

    private void showFooter(SettingFunc func) {
        if (func != null && func.hasChild() && func.getChild().size() > PAGE_SIZE) {
            mBtnUp.setVisibility(View.VISIBLE);
            mBtnDown.setVisibility(View.VISIBLE);
        } else {
            mBtnUp.setVisibility(View.INVISIBLE);
            mBtnDown.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener mUpDownListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ib_up:
                    if (mAdapter != null) {
                        mAdapter.prePage();
                    }
                    break;
                case R.id.ib_down:
                    if (mAdapter != null) {
                        mAdapter.nextPage();
                    }
                    break;
            }
        }
    };

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private SettingFunc mDataItem;
        private int mPage;

        private ListViewAdapter(Context context) {
            this.mContext = context;
            mPage = 0;
        }

        /**更新列表显示内容*/
        private void setData(SettingFunc item) {
            this.mDataItem = item;
            mPage = 0;
            notifyDataSetChanged();
        }

        public void firstPage() {
            mPage = 0;
            notifyDataSetChanged();
        }

        public void prePage() {
            if (mPage > 0) {
                mPage--;
                notifyDataSetChanged();
            }
        }

        public void nextPage() {
            if (mDataItem == null || !mDataItem.hasChild()) {
                return;
            }
            int maxCount = mDataItem.getChild().size();
            int maxPage = maxCount/PAGE_SIZE;
            if (maxCount % PAGE_SIZE != 0) {
                maxPage += 1;
            }
            if (mPage < maxPage-1) {
                mPage++;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return PAGE_SIZE;
        }

        @Override
        public Object getItem(int position) {
            if (mDataItem == null || !mDataItem.hasChild()) {
                return null;
            }
            int index = mPage * PAGE_SIZE + position;
            if (index < mDataItem.getChild().size()) {
                return mDataItem.getChild().get(index);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return (mPage*PAGE_SIZE + position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);

                viewHolder = new ViewHolder() ;
                viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.line = (View) convertView.findViewById(R.id.v_line);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int index = mPage * PAGE_SIZE + position;
            if (mDataItem != null && mDataItem.hasChild() && index < mDataItem.getChild().size()) {
                viewHolder.text.setText(mDataItem.getChild().get(index).getName());
                viewHolder.icon.setVisibility(View.VISIBLE);
                viewHolder.line.setVisibility(View.VISIBLE);
            } else {
                viewHolder.text.setText("");
                viewHolder.icon.setVisibility(View.INVISIBLE);
                viewHolder.line.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        private class ViewHolder{
            TextView text;
            ImageView icon;
            View line;
        }
    }

}
