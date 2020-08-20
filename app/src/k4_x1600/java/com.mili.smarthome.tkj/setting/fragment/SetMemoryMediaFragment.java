package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.NonNull;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.io.File;

public class SetMemoryMediaFragment extends K4BaseFragment implements View.OnClickListener {

    private TextView mTvHead;
    private RecyclerView mRclView;
    private FileAdapter mFileAdapter;

    @Override
    public boolean onKeyCancel() {
        exitFragment();
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_memory_media;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mRclView = findView(R.id.recyclerview);
        mRclView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        ImageButton up = findView(R.id.ib_up);
        up.setOnClickListener(this);
        ImageButton down = findView(R.id.ib_down);
        down.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();
        mTvHead.setText(R.string.setting_040704);

        File[] files = ExternalMemoryUtils.queryVedioList();
        if (files == null) {
            files = ExternalMemoryUtils.queryPhotoList();
        }
        mFileAdapter = new FileAdapter(mContext, files);
        mRclView.setAdapter(mFileAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                mFileAdapter.prePage();
                break;

            case R.id.ib_down:
                mFileAdapter.nextPage();
                break;
        }
    }

    private class FileVH extends RecyclerView.ViewHolder {

        private TextView tvName;

        private FileVH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }

        public void setFile(File file) {
            tvName.setText(file.getName());
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileVH> {

        private int PAGE_NUM = 4;
        private Context mContext;
        private File[] mFiles;
        private int mPage, mMaxPage;

        private FileAdapter(Context context, File[] files) {
            mContext = context;
            mFiles = files;
            if (mFiles == null) {
                mPage = 0;
                mMaxPage = 0;
                return;
            }

            //设置初始页索引及最大页数
            mPage = 0;
            mMaxPage = mFiles.length / PAGE_NUM;
            if (mFiles.length % PAGE_NUM != 0) {
                mMaxPage++;
            }
            LogUtils.d("file len is " + files.length + " mPage is " + mPage + ", maxpage is " + mMaxPage);
        }

        @NonNull
        @Override
        public FileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false);
            return new FileVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileVH holder, int position) {
            if (mFiles == null) {
                return;
            }
            int index = mPage*PAGE_NUM + position;
            if (index < mFiles.length) {
                holder.setFile(mFiles[index]);
            }
        }

        @Override
        public int getItemCount() {
            if (mFiles == null) {
                return 0;
            }
            if (mPage != mMaxPage-1) {
                return PAGE_NUM;
            } else {
                return (mFiles.length - mPage*PAGE_NUM);
            }
        }

        public void prePage() {
            if (mPage > 0) {
                mPage--;
                notifyDataSetChanged();
            }
        }

        public void nextPage() {
            if (mPage < mMaxPage-1) {
                mPage++;
                notifyDataSetChanged();
            }
        }
    }
}
