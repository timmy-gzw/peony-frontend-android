package com.tftechsz.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionOwner;
import com.gyf.immersionbar.components.SimpleImmersionProxy;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.LoadingDialog;

import io.reactivex.disposables.CompositeDisposable;

import static com.tftechsz.common.utils.Utils.runOnUiThread;

public abstract class BaseFragment extends RxFragment implements MvpView, SimpleImmersionOwner {

    private final String TAG = getClass().getSimpleName();
    protected View rootView;
    public Context mContext;
    public Activity mActivity;
    public LoadingDialog loadingDialog;
    private View baseTitle;
    private boolean isFirst = true;
    protected CompositeDisposable mCompositeDisposable;
    public int mPage = 1;
    public SmartRefreshLayout mSmartRefreshLayout;

    private static final int MIN_CLICK_DELAY_TIME = 400;
    private static long lastClickTime;
    private ViewDataBinding mBindLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).init();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            if (getLayout() > 0) {
                rootView = inflater.inflate(getLayout(), container, false);
            } else {
                mBindLayout = DataBindingUtil.inflate(inflater, getBindLayout(), container, false);
                if (mBindLayout != null) {
                    rootView = mBindLayout.getRoot();
                }
            }
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        ARouter.getInstance().inject(this);
        if (getImmersionBar()) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true, 0.2f)
                    .navigationBarDarkIcon(true)
                    .navigationBarColor(R.color.white)
                    .init();
        }
    }


    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        baseTitle = v.findViewById(R.id.base_tool_bar);
        initView(v);
    }


    public void initView(View v) {
    }


    public void hideToolBar() {
        if (null != baseTitle) {
            baseTitle.setVisibility(View.GONE);
        }
    }

    public void showLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(getActivity());
            }
            loadingDialog.show();
            loadingDialog.setProgressPercent("");
        });

    }

    public boolean isLoadingDialogShow() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    public void hideLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.setProgressPercent("");
                loadingDialog.dismiss();
            }
        });

    }

    @Override
    public void toastTip(final String msg) {
        ToastUtil.showToast(BaseApplication.getInstance(), msg);
    }

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initImmersionBar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    public void setTitleBackground(int color) {
        ColorDrawable d = new ColorDrawable(color);
        baseTitle.setBackground(d);
    }


    protected boolean getImmersionBar() {
        return true;
    }


    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    private void fitsLayoutOverlap() {
        if (baseTitle != null) {
            ImmersionBar.with(this).titleBar(baseTitle);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSimpleImmersionProxy.onDestroy();
        ImmersionBar.destroy(this);
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    public View getListEmptyView() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.base_empty_view, null, false);
        return v;
    }


    public void startActivity(Class<?> targetClass) {
        Intent intent = new Intent(getActivity(), targetClass);
        startActivity(intent);
    }

    public void startActivity(Class<?> targetClass, String name, String value) {
        Intent intent = new Intent(getActivity(), targetClass);
        intent.putExtra(name, value);
        startActivity(intent);
    }


    /**
     * ImmersionBar代理类
     */
    private final SimpleImmersionProxy mSimpleImmersionProxy = new SimpleImmersionProxy(this);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mSimpleImmersionProxy.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState);
        if (isFirst) {
            initUI(savedInstanceState);
        }
        isFirst = false;
        if (null != baseTitle && getImmersionBar())
            fitsLayoutOverlap();
        initData();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mSimpleImmersionProxy.onHiddenChanged(hidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSimpleImmersionProxy.onConfigurationChanged(newConfig);
    }


    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(int id) {
        try {
            return (E) rootView.findViewById(id);
        } catch (ClassCastException ex) {
            Log.e(TAG, "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    public String getName() {
        return BaseFragment.class.getName();
    }

    protected abstract int getLayout();

    public int getBindLayout() {
        return 0;
    }

    public ViewDataBinding getBind() {
        return mBindLayout;
    }

    public abstract void initUI(Bundle savedInstanceState);

    protected abstract void initData();


    public class ToolBarBuilder {
        public ImageView backBtn;
        private final TextView titleTv;
        private ImageView right1Iv;
        private final ImageView right2Iv;
        private final TextView rightTv;
        private String title;
        private int titleColor;
        private int right1Res = -1, right2Res = -1;
        private View.OnClickListener right1Listener, right2Listener, rightTextListener;
        private String rightText;


        public ToolBarBuilder() {
            if (null == baseTitle) {
                throw new RuntimeException("没有 include base title");
            } else {
                backBtn = baseTitle.findViewById(R.id.toolbar_back_all);
                titleTv = baseTitle.findViewById(R.id.toolbar_title);
                right2Iv = baseTitle.findViewById(R.id.toolbar_iv_menu);
                rightTv = baseTitle.findViewById(R.id.toolbar_tv_menu);
            }
        }

        public ToolBarBuilder showBack() {

            return this;
        }

        public ToolBarBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ToolBarBuilder setTitleColor(int color) {
            this.titleColor = color;
            return this;
        }

        public ToolBarBuilder setRight1Img(int res, View.OnClickListener l) {
            right1Res = res;
            right1Listener = l;
            return this;
        }

        public ToolBarBuilder setRight2Img(int res, View.OnClickListener l) {
            right2Res = res;
            right2Listener = l;
            return this;
        }

        public ToolBarBuilder setRightText(String res, View.OnClickListener l) {
            rightText = res;
            rightTextListener = l;
            return this;
        }

        public void build() {
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
            }
            if (titleColor > 0) {
                titleTv.setTextColor(getResources().getColor(titleColor));
            }
            if (right1Res > 0) {
                right1Iv.setImageResource(right1Res);
                right1Iv.setOnClickListener(right1Listener);
            }
            if (right2Res > 0) {
                right2Iv.setImageResource(right2Res);
                right2Iv.setOnClickListener(right2Listener);
            }
            if (!TextUtils.isEmpty(rightText)) {
                rightTv.setText(rightText);
                rightTv.setOnClickListener(rightTextListener);
            }

        }
    }

}
