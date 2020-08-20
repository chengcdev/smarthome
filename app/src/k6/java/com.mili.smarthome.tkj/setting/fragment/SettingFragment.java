package com.mili.smarthome.tkj.setting.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View llAll;
    private View llMenu;
    private CaptionAdapter captionAdapter = new CaptionAdapter();
    private MenuAdapter menuAdapter = new MenuAdapter();
    private TextView mTvPrePage;
    private TextView mTvNextPage;
    private View llPage;
    private FrameLayout mFlBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void bindView() {
        llAll = findView(R.id.ll_all);
        llMenu = findView(R.id.ll_menu);
        llPage = findView(R.id.ll_page);
        mTvPrePage = findView(R.id.tv_pre_page);
        mTvNextPage = findView(R.id.tv_next_page);
        mFlBottom = findView(R.id.fl_func);
        mTvPrePage.setOnClickListener(this);
        mTvNextPage.setOnClickListener(this);

        RecyclerView rvParent = findView(R.id.rv_parent);
        rvParent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvParent.setAdapter(captionAdapter);
        //
        RecyclerView rvChild = findView(R.id.rv_child);
        rvChild.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvChild.setAdapter(menuAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        setBackVisibility(View.VISIBLE);
    }

    public void resetFunc(SettingFunc func) {
        mFlBottom.setVisibility(View.GONE);
        FragmentManager fm = getFragmentManager();
        if (fm != null && isAdded()) {
            fm.popBackStackImmediate(null, 1);
        }
        captionAdapter.resetDataSet(func);
        setMenuList(func.getChild());
    }

    public boolean handleBack() {
        SettingFunc func = captionAdapter.takeItem();
        if (func == null)
            return false;
        setMenuList(func.getChild());
        FragmentManager fm = getFragmentManager();
        if (fm != null)
            fm.popBackStackImmediate();
        return true;
    }

    private void onFuncClick(SettingFunc func) {
        mFlBottom.setVisibility(View.GONE);
        captionAdapter.putItem(func);
        if (func.hasChild()) {
            setMenuList(func.getChild());
        } else {
            gotoFuncFragment(func);
        }
    }

    private void setMenuList(List<SettingFunc> funcList) {
        llAll.setVisibility(View.VISIBLE);
        llMenu.setVisibility(View.VISIBLE);
        mFlBottom.setVisibility(View.GONE);

        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            List<Fragment> fragments = fm.getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                fm.popBackStack();
            }
        }

        menuAdapter.setDataSet(funcList);
        if (funcList != null && funcList.size() > menuAdapter.PAGE_SIZE) {
            llPage.setVisibility(View.VISIBLE);
        } else {
            llPage.setVisibility(View.GONE);
        }
    }

    private void gotoFuncFragment(SettingFunc func) {
        if (SettingFunc.SET_LOCK_ATTR.equals(func.getCode())
                || SettingFunc.SET_DOOR_STATUS.equals(func.getCode())
                || SettingFunc.SET_FACE_RECOGNITION.equals(func.getCode()) || SettingFunc.SET_QR_OPEN_TYPE.equals(func.getCode())) {
            llAll.setVisibility(View.GONE);
        } else {
            llMenu.setVisibility(View.GONE);
        }
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }
        toFragment(fm, func.getCode());
    }

    public void toFragment(FragmentManager fm,String code) {
        Fragment fragment;
        switch (code) {
            case SettingFunc.SET_NETWORK:
                fragment = new SetNetworkFragment();
                //跳转到设置IP界面
                AppUtils.getInstance().replaceFragment(getActivity(), fragment, R.id.fl_container, "SetNetworkFragment",
                        Constant.IntentId.INTENT_KEY, false);
                break;
            case SettingFunc.SET_FACE_RECOGNITION:
                fragment = new SetFaceFragment();
                //跳转到人脸界面
                AppUtils.getInstance().replaceFragment(getActivity(), fragment, R.id.fl_container, "SetFaceFragment");
                break;
            case SettingFunc.SET_FINGERPRINT:
                fragment = new SetFingerprintFragment();
                //跳转到指纹界面
                AppUtils.getInstance().replaceFragment(getActivity(), fragment, R.id.fl_container, "SetFingerprintFragment");
                break;
            default:
                mFlBottom.setVisibility(View.VISIBLE);
                fragment = FragmentFactory.create(code);
                if (fragment != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fl_func, fragment);
                    ft.addToBackStack(fragment.getClass().getName());
                    ft.commitAllowingStateLoss();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //上一页
            case R.id.tv_pre_page:
                menuAdapter.prePage();
                break;
            //下一页
            case R.id.tv_next_page:
                menuAdapter.nextPage();
                break;
        }
    }

    // =============================================================== //
    // =============================================================== //

    private class CaptionVH extends RecyclerView.ViewHolder {

        private ImageView ivArrow;
        private TextView tvName;

        private CaptionVH(View itemView) {
            super(itemView);
            ivArrow = ViewUtils.findView(itemView, R.id.iv_arrow);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }

    private class CaptionAdapter extends RecyclerView.Adapter<CaptionVH> {

        private List<SettingFunc> mList = new ArrayList<>();

        public void resetDataSet(SettingFunc node) {
            mList.clear();
            mList.add(node);
            notifyDataSetChanged();
        }

        public void putItem(SettingFunc node) {
            mList.add(node);
            notifyItemInserted(mList.size() - 1);
        }

        public SettingFunc takeItem() {
            int position = mList.size() - 1;
            if (position > 0) {
                mList.remove(position);
                notifyItemRemoved(position);
                return mList.get(mList.size() - 1);
            } else {
                return null;
            }
        }

        @NonNull
        @Override
        public CaptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_caption, parent, false);
            return new CaptionVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CaptionVH holder, int position) {
            final SettingFunc func = mList.get(position);
            holder.ivArrow.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.tvName.setText(func.getName());
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    // =============================================================== //
    // =============================================================== //

    private class MenuVH extends RecyclerView.ViewHolder {

        private TextView tvName;

        private MenuVH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuVH> {

        private final int PAGE_SIZE = 5;

        private List<SettingFunc> mList;
        private int mMaxPage;
        private int mPage;

        public void setDataSet(List<SettingFunc> list) {
            mList = list;
            mPage = 0;
            if (mList == null)
                mMaxPage = 0;
            else
                mMaxPage = (mList.size() - 1) / PAGE_SIZE;
            notifyDataSetChanged();
        }

        public void prePage() {
            mPage--;
            if (mPage < 0) {
                mPage = mMaxPage;
            }
            notifyDataSetChanged();
        }

        public void nextPage() {
            mPage++;
            if (mPage > mMaxPage) {
                mPage = 0;
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MenuVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_menu, parent, false);
            return new MenuVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MenuVH holder, int position) {
            final int index = mPage * PAGE_SIZE + position;
            final SettingFunc func = mList.get(index);

            holder.tvName.setText(func.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFuncClick(func);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mList == null)
                return 0;
            int start = mPage * PAGE_SIZE;
            int count = mList.size() - start;
            return Math.min(count, PAGE_SIZE);
        }
    }
}
