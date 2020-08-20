package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 修改管理密码
 */
public class SetAdminPwdFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener {

    private NumInputView mPwd;
    private NumInputView mConfirmPwd;
    private SetOperateView operateView;
    private KeyBoardView keyBoardView;
    private String currentPwd = "";
    private String currentCorfirmPwd = "";
    private UserInfoDao userInfoDao;
    private LinearLayout mLlTitle;
    private String mFuncCode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_admin_pwd;
    }

    @Override
    protected void bindView() {
        mPwd = findView(R.id.tv_pwd);
        mConfirmPwd = findView(R.id.tv_pwd_again);
        operateView = findView(R.id.rootview);
        keyBoardView = findView(R.id.keyboardview);
        mLlTitle = findView(R.id.ll_title);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        operateView.setSuccessListener(this);
        setBackVisibility(View.VISIBLE);

        getIntentDatas();
        initData();
    }

    private void getIntentDatas() {
        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, "");
            if (!mFuncCode.equals("")) {
                mLlTitle.setVisibility(View.GONE);
            }else {
                mLlTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initData() {
        //初始化
        mPwd.clearText();
        mConfirmPwd.clearText();
        mPwd.requestFocus();
    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mPwd.getCursorIndex() == 0 && !mConfirmPwd.isFocused()) {
                    if (AppConfig.getInstance().getOpenPwdMode() == 1) {
                        AppUtils.getInstance().sendReceiver(Constant.Action.MAIN_REFRESH_ACTION);
                    }
                    requestBack();
                }else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                currentPwd = mPwd.getText().toString();
                currentCorfirmPwd = mConfirmPwd.getText().toString();

                if (currentPwd.equals("") || currentCorfirmPwd.equals("")) {
                    //输入错误
                    operateView.operateBackState(getString(R.string.set_input_error));
                }else {
                    if (currentPwd.equals(currentCorfirmPwd)) {
                        if (userInfoDao == null) {
                            userInfoDao = new UserInfoDao();
                        }
                        //保存密码
                        userInfoDao.setAdminPwd(currentCorfirmPwd);
                        //设置成功
                        operateView.operateBackState(getString(R.string.set_success));
                    } else {
                        //两次密码不一致
                        operateView.operateBackState(getString(R.string.set_pwd_error));
                    }
                }
                setBackVisibility(View.GONE);
                //初始化
                initData();
                break;
            default:
                String id = keyBoardBean.getkId();
                inputNum(Integer.valueOf(id));
                break;
        }
    }

    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
    }
}
