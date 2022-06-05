package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.UserShareListAdapter;
import com.tftechsz.family.databinding.ActShareBinding;
import com.tftechsz.family.mvp.IView.IShareView;
import com.tftechsz.family.mvp.presenter.SharePresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.UserShareDto;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.family.mvp.ui.activity.
 * 描 述 : 分享界面
 */
@Route(path = ARouterApi.ACTIVITY_SHARE)
public class ShareActivity extends BaseMvpActivity<IShareView, SharePresenter> implements IShareView, OnItemChildClickListener, OnLoadMoreListener {

    private ActShareBinding mBind;
    private String mType;
    private int page = 1;
    private UserShareListAdapter mAdapter;
    @Autowired
    UserProviderService service;
    private UserShareDto.SendDTO mSendDto;
    private boolean isEnterFirst;

    @Override
    public SharePresenter initPresenter() {
        return new SharePresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_share);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("").build();
        mType = getIntent().getStringExtra(Interfaces.EXTRA_TYPE);
        mBind.shareRecy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new UserShareListAdapter();
        mAdapter.onAttachedToRecyclerView(mBind.shareRecy);
        mBind.shareRecy.setAdapter(mAdapter);
        mBind.shareRefresh.setOnLoadMoreListener(this);
        mAdapter.addChildClickViewIds(R.id.tv_share, R.id.root_black_list);
        mAdapter.setOnItemChildClickListener(this);

        p.getUserShareList(mType, page);
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        page++;
        p.getUserShareList(mType, page);
    }

    @Override
    public void getUserShareListSuccess(UserShareDto data) {
        if (page == 1) {
            if (data.send != null) {
                mSendDto = data.send;
                if (!isEnterFirst) {
                    isEnterFirst = true;
                    new ToolBarBuilder().showBack(true).setTitle(TextUtils.isEmpty(data.send.invite_intimacy_title) ? "用户列表" : data.send.invite_intimacy_title).build();
                    mBind.shareHint.setText(data.send.invite_intimacy_message);
                    mBind.shareHint.setVisibility(TextUtils.isEmpty(data.send.invite_intimacy_message) ? View.GONE : View.VISIBLE);
                    mAdapter.setLimit(data.send.invite_intimacy_limit);
                }
            }
            mAdapter.setList(data.list);
        } else {
            mAdapter.addData(data.list);
        }
        mBind.setHasData(mAdapter.getItemCount() != 0);
        mBind.shareRefresh.setNoMoreData(!(data.list != null && data.list.size() > 0));
        mBind.shareRefresh.finishLoadMore(true);
        mBind.shareRefresh.finishRefresh(true);
    }

    @Override
    public void getUserShareListError() {
        mBind.shareRefresh.setEnableLoadMore(false);
        mBind.shareRefresh.finishLoadMore(false);
        mBind.shareRefresh.finishRefresh(false);
    }

    @Override
    public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter adapter, @NonNull @NotNull View view, int position) {
        UserShareDto.ListDTO item = mAdapter.getItem(position);
        if (view.getId() == R.id.root_black_list) { //点击用户详情
            if (item != null) {
                ARouterUtils.toMineDetailActivity(String.valueOf(item.user_id));
            }
        } else if (view.getId() == R.id.tv_share) { //点击邀请
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (!NetworkUtils.isConnected()) {
                Utils.toast(R.string.net_error);
                return;
            }
            if (mSendDto == null || TextUtils.isEmpty(mSendDto.content)) {
                Utils.toast("邀请数据为空, 无法分享!");
                return;
            }
            if (item != null) {
                switch (mType) {
                    case Interfaces.SHARE_QUEST_TYPE_FAMILY: //家族邀请
                        ChatMsgUtil.sendFamilyShareMessage(String.valueOf(service.getUserId()), String.valueOf(item.user_id), mSendDto.id, mSendDto.content, mSendDto.image_url, (code, message) -> {
                            if (code == 200) {
                                RxBus.getDefault().post(new MessageCallEvent(message));
                                toastTip("家族分享成功");
                                setItemNoEnable(position);
                            } else {
                                toastTip("家族分享失败");
                            }
                        });
                        break;
                }
            }
        }
    }

    private void setItemNoEnable(int position) {
        TextView tcShare = (TextView) mAdapter.getViewByPosition(position, R.id.tv_share);
        if (tcShare != null) {
            tcShare.setEnabled(false);
            tcShare.setTextColor(Utils.getColor(R.color.color_mid_font));
        }
    }

    @Override
    public void showLoadingDialog() {
        //super.showLoadingDialog();
    }
}
