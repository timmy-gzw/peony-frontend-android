package com.tftechsz.family.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.RoundImageView;
import com.tftechsz.common.widget.pop.BaseCenterPop;


/**
 * 转让弹窗
 */
public class FamilyChangeMasterWindow extends BaseCenterPop implements View.OnClickListener {

    private FamilyMemberDto mData;
    private TextView mTvName, tvType;
    private RoundImageView mIvAvatar;

    public FamilyChangeMasterWindow(Context context) {
        super(context);
        mTvName = findViewById(R.id.tv_name);
        mIvAvatar = findViewById(R.id.iv_avatar);
        tvType = findViewById(R.id.tv_type);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_sure).setOnClickListener(this);
        setPopupFadeEnable(true);
    }


    public void setData(FamilyMemberDto data) {
        mData = data;
        mTvName.setText(mData.nickname);
        GlideUtils.loadRoundImageRadius(mContext, mIvAvatar, mData.icon);
        UIUtils.setFamilyTitle(tvType, mData.role_id);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_change_master);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sure) {
            if (mData != null && listener != null)
                listener.changeMaster(mData.user_id);
            dismiss();
        } else if (id == R.id.tv_cancel) {
            dismiss();
        }
    }


    public interface OnSelectListener {
        void changeMaster(int userId);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
