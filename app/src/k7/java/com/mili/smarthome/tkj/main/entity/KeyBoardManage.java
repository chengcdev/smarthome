package com.mili.smarthome.tkj.main.entity;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.set.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeyBoardManage {

    private static volatile KeyBoardManage keyBoardManage;

    public static KeyBoardManage getInstance() {
        if (keyBoardManage == null) {
            synchronized (KeyBoardManage.class) {
                if (keyBoardManage == null) {
                    keyBoardManage = new KeyBoardManage();
                }
            }
        }
        return keyBoardManage;
    }

    private String[] getRandomNum(int Nums) {
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


    /**
     * 主界面 随机数字键盘
     */
    public List<KeyBoardMoel> getRanNumLists() {
        String[] nums;
        nums = getRandomNum(10);

        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardMoel(Constant.KEY_UP, Constant.KEY_UP, R.drawable.key_last));
        list.add(new KeyBoardMoel(Constant.KEY_DELETE, Constant.KEY_DELETE, R.drawable.key_del));
        list.add(new KeyBoardMoel(Constant.KEY_NEXT, Constant.KEY_NEXT, R.drawable.key_next));
        list.add(new KeyBoardMoel(nums[0], nums[0], getResNums(nums[0])));
        list.add(new KeyBoardMoel(nums[1], nums[1], getResNums(nums[1])));
        list.add(new KeyBoardMoel(nums[2], nums[2], getResNums(nums[2])));
        list.add(new KeyBoardMoel(nums[3], nums[3], getResNums(nums[3])));
        list.add(new KeyBoardMoel(nums[4], nums[4], getResNums(nums[4])));
        list.add(new KeyBoardMoel(nums[5], nums[5], getResNums(nums[5])));
        list.add(new KeyBoardMoel(nums[6], nums[6], getResNums(nums[6])));
        list.add(new KeyBoardMoel(nums[7], nums[7], getResNums(nums[7])));
        list.add(new KeyBoardMoel(nums[8], nums[8], getResNums(nums[8])));
        list.add(new KeyBoardMoel(Constant.KEY_CANCLE, Constant.KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel(nums[9], nums[9], getResNums(nums[9])));
        list.add(new KeyBoardMoel(Constant.KEY_LOCK, Constant.KEY_LOCK, R.drawable.key_lock));
        return list;
    }


    /**
     * 主界面 直按式随机数字键盘
     */
    public List<KeyBoardMoel> getDirecRanNumLists() {
        String[] nums;

        //是否启用动态密码
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            nums = getRandomNum(10);
        } else {
            nums = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        }

        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardMoel(nums[0], nums[0], getResNums(nums[0])));
        list.add(new KeyBoardMoel(nums[1], nums[1], getResNums(nums[1])));
        list.add(new KeyBoardMoel(nums[2], nums[2], getResNums(nums[2])));
        list.add(new KeyBoardMoel(nums[3], nums[3], getResNums(nums[3])));
        list.add(new KeyBoardMoel(nums[4], nums[4], getResNums(nums[4])));
        list.add(new KeyBoardMoel(nums[5], nums[5], getResNums(nums[5])));
        list.add(new KeyBoardMoel(nums[6], nums[6], getResNums(nums[6])));
        list.add(new KeyBoardMoel(nums[7], nums[7], getResNums(nums[7])));
        list.add(new KeyBoardMoel(nums[8], nums[8], getResNums(nums[8])));
        list.add(new KeyBoardMoel(Constant.KEY_CANCLE, Constant.KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel(nums[9], nums[9], getResNums(nums[9])));
        list.add(new KeyBoardMoel(Constant.KEY_LOCK, Constant.KEY_LOCK, R.drawable.key_lock));
        list.add(new KeyBoardMoel(Constant.KEY_UP, Constant.KEY_UP, R.drawable.key_last));
        list.add(new KeyBoardMoel(Constant.KEY_NEXT, Constant.KEY_NEXT, R.drawable.key_next));
        list.add(new KeyBoardMoel(Constant.KEY_LIST, Constant.KEY_LIST, R.drawable.key_list));
        return list;
    }



    private int getResNums(String num) {
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
