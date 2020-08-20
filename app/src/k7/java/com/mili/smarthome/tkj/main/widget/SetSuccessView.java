package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.MessageBean;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.set.widget.AutoScrollView;

import java.util.List;

public class SetSuccessView extends RelativeLayout {
    private Context mContext;
    private ISetCallBackListener callBackListener;
    private Handler handler = new Handler();
    private int messageCount;
    private static final int SCROLL_TIME = 20 * 1000;
    private MessageRun messageRun;
    private View messageView;
    private TextView textView;

    public SetSuccessView(Context context) {
        super(context);
        init(context);
    }


    public SetSuccessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SetSuccessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.set_success_view, this);
    }

    public void showSuccessView(final String text) {
        final TextView textView = getTextView(text);
        if (callBackListener != null) {
            if (text.equals(mContext.getString(R.string.setting_success))) {
                callBackListener.success();
            } else {
                callBackListener.fail();
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView(textView);
            }
        }, 2000);
    }

    public void showSuccessView(final String text, final ISetCallBackListener callBackListener) {
        final TextView textView = getTextView(text);
        if (callBackListener != null) {
            if (text.equals(mContext.getString(R.string.setting_success))) {
                callBackListener.success();
            } else {
                callBackListener.fail();
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView(textView);
            }
        }, 2000);
    }

    public void showSuccessView(final String text,int delayTime, final ISetCallBackListener callBackListener) {
        textView = getTextView(text, 23);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callBackListener != null) {
                    callBackListener.success();
                }
                removeView(textView);
            }
        }, delayTime);
    }

    //显示信息界面
    public void showMessageView(List<MessageBean> list, ISetCallBackListener callBackListener) {
        if (list.size() > 0) {
            messageView = getMessageView();
            handler.removeCallbacks(messageRun);
            messageCount = 0;
            messageRun = new MessageRun(list, messageView, callBackListener);
            handler.postDelayed(messageRun, SCROLL_TIME);
        }
    }

    //关闭信息界面
    public void removeMessageView() {
        if (messageView != null) {
            removeView(messageView);
        }
        if (messageRun != null) {
            handler.removeCallbacks(messageRun);
        }
    }

    public void removeTipTextView() {
        if (textView != null) {
            removeView(textView);
        }
    }

    /**
     * 处理中
     */
    public void showProcessing(final String text, final ISetCallBackListener callBackListener) {
        final TextView textView = getTextView(text);
        callBackListener.success();
    }


    public void setSuccessListener(ISetCallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }


    /**
     * 显示清理界面
     */
    public void showClearView(String title) {
        removeAllViews();
        LayoutInflater.from(mContext).inflate(R.layout.set_clear_view, this);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(title);
    }



    @NonNull
    TextView getTextView(String text) {
//        removeAllViews();
        final TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setBackgroundResource(R.drawable.title_back);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(28);
        textView.setGravity(Gravity.CENTER);
        setGravity(Gravity.CENTER);
        addView(textView);
        return textView;
    }

    @NonNull
    TextView getTextView(String text, int textSize) {
//        removeAllViews();
        final TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setBackgroundResource(R.drawable.title_back);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setLineSpacing(5, 1.2f);
        setGravity(Gravity.CENTER);
        addView(textView);
        return textView;
    }

    @NonNull
    View getMessageView() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.custom_message_view, null);
        addView(view);
        return view;
    }

    class MessageRun implements Runnable {

        private final List<MessageBean> list;
        private final View view;
        private final ISetCallBackListener callBackListener;
        private final AutoScrollView scroll_title;
        private final AutoScrollView scroll_content;

        MessageRun(List<MessageBean> list, View view, ISetCallBackListener callBackListener) {
            this.list = list;
            this.view = view;
            this.callBackListener = callBackListener;
            scroll_title = (AutoScrollView) view.findViewById(R.id.scro_title);
            scroll_content = (AutoScrollView) view.findViewById(R.id.scro_content);
            scroll_title.setContent(list.get(0).getTitle());
            scroll_title.startScroll();
            scroll_content.setContent(list.get(0).getContent());
            scroll_content.startScroll();
        }

        @Override
        public void run() {
            messageCount++;
            //是否有信息
            if (messageCount >= list.size()) {
                if (callBackListener != null) {
                    messageCount = 0;
                    removeView(view);
                    callBackListener.success();
                }
                return;
            }
            scroll_title.setContent(list.get(messageCount).getTitle());
            scroll_title.startScroll();
            scroll_content.setContent(list.get(messageCount).getContent());
            scroll_content.startScroll();
            handler.postDelayed(this, SCROLL_TIME);
        }
    }

}
