package com.mili.smarthome.tkj.set.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class VerticalScroll extends RelativeLayout {

    private TextView textView;
    private Handler handler = new Handler();
    private ScrollRun scrollRun;
    private List<String> showText = new ArrayList<>();
    private Context mContext;
    private int directType = 0;
    public static final int VERTICAL_TYPE = 0;
    public static final int HORIZON_TYPE = 1;
    //竖向每页最多显示字符串长度
    private final int VERTICAL_MAX_LENTH = 64;
    //横向每页最多显示字符串长度
    private final int HORIZON_MAX_LENTH = 16;
    //翻页次数
    private int count;
    private ObjectAnimator animator;
    private IScrollCompleteListener scrollCompleteListener;

    public VerticalScroll(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public VerticalScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public VerticalScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init(){
        textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setTextSize(23);
        textView.setTextColor(Color.WHITE);
        addView(textView);

        if (directType == HORIZON_TYPE) {
            //横向
            animator = ObjectAnimator.ofFloat(textView, "translationX", getWidth(),-getWidth());
            animator.setDuration(8000);
            animator.start();
        }else {
            //竖向
            animator = ObjectAnimator.ofFloat(this, "translationY", getHeight(), 0);
            animator.setDuration(3000);
            animator.start();
        }

    }

    class ScrollRun implements Runnable {
        @Override
        public void run() {
            count++;
            if (count >= showText.size()) {
                count = 0;
            }
            removeAllViews();
            init();
            textView.setText(showText.get(count));
            startScroll();
        }
    }


    public void setTextContent(String textContent,int directType){

        this.directType = directType;

        showText.clear();
        String currentString = textContent;
        int length = currentString.length();
        int MAX_LENTH;

        if (VERTICAL_TYPE == directType) {
            MAX_LENTH = VERTICAL_MAX_LENTH;
        }else {
            MAX_LENTH = HORIZON_MAX_LENTH;
        }

        if (length > MAX_LENTH) {
            int page = length / MAX_LENTH;
            if (length % MAX_LENTH > 0 ) {
                page = page + 1;
            }
            int startPosition = 0;
            int endPosition = MAX_LENTH;
            for (int i = 0; i < page; i++) {
                String sub = currentString.substring(startPosition, endPosition);
                //添加到列表
                showText.add(sub);
                //设置截取的首尾位置
                startPosition = startPosition + MAX_LENTH;
                if (endPosition < currentString.length()) {
                    String substring = currentString.substring(endPosition, currentString.length() - 1);
                    if (substring.length() > MAX_LENTH) {
                        endPosition = endPosition + MAX_LENTH;
                    }else {
                        endPosition = currentString.length();
                    }
                }

            }
        }else {
            showText.add(currentString);
        }
        if (showText.size() > 0) {
            textView.setText(showText.get(0));
        }
    }

    public void startScroll(){
        if (scrollRun != null) {
            handler.removeCallbacks(scrollRun);
        }
        //两条开始翻页
        if (showText.size() > 1) {
            if (scrollRun == null) {
                scrollRun = new ScrollRun();
            }
            handler.postDelayed(scrollRun, 8000);
        }
    }

    public void stopScroll(){
        if (scrollRun != null) {
            handler.removeCallbacks(scrollRun);
        }
    }

    public interface IScrollCompleteListener{
        void completeScroll();
    }

    public void setOnScrollCompleteListener(IScrollCompleteListener scrollCompleteListener){
        this.scrollCompleteListener = scrollCompleteListener;
    }
}
