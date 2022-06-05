package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.OnKeyboardListener;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.ImproveInfoEvent;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.mine.mvp.IView.IImproveInfoView;
import com.tftechsz.mine.mvp.presenter.ImproveInfoPresenter;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;

public
/**
 *  包 名 : com.tftechsz.mine.mvp.ui.fragment

 *  描 述 : TODO
 */
class ImproveInfoFragment_2 extends BaseMvpFragment<IImproveInfoView, ImproveInfoPresenter> implements IImproveInfoView, View.OnClickListener {

    private ImageView mIcon;
    private EditText mEdtName;
    private TextView mTvBirthday;
    private String imgPath;
    public String icon; //头像
    public String nickname;  //用户昵称
    public String birthday;  //用户出生日期
    private LinearLayout mLlRandom;
    private ImageView mIvRotate;
    private ConstraintLayout mRoot_view;

    @Override
    public void initUI(Bundle savedInstanceState) {
        mRoot_view = getView(R.id.root_view);
        mIcon = getView(R.id.iv_icon);
        mIcon.setOnClickListener(this);
        getView(R.id.iv_change_icon).setOnClickListener(this);
        mTvBirthday = getView(R.id.tv_birthday);
        mTvBirthday.setOnClickListener(this);
        mLlRandom = getView(R.id.ll_random);
        mLlRandom.setOnClickListener(this);
        mIvRotate = getView(R.id.iv_rotate);
        mEdtName = getView(R.id.edt_name);
        mEdtName.setFilters(new InputFilter[]{new CustomFilter(Constants.MAX_NAME_LENGTH)});
        mEdtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nickname = s.toString();
                RxBus.getDefault().post(new ImproveInfoEvent(icon, nickname, birthday));
            }
        });
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(false).setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                RxBus.getDefault().post(new ImproveInfoEvent(isPopup));
            }
        }).init();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_improve_info_2;
    }

    @Override
    protected void initData() {
    }

    public void setInfo(String avatar, String etName) {
        if (!TextUtils.isEmpty(imgPath)) {
            GlideUtils.loadRoundImage(mContext, mIcon, imgPath, 6);
        }
        if (!TextUtils.isEmpty(avatar)) {
            icon = avatar;
        }
        if (!TextUtils.isEmpty(etName)) {
            mEdtName.setText(etName);
            nickname = etName;
            mEdtName.setSelection(Utils.getText(mEdtName).length());
        } else {
            p.getRandomNikeName(null);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_icon || id == R.id.iv_change_icon) {//换头像
            ChoosePicUtils.picSingle(getActivity(), true, new OnResultCallbackListener<LocalMedia>() {
                @Override
                public void onResult(List<LocalMedia> result) {
                    if (result != null && result.size() > 0) {
                        LocalMedia localMedia = result.get(0);
                        if (localMedia.isCut()) {
                            imgPath = localMedia.getCutPath();
                        } else {
                            imgPath = localMedia.getPath();
                        }
                        p.uploadAvatar(imgPath);
                    }
                }

                @Override
                public void onCancel() {

                }

            });
        } else if (id == R.id.ll_random) { //随机名称
            RotateAnimation animation = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(800);
            animation.setRepeatCount(-1);
            animation.setFillAfter(true);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mLlRandom.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLlRandom.setEnabled(true);
                    animation.cancel();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                toastTip("暂无网络，请连接网络后重试！");
                return;
            }
            mIvRotate.startAnimation(animation);
            p.getRandomNikeName(mIvRotate);
        } else if (id == R.id.tv_birthday) { //生日
            KeyboardUtils.hideSoftInput(getActivity());
            p.timeChoose(mContext, getView(R.id.rl_complete),2000,5,5);
        }
    }

    @Override
    protected ImproveInfoPresenter initPresenter() {
        return new ImproveInfoPresenter();
    }

    @Override
    public void chooseBirthday(String day) {
        this.birthday =  day;
        RxBus.getDefault().post(new ImproveInfoEvent(icon, nickname, this.birthday));
        mTvBirthday.setText(birthday.replace("/", "-"));
        mTvBirthday.setTextColor(Utils.getColor(R.color.color_333333));
    }

    @Override
    public void completeInfoSuccess(String msg) {

    }

    @Override
    public void getCompleteInfoSuccess(CompleteReq data) {
    }

    @Override
    public void uploadAvatarSucceeded(String url) {
        icon = url;
        Utils.runOnUiThread(() -> {
            GlideUtils.loadRoundImage(mContext, mIcon, imgPath);
            RxBus.getDefault().post(new ImproveInfoEvent(icon, nickname, birthday));
        });
    }

    @Override
    public void getRandomNicknameSucceeded(String nikename) {
        mEdtName.setText(nikename);
        Utils.setFocus(mRoot_view);
    }

    @Override
    public void showLoadingDialog() {
    }

    @Override
    public void hideLoadingDialog() {
    }
}
