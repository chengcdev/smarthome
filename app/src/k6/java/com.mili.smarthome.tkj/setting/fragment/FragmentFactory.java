package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mili.smarthome.tkj.setting.entities.SettingFunc;

public class FragmentFactory {

    public static final String ARGS_FUNCCODE = "args_funccode";

    static Fragment create(String funcCode) {
        Fragment fragment = null;
        switch (funcCode) {
            case SettingFunc.CARD_ADD:
            case SettingFunc.CARD_DEL:
                fragment = new CardManageFragment();
                break;
            case SettingFunc.CARD_CLEAR:
            case SettingFunc.PASSWORD_CLEAR:
            case SettingFunc.SET_FACTORY:
            case SettingFunc.SET_MEMORY_FORMAT:
                fragment = new SetConfirmFragment();
                break;
            case SettingFunc.PASSWORD_ADD:
            case SettingFunc.PASSWORD_DEL:
                fragment = new PwdManageFragment();
                break;
            case SettingFunc.ADMIN_PWD_CHANGE:
                fragment = new SetAdminPwdFragment();
                break;
            case SettingFunc.SET_LOCK_ATTR:
                fragment = new SetLockAttrFragment();
                break;
            case SettingFunc.SET_DOOR_STATUS:
                fragment = new SetDoorStatusFragment();
                break;
            case SettingFunc.SET_FACE_RECOGNITION:
                fragment = new SetFaceFragment();
                break;
            case SettingFunc.SET_OPEN_BY_BLUETOOTH:
                fragment = new SetOpenByBluetoothFragment();
                break;
            case SettingFunc.SET_DEVNO:
            case SettingFunc.SET_AREA_NO:
                fragment = new SetDevNoFragment();
                break;
            case SettingFunc.SET_NETWORK:
                fragment = new SetNetworkFragment();
                break;
            case SettingFunc.SET_NO_RULE:
                fragment = new SetRuleNoFragment();
                break;
            case SettingFunc.SET_ROOM:
                fragment = new SetRoomFragment();
                break;
            case SettingFunc.SET_TIME:
                fragment = new SetTimeFragment();
                break;
            case SettingFunc.SET_CALL_VOLUME:
                fragment = new SetCallVolumeFragment();
                break;
            case SettingFunc.SET_MEMORY_CAPACITY:
                break;
            case SettingFunc.SET_MEMORY_MEDIA:
                break;
            case SettingFunc.SET_QR_OPEN_TYPE:
                fragment = new SetQrOpenTypeFragment();
                break;
            case SettingFunc.SET_FINGERPRINT:
                fragment = new SetFingerprintFragment();
                break;
            case SettingFunc.SET_MEMORY_MANAGE:
                fragment = new SetMemoryManageFragment();
                break;
            case SettingFunc.SET_BODY_DETECTION:
            case SettingFunc.SET_OPEN_BY_SCAN:
            case SettingFunc.SET_PROMPT_TONE:
            case SettingFunc.SET_KEY_TONE:
            case SettingFunc.SET_MEDIA_MUTE:
            case SettingFunc.SET_OPEN_PWD_MODE:
            case SettingFunc.SET_ALARM_PARAM:
            case SettingFunc.SET_PHOTO_VISITOR:
            case SettingFunc.SET_PHOTO_ERR_PWD:
            case SettingFunc.SET_PHOTO_CALL_CENTER:
            case SettingFunc.SET_PHOTO_FACE_OPEN:
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
            case SettingFunc.SET_PHOTO_CARD_OPEN:
            case SettingFunc.SET_PHOTO_PWD_OPEN:
            case SettingFunc.SET_QR_CODE_OPEN:
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
            case SettingFunc.SET_PHOTO_HOLD_PWD:
            case SettingFunc.SET_POWER_SAVING:
            case SettingFunc.SET_SCREEN_SAVER:
            case SettingFunc.SET_PWD_DYNAMIC:
            case SettingFunc.SET_EVENT_PLATFORM:
                fragment = new ItemSelectorFragment();
                break;
            case SettingFunc.SET_DEV_INFO:
                break;
        }
        if (fragment != null) {
            Bundle args = new Bundle();
            args.putString(ARGS_FUNCCODE, funcCode);
            fragment.setArguments(args);
        }
        return fragment;
    }
}
