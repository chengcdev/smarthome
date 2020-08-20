package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.adapter.SetMenuAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.view.KeyHintView;
import com.mili.smarthome.tkj.utils.FragmentUtils;

import java.util.ArrayList;

/**
 * 拍照参数
 * <p>{@link SettingFunc.SET_PHOTO_FUNC}: 拍照参数
 */
public class SetPhotoFuncFragment extends BaseSetFragment implements SetMenuAdapter.OnFuncClickListener {

    private KeyHintView vwKeyHint;
    private SetMenuAdapter menuAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_photo;
    }

    @Override
    protected void bindView() {
        vwKeyHint = findView(R.id.key_hint_view);
        //
        menuAdapter = new SetMenuAdapter(mContext);
        menuAdapter.setOnItemClickListener(this);
        RecyclerView rvChild = findView(R.id.rv_child);
        rvChild.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvChild.setAdapter(menuAdapter);
    }

    @Override
    protected void bindData() {
        super.bindData();
        ArrayList<SettingFunc> funcList = new ArrayList<>();
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_VISITOR));
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_ERR_PWD));
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_HOLD_PWD));
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_CALL_CENTER));
        if (AppConfig.getInstance().isFaceEnabled()) {
            funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_FACE_OPEN));
        }
        if (AppConfig.getInstance().isFingerEnabled()) {
            funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_FINGER_OPEN));
        }
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_CARD_OPEN));
        funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_PWD_OPEN));
        if (AppConfig.getInstance().isQrCodeEnabled()) {
            funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_QRCODE_OPEN));
        }
        if (AppConfig.getInstance().isFaceEnabled()) {
            funcList.add(new SettingFunc().setCode(SettingFunc.SET_PHOTO_FACE_STRANGER));
        }
        menuAdapter.setDataSet(funcList);
        vwKeyHint.setTurnable(menuAdapter.isTurnable());
    }


    @Override
    public void onFuncClick(SettingFunc func) {
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
}
