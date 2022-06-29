package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ConvertUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IRealAuthView;
import com.tftechsz.mine.mvp.presenter.RealAuthPresenter;

import java.util.List;

/**
 * 真人认证状态
 */

@Route(path = ARouterApi.ACTIVITY_REAL_AUTHENTICATION_STATUS)
public class RealAuthenticationStatusActivity extends BaseMvpActivity<IRealAuthView, RealAuthPresenter> implements View.OnClickListener, IRealAuthView {

    @Autowired
    UserProviderService service;
    private TextView mTvStatus;
    private TextView mTvUploadAvatar;  //重新拍照，上传头像
    private ImageView mIvPhoto, mIvAvatar;
    private ImageView mIvPhotoError, mIvAvatarError;
    private TextView mTvTip;
    private RxPermissions mRxPermissions;

    private int mStatus;  // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
    private RealStatusInfoDto mData;
    private RelativeLayout mRlChange1;
    private RelativeLayout mRlChange2;
    private TextView mTvChange1;
    private TextView mTvChange2;
    private String mIconFile, mRealFile;

    @Override
    public RealAuthPresenter initPresenter() {
        return new RealAuthPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .build();
        mTvStatus = findViewById(R.id.tv_status);
        mTvUploadAvatar = findViewById(R.id.tv_upload_avatar);
        mTvTip = findViewById(R.id.tv_tip);
        mTvUploadAvatar.setOnClickListener(this);
        mIvPhoto = findViewById(R.id.iv_photo);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvAvatarError = findViewById(R.id.iv_avatar_error);
        mIvPhotoError = findViewById(R.id.iv_photo_error);
        mRlChange1 = findViewById(R.id.rl_change1);
        mRlChange2 = findViewById(R.id.rl_change2);
        mTvChange1 = findViewById(R.id.tv_change1);
        mTvChange2 = findViewById(R.id.tv_change2);
        mIvPhoto.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mStatus = getIntent().getIntExtra(Constants.EXTRA_STATUS, 0);
        mRealFile = getIntent().getStringExtra(Constants.EXTRA_PATH);
        mData = (RealStatusInfoDto) getIntent().getSerializableExtra(Constants.EXTRA_DATA);
        loadPic();
        if (mStatus == -1) {
            setBeginUpload();
        } else if (mStatus == 0) {
            setUploadSuccess();
        } else {
            mTvStatus.setText("认证未通过");
            mTvUploadAvatar.setText("提交认证");
            mTvUploadAvatar.setEnabled(false);
            mTvTip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.peony_sy_lt_wcg_icon, 0, 0, 0);
            mTvTip.setCompoundDrawablePadding(ConvertUtils.dp2px(4));
            mTvTip.setTextColor(Utils.getColor(R.color.red));
            if (null != mData) {
                ///0.未认证，1.头像未通过，2.真人图片未通过
                if (mData.icon_or_self == 1) {
                    changeTips("重新上传", "点击更换");
                    mIvAvatarError.setVisibility(View.VISIBLE);
                    mTvTip.setText("当前头像未监测到人脸，请重新上传头像");
                } else if (mData.icon_or_self == 2) {
                    changeTips("点击更换", "重新上传");
                    mIvPhotoError.setVisibility(View.VISIBLE);
                    mTvTip.setText("认证照片未监测到人脸，请重新拍照");
                } else if (mData.icon_or_self == 3) {
                    changeTips("点击更换", "点击更换");
                    mIvAvatarError.setVisibility(View.VISIBLE);
                    mIvPhotoError.setVisibility(View.VISIBLE);
                    mTvTip.setText("确保当前头像和认证照片同一人，否则无法通过");
                } else {
                    setBeginUpload();
                    mTvStatus.setText("认证未通过");
                }
            }
        }
    }

    private void setBeginUpload() {
        mTvStatus.setText("真人认证");
        changeTips("点击更换", "点击更换");
        mTvTip.setText("确保当前头像和认证照片同一人，否则无法通过");
        mTvTip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bg_charm, 0, 0, 0);
        mTvTip.setCompoundDrawablePadding(ConvertUtils.dp2px(4));
        mTvUploadAvatar.setText("提交认证");
    }

    private void changeTips(String s, String s1) {
        if (!TextUtils.isEmpty(s)) {
            mTvChange1.setText(s);
        } else {
            mRlChange1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(s1)) {
            mTvChange2.setText(s1);
        } else {
            mRlChange2.setVisibility(View.GONE);
        }
    }

    /**
     * 加载照片
     */
    private void loadPic() {
        if (service.getUserInfo() == null) {
            return;
        }
        if (null == mData) {
            GlideUtils.loadRouteImage(this, mIvAvatar, service.getUserInfo().getIcon(), service.getUserInfo().isBoy() ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
            GlideUtils.loadRouteImage(this, mIvPhoto, mRealFile);
        } else {
            GlideUtils.loadRouteImage(this, mIvAvatar, mData.icon, service.getUserInfo().isBoy() ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
            GlideUtils.loadRouteImage(this, mIvPhoto, mData.image_self);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_real_authentication_status;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_avatar) { //当前头像
            if (mRlChange1.getVisibility() == View.GONE) {
                return;
            }
            if (mRxPermissions == null) {
                mRxPermissions = new RxPermissions(this);
            }
            mCompositeDisposable.add(mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ChoosePicUtils.picSingle(mActivity, true, new OnResultCallbackListener<LocalMedia>() {
                                @Override
                                public void onResult(List<LocalMedia> result) {
                                    mTvChange1.setText("点击更换");
                                    if (result != null && result.size() > 0) {
                                        LocalMedia localMedia = result.get(0);
                                        if (localMedia.isCut()) {
                                            mIconFile = localMedia.getCutPath();
                                        } else {
                                            mIconFile = localMedia.getPath();
                                        }
                                        mIvAvatarError.setVisibility(View.GONE);
                                        mTvUploadAvatar.setEnabled(mIvPhotoError.getVisibility() == View.GONE);

                                        GlideUtils.loadRouteImage(mActivity, mIvAvatar, mIconFile);
                                        //getP().uploadAvatar(path);
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }

                            });
                        } else {
                            toastTip("对不起, 没有权限无法进入");
                        }
                    }));
        } else if (id == R.id.iv_photo) {
            if (mRlChange1.getVisibility() == View.GONE) {
                return;
            }
            if (mRxPermissions == null) {
                mRxPermissions = new RxPermissions(this);
            }
            mCompositeDisposable.add(mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ARouterUtils.toRealCamera(mActivity, 0);
                        } else {
                            toastTip("对不起, 没有权限无法进入");
                        }
                    }));
        } else if (id == R.id.tv_upload_avatar) {   //提交认证
            if (mStatus == 0) {
                //ARouterUtils.toMainActivity();
                finish();
                return;
            }
            showLoadingDialog();
            if (null == mData) {
                upload();
                return;
            }
            if (mData.icon_or_self == 1) {
                if (TextUtils.isEmpty(mIconFile)) {
                    hideLoadingDialog();
                    toastTip("请更换头像后再提交");
                } else {
                    if (mRealFile != null) {
                        p.uploadAvatar(mIconFile, mRealFile, true);
                    } else {
                        p.uploadAvatar(mIconFile, mData.image_self, false);
                    }
                }
                return;
            }
            if (mData.icon_or_self == 2 || mData.icon_or_self == 0) {
                if (TextUtils.isEmpty(mRealFile)) {
                    hideLoadingDialog();
                    toastTip("请更换真人照片后再提交");
                } else {
                    upload();
                }
                return;
            }
            if (mData.icon_or_self == 3) {
                if (TextUtils.isEmpty(mIconFile)) {
                    hideLoadingDialog();
                    toastTip("请更换头像后再提交");
                } else if (TextUtils.isEmpty(mRealFile)) {
                    hideLoadingDialog();
                    toastTip("请更换真人照片后再提交");
                } else {
                    p.uploadAvatar(mIconFile, mRealFile, true);
                }
            }
        }
    }

    private void upload() {
        if (TextUtils.isEmpty(mIconFile)) { //if没有替换头像,直接上传真人图片
            if (service.getUserInfo() == null) {
                return;
            }
            p.uploadAuthAvatar(service.getUserInfo().getIcon(), mRealFile);
        } else {
            p.uploadAvatar(mIconFile, mRealFile, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Interfaces.EXTRA_REAL_CAMERA) {
                mTvChange2.setText("点击更换");
                String name = data.getStringExtra(Interfaces.EXTRA_PATH);
                if (name != null) {
                    mRealFile = name;
                    mIvPhotoError.setVisibility(View.GONE);
                    GlideUtils.loadRouteImage(this, mIvPhoto, mRealFile);
                    mTvUploadAvatar.setEnabled(mIvAvatarError.getVisibility() == View.GONE);
                }
            }
        }
    }

    private void setUploadSuccess() {
        hideLoadingDialog();
        mStatus = 0;
        mTvStatus.setText("审核中");
        mRlChange1.setVisibility(View.GONE);
        mRlChange2.setVisibility(View.GONE);
        mTvTip.setText("您已提交审核，请耐心等待结果…");
        mTvTip.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        mTvTip.setCompoundDrawablePadding(ConvertUtils.dp2px(0));
        mTvTip.setTextColor(ContextCompat.getColor(this, R.color.green));
        mTvUploadAvatar.setText("我知道了");
        mTvUploadAvatar.setBackgroundResource(R.drawable.sp_primary_r_25);
        mIvAvatarError.setVisibility(View.GONE);
        mIvPhotoError.setVisibility(View.GONE);
    }

    @Override
    public void uploadRealAvatarSuccess(Boolean data) {
        setUploadSuccess();
    }

    @Override
    public void uploadAvatarSuccess(String data) {
        //setUploadSuccess();
    }

    @Override
    public void facedetectCheckSuccess(RealCheckDto data) {

    }

    @Override
    public void recheckSuccess(RealCheckDto data) {

    }
}
