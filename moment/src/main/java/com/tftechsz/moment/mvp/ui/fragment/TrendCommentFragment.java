package com.tftechsz.moment.mvp.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.CommentPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.TrendCommentAdapter;
import com.tftechsz.moment.entity.CommentListItem;
import com.tftechsz.moment.entity.LikeListItem;
import com.tftechsz.moment.mvp.IView.ITrendCommentView;
import com.tftechsz.moment.mvp.presenter.TrendCommentPresenter;
import com.tftechsz.moment.other.CommentEvent;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 动态评论
 */
public class TrendCommentFragment extends BaseMvpFragment<ITrendCommentView, TrendCommentPresenter> implements ITrendCommentView {
    private TrendCommentAdapter mAdapter;
    private int blogId, blogUserId, clickPos;
    private View mEmptyView;

    public static TrendCommentFragment getInstance(int mBlogId, int userId) {
        Bundle args = new Bundle();
        args.putInt("blogId", mBlogId);
        args.putInt("userId", userId);
        TrendCommentFragment fragment = new TrendCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            blogId = getArguments().getInt("blogId", 0);
            blogUserId = getArguments().getInt("userId", 0);
        }
        RecyclerView rvTrendComment = getView(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTrendComment.setLayoutManager(layoutManager);
        mSmartRefreshLayout = getView(R.id.refreshLayout);
        setSmartRefreshLayoutListner();
        mAdapter = new TrendCommentAdapter(null);
        mAdapter.addChildClickViewIds(R.id.iv_avatar, R.id.item_trend_comment_root);
        mAdapter.addChildLongClickViewIds(R.id.item_trend_comment_root);
        rvTrendComment.setAdapter(mAdapter);
        initBus();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {//短按监听
            CommentListItem item = mAdapter.getItem(position);
            int id = view.getId();
            if (id == R.id.iv_avatar) {//点击头像跳转到动态详情
                if (item != null) {
                    ARouterUtils.toMineDetailActivity(String.valueOf(item.getUser_id()));
                }
            } else if (id == R.id.item_trend_comment_root) { //弹出回复窗口
                if (item != null) {
                    RxBus.getDefault().post(new CommentEvent(item.getComment_id(), item.getNickname()));
                }
            }
        });

        mAdapter.setOnItemChildLongClickListener((adapter, view, position) -> { //长按监听
            clickPos = position;
            CommentPopWindow popWindow = new CommentPopWindow(mContext);
            CommentListItem item = mAdapter.getItem(position);
            if (item == null) {
                return false;
            }
            popWindow.putInfo(blogUserId, item.getUser_id());
            popWindow.addOnClickListener(position1 -> {
                switch (position1) {
                    case 0://回复
                        RxBus.getDefault().post(new CommentEvent(item.getComment_id(), item.getNickname()));
                        break;

                    case 1://复制
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", item.getContent());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.showToast(mContext, "复制成功");
                        break;

                    case 2://删除
                        p.delTrendComment(item.getComment_id());
                        break;
                }
            });
            popWindow.showPopupWindow();
            return false;
        });
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            CommentListItem item = mAdapter.getItem(position);
            if (item != null) {
                RxBus.getDefault().post(new CommentEvent(item.getComment_id(), item.getNickname()));
            }
        });
    }

    private void setSmartRefreshLayoutListner() {
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage++;
            p.getTrendComment(mPage, blogId);
        }).setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            p.getTrendComment(mPage, blogId);
        });
    }

    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_TREND_COMMENT_SUCCESS) {
                                mPage = 1;
                                p.getTrendComment(mPage, blogId);
                            }
                        }
                ));
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_trend_praise;
    }


    @Override
    protected void initData() {
        mEmptyView = getListEmptyView();
        TextView tvEmpty = mEmptyView.findViewById(R.id.tv_empty);
        tvEmpty.setText("暂无任何评论,快来评论吧~");

        p.getTrendComment(mPage, blogId);

    }


    @Override
    public TrendCommentPresenter initPresenter() {
        return new TrendCommentPresenter();
    }

    @Override
    public void getTrendCommentSuccess(List<CommentListItem> data) {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
        setData(mPage, data);
    }

    private void setData(int pageIndex, List<CommentListItem> data) {
        final int size = data == null ? 0 : data.size();
        mSmartRefreshLayout.setEnableLoadMore(data != null && data.size() > 0);
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mEmptyView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void getTrendPraiseSuccess(List<LikeListItem> data) {

    }

    @Override
    public void delTrendCommentSuccess(boolean isDel) {
        if (isDel) {
            mAdapter.remove(clickPos);
            toastTip("删除成功");
            RxBus.getDefault().post(new CommentEvent(-1, blogId));
        }
    }
}
