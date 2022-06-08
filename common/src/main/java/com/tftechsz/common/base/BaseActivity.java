package com.tftechsz.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.R;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.KeyBoardUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.LoadingDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.disposables.CompositeDisposable;


public abstract class BaseActivity extends RxAppCompatActivity implements MvpView {


    protected final String TAG = getClass().getSimpleName();

    private LoadingDialog mLoadingDialog;
    protected View baseTitle;
    protected CompositeDisposable mCompositeDisposable;
    public int mPageSize = 20;
    public int mPage = 1;
    public LinearLayout mLlEmpty;   // 错误页面
    public TextView mTvEmpty;
    public Activity mActivity;
    public Context mContext;
    private long lastClickTime;
    //dialog样式不能设置 SCREEN_ORIENTATION_PORTRAIT
    public boolean mFlagIsPortrait = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BaseApplication.APP_STATUS != BaseApplication.APP_STATUS_NORMAL) { // 非正常启动流程，直接重新初始化应用界面
            AppUtils.relaunchApp(true);
            return;
        }
        mActivity = this;
        mContext = this;
        if (mFlagIsPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        AppManager.getAppManager().addActivity(this);
        int layout = getLayout();
        if (layout > 0) {
            setContentView(layout);
        }
        if (getImmersionBar()) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true, 0.2f)
                    .statusBarColor(R.color.white)
                    .navigationBarColor(R.color.white)
                    .navigationBarDarkIcon(true)
                    .init();
        }
        baseTitle = findViewById(R.id.base_tool_bar);
        if (null != baseTitle && getImmersionBar()) {
            fitsLayoutOverlap();
        }
        ARouter.getInstance().inject(this);
        // 该方法是【友盟+】Push后台进行日活统计及多维度推送的必调用方法，请务必调用！
//        PushAgent.getInstance(this).onAppStart();
        mCompositeDisposable = new CompositeDisposable();
    }

    protected abstract int getLayout();

    protected abstract void initView(Bundle savedInstanceState);

    protected void initData() {
    }

    protected boolean getImmersionBar() {
        return true;
    }

    private void fitsLayoutOverlap() {
        ImmersionBar.setTitleBar(this, baseTitle);
    }

    public void setTitleBackground(int color) {
        ColorDrawable d = new ColorDrawable(color);
        if (baseTitle != null) baseTitle.setBackground(d);
    }


    @Override
    public void showLoadingDialog() {
        runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "正在加载中,请稍后..."));
    }

    @Override
    public boolean isLoadingDialogShow() {
        return GlobalDialogManager.getInstance().isShow();
    }


    @Override
    public void hideLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalDialogManager.getInstance().dismiss();
            }
        });

    }

    @Override
    public void toastTip(final String msg) {
        ToastUtil.showToast(this, msg);
    }

    public void startActivity(Class<?> targetClass) {
        Intent intent = new Intent(BaseActivity.this, targetClass);
        startActivity(intent);
    }

    public void startActivity(Class<?> targetClass, String name, String value) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtra(name, value);
        startActivity(intent);
    }


    public View getListEmptyView() {
        View v = LayoutInflater.from(this).inflate(R.layout.base_empty_view, null, false);
        return v;
    }

    public class ToolBarBuilder {
        public ImageView backBtn;
        public TextView titleTv;
        public ImageView rightIv;
        public TextView rightTv;
        private String title;
        private int rightRes = -1;
        private View.OnClickListener rightListener, rightTextListener;
        private String rightText;
        private boolean isBackShow;
        private boolean isRightImgShow, isRightTxtShow = true;
        private int backgroundColor = -1, titleTextColor, rightTextColor, rightTextBg;
        private int backRes = -1;

        public ToolBarBuilder() {
            if (null == baseTitle) {
                throw new RuntimeException("没有 include base title");
            } else {
                backBtn = findViewById(R.id.toolbar_back_all);
                titleTv = findViewById(R.id.toolbar_title);
                rightIv = findViewById(R.id.toolbar_iv_menu);
                rightTv = findViewById(R.id.toolbar_tv_menu);
            }
        }

        public ToolBarBuilder showBack(boolean isShow) {
            isBackShow = isShow;
            return this;
        }

        public ToolBarBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ToolBarBuilder setBackImg(int res) {
            this.backBtn.setImageResource(res);
            return this;
        }

        public ToolBarBuilder setTitleColor(int color) {
            this.titleTextColor = color;
            return this;
        }

        public ToolBarBuilder setRightTextColor(int color) {
            this.rightTextColor = color;
            return this;
        }

        public ToolBarBuilder setRightTextBackground(@DrawableRes int color) {
            this.rightTextBg = color;
            return this;
        }


        public ToolBarBuilder setRightImg(int res, View.OnClickListener l) {
            rightRes = res;
            rightListener = l;
            return this;
        }

        public ToolBarBuilder showRightImg(boolean isShow) {
            isRightImgShow = isShow;
            return this;
        }

        public ToolBarBuilder showRightTxt(boolean isShow) {
            isRightTxtShow = isShow;
            return this;
        }

        public ToolBarBuilder setRightText(String res, View.OnClickListener l) {
            rightText = res;
            rightTextListener = l;
            return this;
        }

        public ToolBarBuilder setBackIcon(int res) {
            backRes = res;
            return this;
        }

        public void build() {
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
            }
            if (backgroundColor >= 0) {
                baseTitle.setBackgroundResource(0);
            }
            rightTv.setVisibility(isRightTxtShow ? View.VISIBLE : View.GONE);
            rightIv.setVisibility(isRightImgShow ? View.VISIBLE : View.GONE);
            if (titleTextColor > 0) {
                titleTv.setTextColor(getResources().getColor(titleTextColor));
            }
            if (rightRes > 0) {
                rightIv.setImageResource(rightRes);
                rightIv.setOnClickListener(rightListener);
                rightIv.setVisibility(View.VISIBLE);
            }
            if (rightTextColor > 0) {
                rightTv.setTextColor(getResources().getColor(rightTextColor));
            }
            if (!TextUtils.isEmpty(rightText)) {
                rightTv.setText(rightText);
                rightTv.setOnClickListener(rightTextListener);
                if (rightTextBg > 0) {
                    rightTv.setBackgroundResource(rightTextBg);
                }
            }

            if (isBackShow) {
                backBtn.setVisibility(View.VISIBLE);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
            if (backRes != -1) {
                backBtn.setImageResource(backRes);
            }
        }

        public ToolBarBuilder setBackgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }
    }


    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        int MIN_CLICK_DELAY_TIME = 500;
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        KeyBoardUtil.closeKeyboard(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppManager.getAppManager().removeActivity(this);
            if (mLoadingDialog != null) {
                hideLoadingDialog();
                mLoadingDialog = null;
            }
            ImmersionBar.destroy(this, null);
            if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                mCompositeDisposable.dispose();
                mCompositeDisposable.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
    }

    //让自己的应用不会随系统字体大小而变化,始终保持一致
   /* @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }*/
}
