package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyLineUpMacAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.QueuePartyListBean;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 排队麦序
 */
public class LineUpMacPartyPop extends BaseBottomPop implements View.OnClickListener, PartyLineUpMacAdapter.IListenerUp {

    private PartyApiService service;
    private CompositeDisposable mCompositeDisposable;
    private RecyclerView recyclerView;
    private PartyLineUpMacAdapter partyLineUpMacAdapter;
    private int roomId;
    //分页
    private int mPage = 1;
    private boolean mIsAni;
    private int mUserId;
    private TextView mTvTitle;
    public SmartRefreshLayout mSmartRefreshLayout;
    private int mCountPeople;//总人数
    private int pkInfoId;  //pk id
    private boolean isOnSeat;//是否在麦位
    private TextView mTvCommit;
    private IOnSeatListener iOnSeatListener;
    private TextView tvEmpty;

    public LineUpMacPartyPop(Context context, int roomId, boolean flagAni, int userId, IOnSeatListener iOnSeatListener) {
        super(context);
        mContext = context;
        this.iOnSeatListener = iOnSeatListener;
        this.roomId = roomId;
        this.mIsAni = flagAni;
        mUserId = userId;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
        netConfig();
    }

    /**
     * 是否在麦位
     */
    public void setOnSeat(boolean flag) {
        this.isOnSeat = flag;

        if (mTvCommit != null) {
            mTvCommit.setText(isOnSeat ? "下麦" : "我要上麦");
        }
        if (tvEmpty != null) {
            tvEmpty.setText(isOnSeat ? "暂时没人排队" : "暂时没人排队，快上麦吧～");
        }
    }

