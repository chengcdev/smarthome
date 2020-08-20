package com.mili.smarthome.tkj.main.activity.direct;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.DirectResidentsDao;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.adapter.CallKeyBoard2Adapter;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;
import com.mili.smarthome.tkj.main.face.activity.MegviiFaceRecogActivity;
import com.mili.smarthome.tkj.main.face.activity.WffrFaceRecogActivity;
import com.mili.smarthome.tkj.main.manage.CommonCallBackManage;
import com.mili.smarthome.tkj.main.manage.MessageManage;
import com.mili.smarthome.tkj.main.widget.DirecPressRecyclerView;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.set.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.set.resident.ResidentListManage;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 直按式主界面
 */

public class DirectPressMainActivity extends BaseK7Activity implements CallKeyBoard2Adapter.ItemClickListener {

    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.rv)
    DirecPressRecyclerView rv;
    @BindView(R.id.img_title)
    ImageView imgTitle;
    @BindView(R.id.key_last)
    KeyBoardItemView keyLast;
    @BindView(R.id.key_next)
    KeyBoardItemView keyNext;
    @BindView(R.id.key_lock)
    KeyBoardItemView keyLock;
    private LinearLayoutManager linearLayoutManager;
    private CallKeyBoard2Adapter callKeyBoard2Adapter;
    private int listScrollCount;
    private final int listItemNo = 4;
    public static boolean isEdit;
    private DirectMainReciver mReceiver;
    private List<ResidentListEntity> mResidentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direc_press);
        ButterKnife.bind(this);
        register();
        initClient();
        initResidentList();
        initData();
    }

    private void initResidentList() {
        isEdit = false;
        ResidentListManage.getInstance().addResidentList();
    }

    private void initData() {
        listScrollCount = 0;
        initRecycerView();
        if (isEdit) {
            keyLock.setImgBg(R.drawable.key_cancle);
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.set_zhuhu_cn, R.drawable.set_zhuhu_tw, R.drawable.set_zhuhu_en);
        } else {
            keyLock.setImgBg(R.drawable.key_lock);
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.top_main_1_cn, R.drawable.top_main_1_tw, R.drawable.top_main_1_en);
        }
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ActionId.ACTION_INIT_MAIN);
        intentFilter.addAction(Constant.ActionId.ACTION_DIRECT_EDIT_VIEW);
        intentFilter.addAction(Constant.ActionId.ACTION_ACTIVITY_CLOSE);
        intentFilter.addAction(Constant.ActionId.ACTION_SHOW_MESSAGE);
        intentFilter.addAction(Constant.ActionId.ACTION_INIT_MAIN_DIRECT);
        mReceiver = new DirectMainReciver();
        registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        callKeyBoard2Adapter.setOnItemClick();
        Constant.ScreenId.SCREEN_IS_SET = false;
        MessageManage.getInstance().initMessage();
        if (!isEdit) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void initClient() {
        CallHelper.getInstance().initCallBack(this);
        CommonCallBackManage.getInstance().initCallBack(this);
    }


    private void initRecycerView() {
        //获取住户列表
        mResidentList = ResidentListManage.getInstance().getResidentList();
        linearLayoutManager = new LinearLayoutManager(this);
        callKeyBoard2Adapter = new CallKeyBoard2Adapter(this, mResidentList);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(callKeyBoard2Adapter);
        callKeyBoard2Adapter.setKeyBoardListener(this);
    }


    private void toEditName(int listScrollCount) {
        ResidentListEntity residentListEntity = mResidentList.get(listScrollCount);
        //跳转到编辑住房名称界面
        if (isEdit && !residentListEntity.getRoomNo().equals("")) {
            AppManage.getInstance().toActExtra(this, DirectPressEditActivity.class,
                    Constant.KEY_PARAM, listScrollCount);
        }
        callKeyBoard2Adapter.ItemBgUp();
    }

    private void keyDownEditState(int clickPosition) {
        callKeyBoard2Adapter.ItemBgDown(rv, clickPosition);
    }

    /**
     * 拨号
     */
    private void toCall(int listScrollCount) {
        ResidentListEntity residentListEntity = mResidentList.get(listScrollCount);
        String roomNo = residentListEntity.getRoomNo();
        String roomName = residentListEntity.getRoomName();
        if (roomNo != null && !roomNo.equals("")) {
            if (roomNo.equals(DirectResidentsDao.ROOM_NO_MANAGE)) {
                CallHelper.getInstance().callCenter(this, roomName);
            } else {
                CallHelper.getInstance().callResident(this, roomNo, roomName);
            }
        }
        callKeyBoard2Adapter.imgUp();
    }

    private void keyDownCallState(int clickPosition) {
        ResidentListEntity residentListEntity = mResidentList.get(clickPosition);
        if (residentListEntity.getRoomNo() == null || residentListEntity.getRoomNo().equals("")) {
            return;
        }
        callKeyBoard2Adapter.imgDown(rv, clickPosition);
    }


    @Override
    public void setItemDownClick(View view, int position) {
        if (isEdit) {
            //编辑姓名
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_0:
                case Constant.KeyNumId.KEY_NUM_1:
                    keyDownEditState(0);
                    break;
                case Constant.KeyNumId.KEY_NUM_3:
                case Constant.KeyNumId.KEY_NUM_4:
                    keyDownEditState(1);
                    break;
                case Constant.KeyNumId.KEY_NUM_6:
                case Constant.KeyNumId.KEY_NUM_7:
                    keyDownEditState(2);
                    break;
                case Constant.KeyNumId.KEY_NUM_9:
                case Constant.KeyNumId.KEY_NUM_10:
                    keyDownEditState(3);
                    break;
            }
        } else {
            //拨号
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_2:
                    keyDownCallState(0);
                    break;
                case Constant.KeyNumId.KEY_NUM_5:
                    keyDownCallState(1);
                    break;
                case Constant.KeyNumId.KEY_NUM_8:
                    keyDownCallState(2);
                    break;
                case Constant.KeyNumId.KEY_NUM_11:
                    keyDownCallState(3);
                    break;
            }
        }


        //翻页
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                AppManage.getInstance().keyBoardDown(keyLast);
                break;
            case Constant.KeyNumId.KEY_NUM_13:
                AppManage.getInstance().keyBoardDown(keyNext);
                break;
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyLock);
                break;
        }
    }

    @Override
    public void setItemUpClick(View view, int position) {
        LogUtils.w(" DirectPressMainActivity setItemUpClick...position: " + position);
        if (isEdit) {
            //编辑姓名
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_0:
                case Constant.KeyNumId.KEY_NUM_1:
                    toEditName(listScrollCount);
                    break;
                case Constant.KeyNumId.KEY_NUM_3:
                case Constant.KeyNumId.KEY_NUM_4:
                    toEditName(listScrollCount + 1);
                    break;
                case Constant.KeyNumId.KEY_NUM_6:
                case Constant.KeyNumId.KEY_NUM_7:
                    toEditName(listScrollCount + 2);
                    break;
                case Constant.KeyNumId.KEY_NUM_9:
                case Constant.KeyNumId.KEY_NUM_10:
                    toEditName(listScrollCount + 3);
                    break;
            }
        } else {
            //拨号
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_2:
                    toCall(listScrollCount);
                    break;
                case Constant.KeyNumId.KEY_NUM_5:
                    toCall(listScrollCount + 1);
                    break;
                case Constant.KeyNumId.KEY_NUM_8:
                    toCall(listScrollCount + 2);
                    break;
                case Constant.KeyNumId.KEY_NUM_11:
                    toCall(listScrollCount + 3);
                    break;
            }
        }
        //翻页
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                //上翻
                AppManage.getInstance().keyBoardUp(keyLast);
                if (listScrollCount <= 0) {
                    listScrollCount = mResidentList.size() - listItemNo;
                    rv.moveToPosition(listScrollCount, true);
                } else {
                    listScrollCount -= listItemNo;
                    rv.moveToPosition(listScrollCount, false);
                }
                break;
            case Constant.KeyNumId.KEY_NUM_13:
                //下翻
                AppManage.getInstance().keyBoardUp(keyNext);
                if (listScrollCount >= mResidentList.size() - listItemNo) {
                    listScrollCount = -listItemNo;
                }
                listScrollCount += listItemNo;
                rv.moveToPosition(listScrollCount, false);
                break;
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyLock);
                if (isEdit) {
                    initRecycerView();
                    keyLock.setImgBg(R.drawable.key_lock);
                    isEdit = false;
                } else {
                    //是否启用人脸识别
                    if (AppConfig.getInstance().getFaceRecognition() == 1) {
                        switch (AppConfig.getInstance().getFaceManufacturer()) {
                            case 0:
                                AppManage.getInstance().toAct(WffrFaceRecogActivity.class);
                                break;
                            case 1:
                                AppManage.getInstance().toAct(MegviiFaceRecogActivity.class);
                                break;
                        }
                    } else {
                        //播放语音
                        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
                        AppManage.getInstance().toAct(this, MainActivity.class);
                    }
                }
                break;
        }
    }

    class DirectMainReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case Constant.ActionId.ACTION_INIT_MAIN:
                    isEdit = false;
                    initData();
                    break;
                case Constant.ActionId.ACTION_DIRECT_EDIT_VIEW:
                    isEdit = true;
                    initData();
                    break;
                case Constant.ActionId.ACTION_ACTIVITY_CLOSE:
                    finish();
                    break;
                case Constant.ActionId.ACTION_SHOW_MESSAGE:
                    if (!isEdit) {
                        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
                        AppManage.getInstance().replaceFragment(DirectPressMainActivity.this, messageDialogFragment);
                    }
                    break;
                case Constant.ActionId.ACTION_INIT_MAIN_DIRECT:
                    initRecycerView();
                    break;
            }
        }
    }
}
