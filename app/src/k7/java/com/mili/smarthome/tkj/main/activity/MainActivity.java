package com.mili.smarthome.tkj.main.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.adapter.CallKeyBoard1Adapter;
import com.mili.smarthome.tkj.main.entity.InputBean;
import com.mili.smarthome.tkj.main.entity.KeyBoardManage;
import com.mili.smarthome.tkj.main.entity.KeyBoardMoel;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.interf.IKeyBoardRefreshListener;
import com.mili.smarthome.tkj.main.manage.CommonCallBackManage;
import com.mili.smarthome.tkj.main.manage.KeyBoardEventManage;
import com.mili.smarthome.tkj.main.qrcode.CaptureActivity;
import com.mili.smarthome.tkj.main.widget.DirecPressRecyclerView;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.set.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.set.fragment.SetChoiceLanguageFragment;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.List;

/**
 * 编码式主界面
 */

public class MainActivity extends BaseK7Activity implements CallKeyBoard1Adapter.ItemClickListener, IKeyBoardRefreshListener {


    DirecPressRecyclerView rv;
    KeyBoardItemView keyCall;
    FrameLayout flContainer;
    private List<KeyBoardMoel> numLists;
    private String TAG = "LauncherActivity";
    private KeyBoardReciver reciver;
    private CallKeyBoard1Adapter callKeyBoard1Adapter;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        //初始化数据
        initDatas();
        register();
        initView();
        initRecycerView();
        initListener();
        //头部界面显示
        toTitleFragment();
//        Log.e(TAG,"当前时间："+ DateTimeUtils.getMillis());

    }

    private void initListener() {
        KeyBoardEventManage.getInstance().setRefreshListener(this);
        CallHelper.getInstance().initCallBack(this);
        CommonCallBackManage.getInstance().initCallBack(this);
    }


    private void initDatas() {
        numLists = Constant.getNumLists();
    }

    private void register() {
        reciver = new KeyBoardReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_KEY_CALL_ICON);
        intentFilter.addAction(Constant.ActionId.ACTION_REFRESH_MAIN);
        intentFilter.addAction(Constant.ActionId.ACTION_MAIN_QR);
        intentFilter.addAction(Constant.ActionId.ACTION_REFRESH_MAIN_KEYBOARD);
        intentFilter.addAction(Const.ActionId.KEY_DOWN_UPDATETOUCH);
        intentFilter.addAction(Constant.ActionId.ACTION_INIT_MAIN);
        intentFilter.addAction(Constant.ActionId.ACTION_SHOW_MESSAGE);
        intentFilter.addAction(Constant.ActionId.ACTION_AREA_TO_OPEN_PWD);
        intentFilter.addAction(Constant.ActionId.ACTION_AREA_TO_ROOM_NO);
        registerReceiver(reciver, intentFilter);
    }

    private void initView() {
        rv = (DirecPressRecyclerView) findViewById(R.id.rv);
        keyCall = (KeyBoardItemView) findViewById(R.id.key_call);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        //显示呼叫中心的按键
        keyCall.setVisibility(View.VISIBLE);
    }

    private void toTitleFragment() {
//        //是否第一次安装
        if (AppPreferences.isReset()) {
            SetChoiceLanguageFragment languageFragment = new SetChoiceLanguageFragment();
            AppManage.getInstance().replaceFragment(this, languageFragment);
            //隐藏呼叫中心的按键
            keyCall.setVisibility(View.GONE);
            //更新打勾图标
            updateIcon(Constant.KEY_LOCK, Constant.KEY_CONFIRM, R.drawable.key_ok);
        } else {
            //直按式
            if (AppConfig.getInstance().getCallType() == 1) {
                numLists = KeyBoardManage.getInstance().getDirecRanNumLists();
                keyCall.setVisibility(View.GONE);
                initRecycerView();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_PARAM, new InputBean(KeyBoardEventManage.INPUT_TYPE_OPEN_PWD_DIRECT));
                MainFragment mainFragment = new MainFragment();
                AppManage.getInstance().replaceFragment(this, mainFragment, bundle);
            } else {
                //编码式
                MainFragment mainFragment = new MainFragment();
                AppManage.getInstance().replaceFragment(this, mainFragment);
                //更新锁键图标
                updateIcon(Constant.KEY_CONFIRM, Constant.KEY_LOCK, R.drawable.key_lock);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        callKeyBoard1Adapter.setOnItemClick();
    }


    private void initRecycerView() {
        gridLayoutManager = new GridLayoutManager(this, 3);
        callKeyBoard1Adapter = new CallKeyBoard1Adapter(this, numLists);
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(null);
        rv.setAdapter(callKeyBoard1Adapter);
        callKeyBoard1Adapter.setKeyBoardListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void setItemDownClick(View view, int position) {
        if (keyCall.getVisibility() == View.VISIBLE && position < 3) {
            AppManage.getInstance().keyBoardDown(keyCall);
        } else {
            if (rv.getChildAt(position) != null) {
                CallKeyBoard1Adapter.MyViewHolder childViewHolder = (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
                AppManage.getInstance().keyBoardDown(childViewHolder.itemView);
            }
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void setItemUpClick(View view, int position) {
        LogUtils.w(" MainActivity setItemUpClick...");
        if (rv.getChildAt(position) != null) {
            CallKeyBoard1Adapter.MyViewHolder childViewHolder = (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
            AppManage.getInstance().keyBoardUp(childViewHolder.itemView);
        }


        Fragment fragment = AppManage.getInstance().frgCurrent;
        if (fragment instanceof MessageDialogFragment) {
            if (Constant.KEY_QR.equals(numLists.get(position).getkId())) {
                AppManage.getInstance().toAct(CaptureActivity.class);
            }else {
                MainFragment mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                InputBean inputBean = new InputBean(KeyBoardEventManage.INPUT_TYPE_ROOM_NO_SHOW);
                inputBean.setInputKey(numLists.get(position).getkId());
                bundle.putSerializable(Constant.KEY_PARAM,inputBean);
                AppManage.getInstance().replaceFragment(this, mainFragment,bundle);
            }
        } else {
            if (keyCall.getVisibility() == 0 && position < 3) {
                AppManage.getInstance().keyBoardUp(keyCall);
                KeyBoardEventManage.getInstance().setKeyBoard(Constant.VIEW_ID_KEY_CALL, "");
            } else {
                KeyBoardEventManage.getInstance().setKeyBoard(Constant.VIEW_ID_KEY_BOARD, numLists.get(position).getkId());
            }
        }

    }


    @Override
    protected void onDestroy() {
        if (reciver != null) {
            unregisterReceiver(reciver);
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh(int inputType) {
        switch (inputType) {
            case KeyBoardEventManage.INPUT_TYPE_MAIN:
                showMainInput();
                break;
            case KeyBoardEventManage.INPUT_TYPE_ROOM_NO:
                showInputRoomNo();
                break;
            case KeyBoardEventManage.INPUT_TYPE_OPEN_PWD:
                showInputPwd(false);
                break;
            case KeyBoardEventManage.INPUT_TYPE_ADMIN_PWD:
                showInputAdminPwd();
                break;
            case KeyBoardEventManage.INPUT_TYPE_SET:
                showSetInput();
                break;
            case KeyBoardEventManage.INPUT_TYPE_OPEN_PWD_DIRECT:
                showInputPwd(true);
                break;
        }
    }


    class KeyBoardReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ActionId.ACTION_REFRESH_MAIN_KEYBOARD.equals(action)) {
                initRecycerView();
            } else if (Const.ActionId.KEY_DOWN_UPDATETOUCH.equals(action)) {
                Log.e(TAG, "interceptPowerKeyDownUpdateTouch");
                //关屏后，触摸重新启动app,亮屏
                AppManage.getInstance().closeScreenAct();
            } else if (Constant.ActionId.ACTION_INIT_MAIN.equals(action)) {
                initDatas();
                initRecycerView();
                toTitleFragment();
            } else if (Constant.ActionId.ACTION_SHOW_MESSAGE.equals(action)) {
                //显示信息
                MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
                AppManage.getInstance().replaceFragment(MainActivity.this, messageDialogFragment);
            } else if (Constant.ActionId.ACTION_AREA_TO_OPEN_PWD.equals(action)) {
                //回退到主界面fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_PARAM, new InputBean(KeyBoardEventManage.INPUT_TYPE_OPEN_PWD));
                MainFragment mainFragment = new MainFragment();
                AppManage.getInstance().replaceFragment(MainActivity.this, mainFragment, bundle);
            } else if (Constant.ActionId.ACTION_AREA_TO_ROOM_NO.equals(action)) {
                //回退到主界面fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_PARAM, new InputBean(KeyBoardEventManage.INPUT_TYPE_ROOM_NO));
                MainFragment mainFragment = new MainFragment();
                AppManage.getInstance().replaceFragment(MainActivity.this, mainFragment, bundle);
            }
        }
    }

    public void updateIcon(String oldId, String newId, int resId) {
        for (int i = 0; i < numLists.size(); i++) {
            if (oldId.equals(numLists.get(i).getkId())) {
                LogUtils.w(TAG + " updateIcon ");
                numLists.set(i, new KeyBoardMoel(newId, newId, resId));
//                initRecycerView();
                callKeyBoard1Adapter.notifyItemChanged(i);
            }
        }
    }

    /**
     * 输入房号
     */
    public void showInputRoomNo() {
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            initDatas();
            initRecycerView();
        }
        updateIcon(Constant.KEY_QR, Constant.KEY_CANCLE, R.drawable.key_cancle);
    }

    /**
     * 输入密码
     */
    public void showInputPwd(boolean isDirect) {
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            //随机键盘
            if (isDirect) {
                numLists = KeyBoardManage.getInstance().getDirecRanNumLists();
            } else {
                numLists = KeyBoardManage.getInstance().getRanNumLists();
            }
            initRecycerView();
        }
        updateIcon(Constant.KEY_QR, Constant.KEY_CANCLE, R.drawable.key_cancle);
        if (isDirect) {
            keyCall.setVisibility(View.GONE);
        } else {
            keyCall.setVisibility(View.VISIBLE);
            keyCall.setImgBg(R.drawable.key_ok);
        }
    }

    /**
     * 输入管理密码
     */
    public void showInputAdminPwd() {
        if (AppConfig.getInstance().getPwdDynamic() == 1 || AppConfig.getInstance().getCallType() == 1) {
            //随机键盘
            initDatas();
            initRecycerView();
        }
        updateIcon(Constant.KEY_QR, Constant.KEY_CANCLE, R.drawable.key_cancle);
        keyCall.setVisibility(View.VISIBLE);
        keyCall.setImgBg(R.drawable.key_ok);
    }


    /**
     * 主界面显示
     */
    public void showMainInput() {
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            initDatas();
            initRecycerView();
        }
        //是否显示二维码按键图标
        if (AppConfig.getInstance().getQrScanEnabled() == 1 || !AppConfig.getInstance().getBluetoothDevId().equals("")) {
            updateIcon(Constant.KEY_CANCLE, Constant.KEY_QR, R.drawable.key_qr);
        }
        updateIcon(Constant.KEY_CONFIRM, Constant.KEY_LOCK, R.drawable.key_lock);
        keyCall.setVisibility(View.VISIBLE);
        keyCall.setImgBg(R.drawable.key_center);
    }

    /**
     * 设置
     */
    public void showSetInput() {
        keyCall.setVisibility(View.GONE);
        updateIcon(Constant.KEY_LOCK, Constant.KEY_CONFIRM, R.drawable.key_ok);
    }


}
