package com.tftechsz.moment.widget;

import android.Manifest;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.ChoosePicUtils;
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
    private boolean isTop;
    private View mNull_view;
    private LinearLayout mRoot;
    private CompositeDisposable mCompositeDisposable;

    public SendTrendPop(Activity mActivity, boolean isTop) {
        super(mActivity);
        mCompositeDisposable = new CompositeDisposable();
        this.mActivity = mActivity;
        this.isTop = isTop;
        initUI();
    }

    public void setBotLayoutParams(int bottomHeight) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mNull_view.getLayoutParams();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRoot.getLayoutParams();
        if (isTop) {
            lp.topMargin = ConvertUtils.dp2px(6);
        } else {
            layoutParams.height = bottomHeight + ConvertUtils.dp2px(6);
            mNull_view.setLayoutParams(layoutParams);
            setPopupGravity(Gravity.BOTTOM | Gravity.END);
            //lp.bottomMargin = ConvertUtils.dp2px(120);
        }
        lp.rightMargin = ConvertUtils.dp2px(20);
        mRoot.setLayoutParams(lp);

    }

    private void initUI() {
        mRoot = findViewById(R.id.ll_rot);
        mNull_view = findViewById(R.id.null_view);
        mNull_view.setOnClickListener(v -> dismiss());
        mRoot.setBackgroundResource(isTop ? R.mipmap.pop_send_tred_bg_top : R.mipmap.pop_send_tred_bg_bot);
        if (isTop) {
            setBotLayoutParams(0);
        }
        findViewById(R.id.type_pic).setOnClickListener(v -> {
            mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            dismiss();
                            SPUtils.getInstance().put(Interfaces.SP_SEL_VIDEO, false);
                            ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST);
                        } else {
                            Utils.toast("请允许摄像头权限");
                        }
                    }));
        });
        findViewById(R.id.type_mp4).setOnClickListener(v -> {
            mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            dismiss();
                            SPUtils.getInstance().put(Interfaces.SP_SEL_VIDEO, true);
                            ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
                        } else {
                            Utils.toast("请允许摄像头权限");
                        }
                    }));
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
