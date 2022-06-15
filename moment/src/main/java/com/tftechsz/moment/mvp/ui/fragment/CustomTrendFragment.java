package com.tftechsz.moment.mvp.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.VideoInfo;
import com.tftechsz.common.event.AccostSuccessEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.player.controller.MyVideoController;
import com.tftechsz.common.player.interfaces.OnItemChildVideoClickListener;
import com.tftechsz.common.player.other.VideoClickEvent;
import com.tftechsz.common.player.view.MyErrorView;
import com.tftechsz.common.player.view.VideoView;
import com.tftechsz.common.refresh.FastScrollLinearLayoutManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.TrendAdapter;
import com.tftechsz.moment.mvp.IView.IDynamicView;
import com.tftechsz.moment.mvp.presenter.DynamicRecommendPresenter;
import com.tftechsz.moment.mvp.ui.activity.MineTrendActivity;
import com.tftechsz.moment.mvp.ui.activity.TrendDetailActivity;
import com.tftechsz.moment.other.CommentEvent;
import com.tftechsz.moment.other.CommentPraiseAccostEvent;

import java.util.List;
import java.util.Objects;
import java.util.Timer;

public class CustomTrendFragment extends BaseMvpFragment<IDynamicView, DynamicRecommendPresenter> implements IDynamicView, OnItemChildVideoClickListener {
    public RecyclerView recyclerView;
    private TrendAdapter mAdapter;
    public SmartRefreshLayout mSmartRefreshLayout;
    //动态类型
    private int dynamicType = 0, uid;
    @Autowired
    UserProviderService service;
    private static Timer mTimer;
    //    private boolean MotionEvent_UP;
//    private static boolean isShowFloatImage = true;
//    private static float upTime, startY;
    private CustomPopWindow mTipsPopWindow;
    private VideoView mVideoView;
    /**
     * 当前播放的位置
     */
    protected int mCurPos = -1;
    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    protected int mLastPos = mCurPos;
    public FastScrollLinearLayoutManager mLinearLayoutManager;
    private MyVideoController mController;
    private boolean canLoadMore = true;
    private final int minLeftItemCount = 5;
    private int mClickPosition;
    private int lastItemCount;
    private PageStateManager mPageStateManager;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recommend_trend;
    }

    public static CustomTrendFragment newInstance(int type, SrcollowInterface mSrcollowInterface) {
        return newInstance(type, 0, mSrcollowInterface);
    }

    public static CustomTrendFragment newInstance(int type, int uid, SrcollowInterface mSrcollowInterface) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("uid", uid);
        CustomTrendFragment fragment = new CustomTrendFragment();
        fragment.setArguments(args);
        setSrcollowInterface(mSrcollowInterface);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            dynamicType = getArguments().getInt("type", 0);
            uid = getArguments().getInt("uid", 0);
        }
        mSmartRefreshLayout = getView(R.id.refreshLayout);
        recyclerView = getView(R.id.recyclerView);
        initSmartRefreshLayout();
        initVideoView();

        mPageStateManager = PageStateManager.initWhenUse(mSmartRefreshLayout, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageStateManager.showContent();
                mSmartRefreshLayout.autoRefresh();
                loadData(true);
            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                switch (newState) {
//                    /*正在拖拽*/
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        break;
//
//                    /*滑动停止*/
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        if (MotionEvent_UP) { //滑动已经停止
//                            if (!isShowFloatImage) {
//                                //抬起手指1s后再显示悬浮按钮
//                                //开始1s倒计时
//                                upTime = System.currentTimeMillis();
//                                mTimer = new Timer();
//                                mTimer.schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        if(mSrcollowInterface != null) {
//                                            mSrcollowInterface.showImage();
//                                        }
//                                        isShowFloatImage = true;
//                                    }
//                                }, 500);
//                            }
//                        }
//                        break;
//
//                    /*惯性滑动中*/
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        break;
//                }
//
//            }
//        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //滚动停止
                    autoPlayVideo(recyclerView);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int itemCount = layoutManager.getItemCount();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastItemCount != itemCount && lastPosition == itemCount - 1) {
                        lastItemCount = itemCount;
                        loadData(false);
                    } else {
                        if (itemCount > minLeftItemCount) {
                            if (lastPosition == itemCount - minLeftItemCount) {
                                loadData(false);
                            }
                        }
                    }
                }
            }
        });

