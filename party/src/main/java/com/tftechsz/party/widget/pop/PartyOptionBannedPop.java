package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyBannedAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.PartySetListConfigBean;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 派对管理-禁言时间列表
 */
public class PartyOptionBannedPop extends BaseBottomPop implements View.OnClickListener {

    private PartyApiService service;
    private CompositeDisposable mCompositeDisposable;
    private RecyclerView recyclerView;
    private PartyBannedAdapter partyLuckyRecordAdapter;
    //0 禁言  1 踢出
    private int mTag;
    private String mUserId;
    private PartySetListConfigBean partySetListConfigBean;
    private TextView tvTitle, tvHintText;
    private String roomId;
    private FunctionPopWindow.IFunctionListener iFunctionListener;

    public PartyOptionBannedPop(Context context, String userId, String roomId, int tag, FunctionPopWindow.IFunctionListener iFunctionListener) {
        super(context);
        mContext = context;
        mTag = tag;
        this.roomId = roomId;
        this.mUserId = userId;
        this.iFunctionListener = iFunctionListener;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
        netConfig();
    }

    private void netConfig() {
        mCompositeDisposable.add(service.getSetOptionList().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartySetListConfigBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartySetListConfigBean> response) {
                        if (response.getData() == null) return;
                        partySetListConfigBean = response.getData();
                        setTag(mTag, mUserId);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_lucky_banner);
    }

    private void initUI() {
        recyclerView = findViewById(R.id.rel_party_lucky_ranking_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(null);
        partyLuckyRecordAdapter = new PartyBannedAdapter();
        partyLuckyRecordAdapter.setOnItemClickListener((adapter, view, position) -> partyLuckyRecordAdapter.setSelectData(position));
        recyclerView.setAdapter(partyLuckyRecordAdapter);
        tvTitle = findViewById(R.id.tv_plb_title);
        tvHintText = findViewById(R.id.tv_plb_content);
        findViewById(R.id.tv_commit).setOnClickListener(v -> {
            if (partyLuckyRecordAdapter != null) {
                if (partyLuckyRecordAdapter.getData().size() > 0) {
                    net();
                }
            }
        });

    }


    public void setTag(int position, String userId) {
        mTag = position;
        this.mUserId = userId;
        //0 禁言  1 踢出
        partyLuckyRecordAdapter.setList(mTag == 0 ? partySetListConfigBean.mute : partySetListConfigBean.kick);
        tvTitle.setText(position == 0 ? "房间禁言" : "踢出房间");
        tvHintText.setText(position == 0 ? "请选择禁言时间" : "请选择踢出时间");
    }

    private void net() {// 0房管，1 拉黑，2踢出，3禁言
        mCompositeDisposable.add(service
                .roomManagerSetAct(roomId, mUserId, mTag == 0 ? 3 : 2, partyLuckyRecordAdapter.getOpType())
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        ToastUtil.showToast(mContext, response.getData());
                        iFunctionListener.outSuccess(mTag == 0 ? 3 : 2);
                        dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
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
