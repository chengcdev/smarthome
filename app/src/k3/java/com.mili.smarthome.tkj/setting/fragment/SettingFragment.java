package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.adapter.SetMenuAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.view.KeyHintView;
import com.mili.smarthome.tkj.utils.FragmentUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends BaseSetFragment implements SetMenuAdapter.OnFuncClickListener {

    private KeyHintView vwKeyHint;
    private CaptionAdapter captionAdapter = new CaptionAdapter();
    private SetMenuAdapter menuAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void bindView() {
        vwKeyHint = findView(R.id.key_hint_view);
        //
        RecyclerView rvParent = findView(R.id.rv_parent);
        rvParent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvParent.setAdapter(captionAdapter);
        //
        menuAdapter = new SetMenuAdapter(mContext);
        menuAdapter.setOnItemClickListener(this);
        RecyclerView rvChild = findView(R.id.rv_child);
        rvChild.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvChild.setAdapter(menuAdapter);
    }

    @Override
    public void onBackPressed() {
        SettingFunc func = captionAdapter.take();
        if (func == null) {
            super.onBackPressed();
        } else {
            setMenuList(func.getChildren());
        }
    }

    public void resetFunc(SettingFunc func) {
        captionAdapter.resetDataSet(func);
        setMenuList(func.getChildren());
    }

    public void notifyMenuChanged() {
        SettingFunc func = captionAdapter.peek();
        setMenuList(func.getChildren());
    }

    @Override
    public void onFuncClick(SettingFunc func) {
        if (SettingFunc.SET_APN.equals(func.getCode())) {
            startActivity(new Intent(android.provider.Settings.ACTION_APN_SETTINGS));
            return;
        }
        if (func.hasChild()) {
            captionAdapter.add(func);
            setMenuList(func.getChildren());
        } else {
            gotoFuncFragment(func);
        }
    }

    private void setMenuList(List<SettingFunc> funcList) {
        menuAdapter.setDataSet(funcList);
        vwKeyHint.setTurnable(menuAdapter.isTurnable());
    }

    private void gotoFuncFragment(SettingFunc func) {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = FragmentFactory.create(func.getCode());
        if (fm == null || fragment == null) {
            return;
        }
        FragmentUtils.replace(fm, R.id.fl_container, fragment, true);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == IKeyEventListener.KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_BACK:
                onBackPressed();
                break;
            case IKeyEventListener.KEYCODE_UP:
                menuAdapter.prePage();
                break;
            case IKeyEventListener.KEYCODE_DOWN:
                menuAdapter.nextPage();
                break;
        }
        return true;
    }

    // =============================================================== //
    // =============================================================== //

    private class CaptionVH extends RecyclerView.ViewHolder {

        private ImageView ivArrow;
        private TextView tvName;

        private CaptionVH(View itemView) {
            super(itemView);
            ivArrow = ViewUtils.findView(itemView, R.id.iv_arrow);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }

    private class CaptionAdapter extends RecyclerView.Adapter<CaptionVH> {

        private List<SettingFunc> mList = new ArrayList<>();

        public void resetDataSet(SettingFunc node) {
            mList.clear();
            mList.add(node);
            notifyDataSetChanged();
        }

        public void add(SettingFunc node) {
            mList.add(node);
            notifyItemInserted(mList.size() - 1);
        }

        public SettingFunc take() {
            int position = mList.size() - 1;
            if (position > 0) {
                mList.remove(position);
                notifyItemRemoved(position);
                return mList.get(position - 1);
            } else {
                return null;
            }
        }

        public SettingFunc peek() {
            if (mList.size() > 0) {
                return mList.get(mList.size() - 1);
            }
            return null;
        }

        @NonNull
        @Override
        public CaptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_caption, parent, false);
            return new CaptionVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CaptionVH holder, int position) {
            final SettingFunc func = mList.get(position);
            holder.ivArrow.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.tvName.setText(func.getName());
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }
}