//        recyclerView.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN://手指按下
//                    MotionEvent_UP = false;
//                    if (System.currentTimeMillis() - upTime < 1000) {
//                        //本次按下距离上次的抬起小于1s时，取消Timer
//                        if(mTimer != null ) {
//                            mTimer.cancel();
//                        }
//                    }
//                    startY = event.getY();
//                    break;
//
//                case MotionEvent.ACTION_MOVE://手指滑动
//                    MotionEvent_UP = false;
//                    if (Math.abs(startY - event.getY()) > 10) {
//                        if (isShowFloatImage) {
//                            if(mSrcollowInterface != null) {
//                                mSrcollowInterface.hideImage();
//                            }
//                            isShowFloatImage = false;
//                        }
//                    }
//                    startY = event.getY();
//                    break;
//
//                case MotionEvent.ACTION_UP://手指抬起
//                    MotionEvent_UP = true;
//                    break;
//            }
//            return false;
//        });
        Utils.runOnUiThread(this::initBus);

    }

    private void autoPlayVideo(RecyclerView view) {
        int nextForTag = 0;
        if (view == null) return;
        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        for (int i = firstVisibleItemPosition; i < lastVisibleItemPosition + 1; i++) {
            View itemView = view.getChildAt(nextForTag);
            if (itemView == null) return;
            Rect rect = new Rect();
            View playerContainer = itemView.findViewById(R.id.player_container);
            if (playerContainer == null) return;
            playerContainer.getLocalVisibleRect(rect);
           /* Utils.logE("页面可见:" + firstVisibleItemPosition
                    + " - " + lastVisibleItemPosition
                    + " , 当前pos:" + i
                    + " -->{" + rect.top
                    + " ," + rect.bottom
                    + " ," + rect.left
                    + " ," + rect.right + "}"
            );*/
            if (Objects.requireNonNull(mAdapter.getItem(i)).getType() == 1 && rect.top == 0 && rect.bottom == playerContainer.getHeight()) {
                startPlay(i);
                break;
            } else {
                if (i == mCurPos) {
                    releaseVideoView();
                }
                if (nextForTag < lastVisibleItemPosition) {
                    nextForTag++;
                }
            }
        }
    }

    private void initVideoView() {
        mVideoView = new VideoView(mActivity);
        mVideoView.setOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                Utils.logE("onPlayStateChanged= " + playState);
                //监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mLastPos = mCurPos;
                    mCurPos = -1;
                }
            }
        });

        mController = new MyVideoController(mActivity);
        mController.addControlComponent(new MyErrorView(getActivity()));
        mVideoView.setVideoController(mController);
        mVideoView.setLooping(true);
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_SEND_TREND_SUCCESS) {
                                if (uid == -1) {   //自己动态
                                    p.getSelfTrend(mPage);
                                }
                            } else if (event.type == Constants.NOTIFY_PIC_ACCOST_SUCCESS) {
                                List<CircleBean> data = mAdapter.getData();
                                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                                for (int i = 0; i < data.size(); i++) {
                                    if (data.get(i).getUser_id() == event.familyId) {
                                        CircleBean userInfo = data.get(i);
                                        userInfo.setIs_accost(1);
                                        if (i >= firstVisibleItemPosition && i <= lastVisibleItemPosition) { //只在可见的item执行动画
                                            mAdapter.startAnimation(i);
//                                            mAdapter.notifyItemChangeSinge(i);
                                        }
                                    }
                                }

                            }
                        }
                ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(VideoClickEvent.class) //视频点击回调
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (mVideoView.isPlaying()) {
                                if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
                                CircleBean item = mAdapter.getItem(mCurPos);
                                if (item != null && item.getMedia() != null && item.getMedia().size() != 0) {
                                    VideoInfo info = new VideoInfo(item.getMedia().get(0));
//                                    Utils.startTrendVideoViewActivity(mActivity, mAdapter.getViewByPosition(mCurPos, R.id.player_container), info);
                                    Utils.startTrendVideoViewActivity(mActivity, null, info);
                                }
                            }
                        }
                ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommentEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.getBlogId() != 0)
                                performDatas(event.getType(), event.getBlogId());

                        }
                ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommentPraiseAccostEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            switch (event.type) {
                                case 0:
                                    mAdapter.praiseSuccess(event.blog_id, event.praises);
                                    break;

                                case 1:
                                   /* List<CircleBean> data = mAdapter.getData();
                                    for (int i = 0, j = data.size(); i < j; i++) {
                                        CircleBean circleBean = data.get(i);
                                        if (event.userId == circleBean.getUser_id()) {
                                            mAdapter.startAnimation(recyclerView, i);
                                        }
                                    }*/
                                    break;
                            }
                        }
                ));
    }

    private void performDatas(int type, int blogId) {
        List<CircleBean> data = mAdapter.getData();
        int j = data.size();
        for (int i = 0; i < j; i++) {
            CircleBean circleBean = data.get(i);
            if (blogId == circleBean.getBlog_id()) {
                switch (type) { // 1点赞 2评论  -2帖子删除
                    case 1://点赞
                        circleBean.setPraises(circleBean.getPraises() + 1);
                        circleBean.setIs_praise(1);
                        mAdapter.setData(i, circleBean);
                        break;

                    case 2: //评论
                        circleBean.setComments(circleBean.getComments() + 1);
                        mAdapter.setData(i, circleBean);
                        break;

                    case -1: //删除评论
                        circleBean.setComments(circleBean.getComments() - 1);
                        mAdapter.setData(i, circleBean);
                        break;

                    case -2: //删除帖子
                        mAdapter.remove(i);
                        --j;
                        break;
                }
            }
        }
    }

    @Override
    public void onItemChildClick(int position, View view) {
        /*Intent intent = new Intent(mContext, TrendVideoViewActivity.class);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, view, Interfaces.VIDEO_TRANSITIONS);
        intent.putExtra(Interfaces.EXTRA_VIDEO_URL, mAdapter.getItem(position).getMedia().get(0));
        startActivity(intent, optionsCompat.toBundle());*/
        startPlay(position);
    }

    public interface SrcollowInterface {

        void showImage();

        void hideImage();
    }

    private static SrcollowInterface mSrcollowInterface;

    private static void setSrcollowInterface(SrcollowInterface srcollowInterface) {
        mSrcollowInterface = srcollowInterface;
    }

    @Override
    protected void initData() {
        initAdapter();
        if (mActivity.getClass().equals(MineTrendActivity.class)) {
            loadData(true);
        }
    }

    private boolean isFirstVisible = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirstVisible) {
            isFirstVisible = false;
            mHandler.postDelayed(() -> mSmartRefreshLayout.autoRefresh(), 100);
        }
    }

    private final Handler mHandler = new Handler(Looper.myLooper());

    public void loadData(boolean isRefresh) {
        if (canLoadMore) {
            canLoadMore = false;
            if (isRefresh)
                mPage = 1;
            else
                mPage += 1;
            if (uid != 0) {
                if (uid == -1) {   //自己动态
                    p.getSelfTrend(mPage);
                } else {
                    p.getUserTrend(mPage, String.valueOf(uid));
                }
            } else {
                p.getDynamicRecommend(String.valueOf(dynamicType), mPage);
            }
        }
    }


    @Override
    public DynamicRecommendPresenter initPresenter() {
        return new DynamicRecommendPresenter();
    }

    public void initSmartRefreshLayout() {
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            /*if (!NetworkUtils.isConnected()) {
                mPageStateManager.showError(null);
                return;
            }*/
            loadData(false);
        }).setOnRefreshListener(refreshLayout -> {
            if (!NetworkUtils.isConnected()) {
                mPageStateManager.showError(null);
                mSmartRefreshLayout.finishRefresh(false);
                return;
            }
            loadData(true);
        });
    }

    /**
     * 设置adapter
     */

    private void initAdapter() {
        mLinearLayoutManager = new FastScrollLinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new TrendAdapter(mActivity, this);
        mAdapter.addChildClickViewIds(R.id.dynamic_item_image_root_view, R.id.iv_comment, R.id.tv_like_count, R.id.tv_content, R.id.iv_avatar, R.id.tv_name, R.id.btn_like, R.id.tv_discuss_count, R.id.iv_share, R.id.rl_accost, R.id.tv_del);
        mAdapter.addChildLongClickViewIds(R.id.tv_content);
        mAdapter.onAttachedToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        setListener();
    }

    private void setListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mClickPosition = position;
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            CircleBean item = mAdapter.getItem(position);
            int id = view.getId();
            if (id == R.id.iv_avatar || id == R.id.tv_name) {
                //点击头像跳转到动态详情
                if (item != null) {
                    if (Objects.equals(MineTrendActivity.class, AppManager.getAppManager().currentActivity().getClass())) {
                        mActivity.finish();
                    } else {
                        ARouterUtils.toMineDetailActivity(String.valueOf(item.getUser_id()));
                    }
                }
            } else if (id == R.id.btn_like || id == R.id.tv_like_count) {
                if (item != null && item.getIs_praise() == 0) {
                    //点赞
                    p.blogPraise(position, item.getBlog_id());
                } else {
                    toastTip("您之前已经点赞过该动态 不能再点赞了！");
                }
            } else if (id == R.id.iv_share) {
                BottomShareDialog mBottomShareDialog = BottomShareDialog.newInstance();
                assert getFragmentManager() != null;
                mBottomShareDialog.show(getFragmentManager(), "dialog");
            } else if (id == R.id.rl_accost) {
                assert item != null;
                if (item.isAccost()) {// 搭讪过 进入聊天
                    if (service.getUserInfo().isGirl()) {   //判断女性
                        p.getMsgCheck(item.getUser_id() + "");
                    } else {
                        ARouterUtils.toChatP2PActivity(item.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                    }
                } else { //没有搭讪, 进行搭讪
                    p.accostUser(position, String.valueOf(item.getUser_id()), CommonUtil.isBtnTextTrend(service, item.getSex() == 1) ? 1 : 2);
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("搭讪按钮点击事件", "accost_click", "", JSON.toJSONString(new NavigationLogEntity(CommonUtil.getOSName(), Constants.APP_NAME, service.getUserId(), String.valueOf(item.getUser_id()), CommonUtil.isBtnTextTrend(service, item.getSex() == 1) ? 2 : 1, 3, System.currentTimeMillis())), null);

                }
            } else if (id == R.id.tv_del) {   //删除动态
                assert item != null;
                CustomPopWindow customPopWindow = new CustomPopWindow(getActivity());
                customPopWindow.setContent("删除该动态?");
                customPopWindow.setRightButton("删除");
                customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        p.delTrend(position, item.getBlog_id());
                    }
                });
                customPopWindow.showPopupWindow();

            } else if (id == R.id.dynamic_item_image_root_view || id == R.id.tv_discuss_count || id == R.id.tv_content || id == R.id.iv_comment) { //点击item其他区域
                if (item != null) {
                    if (ClickUtil.canOperate()) {
                        Utils.logE("ClickUtil.canOperate()");
                        p.get_info(item.getBlog_id());
                    }
                }
            }
        });

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mVideoView.isPlaying()) {
                    //Utils.logE("视频播放位置:" + mCurPos + "   firstVisible=" + firstVisible + "  lastVisible=" + lastVisible);
                    if (mCurPos < mLinearLayoutManager.findFirstVisibleItemPosition()
                            || mCurPos > mLinearLayoutManager.findLastVisibleItemPosition()) {//正在播放的视频item 被移除了屏幕 不可见
                        videoPause();
                    }
                }
            }
        });*/
    }

    /*@SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        private MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    try {
                        Bitmap bitmap = (Bitmap) msg.obj;
                        if (Utils.saveBitmap(bitmap, dstPath, false)) {
                            try {
                                ContentValues values = new ContentValues(2);
                                values.put(MediaStore.Images.Media.MIME_TYPE, Constants.MIME_JPEG);
                                values.put(MediaStore.Images.Media.DATA, dstPath);
                                getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                toastTip(getString(R.string.picture_save_to));
                            } catch (Exception e) {
                                toastTip(getString(R.string.picture_save_fail));
                            }
                        } else {
                            toastTip(getString(R.string.picture_save_fail));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastTip(getString(R.string.picture_save_fail));
                    }
                }
            }
        }
    }*/


    /**
     * 搭讪成功
     *
     * @param data data
     */
    @Override
    public void accostUserSuccess(int position, AccostDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CircleBean item = mAdapter.getItem(position);
            //RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS, data.gift.animation));
            if (item != null) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, item.getUser_id()));
                RxBus.getDefault().post(new CommentPraiseAccostEvent(item.getUser_id()));
                RxBus.getDefault().post(new AccostSuccessEvent(AccostSuccessEvent.ACCOUST_MOMENT, item.getUser_id() + "", item.getNickname(), item.getIcon()));
                CommonUtil.sendAccostGirlBoy(service, item.getUser_id(), data, 4);
            }
           /* mAdapter.getItem(position).setIs_accost(1);
            mAdapter.startAnimation(recyclerView, position);*/
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
        }
    }

    /**
     * 点赞成功
     */
    @Override
    public void praiseSuccess(int position, Boolean data) {
        if (data != null && data) {
            CircleBean dataBean = mAdapter.getData().get(position);
            dataBean.setPraises(dataBean.getPraises() + 1);
            dataBean.setIs_praise(1);
            //mAdapter.notifyDataSetChanged();
            RxBus.getDefault().post(new CommentPraiseAccostEvent(dataBean.getBlog_id(), dataBean.getPraises()));
        }
    }


    /**
     * 删除动态成功
     *
     * @param position pos
     * @param data     data
     */
    @Override
    public void deleteTrendSuccess(int position, Boolean data) {
        if (data) {
            toastTip("删除成功");
            boolean playing = mVideoView.isPlaying();
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_TREND_SUCCESS));
            mAdapter.remove(position);
            if (playing) {
                if (position != mCurPos) {
                    if (position < mCurPos) {
                        releaseVideoView();
                    }
                } else {
                    releaseVideoView();
                }
            }
        }
    }

    /**
     * 获取动态列表成功
     *
     * @param data data
     */
    @Override
    public void getTrendSuccess(List<CircleBean> data) {
        canLoadMore = true;
        mSmartRefreshLayout.finishRefresh(true);
        mSmartRefreshLayout.finishLoadMore(true);
        mSmartRefreshLayout.setEnableLoadMore(data != null && data.size() > 0);
        setData(mPage, data);
        videoPause();
        videoResume();
        if (mPage == 1) {
            //滑动到最顶部
            if (recyclerView != null) {
//                recyclerView.smoothScrollToPosition(0);

        /*        LinearLayoutManager mLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mLayoutManager != null) {
                    mLayoutManager.scrollToPositionWithOffset(15, 0);
                }*/
//                recyclerView.smoothScrollToPosition(0);
            }

        }

//        mCurPos = -1;
//        autoPlayVideo(recyclerView);
    }

    @Override
    public void getTrendError() {
        mSmartRefreshLayout.finishRefresh(false);
        mSmartRefreshLayout.finishLoadMore(false);
    }

    @Override
    public void getInfoSucess(CircleBean data) {
        mAdapter.updateData(mClickPosition, data);
        TrendDetailActivity.startInstanceActivity(mActivity, data);
    }

    @Override
    public void getInfoFial(String msg) {
        if (mTipsPopWindow == null) {
            mTipsPopWindow = new CustomPopWindow(mContext);
        }
        mTipsPopWindow.setContent(msg)
                .setSingleButtong()
                .showPopupWindow();
    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
        }
    }

    @Override
    public void getTrendFail(int code, String msg) {
        canLoadMore = true;
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
        if (mAdapter.getData().size() <= 0)
            mAdapter.setEmptyView(getListEmptyView());
    }

    @Override
    public void commentSuccess(boolean data) {
    }

    @Override
    public void attentionSuccess(boolean data) {
    }


    private void setData(int pageIndex, List<CircleBean> data) {
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(getListEmptyView());
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            } else {
                mPage -= 1;
            }
        }
