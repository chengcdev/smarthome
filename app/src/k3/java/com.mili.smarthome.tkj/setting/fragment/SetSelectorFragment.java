package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetFuncAdapter;
import com.mili.smarthome.tkj.setting.view.KeyHintView;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class SetSelectorFragment extends BaseSetFragment implements ItemSelectorAdapter.OnItemClickListener {

    private RecyclerView rvFunc;
    private RecyclerView rvSelector;
    private SetFuncAdapter mFuncAdapter;
    private ItemSelectorAdapter mOptionAdapter;
    private KeyHintView vwKeyHint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_item_selector;
    }

    @Override
    protected void bindView() {
        rvFunc = findView(R.id.rv_func);
        rvFunc.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvSelector = findView(R.id.recyclerview);
        rvSelector.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        vwKeyHint = findView(R.id.key_hint_view);
    }

    @Override
    protected void bindData() {
        mFuncAdapter = new SetFuncAdapter(mContext);
        rvFunc.setAdapter(mFuncAdapter);
        mOptionAdapter = new ItemSelectorAdapter(mContext);
        mOptionAdapter.setOnItemClickListener(this);
        rvSelector.setAdapter(mOptionAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String funcCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
            mFuncAdapter.setFuncList(funcCode);
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_UP:
                prePage();
                break;
            case KEYCODE_DOWN:
                nextPage();
                break;
        }
        return super.onKeyEvent(keyCode, keyState);
    }

    public void setFuncList(List<String> funcList) {
        if (mFuncAdapter != null) {
            mFuncAdapter.setFuncList(funcList);
            mFuncAdapter.notifyDataSetChanged();
        }
    }

    public void setFuncList(String[] funcList) {
        setFuncList(Arrays.asList(funcList));
    }

    public void setOptions(List<String> options) {
        if (mOptionAdapter != null) {
            mOptionAdapter.setOptions(options);
            mOptionAdapter.notifyDataSetChanged();
            vwKeyHint.setTurnable(mOptionAdapter.isTurnable());
        }
    }

    public void setOptions(String[] options) {
        setOptions(Arrays.asList(options));
    }

    protected void setSelection(int selection) {
        if (mOptionAdapter != null) {
            mOptionAdapter.setSelection(selection);
        }
    }

    protected void prePage() {
        if (mOptionAdapter != null) {
            mOptionAdapter.prePage();
        }
    }

    protected void nextPage() {
        if (mOptionAdapter != null) {
            mOptionAdapter.nextPage();
        }
    }
}
