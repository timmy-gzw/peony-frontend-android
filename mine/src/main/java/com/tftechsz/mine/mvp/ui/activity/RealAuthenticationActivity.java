package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.config.PictureConfig;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.adapter.RealPopAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.mine.R;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.mine.mvp.IView.IRealAuthView;
import com.tftechsz.mine.mvp.presenter.RealAuthPresenter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 真人认证
 */
@Route(path = ARouterApi.ACTIVITY_REAL_AUTHENTICATION)
public class RealAuthenticationActivity extends BaseMvpActivity<IRealAuthView, RealAuthPresenter> implements View.OnClickListener, IRealAuthView {


    @Autowired
    UserProviderService service;
    private String mPath;
    private RxPermissions mRxPermissions;
    private TextView mRelaTips;

    @Override
    public RealAuthPresenter initPresenter() {
        return new RealAuthPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .build();
        ImageView ivAvatar = findViewById(com.tftechsz.common.R.id.iv_true_avatar);
        mRelaTips = findViewById(R.id.rela_tips);
        mRelaTips.setVisibility(service.getUserInfo().isBoy() ? View.GONE : View.VISIBLE);
        RecyclerView recy = findViewById(R.id.real_recy);
        ConfigInfo configInfo = service.getConfigInfo();
        if (service.getUserInfo().isGirl() && configInfo.sys.content.real_icon != null && configInfo.sys.content.real_icon.size() > 0) {
            recy.setVisibility(View.VISIBLE);
            int size = configInfo.sys.content.real_icon.size();
            recy.setLayoutManager(new GridLayoutManager(mContext, Math.min(size, 4)));
            RealPopAdapter adapter = new RealPopAdapter(configInfo.sys.content.real_icon);
            recy.setAdapter(adapter);
        }

        ivAvatar.setBackgroundResource(service.getUserInfo().isBoy() ? R.mipmap.ic_boy_true_avatar : R.mipmap.ic_true_avatar);
        findViewById(R.id.tv_authentication).setOnClickListener(this);

        TextView tops = findViewById(R.id.tv_tip);
        tops.setText(new SpannableStringUtils.Builder()
                .append(service.getUserInfo().isGirl() ? "- 请露出正脸和上半身，并保持手戳脸姿势拍照" : "- 请露出正脸和上半身")
                .append("\n- 请保证头像和真人认证为同一人")
                .append("\n- 不满足上述认证要求将被驳回真人认证")
                .append("\n- 拍摄图片仅用做认证，官方将对图片保密")
                .create()
        );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_real_authentication;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_authentication) {   //真人认证
            //ChoosePicUtils.takePhoto(this);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
               /* String path = "";
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (null != selectList && selectList.size() > 0) {
                    if (selectList.get(0).isCompressed()) {
                        path = selectList.get(0).getCompressPath();
                    } else {
                        path = selectList.get(0).getPath();
                    }
                }
                mPath = path;
                showLoadingDialog();
                p.uploadAuthAvatar(path);*/
            } else if (requestCode == Interfaces.EXTRA_REAL_CAMERA) {
                if (data != null) {
                    String name = data.getStringExtra(Interfaces.EXTRA_PATH);
                    mPath = name;
                    ARouterUtils.toRealAuthentication(-1, mPath, null);
                    finish();
                    //showLoadingDialog();
                    //p.uploadAuthAvatar(name);
                }
            }
        }
    }

    @Override
    public void uploadRealAvatarSuccess(Boolean data) {
        hideLoadingDialog();
        ARouterUtils.toRealAuthentication(0, mPath, null);
    }

    @Override
    public void uploadAvatarSuccess(String data) {
    }

    @Override
    public void facedetectCheckSuccess(RealCheckDto data) {

    }

    @Override
    public void recheckSuccess(RealCheckDto data) {

    }
}
