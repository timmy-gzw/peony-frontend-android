package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.permissions.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.UploadAvatarPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActivityRealAuthenticationNewBinding;
import com.tftechsz.mine.mvp.IView.IRealAuthView;
import com.tftechsz.mine.mvp.presenter.RealAuthPresenter;

import java.util.List;

/**
 * 真人认证
 */
@Route(path = ARouterApi.ACTIVITY_REAL_AUTHENTICATION_NEW)
public class RealAuthenticationActivityNew extends BaseMvpActivity<IRealAuthView, RealAuthPresenter> implements View.OnClickListener, IRealAuthView {

    @Autowired
    UserProviderService service;
    private ActivityRealAuthenticationNewBinding mBinding;
    private RxPermissions mPermission;
    private UploadAvatarPopWindow mPopWindow;
    private String path = "";  //头像路径
    private boolean isCheck = false;

    @Override
    public RealAuthPresenter initPresenter() {
        return new RealAuthPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("真人认证").build();
        mBinding.btn.setOnClickListener(this);
        mBinding.ivIcon.setOnClickListener(this);
        mBinding.ivCheck.setOnClickListener(this);
        mPermission = new RxPermissions(mActivity);
        initRxBus();

       /* ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) mBinding.ivIcon.getLayoutParams();
        int padding = (ScreenUtils.getScreenWidth()) / 3;
        lp1.setMargins(padding, padding, padding, padding);
        mBinding.ivIcon.setLayoutParams(lp1);
*/
        UserInfo userInfo = service.getUserInfo();
        if (userInfo != null)
            GlideUtils.loadRouteImage(mContext, mBinding.ivIcon, userInfo.getIcon(), userInfo.getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
        p.facedetectCheck();
    }

    @Override
    protected int getLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_real_authentication_new);
        return 0;
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_FINISH_REAL) {
                                finish();
                            }
                        }
                ));
    }


    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) return;
        int id = v.getId();
        if (id == R.id.btn) {
            mCompositeDisposable.add(mPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            startActivity(FaceCheckActivity.class);
                        } else {
                            // TODO: fix this
                            PermissionUtil.showPermissionPop(RealAuthenticationActivityNew.this);
                        }
                    }));
        } else if (id == R.id.iv_icon) {
            mCompositeDisposable.add(mPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            if (mPopWindow == null) {
                                mPopWindow = new UploadAvatarPopWindow(mContext);
                                mPopWindow.addOnClickListener(() -> ChoosePicUtils.picSingle(mActivity, true, new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(List<LocalMedia> result) {
                                        if (result != null && result.size() > 0) {
                                            LocalMedia localMedia = result.get(0);
                                            if (localMedia.isCut()) {
                                                path = localMedia.getCutPath();
                                            } else {
                                                path = localMedia.getPath();
                                            }
                                            mBinding.msg.setText(null);
                                            mBinding.btn.setEnabled(false);
                                            getP().uploadAvatarNew(path);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }

                                }));
                            }
                            mPopWindow.showPopupWindow();

                        } else {
                            Utils.toast("权限被禁止，无法选择本地图片");
                        }
                    }));


        }else if(id == R.id.iv_check){
            isCheck = !isCheck;
            mBinding.ivCheck.setImageResource(isCheck?R.mipmap.ic_check_selector:R.mipmap.ic_check_normal);
            mBinding.btn.setEnabled(isCheck);
        }
    }


    @Override
    public void uploadRealAvatarSuccess(Boolean data) {
    }

    @Override
    public void uploadAvatarSuccess(String data) {
        GlideUtils.loadImage(mContext, mBinding.ivIcon, path);
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        p.facedetectCheck();
    }

    @Override
    public void facedetectCheckSuccess(RealCheckDto data) {
        if (data.pass) {
            mBinding.btn.setEnabled(true);
            mBinding.msg.setText(data.msg);
            mBinding.msg.setTextColor(Utils.getColor(R.color.green));
        } else {
            mBinding.btn.setEnabled(false);
            mBinding.msg.setText(data.msg);
            mBinding.msg.setTextColor(Utils.getColor(R.color.red));
        }
    }

    @Override
    public void recheckSuccess(RealCheckDto data) {

    }
}
