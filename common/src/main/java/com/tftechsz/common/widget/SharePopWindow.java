package com.tftechsz.common.widget;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.SharePopAdapter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.ShareBean;
import com.tftechsz.common.entity.ShareDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.ShareHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

/**
 *  分享弹窗
 */
public class SharePopWindow extends BaseBottomPop implements IUiListener, OnItemClickListener {
    private final CompositeDisposable mCompositeDisposable;
    private TextView mTvTitle;
    private TextView mTvDesc;
    private RecyclerView mRecyclerView;
    private final Activity mActivity;
    private SharePopAdapter mAdapter;
    private ShareDto mDto;
    private String type;

    public SharePopWindow(Activity activity) {
        super(activity);
        this.mActivity = activity;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
        mCompositeDisposable = new CompositeDisposable();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_share);
    }

    private void initUI() {
        mTvTitle = findViewById(R.id.tv_share_title);
        mTvDesc = findViewById(R.id.tv_share_desc);
        mRecyclerView = findViewById(R.id.share_recy);
        mAdapter = new SharePopAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void questData(String type) {
        questData(type, 0, null);
    }

    public void questData(String type, int id, String roomName) {
        if (!NetworkUtils.isConnected()) {
            Utils.toast(R.string.net_error);
            return;
        }
        this.type = type;

        switch (type) {
            case Interfaces.SHARE_QUEST_TYPE_PARTY: //派对多参数提交
                mCompositeDisposable.add(RetrofitManager.getInstance().createConfigApi(PublicService.class)
                        .getShareConfigToPartyParam(type, String.valueOf(id), roomName).compose(RxUtil.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<ShareDto>>() {
                            @Override
                            public void onSuccess(BaseResponse<ShareDto> response) {
                                extracted(response);
                            }
                        }));
                break;
            default:
                mCompositeDisposable.add(RetrofitManager.getInstance().createConfigApi(PublicService.class)
                        .getShareConfig(type).compose(RxUtil.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<ShareDto>>() {
                            @Override
                            public void onSuccess(BaseResponse<ShareDto> response) {
                                extracted(response);
                            }
                        }));
                break;
        }

    }

    private void extracted(BaseResponse<ShareDto> response) {
        if (response.getData() != null) {
            mDto = response.getData();
            mTvTitle.setText(mDto.title);
            mTvDesc.setText(mDto.message);
            mTvDesc.setVisibility(!TextUtils.isEmpty(mDto.message) ? View.VISIBLE : View.GONE);
            if (!mDto.invites.isEmpty()) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, Math.min(mDto.invites.size(), 5)));
                mAdapter.setList(mDto.invites);
            }
            showPopupWindow();
        }
    }

    @Override
    public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
        ShareBean item = mAdapter.getItem(position);
        if (item != null) {
            switch (item.type) {
                case Interfaces.SHARE_RESULT_TYPE_APPLY: //好友
                    ARouterUtils.toUserShareList(type);
                    break;

                case Interfaces.SHARE_RESULT_TYPE_WECHAT_MOMENTS: //朋友圈
                    ShareHelper.get().shareWx(mActivity, item.send.target_url, item.send.title, item.send.content, item.send.image_url, 2);
                    break;

                case Interfaces.SHARE_RESULT_TYPE_WECHAT_FRIENDS: //微信好友
                    ShareHelper.get().shareWx(mActivity, item.send.target_url, item.send.title, item.send.content, item.send.image_url, 1);
                    break;

                case Interfaces.SHARE_RESULT_TYPE_QQ: //qq
                    ShareHelper.get().shareQQ(mActivity, item.send.target_url, item.send.title, item.send.content, item.send.image_url, 3, this);
                    break;

                case Interfaces.SHARE_RESULT_TYPE_QZONE: //qq空间
                    ShareHelper.get().shareQQ(mActivity, item.send.target_url, item.send.title, item.send.content, item.send.image_url, 4, this);
                    break;

                case Interfaces.SHARE_RESULT_TYPE_SINA: //sina
                    break;
            }
        }
    }

    @Override
    public void onComplete(Object o) {
        Utils.toast("分享成功");
    }

    @Override
    public void onError(UiError uiError) {
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onWarning(int i) {

    }

}
