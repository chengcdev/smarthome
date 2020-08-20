package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;

import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

class FragmentFactory {

    static final String ARGS_FUNCCODE = "args_funccode";

    static K3BaseFragment create(String funcCode) {
        K3BaseFragment fragment = null;
        switch (funcCode) {
            case SettingFunc.CARD_ADD:
            case SettingFunc.CARD_DEL:
                fragment = new CardManageFragment();
                break;
            case SettingFunc.CARD_CLEAR:
            case SettingFunc.PASSWORD_CLEAR:
            case SettingFunc.SET_FACE_CLEAR:
            case SettingFunc.SET_FINGER_CLEAR:
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
            case SettingFunc.SET_FACE_SECU_LEVEL:
                fragment = new SetFaceSecuLevelFragment();
                break;
            case SettingFunc.SET_FACE_LIVENESS:
                fragment = new SetFaceLivenessFragment();
                break;
            case SettingFunc.SET_IPC_URL:
                fragment = new SetRtspFragment();
                break;
            case SettingFunc.SET_QRCODE_OPEN:
                fragment = new SetQrCodeOpenFragment();
                break;
            case SettingFunc.SET_FINGER:
                fragment = new SetFingerFragment();
                break;
            case SettingFunc.SET_FINGER_ADD:
                fragment = new SetFingerAddFragment();
                break;
            case SettingFunc.SET_FINGER_DEL:
                fragment = new SetFingerDelFragment();
                break;
            case SettingFunc.SET_BODY_DETECTION:
                fragment = new SetBodyDetectionFragment();
                break;
            case SettingFunc.SET_STAIR_NO:
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
            case SettingFunc.SET_MEMORY_MANAGE:
                fragment = new SetMemoryManageFragment();
                break;
            case SettingFunc.SET_MEMORY_CAPACITY:
            case SettingFunc.SET_MEMORY_EXT_CAPACITY:
                fragment = new SetMemoryCapacityFragment();
                break;
            case SettingFunc.SET_MEMORY_MEDIA:
                fragment = new SetMemoryMediaFragment();
                break;
            case SettingFunc.SET_OPEN_PWD_MODE:
                fragment = new SetPwdModeFragment();
                break;
            case SettingFunc.SET_ALARM_PARAM:
                fragment = new SetAlarmParamFragment();
                break;
            case SettingFunc.SET_PHOTO_FUNC:
                fragment = new SetPhotoFuncFragment();
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
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                fragment = new SetPhotoParamFragment();
                break;
            case SettingFunc.SET_POWER_SAVING:
                fragment = new SetPowerSavingFragment();
                break;
            case SettingFunc.SET_SCREEN_SAVER:
                fragment = new SetScreenSaverFragment();
                break;
            case SettingFunc.SET_SENSITIVITY:
                fragment = new SetSensitivityFragment();
                break;
            case SettingFunc.SET_PROMPT_TONE:
            case SettingFunc.SET_KEY_TONE:
            case SettingFunc.SET_MEDIA_MUTE:
                fragment = new SetSoundFragment();
                break;
            case SettingFunc.SET_THEME:
                fragment = new SetThemeFragment();
                break;
            case SettingFunc.SET_EVENT_PLATFORM:
                fragment = new SetEventPlatformFragment();
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
