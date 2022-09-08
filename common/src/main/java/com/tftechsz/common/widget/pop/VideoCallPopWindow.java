package com.tftechsz.common.widget.pop;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.DensityUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.SPUtils;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

/**
 *   语音视频弹窗
 */
public class VideoCallPopWindow extends BasePopupWindow implements View.OnClickListener {

    private final Activity mContext;
    private TextView mTvVideo, mTvVoice;
    private CallCheckDto mData;
    protected CompositeDisposable mCompositeDisposable;
    private RechargePopWindow rechargePopWindow;
    private ImageView ivFaceOff;
    private final UserProviderService service;
    private RechargeBeforePop beforePop;
    //触发场景
    private final int scene;
    private final String userId;

    public VideoCallPopWindow(Activity context, int scene, String userId) {
        super(context);
        this.scene = scene;
        this.userId = userId;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mContext = context;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        ivFaceOff = findViewById(R.id.iv_face_off);
        mTvVideo = findViewById(R.id.tv_video);
        mTvVideo.setOnClickListener(this);
        mTvVoice = findViewById(R.id.tv_audio);
        mTvVoice.setOnClickListener(this);
        mCompositeDisposable = new CompositeDisposable();
    }


    private void initData() {
        if (null != mData.list) {
            if (null != mData.list.video) {   // 视频
                mTvVideo.setText(mData.list.video.title);
                Drawable drawable;
                if (mData.list.video.is_lock) {
                    drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_call_lock);
                } else {
                    drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_video_open);
                }
                mTvVideo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                mTvVideo.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 3));
            }
            if (null != mData.list.voice) {   // 语音
                mTvVoice.setText(mData.list.voice.title);
                Drawable drawable;
                if (mData.list.voice.is_lock) {
                    drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_call_lock);
                } else {
                    drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_voice_open);
                }
                mTvVoice.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                mTvVoice.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 3));
            }

        }
    }

    public void setData(CallCheckDto data) {
        mData = data;
        initData();
        int isShowFaceOn = SPUtils.getInt(Constants.IS_SHOW_FACE_OFF, 0);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.icon != null) {
            if (!TextUtils.isEmpty(service.getConfigInfo().sys.icon.ic_face_off_tip)) {
                GlideUtils.loadRouteImage(mContext, ivFaceOff, service.getConfigInfo().sys.icon.ic_face_off_tip);
                if (isShowFaceOn == 0 && service.getUserInfo().getSex() == 1) {
                    ivFaceOff.setVisibility(View.VISIBLE);
                } else {
                    ivFaceOff.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_video);
        return v;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_BOTTOM)
                .toDismiss();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mData == null) return;
        if (id == R.id.tv_video) {
            if (mData.list != null && null != mData.list.video) {
                if (mData.list.video.is_lock) {
                    if (null != mData.error && null != mData.error.intimacy) {
                        showCustomPop(mData.error.intimacy.msg);
                    } else if (null != mData.error && null != mData.error.video) {
                        if (TextUtils.equals(mData.error.video.cmd_type, Constants.DIRECT_RECHARGE)) {
                            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_limit_from_channel == 1) {
                                if (beforePop == null)
                                    beforePop = new RechargeBeforePop(mContext);
                                beforePop.addOnClickListener(this::showRechargePop);
                                beforePop.showPopupWindow();
                            } else {
                                showRechargePop();
                            }
                        } else {
                            showCustomPop(mData.error.video.msg);
                        }
                    }
                } else {
                    if (listener != null)
                        initPermissions(2);
                }
            }
            dismiss();
        } else if (id == R.id.tv_audio) {
            if (mData.list != null && null != mData.list.voice) {
                if (mData.list.voice.is_lock) {
                    if (null != mData.error && null != mData.error.intimacy) {
                        showCustomPop(mData.error.intimacy.msg);
                    } else if (null != mData.error && null != mData.error.voice) {
                        if (TextUtils.equals(mData.error.voice.cmd_type, Constants.DIRECT_RECHARGE)) {
                            showRechargePop();
                        } else {
                            showCustomPop(mData.error.voice.msg);
                        }
                    }
                } else {
                    if (listener != null)
                        initPermissions(1);

                }
            }
            dismiss();
        }
    }

    /**
     * 显示充值列表
     */
    private void showRechargePop() {
        if (rechargePopWindow == null)
            rechargePopWindow = new RechargePopWindow(mContext, 3, scene, 0, 0, userId);
        rechargePopWindow.getCoin();
        rechargePopWindow.requestData();
        rechargePopWindow.showPopupWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    private void initPermissions(int type) {
        mCompositeDisposable.add(new RxPermissions((FragmentActivity) mContext)
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (!ClickUtil.canOperate()) return;
                        listener.chatType(type);
                    } else {
                        PermissionUtil.showPermissionPop(mContext);
                    }
                }));
    }


    private void showCustomPop(String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(mContext);
        customPopWindow.setContent(content);
        customPopWindow.setRightGone();
        customPopWindow.showPopupWindow();
    }


    public interface OnSelectListener {
        void chatType(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
