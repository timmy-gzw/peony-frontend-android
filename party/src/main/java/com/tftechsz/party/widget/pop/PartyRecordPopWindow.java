package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyLuckyRecordAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.PartyTurntableBean;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 转盘-抽奖记录
 */
public class PartyRecordPopWindow extends BaseBottomPop implements View.OnClickListener {

    private PartyApiService service;
    private CompositeDisposable mCompositeDisposable;
    private RecyclerView recyclerView;
    private PartyLuckyRecordAdapter partyLuckyRecordAdapter;
    private SmartRefreshLayout smartRefreshLayout;
    private int mPage = 1;
    private boolean mIsActivityBg;//活动皮肤
    private ImageView mImgHeader;
    private LuckyWheelPopWindow.ICallBackGiftResult iCallBackGiftResult;

    public PartyRecordPopWindow(Context context, boolean mIsActivityBg, LuckyWheelPopWindow.ICallBackGiftResult iCallBackGiftResult) {
        super(context);
        mContext = context;
        this.mIsActivityBg = mIsActivityBg;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        this.iCallBackGiftResult = iCallBackGiftResult;
        initUI();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_lucky_record);
    }

    private void initUI() {
        ImageView imgBg3 = findViewById(R.id.img_bg3);
        imgBg3.setVisibility(mIsActivityBg ? View.VISIBLE : View.GONE);
        RelativeLayout relRoot = findViewById(R.id.rel_bg_root);
        relRoot.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_bg_luckywheel_pop2 : R.drawable.party_bg_luckywheel_pop);
        mImgHeader = findViewById(R.id.align_img_textxyzp);
        mImgHeader.setImageResource(mIsActivityBg ? R.drawable.activity_party_record_lotto : R.drawable.party_record_lotto);
        ImageView imgClickReturn = findViewById(R.id.img_click_return);
        imgClickReturn.setImageResource(mIsActivityBg ? R.drawable.activity_party_ranking_return : R.drawable.party_ranking_return);
        RelativeLayout relBg2 = findViewById(R.id.rel_ranking_center);
        relBg2.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_result_bg1 : R.drawable.party_result_bg1);

        ImageView imgBg1 = findViewById(R.id.img_bg1);
        imgBg1.setImageResource(mIsActivityBg ? R.drawable.activity_party_rule_bg_1 : R.drawable.party_rule_bg_1);
        ImageView imgBgLine1 = findViewById(R.id.img_left_line);
        ImageView imgBgLine2 = findViewById(R.id.img_left_line2);
        imgBgLine1.setImageResource(mIsActivityBg ? R.drawable.activity_party_record_imgalgin : R.drawable.party_record_imgalgin);
        imgBgLine2.setImageResource(mIsActivityBg ? R.drawable.activity_party_record_imgalgin : R.drawable.party_record_imgalgin);
        ImageView imgBg = findViewById(R.id.img_bg2);
        imgBg.setImageResource(mIsActivityBg ? R.drawable.activity_party_rule_bg_2 : R.drawable.party_rule_bg_2);
        findViewById(R.id.img_click_rule_ranking).setVisibility(View.INVISIBLE);
        findViewById(R.id.img_click_record_ranking).setVisibility(View.INVISIBLE);
        smartRefreshLayout = findViewById(R.id.refresh);
        imgClickReturn.setOnClickListener(this);
        recyclerView = findViewById(R.id.rel_party_lucky_ranking_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(null);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage = mPage + 1;
            net();
        });
        View emptyView = View.inflate(mContext, R.layout.base_empty_view, null);
        TextView tvEmpty = emptyView.findViewById(R.id.tv_empty);
        ImageView img = emptyView.findViewById(R.id.iv_empty);
        img.setImageResource(mIsActivityBg ? R.drawable.party_record_notdata_bg2 : R.drawable.party_record_notdata_bg);
        tvEmpty.setText("暂无抽奖记录哦～");
        tvEmpty.setTextSize(12);
        tvEmpty.setTextColor(Color.parseColor("#7BF1F9"));
        partyLuckyRecordAdapter = new PartyLuckyRecordAdapter(mIsActivityBg);
        partyLuckyRecordAdapter.setEmptyView(emptyView);
        recyclerView.setAdapter(partyLuckyRecordAdapter);
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        mPage = 1;
        if (partyLuckyRecordAdapter != null) {
            partyLuckyRecordAdapter.getData().clear();
        }
        net();
        if (iCallBackGiftResult != null) {
            iCallBackGiftResult.recordCallback();
        }
    }

    private void net() {
        mCompositeDisposable.add(service
                .partyFlow(String.valueOf(mPage), "20")
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<PartyTurntableBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<PartyTurntableBean>> response) {
                       /* if (partyRankingAdapter != null && response.getData() != null) {
                            partyRankingAdapter.setList(response.getData().list);
                        }
*/
                        if (response.getData() != null && response.getData().size() > 0) {
                            partyLuckyRecordAdapter.addData(response.getData());
                        }
                        smartRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        smartRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        smartRefreshLayout.finishLoadMore();
                    }
                }));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.img_click_return) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
