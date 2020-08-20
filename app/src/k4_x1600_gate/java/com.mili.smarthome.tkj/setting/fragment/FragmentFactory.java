package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;

import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

class FragmentFactory {

    static final String ARGS_FUNCCODE = "args_funccode";

    static K4BaseFragment create(String funcCode) {
        K4BaseFragment fragment = null;
        switch (funcCode) {
            case SettingFunc.CARD_ADD:
            case SettingFunc.CARD_DEL:
            case SettingFunc.CARD_CLEAR:
                fragment = new CardManageFragment();
                break;

            case SettingFunc.PASSWORD_ADD:
            case SettingFunc.PASSWORD_DEL:
            case SettingFunc.PASSWORD_CLEAR:
                fragment = new PwdManageFragment();
                break;
            case SettingFunc.ADMIN_PWD_CHANGE:
                fragment = new SetAdminPwdFragment();
                break;

            case SettingFunc.SET_LOCK_ATTR:
                fragment = new SetLockparamFragment();
                break;
            case SettingFunc.SET_DOOR_STATUS:
                fragment = new SetDoorStatusFragment();
                break;
            case SettingFunc.SET_FACE_RECOGNITION:
                fragment = new SetFaceFragment();
                break;
            case SettingFunc.SET_QRCODE_OPEN:
                fragment = new SetQrcodeFragment();
                break;
            case SettingFunc.SET_FINGER:
                fragment = new SetFingerFragment();
                break;
            case SettingFunc.SET_BODY_DETECTION:
                fragment = new SetBodyInductionFragment();
                break;

            case SettingFunc.SET_DEVNO:
                fragment = new SetDevNoFragment();
                break;
            case SettingFunc.SET_DEVNO_AREA:
                fragment = new SetAreaNoFragment();
                break;
            case SettingFunc.SET_NETWORK:
                fragment = new SetNetworkFragment();
                break;
            case SettingFunc.SET_NO_RULE:
                fragment = new SetDevRuleFragment();
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
            case SettingFunc.SET_PROMPT_TONE:
            case SettingFunc.SET_KEY_TONE:
            case SettingFunc.SET_MEDIA_MUTE:
                fragment = new SetSoundFragment();
                break;

            case SettingFunc.SET_MEMORY_CAPACITY:
            case SettingFunc.SET_MEMORY_CAPACITY_SD:
            case SettingFunc.SET_MEMORY_FORMAT:
                fragment = new SetMemoryFragment();
                break;
            case SettingFunc.SET_MEMORY_MEDIA:
                fragment = new SetMemoryMediaFragment();
                break;

            case SettingFunc.SET_FACTORY:
                fragment = new SetResetFragment();
                break;

            case SettingFunc.SET_OPEN_PWD_MODE:
                fragment = new SetPwdModeFragment();
                break;
            case SettingFunc.SET_ALARM_PARAM:
                fragment = new SetAlarmParamFragment();
                break;
            case SettingFunc.SET_PHOTO_VISITOR:
            case SettingFunc.SET_PHOTO_ERR_PWD:
            case SettingFunc.SET_PHOTO_HOLD_PWD:
            case SettingFunc.SET_PHOTO_CALL_CENTER:
            case SettingFunc.SET_PHOTO_FACE_OPEN:
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
            case SettingFunc.SET_PHOTO_CARD_OPEN:
            case SettingFunc.SET_PHOTO_PWD_OPEN:
            case SettingFunc.SET_PHOTO_QRCODE_OPEN:
                fragment = new SetPhotoParamFragment();
                break;
            case SettingFunc.SET_POWER_SAVING:
                fragment = new SetPowerModeFragment();
                break;
            case SettingFunc.SET_PWD_DYNAMIC:
                fragment = new SetPwdDynamicFragment();
                break;
            case SettingFunc.SET_SCREEN_SAVER:
                fragment = new SetScreenSaverFragment();
                break;
            case SettingFunc.SET_THEME:
                fragment = new SetThemeFragment();
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
