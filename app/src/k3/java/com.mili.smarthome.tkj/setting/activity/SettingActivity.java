package com.mili.smarthome.tkj.setting.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K3BaseActivity;
import com.mili.smarthome.tkj.base.K3Const;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.entities.SettingFuncManager;
import com.mili.smarthome.tkj.setting.fragment.DevInfoFragment;
import com.mili.smarthome.tkj.setting.fragment.SetAdminPwdFragment;
import com.mili.smarthome.tkj.setting.fragment.SettingFragment;
import com.mili.smarthome.tkj.utils.FragmentUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.List;

public class SettingActivity extends K3BaseActivity {

    private SettingFragment fSetting;
    private SetAdminPwdFragment fAdminPwd;
    private DevInfoFragment fDevInfo;
    private Fragment fCurrent;

    private TabAdapter mTabAdapter;

    private SettingReceiver mReceiver = new SettingReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        RecyclerView rvLeft = findView(R.id.rv_tab);
        rvLeft.setLayoutManager(new LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL, false));

        fSetting = new SettingFragment();
        fragmentReplace(fSetting);

        mTabAdapter = new TabAdapter();
        rvLeft.setAdapter(mTabAdapter);

        mReceiver.register(mContext);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTabAdapter.setSelection(0);
            }
        }, 10);
    }

    @Override
    protected void onDestroy() {
        mReceiver.unregister(mContext);
        super.onDestroy();
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (freeTime > 30000) {
            finish();
        }
        return true;
    }

    private void fragmentReplace(Fragment fragment) {
        FragmentUtils.clear(this);
        if (fCurrent != fragment) {
            FragmentUtils.replace(this, R.id.fl_container, fragment);
            fCurrent = fragment;
        }
    }

    private void onSelectionChanged(SettingFunc func) {
        switch (func.getCode()) {
            case SettingFunc.SET_DEV_INFO:
                if (fDevInfo == null)
                    fDevInfo = new DevInfoFragment();
                fragmentReplace(fDevInfo);
                break;
            case SettingFunc.SET_PASSWORD:
                if (AppConfig.getInstance().getOpenPwdMode() == 1) {
                    if (fAdminPwd == null)
                        fAdminPwd = new SetAdminPwdFragment();
                    fragmentReplace(fAdminPwd);
                    return;
                }
            default:
                fragmentReplace(fSetting);
                fSetting.resetFunc(func);
                break;
        }
    }

    private class TabVH extends RecyclerView.ViewHolder {

        private TextView textView;

        private TabVH(View itemView) {
            super(itemView);
            textView = ViewUtils.findView(itemView, R.id.textview);
        }
    }

    private class TabAdapter extends RecyclerView.Adapter<TabVH> {

        private List<SettingFunc> mList;
        private int mSelection;

        private TabAdapter() {
            mList = SettingFuncManager.getFuncList();
        }

        public void setSelection(int selection) {
            if (mSelection != selection) {
                int unselect = mSelection;
                mSelection = selection;
                if (unselect >= 0 && unselect < getItemCount())
                    notifyItemChanged(unselect);
                if (mSelection >= 0 && mSelection < getItemCount())
                    notifyItemChanged(mSelection);
            }
            onSelectionChanged(mList.get(selection));
        }

        @NonNull
        @Override
        public TabVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_tab, parent, false);
            return new TabVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TabVH holder, int position) {
            final SettingFunc func = mList.get(position);

            holder.textView.setText(func.getName());
            TypedValue typedValue = new TypedValue();
            if (position == mSelection) {
                mContext.getTheme().resolveAttribute(R.attr.btn_bg_checked, typedValue, true);
            } else {
                mContext.getTheme().resolveAttribute(R.attr.btn_bg, typedValue, true);
            }
            holder.itemView.setBackgroundColor(typedValue.data);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelection(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class SettingReceiver extends BroadcastReceiver {

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction(K3Const.ACTION_PWD_MODE_CHANGED);
            intentFilter.addAction(K3Const.ACTION_FACE_RECOG_CHANGED);
            intentFilter.addAction(K3Const.ACTION_QR_CODE_CHANGED);
            context.registerReceiver(SettingReceiver.this, intentFilter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(SettingReceiver.this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.length() > 0) {
                switch (action) {
                    //case K3Const.ACTION_PWD_MODE_CHANGED:
                    case K3Const.ACTION_FACE_RECOG_CHANGED:
                    case K3Const.ACTION_QR_CODE_CHANGED:
                        fSetting.notifyMenuChanged();
                        break;
                }
            }
        }
    }
}
