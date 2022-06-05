package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ScreenUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.RedEnvelopeDetailsAdapter;
import com.tftechsz.common.entity.RedOpenDetails;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  红包领取详情
 */
public class RedEnvelopeDetailsPopWindow extends BaseBottomPop implements View.OnClickListener {

    private int red_packet_id;
    private final PublicService service;
    protected CompositeDisposable mCompositeDisposable;
    private Context mContext;
    UserProviderService mUserProviderService;
    private ImageView mIvIcon;
    private TextView mTvName, mTvTitle;
    private RecyclerView mRecy;
    private TextView mTvReceived;
    private TextView mTvGold;
    private RedEnvelopeDetailsAdapter mAdapter;
    private TextView mTvUnit;
    private TextView mTvMyGold;

    public RedEnvelopeDetailsPopWindow(Context context) {
        super(context);
        this.mContext = context;
        service = RetrofitManager.getInstance().createExchApi(PublicService.class);
        mUserProviderService = ARouter.getInstance().navigation(UserProviderService.class);
        mCompositeDisposable = new CompositeDisposable();
        setOutSideDismiss(true);
        initUI();
    }

    public void showPopup(int red_packet_id) {
        if (isShowing()) {
            return;
        }
        this.red_packet_id = red_packet_id;
        getRedRnvelopeDetails();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_red_envelope_details);
    }

    private void initUI() {
        mIvIcon = findViewById(R.id.icon);
        mTvName = findViewById(R.id.form_name);
        mTvTitle = findViewById(R.id.tv_title); // 标题
        mTvGold = findViewById(R.id.tv_gold);// 金币
        mTvUnit = findViewById(R.id.gold_unit);// 单位
        mTvMyGold = findViewById(R.id.my_gold);
        findViewById(R.id.ic_close).setOnClickListener(this);
        findViewById(R.id.my_gold).setOnClickListener(this); // 我的金币
        mTvReceived = findViewById(R.id.tv_received);// 已领取
        mRecy = findViewById(R.id.recycleview); // 领取列表

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mRecy.getLayoutParams();
        lp.height = (int) (ScreenUtils.getScreenHeight() / 2.3);
        mRecy.setLayoutParams(lp);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.my_gold) { //我的金币
            ARouterUtils.toIntegralDetailedActivity(1);
        } else if (id == R.id.ic_close) { //返回
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    private void getRedRnvelopeDetails() {
        if (red_packet_id != 0) {
            mCompositeDisposable.add(service.openList(red_packet_id).compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<RedOpenDetails>>() {
                        @Override
                        public void onSuccess(BaseResponse<RedOpenDetails> response) {
                            setData(response.getData());
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);
                        }
                    }));
        }
    }

    private void setData(RedOpenDetails data) {
        GlideUtils.loadRoundImage(mContext, mIvIcon, data.icon, 3);
        mTvName.setText(String.format("%s的红包", data.nickname));
        mTvTitle.setText(data.des);
        if (data.is_receive == 1) { //当前用户是否领取过
            mTvGold.setText(String.valueOf(data.get_coin));
            mTvGold.setVisibility(View.VISIBLE);
            mTvUnit.setVisibility(View.VISIBLE);
            mTvMyGold.setVisibility(View.VISIBLE);
        } else {
            mTvGold.setVisibility(View.GONE);
            mTvUnit.setVisibility(View.GONE);
            mTvMyGold.setVisibility(View.GONE);
        }
        mTvReceived.setText(data.plan_message);

        mRecy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new RedEnvelopeDetailsAdapter(mContext, data.max_user_id);
        mRecy.setAdapter(mAdapter);
        mAdapter.setList(data.list);

        showPopupWindow();
    }

}
