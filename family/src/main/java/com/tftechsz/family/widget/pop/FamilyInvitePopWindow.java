package com.tftechsz.family.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tftechsz.family.R;
import com.tftechsz.common.entity.FamilyInviteBean;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 * 包 名 : com.tftechsz.family.widget.pop
 * 描 述 : 家族邀请弹窗
 */
public class FamilyInvitePopWindow extends BaseCenterPop implements View.OnClickListener {
    private Context mContext;
    private FamilyInviteBean data;
    private FamilyInviteListener mListener;

    public FamilyInvitePopWindow(Context context, FamilyInviteBean data, FamilyInviteListener listener) {
        super(context);
        mContext = context;
        this.data = data;
        mListener = listener;
        initUI();
        setPopupFadeEnable(true);
    }

    public interface FamilyInviteListener {
        void onFamilyInviteClick(int family_id);
    }

    private void initUI() {
        ImageView top_bg = findViewById(R.id.top_bg);
        ImageView iv_icon = findViewById(R.id.iv_icon);
        TextView tv_name = findViewById(R.id.tv_name);
        TextView tips = findViewById(R.id.tips);
        TextView tv_level = findViewById(R.id.tv_level);
        ImageView iv_level = findViewById(R.id.iv_level);
        TextView tv_people = findViewById(R.id.tv_people);
        TextView tc_desc = findViewById(R.id.tc_desc);
        findViewById(R.id.tv_accept).setOnClickListener(this);
        findViewById(R.id.iv_del).setOnClickListener(v -> dismiss());

        if (data.level > 0 && !TextUtils.isEmpty(data.level_icon)) {
            tv_level.setText(String.valueOf(data.level)); //等级
            GlideUtils.loadRouteImage(mContext, iv_level, data.level_icon);
        }
        GlideUtils.loadRoundImage(mContext, iv_icon, data.icon, 6);
        GlideUtils.loadImageGaussian(mContext, top_bg, data.icon, R.mipmap.ic_default_avatar);
        tips.setText(data.tips);
        tv_name.setText(data.tname);
        tv_people.setText(data.user_count);
        tc_desc.setText(data.intro);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_invite);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_accept) { //接受邀请
            if (mListener != null) {
                mListener.onFamilyInviteClick(data.family_id);
            }
            dismiss();
        }
    }
}
