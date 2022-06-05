package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.GiftRankingPartyAdapter;
import com.tftechsz.party.adapter.PartyRankingAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.PartyRankBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 转盘-排行榜
 */
public class RankingPopWindow extends BaseBottomPop implements View.OnClickListener {

    private final PartyApiService service;
    private final CompositeDisposable mCompositeDisposable;
    private RecyclerView recMy;
    private PartyRankingAdapter partyRankingAdapter;
    private TextView mTvBottomNumber, mTvNameMy;
    private ImageView mImgMyHeader, mImgTextNotData;
    private boolean mIsActivityBg;//活动皮肤
    private LuckyWheelPopWindow.ICallBackGiftResult iCallBackGiftResult;

    public RankingPopWindow(Context context, boolean mIsActivityBg, LuckyWheelPopWindow.ICallBackGiftResult iCallBackGiftResult) {
        super(context);
        mContext = context;
        this.mIsActivityBg = mIsActivityBg;
        this.iCallBackGiftResult = iCallBackGiftResult;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();

    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        net();
        iCallBackGiftResult.rankingCallback();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_ranking);
    }

    private void initUI() {
        RelativeLayout relBgBottom = findViewById(R.id.rel_pop_bottom_alg);
        relBgBottom.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_bottom_ranking : R.drawable.party_bottom_ranking);
        ImageView imgBg3 = findViewById(R.id.img_bg3);
        imgBg3.setVisibility(mIsActivityBg ? View.VISIBLE : View.GONE);
        ImageView imgBg = findViewById(R.id.img_bg2);
        imgBg.setImageResource(mIsActivityBg ? R.drawable.activity_party_rule_bg_2 : R.drawable.party_rule_bg_2);
        ImageView imgBg1 = findViewById(R.id.img_bg1);
        imgBg1.setImageResource(mIsActivityBg ? R.drawable.activity_party_rule_bg_1 : R.drawable.party_rule_bg_1);
        RelativeLayout relRoot = findViewById(R.id.rel_bg_root);
        relRoot.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_bg_luckywheel_pop2 : R.drawable.party_bg_luckywheel_pop);
        RelativeLayout relBg2 = findViewById(R.id.rel_ranking_center);
        relBg2.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_result_bg1 : R.drawable.party_result_bg1);
        ImageView imgHeader = findViewById(R.id.align_img_textxyzp);
        imgHeader.setImageResource(mIsActivityBg ? R.drawable.activity_party_ranking_titlebg : R.drawable.party_ranking_titlebg);
        mImgTextNotData = findViewById(R.id.tv_pr_zwcy);
        mImgTextNotData.setImageResource(mIsActivityBg ? R.drawable.acitivyt_ranking_not_participation : R.drawable.ranking_not_participation);
        ImageView imgReturn = findViewById(R.id.img_click_return);
        imgReturn.setImageResource(mIsActivityBg ? R.drawable.activity_party_ranking_return : R.drawable.party_ranking_return);
        recMy = findViewById(R.id.rec_pr);
        mTvNameMy = findViewById(R.id.tv_name_ran);
        mTvNameMy.setTextColor(mIsActivityBg ? Color.parseColor("#FFE5A8") : Color.parseColor("#ff7bf1f9"));
        mImgMyHeader = findViewById(R.id.img_ran_header);
        mTvBottomNumber = findViewById(R.id.tv_ran_number);
        findViewById(R.id.img_ran_header2).setVisibility(mIsActivityBg ? View.VISIBLE : View.GONE);
        imgReturn.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.rel_party_lucky_ranking_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        partyRankingAdapter = new PartyRankingAdapter(mIsActivityBg);
        recyclerView.setAdapter(partyRankingAdapter);
        mTvBottomNumber = findViewById(R.id.tv_ran_number);
        AssetManager mgr = getContext().getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/ranking.ttf");
        mTvBottomNumber.setTypeface(tf);

    }


    private void net() {
        mCompositeDisposable.add(service
                .partyRank()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyRankBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyRankBean> response) {
                        if (partyRankingAdapter != null && response.getData() != null) {
                            partyRankingAdapter.setList(response.getData().list);
                        }
                        if (response.getData() != null && response.getData().my != null) {
                            //设置bottom item
                            mTvBottomNumber.setText(response.getData().my.index);
                            GlideUtils.loadCircleImage(mContext, mImgMyHeader, response.getData().my.icon, R.drawable.party_ic_member_empty);
                            mTvNameMy.setText(response.getData().my.nickname);

                            if (response.getData().my.index.equals("暂未上榜")) {
                                recMy.setVisibility(View.GONE);
                                mImgTextNotData.setVisibility(View.VISIBLE);
                            } else {
                                if (response.getData().my.gift_list != null) {
                                    GiftRankingPartyAdapter giftRankingPartyAdapter = new GiftRankingPartyAdapter();
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                                    recMy.setLayoutManager(layoutManager);
                                    List<PartyRankBean.ListDTO.GiftListDTO> list = new ArrayList();
                                    for (int i = 0; i < response.getData().my.gift_list.size(); i++) {
                                        PartyRankBean.ListDTO.GiftListDTO giftListDTO = new PartyRankBean.ListDTO.GiftListDTO();
                                        giftListDTO.gift_image = response.getData().my.gift_list.get(i).gift_image;
                                        giftListDTO.num = response.getData().my.gift_list.get(i).num;
                                        list.add(giftListDTO);
                                    }
                                    giftRankingPartyAdapter.setList(list);
                                    recMy.setAdapter(giftRankingPartyAdapter);
                                }

                                recMy.setVisibility(View.VISIBLE);
                                mImgTextNotData.setVisibility(View.GONE);
                            }

                        }
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
