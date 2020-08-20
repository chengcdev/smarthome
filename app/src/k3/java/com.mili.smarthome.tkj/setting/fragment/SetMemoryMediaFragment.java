package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.io.File;

public class SetMemoryMediaFragment extends BaseSetFragment {

    private RecyclerView rvMedia;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_memory_media;
    }

    @Override
    protected void bindView() {
        super.bindView();
        rvMedia = findView(R.id.recyclerview);
        rvMedia.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        super.bindData();
        File[] fileList = ExternalMemoryUtils.queryVedioList();
        if (fileList == null || fileList.length == 0) {
            fileList = ExternalMemoryUtils.queryPhotoList();
        }
        FileAdapter fileAdapter = new FileAdapter(mContext, fileList);
        rvMedia.setAdapter(fileAdapter);
    }

    private class FileVH extends RecyclerView.ViewHolder {

        private TextView tvName;

        public FileVH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }

        public void setFile(File file) {
            tvName.setText(file.getName());
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileVH> {

        private Context mContext;
        private File[] mFiles;

        public FileAdapter(Context context, File[] files) {
            mContext = context;
            mFiles = files;
        }

        @NonNull
        @Override
        public FileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false);
            return new FileVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileVH holder, int position) {
            holder.setFile(mFiles[position]);
        }

        @Override
        public int getItemCount() {
            return mFiles == null ? 0 : mFiles.length;
        }
    }
}
