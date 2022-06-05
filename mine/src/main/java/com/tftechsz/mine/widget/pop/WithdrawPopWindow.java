package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.common.entity.WithdrawReq;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.basepopup.BasePopupWindow;

/**
 * 提现确认弹窗
 */
public class WithdrawPopWindow extends BasePopupWindow implements View.OnClickListener {
    public MineApiService service;
    private TextView mTvAccount, mTvPhone;
    protected CompositeDisposable mCompositeDisposable;
    public MineApiService userService;

    public WithdrawPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        service = RetrofitManager.getInstance().createExchApi(MineApiService.class);
        userService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mTvAccount = findViewById(R.id.tv_account);
        mTvPhone = findViewById(R.id.tv_phone);
        findViewById(R.id.tv_withdraw).setOnClickListener(this);
        findViewById(R.id.tv_update_account).setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        withdrawWay();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_withdraw);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.tv_withdraw) {   //提现
            if (listener != null)
                listener.reportUser();
            dismiss();
        } else if (id == R.id.tv_update_account) {   //修改账号
            if (listener != null)
                listener.reportUser();
            dismiss();
        }
    }


    /**
     * 支付宝申请提现
     */
    public void withdraw(int typeId, String name, String account, String card, String phone) {
        WithdrawReq withdrawReq = new WithdrawReq();
        withdrawReq.type_id = typeId;
        WithdrawReq.Withdraw withdraw = new WithdrawReq.Withdraw();
        withdraw.account = account;
        withdraw.name = name;
        withdraw.identity = card;
        withdraw.phone = phone;
        withdrawReq.withdraw = withdraw;
        mCompositeDisposable.add(service.withdraw(withdrawReq).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {

                        dismiss();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 提现方式获取
     */
    public void withdrawWay() {
        mCompositeDisposable.add(userService.withdrawWay().compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<WithdrawReq.Withdraw>>() {
                    @Override
                    public void onSuccess(BaseResponse<WithdrawReq.Withdraw> response) {
                        if (response.getData() != null) {
                            mTvAccount.setText(response.getData().phone);
                        }

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    public interface OnSelectListener {
        void reportUser();

        void blackUser();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
