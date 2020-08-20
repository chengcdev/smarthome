package com.mili.smarthome.tkj.setting.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体信息
 */
public class SetMediaInfoFragment extends BaseFragment implements View.OnClickListener {


    private RecyclerView mRv;
    private List<String> mediaList = new ArrayList<>();
    private LinearLayout mLlPage;
    private TextView mTvPrePage;
    private TextView mTvNextPage;
    private int firstCount = 6;
    private MediaAdapter mediaAdapter;
    private List<String> currentMediaList = new ArrayList<>();
    private int papgeCount;
    private int toalaPageSize;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_media_infolist;
    }

    @Override
    protected void bindView() {
        mRv = findView(R.id.recyclerview);
        ImageView mImaBack = findView(R.id.iv_back);
        mLlPage = findView(R.id.ll_page);
        mTvPrePage = findView(R.id.tv_pre_page);
        mTvNextPage = findView(R.id.tv_next_page);

        mImaBack.setOnClickListener(this);
        mTvPrePage.setOnClickListener(this);
        mTvNextPage.setOnClickListener(this);
        setBackVisibility(View.GONE);
    }

    @Override
    protected void bindData() {
        currentMediaList.clear();
        //获取媒体信息
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
//        //视频路径
        File[] videoList = ExternalMemoryUtils.queryVedioList();
        File[] queryPhotoList = ExternalMemoryUtils.queryPhotoList();
        if (videoList != null && videoList.length > 0) {
            mediaList.clear();
            for (File file : videoList) {
                mediaList.add(file.getName());
            }
        } else if (queryPhotoList != null && queryPhotoList.length > 0) {
            mediaList.clear();
            for (File file : queryPhotoList) {
                mediaList.add(file.getName());
            }
        }


        toalaPageSize = mediaList.size() / 6;
        if (mediaList.size() % 6 > 0) {
            toalaPageSize += 1;
        }

        if (mediaList.size() > 6) {
            for (int i = 0; i < mediaList.size(); i++) {
                if (i < 6) {
                    currentMediaList.add(mediaList.get(i));
                }
            }
            mLlPage.setVisibility(View.VISIBLE);
        } else {
            currentMediaList.addAll(mediaList);
            mLlPage.setVisibility(View.GONE);
        }

        mediaAdapter = new MediaAdapter(currentMediaList);
        mRv.setAdapter(mediaAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setBackVisibility(View.VISIBLE);
                exitFragment(this);
                break;
            case R.id.tv_pre_page:
                //上一页
                papgeCount--;
                if (papgeCount < 0) {
                    papgeCount = toalaPageSize - 1;
                }
                setPage();
                break;
            case R.id.tv_next_page:
                papgeCount++;
                //下一页
                setPage();
                break;
        }
    }

    private void setPage() {
        currentMediaList.clear();
        for (int i = 0; i < mediaList.size(); i++) {
            if (i >= papgeCount * 6 && i <= papgeCount * 6 + 5) {
                currentMediaList.add(mediaList.get(i));
            }
        }
        if (currentMediaList.size() == 0) {
            papgeCount = 0;
            if (mediaList.size() > 6) {
                for (int i = 0; i < mediaList.size(); i++) {
                    if (i < 6) {
                        currentMediaList.add(mediaList.get(i));
                    }
                }
            } else {
                currentMediaList.addAll(mediaList);
            }
        }
        mediaAdapter = new MediaAdapter(currentMediaList);
        mRv.setAdapter(mediaAdapter);
    }

    class MediaAdapter extends RecyclerView.Adapter<MediaViewHolder> {

        private List<String> mList;

        private MediaAdapter(List<String> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_mdeia_info, parent, false);
            return new MediaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
            holder.textView.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        private MediaViewHolder(View itemView) {
            super(itemView);
            textView = ViewUtils.findView(itemView, R.id.textview);
        }
    }
}
