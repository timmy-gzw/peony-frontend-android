package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.widget.CommonFrameView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 管理家族权限弹窗
 */
public class FamilyManagerPopWindow extends BaseBottomPop implements View.OnClickListener {
    private final Context mContext;
    private RecyclerView mRvFamilyManager;
    private CommonFrameView mFlTitle;
    private LinearLayout mLlCancel;
    protected CompositeDisposable mCompositeDisposable;
    private int mRoleId;

    public FamilyManagerPopWindow(Context context, int roleId) {
        super(context);
        mContext = context;
        mRoleId = roleId;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mFlTitle = findViewById(R.id.fl_title);
        mLlCancel = findViewById(R.id.ll_cancel);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        mRvFamilyManager = findViewById(R.id.rv_family_manager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvFamilyManager.setLayoutManager(layoutManager);
        mCompositeDisposable = new CompositeDisposable();
    }


    /**
     * 加载数据
     */
    public void initData(int other_role_id, String name, List<FamilyRoleDto> data) {
        mFlTitle.setContentText((mRoleId > other_role_id ? "管理" : "") + name);
        mFlTitle.setVisibility(View.VISIBLE);
       /* if (mRoleId > other_role_id) {
            mFlTitle.setContentText("管理" + name);
            mFlTitle.setVisibility(View.VISIBLE);
        } else {
            mFlTitle.setVisibility(View.GONE);
        }*/
        FamilyManagerAdapter adapter = new FamilyManagerAdapter(data);
        mRvFamilyManager.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            FamilyRoleDto data1 = adapter.getData().get(position);
            if (null != listener && null != data1) {
                if (TextUtils.equals("user-info", data1.type)) {
                    listener.lookUser();
                } else if (TextUtils.equals("out", data1.type)) {
                    listener.removeUser();
                } else if (TextUtils.equals("mute-on", data1.type)) {  //禁言
                    listener.muteUser(1);
                } else if (TextUtils.equals("mute-off", data1.type)) {  //取消禁言
                    listener.muteUser(0);
                } else if (TextUtils.equals("block-on", data1.type)) {  //拉黑
                    listener.blockUser(1);
                } else if (TextUtils.equals("block-off", data1.type)) {  //取消拉黑
                    listener.blockUser(0);
                } else if (TextUtils.equals("user-report", data1.type)) {  //举报
                    listener.reportUser();
                } else {
                    listener.setRole(data1.id);
                }
            }
            dismiss();
        });

    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_manager);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    static class FamilyManagerAdapter extends BaseQuickAdapter<FamilyRoleDto, BaseViewHolder> {

        public FamilyManagerAdapter(@Nullable List<FamilyRoleDto> data) {
            super(R.layout.item_family_setting, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, FamilyRoleDto item) {
            helper.setText(R.id.tv_content, item.name);
            helper.setVisible(R.id.view, helper.getLayoutPosition() != getData().size() - 1);
            if (TextUtils.equals("out", item.type)) {
                helper.setTextColor(R.id.tv_content, ContextCompat.getColor(getContext(), R.color.red));
            }
        }
    }


    public interface OnSelectListener {
        //设置角色
        void setRole(int roleId);

        //查看用户
        void lookUser();

        //举报用户
        void muteUser(int mute);

        //拉黑用户
        void blockUser(int block);

        //举报
        void reportUser();

        //移除用户
        void removeUser();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
