package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

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
    private ImageView ivRotate, rightIcon;
    private View iconStroke;
    private TextView levelCard, levelTips;
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
        rightIcon = findViewById(R.id.right_icon);
        iconStroke = findViewById(R.id.icon_stroke);
        levelCard = findViewById(R.id.level_card);
        levelTips = findViewById(R.id.level_tips);
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
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(5 * 1000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        ivRotate.setAnimation(rotate);
    }

    public void setData(ChatMsg.UserLevelUp data) {
        levelCard.setText(data.user_title);
        levelTips.setText(data.user_tips);
        if (data.user_type.equals("charm")) {
            rightIcon.setImageResource(R.mipmap.mine_ic_charm_love);
            levelCard.setBackgroundResource(R.mipmap.mine_bg_charm_content);
            iconStroke.setBackgroundResource(R.drawable.shape_meili_icon_bg_pop);
        }

    }
}
