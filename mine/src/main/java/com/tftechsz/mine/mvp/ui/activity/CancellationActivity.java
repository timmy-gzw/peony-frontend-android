package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.AlignTextView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LogoutStatusDto;
import com.tftechsz.mine.mvp.IView.ILogoutView;
import com.tftechsz.mine.mvp.presenter.ILogoutPresenter;
import com.tftechsz.mine.widget.pop.LogoutAgreementPop;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 注销账号
 */
@Route(path = ARouterApi.ACTIVITY_CANCELLATION)
public class CancellationActivity extends BaseMvpActivity<ILogoutView, ILogoutPresenter> implements View.OnClickListener, ILogoutView {

    @Autowired
    UserProviderService service;
    private TextView mBtn;
    private TextView mTvTime;
    private LogoutAgreementPop mLogoutAgreementPop;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int countTime;
    private boolean isLogout;
    private String hint = "1. 无法登录%sAPP；\n\n" +
            "2. 所有信息将被永久删除（动态、圈子等内容），你的好友无法再与你取得联系（包括关注，粉丝，家族等）； \n\n" +
            "3. 绑定手机/微信/QQ账号将会解绑，解绑后可再次注册%s（注册需满足通用规定，如同一手机/微信/QQ账号，90天内只能注册一个%s帐号）； \n\n" +
            "4. 你的实名信息会解绑，180天后可以再次绑定其他%s号；\n\n" +
            "5. 家族长注销后，家族将会被自动解散；\n\n" +
            "6. 如果您是派对房主，则需要取消房主权限后才能注销。\n";
    private String mRepeatMsg;

    @Override
    public ILogoutPresenter initPresenter() {
        return new ILogoutPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("账号注销")
                .build();
        initListener();
        p.getLogoutStatus();
    }

    private void initListener() {
        mBtn = findViewById(R.id.tv_cancellation);
        mTvTime = findViewById(R.id.tv_time);
        findViewById(R.id.tv_cancellation).setOnClickListener(this);
        AlignTextView tvCancelTip = findViewById(R.id.tv_cancel_tip);
        if (service != null && service.getConfigInfo() != null && service.getConfigInfo().sys != null && !TextUtils.isEmpty(service.getConfigInfo().sys.logout_hint)) {
            tvCancelTip.setText(service.getConfigInfo().sys.logout_hint.replace("%s", getString(R.string.app_name)));
        } else {
            tvCancelTip.setText(hint.replace("%s", getString(R.string.app_name)));
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_cancellation;
    }


    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancellation) {  //注销登录
            //toastTip("注销申请已发送,请等待审核结果");
            if (isLogout) {
                p.unDestroyAccount();
                return;
            }
            mLogoutAgreementPop = new LogoutAgreementPop(mContext, agreementUrl, () -> mBtn.postDelayed(() -> {
                if (!TextUtils.isEmpty(mRepeatMsg))
                    getP().showPop(this, mRepeatMsg);
            }, 100));
            mLogoutAgreementPop.showPopupWindow();
        }

    }

    String agreementUrl;

    @Override
    public void getLogoutStatusSuccess(LogoutStatusDto data) {
        if (!TextUtils.isEmpty(data.link)) {
            agreementUrl = data.link;
        }
        if (!TextUtils.isEmpty(data.repeat_msg))
            mRepeatMsg = data.repeat_msg;
        mBtn.setVisibility(View.VISIBLE);
        if (data.count_down > 0) {//注销中,倒计时
            isLogout = true;
            startTimer(data.count_down);
            mTvTime.setText(String.format("注销倒计时: %s", getLastTime(data.count_down)));
            mTvTime.setVisibility(View.VISIBLE);
            mBtn.setText("撤销注销申请");
            mBtn.setTextColor(Utils.getColor(R.color.white));
            mBtn.setBackgroundResource(R.drawable.bg_red);
        } else {
            mBtn.setText("下一步");
            mBtn.setBackgroundResource(R.drawable.bg_orange_enable);
            mBtn.setTextColor(Utils.getColor(R.color.white));
        }

    }


    @Override
    public void destroyAccountSuccess(LogoutStatusDto data) {
        getLogoutStatusSuccess(data);
    }

    @Override
    public void undestroyAccountSuccess(String data) {
        toastTip("解除注销成功");
        stopTimer();
        isLogout = false;
        mBtn.setText("下一步");
        mBtn.setBackgroundResource(R.drawable.bg_orange_enable);
        mBtn.setTextColor(Utils.getColor(R.color.white));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private String getLastTime(int time) {
        int day = time / 86400;
        int h = time / 3600;
        int m = time / 60;
        if (day > 0) {
            return String.format("%s天%s时%s分%s秒", day, h % 24, m % 60, time % 60);
        }
        if (h > 0) {
            return String.format("%s时%s分%s秒", h % 24, m % 60, time % 60);
        }
        if (m > 0) {
            return String.format("%s分%s秒", m % 60, time % 60);
        }
        return String.format("%s秒", time % 60);
    }

    private void startTimer(int count_down) {
        countTime = count_down;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (countTime > 0) {
                    countTime--;
                    mTvTime.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvTime.setText(String.format("注销倒计时: %s", getLastTime(countTime)));
                        }
                    });
                } else {
                    p.getLogoutStatus();
                    stopTimer();
                }
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        mTvTime.setVisibility(View.GONE);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
