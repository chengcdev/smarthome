package com.mili.smarthome.tkj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.entity.KeyBoardBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeyBoardView extends RecyclerView {

    private Context mContext;
    private KeyBoardAdapter keyBoardAdapter;
    public static int KEY_BOARD_PWD = 0;
    public static int KEY_BOARD_CALL = 1;
    public static int KEY_BOARD_SET = 2;
    private boolean isRandom = false;
    private int keyboardType;

    public KeyBoardView(Context context) {
        this(context,null);
    }

    public KeyBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public KeyBoardView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void init(int keyboardType) {
        this.keyboardType = keyboardType;
        keyBoardAdapter = new KeyBoardAdapter(mContext,getKeyList(keyboardType));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
        setLayoutManager(gridLayoutManager);
        setAdapter(keyBoardAdapter);
    }

    public void init(boolean isRandom){
        this.isRandom = isRandom;
        init(keyboardType);
    }

    public void setRandomKeyBoard(boolean isRandom) {
        this.isRandom = isRandom;
    }

   public void setKeyBoardListener(KeyBoardAdapter.IKeyBoardListener keyBoardListener){
       if (keyBoardAdapter != null) {
           keyBoardAdapter.setOnKeyBoardListener(keyBoardListener);
       }
   }

    private List<KeyBoardBean> getKeyList(int keyboardType){
        String[] nums;
        //是否启用动态密码
        if (isRandom) {
            nums = getRandomNum(10);
        } else {
            nums = new String[]{ "0","1", "2", "3", "4", "5", "6", "7", "8", "9"};
        }
        List<KeyBoardBean> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardBean(nums[1], nums[1], getResNums(nums[1])));
        list.add(new KeyBoardBean(nums[2], nums[2], getResNums(nums[2])));
        list.add(new KeyBoardBean(nums[3], nums[3], getResNums(nums[3])));
        list.add(new KeyBoardBean(nums[4], nums[4], getResNums(nums[4])));
        list.add(new KeyBoardBean(nums[5], nums[5], getResNums(nums[5])));
        list.add(new KeyBoardBean(nums[6], nums[6], getResNums(nums[6])));
        list.add(new KeyBoardBean(nums[7], nums[7], getResNums(nums[7])));
        list.add(new KeyBoardBean(nums[8], nums[8], getResNums(nums[8])));
        list.add(new KeyBoardBean(nums[9], nums[9], getResNums(nums[9])));
        list.add(new KeyBoardBean(Const.KeyBoardId.KEY_CANCEL,Const.KeyBoardId.KEY_CANCEL, R.drawable.key_cancel));
        list.add(new KeyBoardBean(nums[0], nums[0], getResNums(nums[0])));
        if (KEY_BOARD_SET == keyboardType) {
            list.add(new KeyBoardBean(Const.KeyBoardId.KEY_CONFIRM,Const.KeyBoardId.KEY_CONFIRM, R.drawable.key_ok));
        }else if (KEY_BOARD_CALL == keyboardType) {
            list.add(new KeyBoardBean(Const.KeyBoardId.KEY_CALL,Const.KeyBoardId.KEY_CALL, R.drawable.key_answer));
        } else {
            list.add(new KeyBoardBean(Const.KeyBoardId.KEY_LOCK,Const.KeyBoardId.KEY_LOCK, R.drawable.key_lock));
        }
        return list;
    }


    public String[] getRandomNum(int Nums) {
        List<Integer> list = new ArrayList<>();
        String[] results = new String[Nums];
        Random random = new Random();
        for (int i = 0; i < Nums; i++) {
            list.add(i);
        }
        for (int i = 0; i < Nums; i++) {
            int anInt = random.nextInt(Nums - i);
            results[i] = String.valueOf(list.get(anInt));
            list.remove(anInt);
        }
        return results;
    }

    public int getResNums(String num) {
        switch (num) {
            case "0":
                return R.drawable.key_0;
            case "1":
                return R.drawable.key_1;
            case "2":
                return R.drawable.key_2;
            case "3":
                return R.drawable.key_3;
            case "4":
                return R.drawable.key_4;
            case "5":
                return R.drawable.key_5;
            case "6":
                return R.drawable.key_6;
            case "7":
                return R.drawable.key_7;
            case "8":
                return R.drawable.key_8;
            case "9":
                return R.drawable.key_9;
        }
        return R.drawable.key_0;
    }


}