//        recyclerView.setItemViewCacheSize(mAdapter.getItemCount());
    }


    @Override
    public void onPause() {
        super.onPause();
        videoPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoResume();
    }

    /**
     * 开始播放
     *
     * @param position 列表位置
     */

    private void startPlay(int position) {
        if (position >= mAdapter.getItemCount()) {
            return;
        }
        if (mAdapter.getData().size() <= 0)
            return;
        CircleBean circleBean = mAdapter.getData().get(position);
        if (circleBean == null) {
            return;
        }
        Utils.logE("mCurPos=" + mCurPos + ", 开始第" + position + "个播放,title=\"" + circleBean.getContent() + "\", url=  " + circleBean.getMedia().get(0));
        if (mCurPos == position) return;
        if (mCurPos != -1) {
            releaseVideoView();
        }
        if (mAdapter.getItemCount() == 0) {
            return;
        }
        String proxyUrl = BaseApplication.getProxy(mContext).getProxyUrl(circleBean.getMedia().get(0));
        mVideoView.setUrl(proxyUrl);
        View itemView = mLinearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        TrendAdapter.TendHolder tendHolder = (TrendAdapter.TendHolder) itemView.getTag();
        mController.addControlComponent(tendHolder.mPrepareView, true);
        Utils.removeViewFormParent(mVideoView);
        tendHolder.mPlayerContainer.addView(mVideoView, 0);
        Utils.logE("正在播放... ");
        mVideoView.start();
        mVideoView.setMute(true);
        if (!NetworkUtils.isConnected()) {
            mVideoView.onError();
        }
        mCurPos = position;
        Utils.logE("mCurPos=" + position);
    }

    private void videoResume() {
        if (mLastPos == -1) {
            return;
        }
        //恢复上次播放的位置
        startPlay(mLastPos);
    }

    protected void videoPause() {
        if (mVideoView != null && !mVideoView.isPlaying()) {
            mLastPos = -1;
        }
        releaseVideoView();
    }

    private void releaseVideoView() {
        if (mVideoView != null) {
            mVideoView.release();
        }
        mCurPos = -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseVideoView();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mSrcollowInterface = null;
    }
}
