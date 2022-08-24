package com.tftechsz.common.widget.pop;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.databinding.PopNobleNoticeBinding;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;

import androidx.databinding.DataBindingUtil;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 贵族通知弹窗
 */
public class NobleNoticePop extends BaseCenterPop {

    private PopNobleNoticeBinding mBind;
    private final UserProviderService service;

    public NobleNoticePop() {
        super(BaseApplication.getInstance());
        setOutSideDismiss(false);
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_noble_notice);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    public NobleNoticePop setData(String content) {
        ChatMsg.NobleNotice notice = JSON.parseObject(content, ChatMsg.NobleNotice.class);
        if (notice != null) {
            GlideUtils.loadRouteImage(getContext(), mBind.icon, service.getUserInfo().getIcon());
            switch (notice.id) {
//                case 1:
//                    mBind.frameBg.setBackgroundResource(R.drawable.vip_style_picture_frame_s18_start);
//                    mBind.rootBg.setBackgroundResource(R.mipmap.noble_notice_bg_1);
//                    break;
//                case 2:
//                    mBind.frameBg.setBackgroundResource(R.drawable.vip_style_picture_frame_s19_start);
//                    mBind.rootBg.setBackgroundResource(R.mipmap.noble_notice_bg_2);
//                    break;
//                case 3:
//                    mBind.frameBg.setBackgroundResource(R.drawable.vip_style_picture_frame_s20_start);
//                    mBind.rootBg.setBackgroundResource(R.mipmap.noble_notice_bg_3);
//                    break;
//                case 4:
//                    mBind.frameBg.setBackgroundResource(R.drawable.vip_style_picture_frame_s21_start);
//                    mBind.rootBg.setBackgroundResource(R.mipmap.noble_notice_bg_4);
//                    break;
            }
            mBind.title.setText(notice.title);
            mBind.tips.setText(notice.tips);
            mBind.knowBtn.setOnClickListener(v -> dismiss());

            RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            rotate.setInterpolator(lin);
            rotate.setDuration(5 * 1000);
            rotate.setRepeatCount(-1);
            rotate.setFillAfter(true);
            rotate.setStartOffset(10);
            mBind.ivBg.setAnimation(rotate);
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    mBind.ivBg.clearAnimation();
                }
            });

            showPopupWindow();
        }
        return this;
    }
}
