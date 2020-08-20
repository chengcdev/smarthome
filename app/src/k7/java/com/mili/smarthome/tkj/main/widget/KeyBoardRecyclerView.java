package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;

import java.util.List;

public class KeyBoardRecyclerView extends RecyclerView {

    private int count;
    private SettingAdapter adapter;
    private Context mContext;

    public KeyBoardRecyclerView(Context context) {
        super(context);
        init(context);
    }


    public KeyBoardRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyBoardRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void initAdapter(List<SettingModel> lists) {
        adapter = new SettingAdapter(mContext, lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(linearLayoutManager);
        if (count != 0) {
            adapter.refreshList(count);
        }
        setAdapter(adapter);
    }

    public void initAdapter(final List<SettingModel> lists, final String selecId) {
        adapter = new SettingAdapter(mContext, lists, new SettingAdapter.ISettingAdapterListener() {
            @Override
            public void notifyAdapter() {
                scrollToPosition(0);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(linearLayoutManager);
        if (count != 0) {
            if (selecId != null && !selecId.equals("") &&
                    lists.get(count).getkId() != null &&
                    !lists.get(count).getkId().equals(selecId)) {
                count = 0;
            }
            adapter.refreshList(count);
        }
        setAdapter(adapter);
    }

    public void initAdapter(List<SettingModel> lists, int selecPosition) {
        count = selecPosition;
        adapter = new SettingAdapter(mContext, lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(linearLayoutManager);
        setAdapter(adapter);
        adapter.refreshList(count);
        adapter.notifyDataSetChanged();
        scrollToPosition(count);
    }

    public void notifyList() {
        adapter.refreshList(count);
        adapter.notifyDataSetChanged();
        scrollToPosition(count);
    }

    //向上选择
    public void preScroll() {
        count--;
        if (count < 0) {
            count = adapter.getItemCount() - 1;
        }
        notifyList();
    }

    //向下选择
    public void nextScroll() {
        count++;
        if (count > adapter.getItemCount() - 1) {
            count = 0;
        }
        notifyList();
    }

    public void setItemPosition(int count) {
        this.count = count;
        notifyList();
    }

    public int getItemPosition() {
        return count;
    }

}