    private void netConfig() {
        mCompositeDisposable.add(service.queueList(roomId, mPage).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<QueuePartyListBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<QueuePartyListBean> response) {
                        if (response.getData() == null) return;
                        if (mPage == 1) {
                            partyLineUpMacAdapter.getData().clear();
                        }
                        partyLineUpMacAdapter.addData(response.getData().queue);
                        mSmartRefreshLayout.finishLoadMore();
                        mSmartRefreshLayout.finishRefresh();
                        mCountPeople = response.getData().count;
                        mTvTitle.setText("排麦人数" + mCountPeople + "人");
                        updateBtnMy();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mSmartRefreshLayout.finishLoadMore();
                        mSmartRefreshLayout.finishRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSmartRefreshLayout.finishLoadMore();
                        mSmartRefreshLayout.finishRefresh();
                    }
                }));
    }

    /**
     * 在麦位不显示 上麦按钮
     */
    private void updateBtnMy() {
        if (partyLineUpMacAdapter != null && partyLineUpMacAdapter.getData() != null) {
            int size = partyLineUpMacAdapter.getData().size();
            for (int i = 0; i < size; i++) {
                if (partyLineUpMacAdapter.getData().get(i).user_id == mUserId) {
                    mTvCommit.setVisibility(View.INVISIBLE);
                    return;
                }
            }
        }

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_line_up_party);
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        if (mTvCommit != null) {
            mTvCommit.setVisibility(View.VISIBLE);
        }
        mPage = 1;
        netConfig();
    }

    private void initUI() {
        mTvCommit = findViewById(R.id.tv_commit);
        mTvTitle = findViewById(R.id.tv_plb_title);
        recyclerView = findViewById(R.id.rel_party_lucky_ranking_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(null);
        if (!mIsAni) {
            findViewById(R.id.tv_clear_list).setVisibility(View.GONE);
        }
        partyLineUpMacAdapter = new PartyLineUpMacAdapter(mIsAni, this, mUserId);
        recyclerView.setAdapter(partyLineUpMacAdapter);
        //空数据
        View emptyView = View.inflate(mContext, R.layout.base_empty_view, null);
        tvEmpty = emptyView.findViewById(R.id.tv_empty);
        tvEmpty.setText("暂时没人排队，快上麦吧～");
        tvEmpty.setTextSize(12);
        partyLineUpMacAdapter.setEmptyView(emptyView);

        findViewById(R.id.tv_commit).setOnClickListener(v -> {
            upMac();
        });
        findViewById(R.id.tv_clear_list).setOnClickListener(v -> {
            if (partyLineUpMacAdapter != null && partyLineUpMacAdapter.getData().size() > 0) {
                //清楚列表所有
                cancelMac(-1);
            }
        });
        mSmartRefreshLayout = findViewById(R.id.refresh);

        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            netConfig();
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage++;
            netConfig();
        });
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

    @Override
    public void up(int userId, int id) {
        if (mIsAni) {
            //管理员同意上麦
            mCompositeDisposable.add(service.agreeUserSequence(roomId, id, userId).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                            removeUser(userId);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);
                            if (code == 1002) {
                                removeUser(userId);
                            }

                        }
                    }));
        } else {
            mCompositeDisposable.add(service.agreeSeat(roomId, userId, 0).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                        @Override
                        public void onSuccess(BaseResponse<String> response) {
                            mPage = 1;
                            netConfig();

                        }
                    }));
        }


    }

    /**
     * 离开房间移除用户
     */
    private void removeUser(int userId) {
        if (partyLineUpMacAdapter != null && partyLineUpMacAdapter.getData().size() > 0) {
            int size = partyLineUpMacAdapter.getData().size();
            for (int i = 0; i < size; i++) {
                if (partyLineUpMacAdapter.getData().get(i).user_id == userId) {
                    partyLineUpMacAdapter.remove(i);
                    mCountPeople--;
                }
            }
            mTvTitle.setText("排麦人数" + mCountPeople + "人");
        }

    }


    public void setPkInfoId(int pkInfoId) {
        this.pkInfoId = pkInfoId;
    }


    private void upMac() {
        if (!ClickUtil.canOperate()) {
            return;
        }
        if (!isOnSeat) {
            if (mIsAni) {
                dismiss();
            }
            //抱上麦序 -1是麦序 --我要上麦
            mCompositeDisposable.add(service.applySeat(roomId, -1, pkInfoId).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                            mPage = 1;
                            netConfig();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            mPage = 1;
                            Utils.toast(msg);
                            netConfig();
                        }
                    }));
        } else {
            dismiss();
            iOnSeatListener.bottomSeat();
        }

    }


    @Override
    public void cancelMac(int userId) {
        if (userId == -1) {
            //取消所有列表人
            mCompositeDisposable.add(service.clearQueue(roomId, 0).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                            if (partyLineUpMacAdapter != null) {
                                partyLineUpMacAdapter.getData().clear();
                                partyLineUpMacAdapter.notifyDataSetChanged();
                                mCountPeople = 0;
                                mTvTitle.setText("排麦人数0人");
                            }

                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);

                        }
                    }));

        } else {
//            取消上麦-单人
            mCompositeDisposable.add(service.emptyMicrophone(roomId).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                            if (partyLineUpMacAdapter != null && partyLineUpMacAdapter.getData().size() > 0) {
                                int size = partyLineUpMacAdapter.getData().size();
                                for (int i = 0; i < size; i++) {
                                    if (partyLineUpMacAdapter.getData().get(i).user_id == mUserId) {
                                        partyLineUpMacAdapter.remove(i);
                                        mCountPeople--;
                                    }
                                }
                                mTvTitle.setText("排麦人数" + mCountPeople + "人");
                                mTvCommit.setVisibility(View.VISIBLE);
                                tvEmpty.setText("暂时没人排队，快上麦吧～");
                                mTvCommit.setText("我要上麦");
                            }

                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);
                        }
                    }));


        }


    }

    /**
     * 上麦刷新一次列表
     */
    public void notifyListItem(int userId) {
        if (partyLineUpMacAdapter != null && partyLineUpMacAdapter.getData() != null) {
            int size = partyLineUpMacAdapter.getData().size();
            for (int i = size - 1; i > 0; i--) {
                if (partyLineUpMacAdapter.getData().get(i).user_id == userId) {
                    partyLineUpMacAdapter.getData().remove(i);
                    partyLineUpMacAdapter.notifyDataSetChanged();
                    mCountPeople--;
                    mTvTitle.setText("排麦人数" + mCountPeople + "人");
                    return;
                }
            }
            mTvTitle.setText("排麦人数" + mCountPeople + "人");
        }

    }

    public interface IOnSeatListener {
        void bottomSeat();
    }
}
