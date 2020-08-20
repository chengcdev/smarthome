package com.mili.smarthome.tkj.main.activity.direct;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.DirectResidentsDao;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.adapter.CallKeyBoard1Adapter;
import com.mili.smarthome.tkj.main.adapter.CallKeyBoard3Adapter;
import com.mili.smarthome.tkj.main.entity.KeyBoardMoel;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.set.resident.ResidentListManage;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 直按式 字母键盘界面
 */

public class DirectPressLetterActivity extends BaseK7Activity implements CallKeyBoard1Adapter.ItemClickListener, TextWatcher, KeyBoardItemView.IOnKeyClickListener {


    @BindView(R.id.rv_house_hold)
    KeyBoardRecyclerView rvHouseHold;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rl_call)
    RelativeLayout rlCall;
    @BindView(R.id.key_last)
    KeyBoardItemView keyLast;
    @BindView(R.id.key_next)
    KeyBoardItemView keyNext;
    @BindView(R.id.key_call)
    KeyBoardItemView keyCall;
    @BindView(R.id.lin_bottom)
    LinearLayout linBottom;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.img_title)
    ImageView imgTitle;
    private GridLayoutManager gridLayoutManager;
    private CallKeyBoard1Adapter callKeyBoard1Adapter;
    private long lastClickTime;
    private long lastClickTime2;
    private int maxCount = 0;
    private int count;
    private String currentLetter = "";
    private String showStr = "";
    private String str;
    private String tempStr = "";
    private LinearLayoutManager linearLayoutManager;
    private CallKeyBoard3Adapter callKeyBoard3Adapter;
    private int index = 0;
    private List<KeyBoardMoel> numLists;
    private List<ResidentListEntity> roomList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direc_letter);
        ButterKnife.bind(this);

        AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.top_edit_1_cn, R.drawable.top_edit_1_tw, R.drawable.top_edit_1_en);
        initRecycerView();
        tvContent.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callKeyBoard1Adapter.setOnItemClick();
    }

    private void initRecycerView() {
        numLists = Constant.getDirecLetterCapitalLists(false);
        gridLayoutManager = new GridLayoutManager(this, 3);
        callKeyBoard1Adapter = new CallKeyBoard1Adapter(this, numLists);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(callKeyBoard1Adapter);
        callKeyBoard1Adapter.setKeyBoardListener(this);
    }

    @Override
    public void setItemDownClick(View view, int position) {
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                AppManage.getInstance().keyBoardDown(keyLast);
                return;
            case Constant.KeyNumId.KEY_NUM_13:
                AppManage.getInstance().keyBoardDown(keyNext);
                return;
            case Constant.KeyNumId.KEY_NUM_14:
                keyCall.setImgBg(R.drawable.list_btn_1);
                AppManage.getInstance().playKeySound(keyCall);
                return;
        }

        if (rv.getChildAt(position) != null) {
            CallKeyBoard1Adapter.MyViewHolder childViewHolder =
                    (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
            AppManage.getInstance().keyBoardDown(childViewHolder.itemView);
        }
    }

    @Override
    public void setItemUpClick(View view, int position) {

        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                AppManage.getInstance().keyBoardUp(keyLast);
                if (index == 0 && roomList.size() == 0) {
                    tvContent.setText("");
                    return;
                }
                index--;
                //根据输入内容，筛选列表
                if (index < 0) {
                    index = roomList.size() - 1;
                }
                if (callKeyBoard3Adapter != null) {
                    callKeyBoard3Adapter.refreshList(index);
                    callKeyBoard3Adapter.notifyDataSetChanged();
                    rvHouseHold.scrollToPosition(index);
                }
                return;
            case Constant.KeyNumId.KEY_NUM_13:
                AppManage.getInstance().keyBoardUp(keyNext);
                if (index == 0 && roomList.size() == 0) {
                    tvContent.setText("");
                    return;
                }
                index++;
                if (index > roomList.size() - 1) {
                    index = 0;
                }
                if (callKeyBoard3Adapter != null) {
                    callKeyBoard3Adapter.refreshList(index);
                    callKeyBoard3Adapter.notifyDataSetChanged();
                    rvHouseHold.scrollToPosition(index);
                }
                return;
            case Constant.KeyNumId.KEY_NUM_14:
                keyCall.setImgBg(R.drawable.list_btn);
                if (roomList != null && roomList.size() > 0) {
                    //开始呼叫
                    if (DirectResidentsDao.ROOM_NO_MANAGE.equals(roomList.get(index).getRoomNo())) {
                        //管理中心
                        CallHelper.getInstance().callCenter(this, roomList.get(index).getRoomName());
                    } else {
                        //呼叫住户
                        CallHelper.getInstance().callResident(this, roomList.get(index).getRoomNo(), roomList.get(index).getRoomName());
                    }
                    finish();
                }
                return;
        }

        if (rv.getChildAt(position) != null) {
            CallKeyBoard1Adapter.MyViewHolder childViewHolder =
                    (CallKeyBoard1Adapter.MyViewHolder) rv.getChildViewHolder(rv.getChildAt(position));
            AppManage.getInstance().keyBoardUp(childViewHolder.itemView);
        }

        //隐藏提示图
        imgTitle.setVisibility(View.GONE);
        String kId = numLists.get(position).getkId();
        switch (kId) {
            case Constant.KEY_CANCLE:
                String str = tvContent.getText().toString();
                if (str.equals("")) {
                    finish();
                    return;
                } else if (str.length() <= 1) {
                    index = 0;
                    str = "";
                }
                if (!str.equals("")) {
                    str = str.substring(0, (str.length() - 1));
                }
                tvContent.setText(str);
                break;
            case Constant.KEY_LOCK:
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
                AppManage.getInstance().toActFinish(this, MainActivity.class);
                break;
            case Constant.KEY_CONFIRM:

                break;
            default:
                showContent(kId);
                break;
        }
    }

    void showContent(String kId) {
        str = getShowStr(kId);
        //点击不同按键，字母初始化
        if (!currentLetter.equals(kId) || kId.equals("1")) {
            showStr = showStr + str;
            tvContent.setText(showStr);
            currentLetter = kId;
            lastClickTime2 = System.currentTimeMillis();
            lastClickTime = System.currentTimeMillis();
            return;
        }

        if (!isFastClick2()) {
            showStr = showStr + str;
            tvContent.setText(showStr);
        } else {
            String substring = showStr.substring(0, (showStr.length() - 1));
            tvContent.setText(substring + str);
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
        Log.e(this.getClass().getName(), "count：" + count);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        showStr = s.toString();
        //根据输入内容，筛选列表
        List<ResidentListEntity> residentList = ResidentListManage.getInstance().getResidentList();
        roomList.clear();
        for (int i = 0; i < residentList.size(); i++) {
            String roomName = residentList.get(i).getRoomName();
            String roomNo = residentList.get(i).getRoomNo();
            if (!roomNo.equals("") && (roomName.toLowerCase()).contains(showStr)) {
                roomList.add(new ResidentListEntity(roomNo, roomName));
            }
        }
        initRoomList();
    }

    void initRoomList() {
        imgTitle.setVisibility(View.GONE);
        rlTop.setBackgroundResource(R.drawable.top_edit);
        linearLayoutManager = new LinearLayoutManager(this);
        callKeyBoard3Adapter = new CallKeyBoard3Adapter(this, roomList);
        rvHouseHold.setLayoutManager(linearLayoutManager);
        if (roomList.size() > 0) {
            index = 0;
            callKeyBoard3Adapter.refreshList(index);
        }
        rvHouseHold.setAdapter(callKeyBoard3Adapter);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
    }

    @Override
    public void OnViewUpClick(int code, View view) {
    }
}
