package com.mili.smarthome.tkj.setting.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储卡管理
 */
public class SetMemoryManageFragment extends BaseFragment implements SetOperateView.IOperateListener {


    private SetOperateView mOperateView;
    private RecyclerView mRecyclerview;
    private SetClearDatasFragment clearFaceFragment;
    private SetCapacityFragment capacityFragment;
    private SetMediaInfoFragment mediaInfoFragment;
    private List<Integer> list = new ArrayList<>();
    private SdStateReceiver receiver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_memory_manage;
    }

    @Override
    protected void bindView() {
        mOperateView = findView(R.id.rootview);
        mRecyclerview = findView(R.id.recyclerview);
    }

    @Override
    public void onResume() {
        initRecyclerView();

        receiver = new SdStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd对象是存在并具有读/写权限
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但没有被安装
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING); //表明对象正在磁盘检查
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT); //物理的拔出 SDCARD
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED); //完全拔出
        intentFilter.addDataScheme("file"); // 必须要有此行，否则无法收到广播
        getContext().registerReceiver(receiver, intentFilter);

        super.onResume();
    }

    @Override
    public void onDestroyView() {
        getContext().unregisterReceiver(receiver);
        super.onDestroyView();
    }

    private void initRecyclerView() {
        list.clear();
        list.add(R.string.setting_local_memory);
        //是否存在sd卡
        if (ExternalMemoryUtils.externalMemoryAvailable()) {
            list.add(R.string.setting_external_memory);
        }
        list.add(R.string.setting_reset_memory);
        list.add(R.string.setting_media_info);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MemoryAdapter adapter = new MemoryAdapter(getContext());
        mRecyclerview.setAdapter(adapter);
        adapter.setTextYellow(false);
    }

    @Override
    protected void bindData() {
        mOperateView.setSuccessListener(this);
    }

    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    private class MemoryAdapter extends ItemSelectorAdapter {

        public MemoryAdapter(Context context) {
            super(context, list);
        }

        @Override
        protected int getStringArrayId() {
            return 0;
        }

        @Override
        protected void onItemClick(int position) {
            switch (list.get(position)) {
                case R.string.setting_local_memory:
                    if (capacityFragment == null) {
                        capacityFragment = new SetCapacityFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), capacityFragment, R.id.fl_container,
                            "SetCapacityFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_MEMORY_CAPACITY);
                    break;
                case R.string.setting_external_memory:
                    if (capacityFragment == null) {
                        capacityFragment = new SetCapacityFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), capacityFragment, R.id.fl_container,
                            "SetCapacityFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_EXTERNAL_MEMORY);
                    break;
                case R.string.setting_reset_memory:
                    setBackVisibility(View.GONE);
                    if (clearFaceFragment == null) {
                        clearFaceFragment = new SetClearDatasFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), clearFaceFragment, R.id.fl_container,
                            "SetClearFaceFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_MEMORY_FORMAT);
                    break;
                case R.string.setting_media_info:
                    if (mediaInfoFragment == null) {
                        mediaInfoFragment = new SetMediaInfoFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), mediaInfoFragment, R.id.fl_container,
                            "SetMediaInfoFragment");
                    break;
            }
        }
    }

    class SdStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_MEDIA_CHECKING:
//                    LogUtils.e(" SdStateReceiver："+Intent.ACTION_MEDIA_CHECKING);
                case Intent.ACTION_MEDIA_MOUNTED:
//                    LogUtils.e(" SdStateReceiver："+Intent.ACTION_MEDIA_MOUNTED);
                case Intent.ACTION_MEDIA_EJECT:
//                    LogUtils.e(" SdStateReceiver："+Intent.ACTION_MEDIA_EJECT);
                case Intent.ACTION_MEDIA_UNMOUNTED:
//                    LogUtils.e(" SdStateReceiver："+Intent.ACTION_MEDIA_UNMOUNTED);
                case Intent.ACTION_MEDIA_REMOVED:
                    initRecyclerView();
//                    LogUtils.e(" SdStateReceiver："+Intent.ACTION_MEDIA_REMOVED);
                    break;
            }
        }
    }

}
