package com.tftechsz.moment.widget;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.SPUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.moment.R;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.basepopup.BasePopupWindow;

/**
 * 包 名 : com.tftechsz.moment.widget
 * 描 述 : TODO
 */
public class SendTrendPop extends BasePopupWindow {
    private final Activity mActivity;
    private final boolean isTop;
    private View mNull_view;
    private LinearLayout mRoot;
    private final CompositeDisposable mCompositeDisposable;

    public SendTrendPop(Activity mActivity, boolean isTop) {
        super(mActivity);
        mCompositeDisposable = new CompositeDisposable();
        this.mActivity = mActivity;
        this.isTop = isTop;
        initUI();
    }

    private void initUI() {
        mRoot = findViewById(R.id.ll_rot);
        mNull_view = findViewById(R.id.null_view);
        mNull_view.setOnClickListener(v -> dismiss());
        mRoot.setBackgroundResource(isTop ? R.mipmap.pop_send_tred_bg_top : R.mipmap.pop_send_tred_bg_bot);
        findViewById(R.id.type_pic).setOnClickListener(v -> {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(mActivity, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    dismiss();
                                    SPUtils.getInstance().put(Interfaces.SP_SEL_VIDEO, false);
                                    ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST);
                                } else {
                                    PermissionUtil.showPermissionPop(mActivity, mActivity.getString(R.string.chat_open_storage_camera_permission));
                                }
                            }));
                }
            });
        });
        findViewById(R.id.type_mp4).setOnClickListener(v -> {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(mActivity, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    dismiss();
                                    SPUtils.getInstance().put(Interfaces.SP_SEL_VIDEO, true);
                                    ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
                                } else {
                                    PermissionUtil.showPermissionPop(mActivity, mActivity.getString(R.string.chat_open_storage_camera_permission));
                                }
                            }));
                }

            });
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null)
            mCompositeDisposable.clear();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_send_trend_type);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return isTop ? Utils.getTopScaleAnimation(0.6f, 0.0f, true) : null;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return isTop ? Utils.getTopScaleAnimation(0.6f, 0.0f, false) : null;
       /* return AnimationHelper.asAnimation()
                .withTranslation(isTop ? TranslationConfig.TO_TOP : TranslationConfig.TO_BOTTOM)
                .toDismiss();*/
    }
}
