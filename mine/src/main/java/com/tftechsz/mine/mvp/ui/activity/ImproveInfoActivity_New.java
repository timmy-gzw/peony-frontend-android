package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ScreenUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.chat.NoScrollViewPager;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.ImproveInfoEvent;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.mine.mvp.IView.IImproveInfoView;
import com.tftechsz.mine.mvp.presenter.ImproveInfoPresenter;
import com.tftechsz.mine.mvp.ui.fragment.ImproveInfoFragment_1;
import com.tftechsz.mine.mvp.ui.fragment.ImproveInfoFragment_2;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


/**
 * 完善个人信息
 */
public class ImproveInfoActivity_New extends BaseMvpActivity<IImproveInfoView, ImproveInfoPresenter> implements View.OnClickListener, IImproveInfoView, ViewPager.OnPageChangeListener {
    private EditText mEtCode;
    private ImageView mRedQp;
    private TextView mTvNext;
    private NoScrollViewPager mViewPager;
    List<Fragment> fragments = new ArrayList<>();
    private ToolBarBuilder mToolBarBuilder;
    private TextView mTvTopTips;
    private int clickPos;//1:选择了女  2:选择了南
    private CompleteReq mCompleteReq;  //用户信息
    private int mType;   //登录类型   0 手机号  1 微信 2 QQ
    private int currentIndex; //当前处于哪一页
    private ImproveInfoFragment_1 mImproveInfoFragment_1;
    private ImproveInfoFragment_2 mImproveInfoFragment_2;
    private String mAvatar, mEtName;
    private RelativeLayout mRootView;

    @Override
    public ImproveInfoPresenter initPresenter() {
        return new ImproveInfoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mToolBarBuilder = new ToolBarBuilder();
        mToolBarBuilder.showBack(true)
                .setTitle("选择性别")
                .build();
        mRootView = findViewById(R.id.rl_complete);
        mViewPager = findViewById(R.id.noscroll);
        mEtCode = findViewById(R.id.edt_code);
        mTvNext = findViewById(R.id.tv_next);
        mTvTopTips = findViewById(R.id.tv_top_tips);
        mRedQp = findViewById(R.id.iv_red_qp);
        mTvNext.setOnClickListener(this);
        mRedQp.setVisibility(View.GONE);
        mImproveInfoFragment_1 = new ImproveInfoFragment_1();
        mImproveInfoFragment_2 = new ImproveInfoFragment_2();
        fragments.add(mImproveInfoFragment_1);
        fragments.add(mImproveInfoFragment_2);
        FragmentVpAdapter myFragmentPagerAdapter = new FragmentVpAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        initRxBus();

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            mToolBarBuilder.setTitle("选择性别")
                    .build();
            mTvTopTips.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(0, true);
            KeyboardUtils.hideSoftInput(this);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void initData() {
        Utils.setFocus(mTvTopTips);
        mCompleteReq = new CompleteReq();
        mCompleteReq.sex = 1;
        mType = getIntent().getIntExtra("type", 0);

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCompleteReq.user_code = s.toString();
            }
        });

        mEtCode.postDelayed(() -> mEtCode.setText(p.getReferralCode(mActivity)), 100);
        if (mType != 0) {
            p.getCompleteInfo();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_improve_info_new;
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(ImproveInfoEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            switch (event.eventType) {
                                case 0:
                                    clickPos = event.clickPos;
                                    if (event.clickPos == 1) {
                                        mCompleteReq.sex = 2;
                                        mTvNext.setText("下一步");
                                    } else {
                                        mCompleteReq.sex = 1;
                                        mTvNext.setText("开启芍药之旅");
                                    }
                                    break;

                                case 1://键盘弹起关闭
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTvNext.getLayoutParams();
                                    if (event.isPopup) {
                                        lp.height = 0;
                                        lp.bottomMargin = 0;
                                    } else {
                                        lp.height = Utils.dp2px(this, 49);
                                        lp.bottomMargin = Utils.dp2px(this, 46);
                                    }
                                    mTvNext.setLayoutParams(lp);
                                    break;

                                case 2: //配置信息
                                    mCompleteReq.icon = event.icon;
                                    mCompleteReq.nickname = event.nickname;
                                    mCompleteReq.birthday = event.birthday;
                                    checknoNullInfo();
                                    break;
                            }
                        }
                ));
    }

    private void checknoNullInfo() {
        if (currentIndex == 1) {
            if (!TextUtils.isEmpty(mCompleteReq.icon)
                    && !TextUtils.isEmpty(mCompleteReq.nickname)
                    && !TextUtils.isEmpty(mCompleteReq.birthday)) {
                mTvNext.setEnabled(true);
            } else {
                mTvNext.setEnabled(false);
            }
        } else {
            mTvNext.setEnabled(true);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_next) {   //下一步
            if (currentIndex == 0) {
                if (clickPos == 1) { //女
                    mToolBarBuilder.setTitle("完善资料").build();
                    mTvTopTips.setVisibility(View.GONE);
                    mTvNext.setVisibility(View.VISIBLE);
                    mViewPager.setCurrentItem(1, true);
                    if (mType == 0) {
                        mImproveInfoFragment_2.setInfo(mCompleteReq.icon, mCompleteReq.nickname);
                    }
                } else {//男
                    p.completeInfo(mCompleteReq);
                }
            } else { //第二页
                p.completeInfo(mCompleteReq);
            }
        }
    }

    @Override
    public void chooseBirthday(String day) {

    }

    /**
     * 完善信息成功
     */
    @Override
    public void completeInfoSuccess(String msg) {
        MobclickAgent.onEvent(ImproveInfoActivity_New.this, "user_register");
        toastTip(msg);
        SPUtils.put(Constants.IS_COMPLETE_INFO, 1);
//        /**
//         * 字节跳动推广新注册用户
//         */
//        JSONObject object = new JSONObject();
//        try {
//            object.put("user_register", UserManager.getInstance().getUserId());
//            GameReportHelper.onEventRegister(object.toString(),true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        p.getUserInfo(this);
    }

    /**
     * 设置获取的个人信息
     */
    @Override
    public void getCompleteInfoSuccess(CompleteReq data) {
        if (null != data) {
            mCompleteReq = data;
            if (mCompleteReq.sex == 0) {   // 未返回性别时默认女
                mCompleteReq.sex = 2;
                mImproveInfoFragment_1.selectIcon(1);
            } else {
                mImproveInfoFragment_1.selectIcon(2);
            }
            mAvatar = mCompleteReq.icon;
            mEtName = mCompleteReq.nickname;
            mImproveInfoFragment_2.setInfo(mAvatar, mEtName);
        }
    }

    @Override
    public void uploadAvatarSucceeded(String url) {

    }

    @Override
    public void getRandomNicknameSucceeded(String nikename) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        checknoNullInfo();
        if (position == 0) {
            mTvNext.setText("下一步");
            mRedQp.setVisibility(View.GONE);
            mEtCode.setVisibility(View.VISIBLE);
        } else {
            KeyboardUtils.hideSoftInput(this);
            mTvNext.setText("开启芍药之旅");
            mRedQp.setVisibility(View.VISIBLE);
            mEtCode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) { //点击外部
                KeyboardUtils.hideSoftInput(this);
                Utils.setFocus(mTvTopTips);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            mEtCode.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = ScreenUtils.getScreenWidth(this);

            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
}
