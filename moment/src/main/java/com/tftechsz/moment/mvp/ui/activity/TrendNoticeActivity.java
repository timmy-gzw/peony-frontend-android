package com.tftechsz.moment.mvp.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ScreenUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.TrendNoticeAdapter;
import com.tftechsz.moment.mvp.IView.INoticeView;
import com.tftechsz.moment.mvp.entity.NoticeBean;
import com.tftechsz.moment.mvp.presenter.INoticePresenter;
import com.tftechsz.moment.other.CommentEvent;

import java.util.ArrayList;
import java.util.List;

import razerdp.util.KeyboardUtils;

public class TrendNoticeActivity extends BaseMvpActivity<INoticeView, INoticePresenter> implements INoticeView, View.OnClickListener {
    private TrendNoticeAdapter mAdapter;
    private RecyclerView mRecy;
    private View mNotDataView;
    public SmartRefreshLayout mSmartRefreshLayout;
    private RelativeLayout mRootBotInput;
    private EditText mEdtComment;
    private int form_id;
    private CustomPopWindow mTipsPopWindow;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TrendNoticeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.act_trend_notice;
    }

    @Override
    public INoticePresenter initPresenter() {
        return new INoticePresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("动态通知")
                .build();
        ImmersionBar.with(this)
                .keyboardEnable(true)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .setOnKeyboardListener((isPopup, keyboardHeight) -> {
                    if (isPopup) {
                        mEdtComment.requestFocus();
                        mRootBotInput.setVisibility(View.VISIBLE);
                    } else {
                        mEdtComment.clearFocus();
                        mRootBotInput.setVisibility(View.GONE);
                        mEdtComment.setText("");
                    }
                })
                .init();
        mRecy = findViewById(R.id.notice_recycleview);
        mRecy.setLayoutManager(new LinearLayoutManager(this));
        mSmartRefreshLayout = findViewById(R.id.smart_refresh);
        mRootBotInput = findViewById(R.id.root_bot_input);
        mRootBotInput.setVisibility(View.GONE);
        mEdtComment = findViewById(R.id.et_comment);
        TextView tcSend = findViewById(R.id.tv_send);
        tcSend.setOnClickListener(this);

        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mRecy.getParent(), false);
        ImageView ivEmpty = mNotDataView.findViewById(R.id.iv_empty);
        TextView tvEmpty = mNotDataView.findViewById(R.id.tv_empty);
        tvEmpty.setText(R.string.ph_empty_notice);
        ivEmpty.setVisibility(View.VISIBLE);
        ivEmpty.setImageResource(R.mipmap.ph_empty_moment);
        TextView tvSend = mNotDataView.findViewById(R.id.tv_send);
        tvSend.setVisibility(View.VISIBLE);
        tvSend.setOnClickListener(v -> {
            showMediaSelector();
        });
        setSmartRefreshLayoutListner();
        initBus();
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommentEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.getBlogId() != 0)
                                performDatas(event.getType(), event.getBlogId());

                        }
                ));
    }

    private void performDatas(int type, int blogId) {
        List<NoticeBean> data = mAdapter.getData();
        for (int i = 0, j = data.size(); i < j; i++) {
            NoticeBean noticeBean = data.get(i);
            if (blogId == noticeBean.getBlog_id()) {
                if (type == -2) { //删除帖子, 设置帖子状态不让点击回复
                    noticeBean.setIs_blog(0);
                    mAdapter.setData(i, noticeBean);
                    break;
                }
            }
        }
    }

    @Override
    protected void initData() {
        mAdapter = new TrendNoticeAdapter(null);
        mRecy.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.item_trend_comment_root, R.id.iv_icon, R.id.mid_cl, R.id.image);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            NoticeBean item = mAdapter.getItem(position);
            int id = view.getId();
            if (id == R.id.iv_icon) {//点击头像进入个人主页
                if (item != null)
                    ARouterUtils.toMineDetailActivity(String.valueOf(item.getUser_id()));
            } else if (id == R.id.mid_cl) {
                assert item != null;

                if (item.getIs_blog() == 0) { //动态不存在
                    if (mTipsPopWindow == null) {
                        mTipsPopWindow = new CustomPopWindow(TrendNoticeActivity.this);
                    }
                    mTipsPopWindow.setContent(Interfaces.notice_blog_delete)
                            .setSingleButtong()
                            .showPopupWindow();
                } else {
                    if (item.getStatus() == 1) {//动态评论存在
                        switch (item.getFrom_type()) {
                            case "reply-comment": //回复
                            case "comment": //评论
                                mEdtComment.setHint("回复@" + item.getNickname());
                                form_id = item.getFrom_id();
                                KeyboardUtils.open(getBaseContext());
                                break;

                            default://点赞 跳转详情
                                p.get_info(item.getBlog_id());
                                break;
                        }
                    } else {
                        p.get_info(item.getBlog_id());
                    }
                }
            } else if (id == R.id.item_trend_comment_root || id == R.id.image) {//跳转动态详情
                if (item != null)
                    p.get_info(item.getBlog_id());
            }
        });
        p.getNotice(mPage);
    }

    private void setSmartRefreshLayoutListner() {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                p.getNotice(mPage);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                p.getNotice(mPage);

            }
        });
    }

    @Override
    public void getNoticeSucess(List<NoticeBean> data) {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
        setData(data);
    }

    @Override
    public void getNoticeFial() {
        mSmartRefreshLayout.finishRefresh(false);
        mSmartRefreshLayout.finishLoadMore(false);
    }

    @Override
    public void commentSuccess(Boolean data) {
        if (data) {
            toastTip("回复成功");
            mEdtComment.setText("");
            KeyboardUtils.close(mRootBotInput);
        }
    }

    @Override
    public void getInfoSucess(CircleBean data) {
        TrendDetailActivity.startInstanceActivity(this, data);
    }

    @Override
    public void getInfoFial(String msg) {
        if (mTipsPopWindow == null) {
            mTipsPopWindow = new CustomPopWindow(this);
        }
        mTipsPopWindow.setContent(msg)
                .setSingleButtong()
                .showPopupWindow();
    }

    private void setData(List<NoticeBean> data) {
        final int size = data == null ? 0 : data.size();
        if (mPage == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mNotDataView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < mPageSize) {
            //第一页如果不够一页就不显示没有更多数据布局
            mSmartRefreshLayout.setEnableLoadMore(false);
            //mAdapter.loadMoreEnd(false);
        } else {
            mSmartRefreshLayout.setEnableLoadMore(true);
            //mAdapter.loadMoreComplete();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_send) { //发送
            String edt = Utils.getText(mEdtComment);
            if (!TextUtils.isEmpty(edt)) {
                p.trendComment(form_id, edt);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) { //点击外部
                KeyboardUtils.close(mRootBotInput);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() > 0) {
                ArrayList<LocalMedia> list = new ArrayList<>(selectList);
                Intent intent = new Intent(this, SendTrendActivity.class);
                intent.putParcelableArrayListExtra(Interfaces.EXTRA_TREND, list);
                startActivity(intent);
            }
        }
    }

    private void showMediaSelector() {
        mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
                    } else {
                        Utils.toast("请允许摄像头权限");
                    }
                })
        );
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            mRootBotInput.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = ScreenUtils.getScreenWidth(this);

            // 点击EditText的事件，忽略它。
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
}
