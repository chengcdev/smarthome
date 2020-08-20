package com.mili.smarthome.tkj.main.activity.direct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.DirectResidentsDao;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.adapter.CallKeyBoard1Adapter;
import com.mili.smarthome.tkj.main.entity.KeyBoardMoel;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.resident.ResidentListManage;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 编辑住户名称
 */

public class DirectPressEditActivity extends BaseK7Activity implements CallKeyBoard1Adapter.ItemClickListener {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_room_no)
    TextView tvRoomNo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.root)
    SetSuccessView mSuccessView;
    @BindView(R.id.rv)
    KeyBoardRecyclerView rv;
    @BindView(R.id.rl_call)
    RelativeLayout rlCall;
    private GridLayoutManager gridLayoutManager;
    private List<KeyBoardMoel> numLists;
    private CallKeyBoard1Adapter callKeyBoard1Adapter;
    private long lastClickTime;
    private long lastClickTime2;
    private int maxCount = 0;
    private int count;
    private String currentLetter = "";
    private String showStr = "";
    private String str;
    //是否大写
    private boolean isCapital;
    private List<ResidentListEntity> mResidentList;
    private int mClickPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_edit);
        ButterKnife.bind(this);

        initView();
        initDatas();
        initRecycerView();
    }

    private void initDatas() {
        numLists = Constant.getDirecLetterLowerLists();
    }

    private void initView() {
        tvTitle.setText(getString(R.string.setting_household));

        Intent intent = getIntent();
        if (intent != null) {
            //获取列表点击的position
            mClickPosition = intent.getIntExtra(Constant.KEY_PARAM,0);
            mResidentList = ResidentListManage.getInstance().getResidentList();
            ResidentListEntity residentListEntity = mResidentList.get(mClickPosition);
            if (residentListEntity != null) {
                if (residentListEntity.getRoomNo().equals(DirectResidentsDao.ROOM_NO_MANAGE)) {
                    //管理中心
                    tvRoomNo.setText(R.string.manage_center);
                }else {
                    tvRoomNo.setText(residentListEntity.getRoomNo());
                }
                if (!residentListEntity.getRoomNo().equals(residentListEntity.getRoomName())) {
                    tvName.setText(residentListEntity.getRoomName());
                    if (residentListEntity.getRoomName().equals(getString(R.string.manage_center))) {
                        tvName.setText("");
                    }
                }
            }
        }
    }

    private void initRecycerView() {
        gridLayoutManager = new GridLayoutManager(this, 3);
        callKeyBoard1Adapter = new CallKeyBoard1Adapter(this, numLists);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(callKeyBoard1Adapter);
        callKeyBoard1Adapter.setKeyBoardListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callKeyBoard1Adapter.setOnItemClick();
    }

    @Override
    public void setItemDownClick(View view, int position) {
        if (rv.getChildAt(position) != null) {
            CallKeyBoard1Adapter.MyViewHolder childViewHolder = (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
            AppManage.getInstance().keyBoardDown(childViewHolder.itemView);
        }

    }

    @Override
    public void setItemUpClick(View view, int position) {
        if (rv.getChildAt(position) != null) {
            CallKeyBoard1Adapter.MyViewHolder childViewHolder = (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
            AppManage.getInstance().keyBoardUp(childViewHolder.itemView);
        }

        String kId = numLists.get(position).getkId();
        String str = tvName.getText().toString();
        switch (kId) {
            case Constant.KEY_DELETE:
                if (str.length() <= 1) {
                    str = "";
                }
                if (!str.equals("")) {
                    str = str.substring(0, (str.length() - 1));
                }
                tvName.setText(str);
                break;
            case Constant.KEY_CONFIRM:
                String roomName = tvName.getText().toString();
                String roomNo = tvRoomNo.getText().toString();
                if (roomNo.equals(getString(R.string.manage_center))) {
                    roomNo = DirectResidentsDao.ROOM_NO_MANAGE;
                }
                if (roomName.equals("")) {
                    ResidentListManage.getInstance().setResidentList(mClickPosition,new ResidentListEntity(roomNo, roomNo));
                } else {
                    ResidentListManage.getInstance().setResidentList(mClickPosition,new ResidentListEntity(roomNo, roomName));
                }
                //设置成功
                mSuccessView.showSuccessView(getString(R.string.setting_success), 2000, new ISetCallBackListener() {
                    @Override
                    public void success() {
                        AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_DIRECT_EDIT_VIEW);
                        finish();
                    }

                    @Override
                    public void fail() {

                    }
                });
                break;
            case Constant.KEY_CANCLE:
                //取消退出
                finish();
                break;
            case Constant.KEY_LOWER:
                isCapital = true;
                for (int i = 0; i < numLists.size(); i++) {
                    int resId = capitalIcon(numLists.get(i).getkId());
                    if (resId != 0) {
                        if (Constant.KEY_LOWER.equals(numLists.get(i).getkId())) {
                            numLists.set(i, new KeyBoardMoel(Constant.KEY_CAPITAL, Constant.KEY_CAPITAL, resId));
                        } else {
                            numLists.set(i, new KeyBoardMoel(numLists.get(i).getkId(), numLists.get(i).getkId(), resId));
                        }
                    }
                }
                initRecycerView();
                break;
            case Constant.KEY_CAPITAL:
                isCapital = false;
                for (int i = 0; i < numLists.size(); i++) {
                    int resId = lowerIcon(numLists.get(i).getkId());
                    if (resId != 0) {
                        if (Constant.KEY_CAPITAL.equals(numLists.get(i).getkId())) {
                            numLists.set(i, new KeyBoardMoel(Constant.KEY_LOWER, Constant.KEY_LOWER, resId));
                        } else {
                            numLists.set(i, new KeyBoardMoel(numLists.get(i).getkId(), numLists.get(i).getkId(), resId));
                        }
                    }
                }
                initRecycerView();
                break;
            default:
                showStr = str;
                showContent(kId);
                break;
        }
    }

    void showContent(String kId) {
        str = getShowStr(kId);
        if (!currentLetter.equals(kId) || kId.equals("1")) {
            showStr = showStr + str;
            tvName.setText(showStr);
            currentLetter = kId;
            lastClickTime2 = System.currentTimeMillis();
            lastClickTime = System.currentTimeMillis();
            return;
        }
        if (!isFastClick2()) {
            showStr = showStr + str;
            tvName.setText(showStr);
        } else {
            String substring = showStr.substring(0, (showStr.length() - 1));
            tvName.setText(substring + str);
            showStr = substring + str;
        }
    }

    private String getShowStr(String kId) {
        String letter = null;
        switch (kId) {
            case "1":
                maxCount = Constant.letter1.length;
                letter = Constant.letter1[getCount(kId)];
                break;
            case "2":
                maxCount = Constant.letter2.length;
                letter = Constant.letter2[getCount(kId)];
                break;
            case "3":
                maxCount = Constant.letter3.length;
                letter = Constant.letter3[getCount(kId)];
                break;
            case "4":
                maxCount = Constant.letter4.length;
                letter = Constant.letter4[getCount(kId)];
                break;
            case "5":
                maxCount = Constant.letter5.length;
                letter = Constant.letter5[getCount(kId)];
                break;
            case "6":
                maxCount = Constant.letter6.length;
                letter = Constant.letter6[getCount(kId)];
                break;
            case "7":
                maxCount = Constant.letter7.length;
                letter = Constant.letter7[getCount(kId)];
                break;
            case "8":
                maxCount = Constant.letter8.length;
                letter = Constant.letter8[getCount(kId)];
                break;
            case "9":
                maxCount = Constant.letter9.length;
                letter = Constant.letter9[getCount(kId)];
                break;
            case "0":
                maxCount = Constant.letter0.length;
                letter = Constant.letter0[getCount(kId)];
                break;
            case Constant.KEY_CHAR:
                maxCount = Constant.letterZ.length;
                letter = Constant.letterZ[getCount(kId)];
                break;
        }
        //大写
        if (isCapital) {
            assert letter != null;
            letter = letter.toUpperCase();
        }

        return letter;
    }


    private int getCount(String kId) {
        if (!currentLetter.equals(kId)) {
            count = 0;
        } else if (isFastClick()) {
            count++;
            if (count > maxCount - 1) {
                count = 0;
            }
        } else {
            count = 0;
        }
        return count;
    }


    private boolean isFastClick() {
        boolean isFast = false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 2000) {
            isFast = true;
        }
        lastClickTime = currentTime;
        return isFast;
    }

    private boolean isFastClick2() {
        boolean isFast = false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime2 < 2000) {
            isFast = true;
        }
        lastClickTime2 = currentTime;
        return isFast;
    }


    private int capitalIcon(String sId) {
        switch (sId) {
            case "1":
                return R.drawable.key_1;
            case "2":
                return R.drawable.letter_2;
            case "3":
                return R.drawable.letter_3;
            case "4":
                return R.drawable.letter_4;
            case "5":
                return R.drawable.letter_5;
            case "6":
                return R.drawable.letter_6;
            case "7":
                return R.drawable.letter_7;
            case "8":
                return R.drawable.letter_8;
            case "9":
                return R.drawable.letter_9;
            case "0":
                return R.drawable.letter_0;
            case Constant.KEY_LOWER:
                return R.drawable.key_capital;
        }
        return 0;
    }

    private int lowerIcon(String sId) {
        switch (sId) {
            case "1":
                return R.drawable.key_1;
            case "2":
                return R.drawable.letter1_2;
            case "3":
                return R.drawable.letter1_3;
            case "4":
                return R.drawable.letter1_4;
            case "5":
                return R.drawable.letter1_5;
            case "6":
                return R.drawable.letter1_6;
            case "7":
                return R.drawable.letter1_7;
            case "8":
                return R.drawable.letter1_8;
            case "9":
                return R.drawable.letter1_9;
            case "0":
                return R.drawable.letter_0;
            case Constant.KEY_CAPITAL:
                return R.drawable.key_lower;
        }
        return 0;
    }
}
