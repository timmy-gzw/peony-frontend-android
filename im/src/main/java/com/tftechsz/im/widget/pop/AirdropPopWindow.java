package com.tftechsz.im.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.AirdropAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.AirdropDto;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.common.widget.pop.RechargeBeforePop;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 召唤空投
 */
public class AirdropPopWindow extends BaseBottomPop implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private TextView mTvBalance;
    private String mCoin;//金币
    private AirdropAdapter mAdapter;
    private AirdropDto dto;
    private final UserProviderService userService;
    private ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;
    private RechargePopWindow rechargePopWindow;
    private RechargeBeforePop beforePop;
    private int airdropId;
    private int rule = 0, time = 5;
    private int mTeamId;

    public AirdropPopWindow(Context context, int teamId) {
        super(context);
        this.mTeamId = teamId;
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }

    private void initUI() {
        RadioGroup rvCondition = findViewById(R.id.rg_condition);
        rvCondition.setOnCheckedChangeListener(this);
        RadioGroup rgTime = findViewById(R.id.rg_time);
        rgTime.setOnCheckedChangeListener(this);
        findViewById(R.id.iv_prestige).setOnClickListener(this);
        findViewById(R.id.tv_airdrop).setOnClickListener(this);
        RecyclerView mRvShortPosition = findViewById(R.id.rv_short_position);
        findViewById(R.id.tv_recharge).setOnClickListener(this);
        mTvBalance = findViewById(R.id.tv_balance);
        mRvShortPosition.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AirdropAdapter();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            mAdapter.setCheckPositions(position);
            airdropId = mAdapter.getData().get(position).id;
            dto = mAdapter.getData().get(position);
        });
        mRvShortPosition.setAdapter(mAdapter);
        mAdapter.addOnClickListener((id, position) -> {
            airdropId = id;
            dto = mAdapter.getData().get(position);
        });
        getAirdrop();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_airdrop);
    }


    public void setCoin(String coin) {
        mCoin = coin;
        mTvBalance.setText(String.format("金币余额：%s金币", coin));
    }

    public void getCoin() {
        userService.getField("property", "coin", new ResponseObserver<BaseResponse<IntegralDto>>() {
            @Override
            public void onSuccess(BaseResponse<IntegralDto> response) {
                if (response.getData() != null) {
                    UserInfo userInfo = userService.getUserInfo();
                    userInfo.setCoin(response.getData().coin);
                    userService.setUserInfo(userInfo);
                    setCoin(response.getData().coin);
                }
            }
        });
    }


    /**
     * 获取空投信息
     */
    public void getAirdrop() {
        mCompositeDisposable.add(chatApiService.getAirdrop().compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<AirdropDto>>>() {
            @Override
            public void onSuccess(BaseResponse<List<AirdropDto>> response) {
                if (response.getData() != null && response.getData().size() > 0) {
                    mAdapter.setList(response.getData());
                    airdropId = response.getData().get(0).id;
                    dto = response.getData().get(0);
                }
            }
        }));
    }

    /**
     * 发送空投信息
     */
    public void sendAirdrop() {
        mCompositeDisposable.add(chatApiService.sendAirdrop(airdropId, rule, time).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (response.getData() != null) {
                    dismiss();
                }
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_prestige) { //协议
            AirdropAgreementPopWindow popWindow = new AirdropAgreementPopWindow(mContext);
            popWindow.showPopupWindow();
        } else if (id == R.id.tv_recharge) { //去充值
            ARouterUtils.toRechargeActivity();
        } else if (id == R.id.tv_airdrop) {  // 发送空投
            if (dto == null) return;
            double selfCoin = TextUtils.isEmpty(mCoin) ? 0 : Double.parseDouble(mCoin);
            if (dto.coin > selfCoin) {
                if (userService.getConfigInfo() != null && userService.getConfigInfo().share_config != null && userService.getConfigInfo().share_config.is_limit_from_channel == 1) {
                    if (beforePop == null)
                        beforePop = new RechargeBeforePop(mContext);
                    beforePop.addOnClickListener(() -> {
                        if (null == rechargePopWindow)
                            rechargePopWindow = new RechargePopWindow((Activity) mContext, 1, 2);
                        rechargePopWindow.getCoin();
                        rechargePopWindow.requestData();
                        rechargePopWindow.setFormType(1);
                        rechargePopWindow.showPopupWindow();
                    });
                    beforePop.showPopupWindow();
                } else {
                    if (null == rechargePopWindow)
                        rechargePopWindow = new RechargePopWindow((Activity) mContext, 1, 2);
                    rechargePopWindow.getCoin();
                    rechargePopWindow.requestData();
                    rechargePopWindow.setFormType(1);
                    rechargePopWindow.showPopupWindow();
                }
                return;
            }
            sendAirdrop();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_condition) {
            if (checkedId == R.id.rb_all) {
                rule = 0;
            } else if (checkedId == R.id.rb_boy) {
                rule = 1;
            } else if (checkedId == R.id.rb_girl) {
                rule = 2;
            }
        } else if (group.getId() == R.id.rg_time) {
            if (checkedId == R.id.rb_five) {
                time = 5;
            } else if (checkedId == R.id.rb_now) {
                time = 0;
            }
        }
    }

    public interface OnSelectListener {
        void deleteContact();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
