package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : TODO
 */
public class UserLevelUpPop extends BaseCenterPop {
    private ImageView ivRotate, ivLevelBg;
    private View iconStroke;
    private TextView levelTips,tvLevelTitle;
    private final UserProviderService service;

    public UserLevelUpPop(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initUI();
    }

    private void initUI() {
        ivRotate = findViewById(R.id.iv_rotate);
        ivRotate.setVisibility(View.GONE);
        ImageView icon = findViewById(R.id.icon);
        iconStroke = findViewById(R.id.icon_stroke);
        levelTips = findViewById(R.id.level_tips);
        ivLevelBg = findViewById(R.id.level_bg);
        tvLevelTitle = findViewById(R.id.tv_level_cont);
        GlideUtils.loadRouteImage(getContext(), icon, service.getUserInfo().getIcon());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                ivRotate.clearAnimation();
            }
        });
        findViewById(R.id.del).setOnClickListener(v -> dismiss());
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_user_level_up);
    }

    public void setRotate() {
        ivRotate.setVisibility(View.VISIBLE);
    }

    public void setData(ChatMsg.UserLevelUp data) {
        levelTips.setText(data.user_tips);
        if (data.user_type.equals("charm")) {
            ivRotate.setBackgroundResource(R.mipmap.peony_qd_bbg_icon_2);
            ivLevelBg.setImageResource(R.mipmap.peony_bpqtt_img_2);
            iconStroke.setBackgroundResource(R.drawable.shape_meili_icon_bg_pop);
            levelTips.setTextColor(ContextCompat.getColor(mContext, R.color.c_cc3d3d));
            tvLevelTitle.setTextColor(ContextCompat.getColor(mContext, R.color.c_cc3d3d));
        }
    }
}
