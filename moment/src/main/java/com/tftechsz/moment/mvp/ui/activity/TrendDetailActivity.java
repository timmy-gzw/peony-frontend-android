package com.tftechsz.moment.mvp.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.like.LikeButton;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.robinhood.ticker.TickerView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.UserViewInfo;
import com.tftechsz.common.entity.VideoInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.player.controller.MyVideoController;
import com.tftechsz.common.player.other.VideoClickEvent;
import com.tftechsz.common.player.view.MyErrorView;
import com.tftechsz.common.player.view.PrepareView;
import com.tftechsz.common.player.view.VideoView;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ConstraintUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.MaxTextTwoLengthFilter;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.TrendImageAdapter;
import com.tftechsz.moment.mvp.IView.IDynamicView;
import com.tftechsz.moment.mvp.presenter.DynamicRecommendPresenter;
import com.tftechsz.moment.mvp.ui.fragment.TrendCommentFragment;
import com.tftechsz.moment.other.CommentEvent;
import com.tftechsz.moment.widget.TrendDetailPopWindow;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import java.util.ArrayList;
import java.util.List;

import razerdp.util.KeyboardUtils;

public class TrendDetailActivity extends BaseMvpActivity<IDynamicView, DynamicRecommendPresenter> implements View.OnClickListener, IDynamicView {
    private CircleBean dataBean;
    //图片展示区
    private RecyclerView mRvTrendImage;
    //头像
    //private ImageView dynamicDetailavatar;
    //昵称
    private TextView dynamicDetailname;
    //性别
    private TextView dynamicDetailsex;
    //是否真人
    private ImageView dynamicDetailreal_people;
    //是否VIP
    private ImageView ivVip;
    //评论数
    private TickerView dynamicDetailcommentNum;
    //点赞数
    private TickerView dynamicDetailpraise;
    //动态评论
    private EditText mEtComment;
    private TextView mTvContent;   //动态内容
    private TextView mTvTime;
    private TextView mTvDel;

    private TextView mTvTrendTimeAddressInfo;

    private GridLayoutManager gridLayoutManager;
    private int comment_id;
    @Autowired
    UserProviderService service;
    private TextView mTvSend;
    private RelativeLayout mBot_input;
    private LinearLayout mRootview;
    private CustomPopWindow mTipsPopWindow;
    private LikeButton mLikeButton;
    private RelativeLayout mRlAccost;
    private FrameLayout mPlayerContainer;
    private PrepareView mPrepareView;
    private ImageView mThumb, mVideoBg;
    protected VideoView mVideoView;
    private ImageView mIvComment;
    private ConstraintLayout mDynamic_item_image_root_view;
    private ImageView mIvAvatar;

    @Override
    protected int getLayout() {
        return R.layout.activity_trend_detail;
    }

    @Override
    public DynamicRecommendPresenter initPresenter() {
        return new DynamicRecommendPresenter();
    }

    public static void startInstanceActivity(Activity context, CircleBean mDataBean) {
        startInstanceActivity(context, mDataBean, null);
    }

    public static void startInstanceActivity(Activity context, CircleBean mDataBean, Pair<View, String>[] sharedElements) {
        Intent intent = new Intent(context, TrendDetailActivity.class);
        intent.putExtra(Interfaces.EXTRA_DATA, mDataBean);
        if (sharedElements == null || sharedElements.length == 0) {
            context.startActivity(intent);
        } else {
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, sharedElements).toBundle());
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        dataBean = (CircleBean) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
        initViewById();
        setOnClick();
        initRxBus();
        if (service.getUserId() != dataBean.getUser_id()) {
            new ToolBarBuilder().showBack(true)
                    .setTitle("动态详情")
                    .setRightImg(R.mipmap.ic_more, v -> {
                        TrendDetailPopWindow popWindow = new TrendDetailPopWindow(TrendDetailActivity.this, dataBean.getBlog_id(), dataBean.is_follow);
                        popWindow.addOnClickListener(() -> p.attentionUser(dataBean.getUser_id()));
                        popWindow.showPopupWindow(findViewById(R.id.toolbar_iv_menu));
                    })
                    .build();
        } else {
            new ToolBarBuilder().showBack(true)
                    .setTitle("动态详情")
                    .build();

        }

        listenKeyboardVisible();
    }

    private void listenKeyboardVisible() {
        final View activityRoot = getWindow().getDecorView();
        if (activityRoot == null) {
            return;
        }

        mRootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r);
                int diff = activityRoot.getRootView().getHeight() - r.height();
                //键盘是否弹出
                boolean isOpen = (diff > 200);
                if (isOpen) {//键盘弹起
                    mEtComment.requestFocus();
                } else {//键盘关闭
                    comment_id = 0;
                    mEtComment.setHint("请输入评论…");
                    mEtComment.clearFocus();
                }
            }
        });
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommentEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            switch (event.getType()) {
                                case 0: //回复
                                    com.blankj.utilcode.util.KeyboardUtils.showSoftInput();
                                    comment_id = event.getComment_id();
                                    mEtComment.setHint("回复@" + event.getUser_name());
                                    break;

                                case -1: //删除
                                    dataBean.setComments(dataBean.getComments() - 1);
                                    dynamicDetailcommentNum.setAnimationDuration(dataBean.getComments() == 0 ? 0 : Interfaces.TICKERVIEW_ANIMATION_LIKE);
                                    dynamicDetailcommentNum.setText(dataBean.getComments() == 0 ? "评论" : String.valueOf(dataBean.getComments()));
                                    break;
                            }
                        }
                ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(VideoClickEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (mVideoView != null && mVideoView.isPlaying()) {
                                VideoInfo info = new VideoInfo(dataBean.getMedia().get(0));
                                Utils.startTrendVideoViewActivity(mActivity, mPlayerContainer, info);
                            }
                        }
                ));
    }

    private void initViewById() {
        mDynamic_item_image_root_view = findViewById(R.id.dynamic_item_image_root_view);
        mRootview = findViewById(R.id.root_view);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mRvTrendImage = findViewById(R.id.rv_trend_image);
        mPlayerContainer = findViewById(R.id.player_container);
        mPrepareView = findViewById(R.id.prepare_view);
        mThumb = mPrepareView.findViewById(R.id.thumb);
        mRvTrendImage.setLayoutManager(gridLayoutManager);
        //dynamicDetailavatar = findViewById(R.id.iv_photo);
        dynamicDetailname = findViewById(R.id.tv_name);
        dynamicDetailsex = findViewById(R.id.iv_sex);
        dynamicDetailreal_people = findViewById(R.id.iv_real);
        ivVip = findViewById(R.id.img_vip_dii);
        dynamicDetailcommentNum = findViewById(R.id.tv_discuss_count);
        // mVideoBg = findViewById(R.id.video_bg);
        mIvComment = findViewById(R.id.iv_comment);
        mLikeButton = findViewById(R.id.btn_like);
        dynamicDetailpraise = findViewById(R.id.tv_like_count);
        mTvContent = findViewById(R.id.tv_content);
        mTvDel = findViewById(R.id.tv_del);
        mTvTime = findViewById(R.id.tv_time);
        mEtComment = findViewById(R.id.et_comment);  //评论
        mEtComment.setFilters(new InputFilter[]{new MaxTextTwoLengthFilter(200, this)});

        mBot_input = findViewById(R.id.root_bot_input);
//        InputFilter mInputFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                return source.toString().replace("\n", "");
//            }
//        };
//        mEtComment.setFilters(new InputFilter[]{mEtComment.getFilters()[0],mInputFilter});
        mTvSend = findViewById(R.id.tv_send);//发送
        mRlAccost = findViewById(R.id.rl_accost);
        mTvTrendTimeAddressInfo = findViewById(R.id.tv_address);  // 时间浏览次数，地址
    }

    private void setOnClick() {
        mIvAvatar.setOnClickListener(this);
        mLikeButton.setOnClickListener(this);
        dynamicDetailcommentNum.setOnClickListener(this);
        mIvComment.setOnClickListener(this);
        dynamicDetailpraise.setOnClickListener(this);
        mTvDel.setOnClickListener(this);
        findViewById(R.id.rl_accost).setOnClickListener(this);
        mTvSend.setOnClickListener(this);
        mEtComment.setOnKeyListener((v, keyCode, event) -> {        // 开始搜索
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                KeyboardUtils.close(this);
                return true;
            }
            return false;
        });
    }


    @Override
    protected void initData() {
        super.initData();
        setData();
    }

    private void setData() {
        if (dataBean == null) return;
//        GlideUtils.loadRoundImageRadius(this, mIvAvatar.getImageView(), dataBean.getIcon());
//        mIvAvatar.setBgFrame(dataBean.picture_frame);
        GlideUtils.loadRoundImageRadius(this, mIvAvatar, dataBean.getIcon());
        CommonUtil.setUserName(dynamicDetailname, dataBean.getNickname(), false, dataBean.isVip());

        //内容
        String content = dataBean.getContent().trim();
        if (!TextUtils.isEmpty(content)) {
            mTvContent.setText(content);
            mTvContent.setMaxLines(Integer.MAX_VALUE);
        } else {
            mTvContent.setVisibility(View.GONE);
        }
        //1.男，2.女
        int sex = dataBean.getSex();
        CommonUtil.setSexAndAge(this, sex, dataBean.age, dynamicDetailsex);
        dynamicDetailreal_people.setVisibility(dataBean.getIs_real() == 1 ? View.VISIBLE : View.GONE);  //是否真人
        ivVip.setVisibility(dataBean.isVip() ? View.VISIBLE : View.GONE);  //是否vip
        mLikeButton.setLiked(dataBean.getIs_praise() == 1);//点赞了
        dynamicDetailpraise.setText(dataBean.getPraises() == 0 ? "点赞" : String.valueOf(dataBean.getPraises()));
        //评论数
        dynamicDetailcommentNum.setText(dataBean.getComments() == 0 ? "评论" : String.valueOf(dataBean.getComments()));
        mTvDel.setVisibility(service.getUserId() == dataBean.getUser_id() ? View.VISIBLE : View.GONE); //自己的动态才可删除

        //搭讪是否显示
        mRlAccost.setVisibility(service.getUserId() == dataBean.getUser_id() ? View.INVISIBLE : View.VISIBLE);    //自己的动态不可发消息
        mTvTime.setText(dataBean.getCreated_at());
        mTvTrendTimeAddressInfo.setText(dataBean.getCity());
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        new ConstraintUtil(mDynamic_item_image_root_view)
                .begin()
                .Left_toLeftOf(R.id.iv_sex, R.id.tv_name)
                .Top_toBottomOf(R.id.iv_sex, R.id.tv_name)
                .setMarginTop(R.id.iv_sex, ConvertUtils.dp2px(6))
                .commit();
        ViewPager mViewPager = findViewById(R.id.vp);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(TrendCommentFragment.getInstance(dataBean.getBlog_id(), dataBean.getUser_id()));//评论
        // fragments.add(TrendLikeFragment.getInstance(dataBean.getBlog_id()));//点赞
        List<String> titles = new ArrayList<>();
        titles.add("评论");
        // titles.add("获赞");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(titles.size());
        mViewPager.setCurrentItem(0);

        mRvTrendImage.setVisibility(View.GONE);
        mPlayerContainer.setVisibility(View.GONE);
        if (dataBean.getMedia() == null || dataBean.getMedia().size() == 0) {
            return;
        }
        if (dataBean.getMedia().size() == 1) {
            switch (dataBean.getType()) {
                case 1: //视频
                    setVideoView();
                    break;

                case 2: //音频
                    break;

                case 3: //图片
                    gridLayoutManager = new GridLayoutManager(this, 2);
                    mRvTrendImage.setLayoutManager(gridLayoutManager);
                    setImgAdapter();
                    break;
            }
        } else {
            gridLayoutManager = new GridLayoutManager(this, 3);
            mRvTrendImage.setLayoutManager(gridLayoutManager);
            setImgAdapter();
        }
        p.trendViewCount(String.valueOf(dataBean.getBlog_id()));
    }

    private void setVideoView() {
        if (dataBean.getVideo_size() == null || dataBean.getVideo_size().size() < 2) {
            return;
        }
        mVideoView = new VideoView(mActivity);
        MyVideoController controller = new MyVideoController(mActivity);
        controller.addControlComponent(new MyErrorView(mContext));
        mVideoView.setVideoController(controller);
        mVideoView.setLooping(true);

        mPlayerContainer.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams lp = Utils.filterVideoWH(mPlayerContainer.getLayoutParams(),
                (int) (ScreenUtils.getScreenWidth() / Interfaces.VideoW), (int) (ScreenUtils.getScreenHeight() / Interfaces.VideoH), dataBean.getVideo_size().get(0), dataBean.getVideo_size().get(1));
        mPlayerContainer.setLayoutParams(lp);
        controller.addControlComponent(mPrepareView, true);
        //Utils.removeViewFormParent(mVideoView);
        GlideUtils.loadRouteImage(mContext, mThumb, dataBean.getMedia_mini() != null ? dataBean.getMedia_mini().get(0) : dataBean.getMedia().get(0), android.R.color.darker_gray);
        mPlayerContainer.addView(mVideoView, 0);
        Glide.with(mContext)
                .asBitmap()
                .load(dataBean.getMedia_mini() != null ? dataBean.getMedia_mini().get(0) : dataBean.getMedia().get(0))
                .placeholder(android.R.color.darker_gray)
                .into(mThumb);

        String proxyUrl = BaseApplication.getProxy(mContext).getProxyUrl(dataBean.getMedia().get(0));
        mVideoView.setUrl(proxyUrl);
        mVideoView.start();
        mVideoView.setMute(true);
    }

    private void setImgAdapter() {
        mRvTrendImage.setVisibility(View.VISIBLE);
        ArrayList<UserViewInfo> userViewInfos = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>(dataBean.getMedia());
        if (dataBean.getMedia_mini() != null) {
            for (String url : dataBean.getMedia_mini()) {
                userViewInfos.add(new UserViewInfo(url));
            }
        } else {
            for (String url : dataBean.getMedia()) {
                userViewInfos.add(new UserViewInfo(url));
            }
        }
        TrendImageAdapter adapter = new TrendImageAdapter(userViewInfos);
        mRvTrendImage.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) ->
                Mojito.with(mContext)
                        .urls(urls)
                        .position(position, 0, urls.size())
                        .views(mRvTrendImage, urls.size() == 1 ? R.id.iv1_5 : R.id.iv1)
                        .autoLoadTarget(false)
                        .setProgressLoader(DefaultPercentProgress::new)
                        .setIndicator(new NumIndicator(mActivity))
                        .start());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_avatar) {
            ARouterUtils.toMineDetailActivity(String.valueOf(dataBean.getUser_id()));
        } else if (id == R.id.rl_accost) {  //搭讪私信
            if (dataBean.isAccost()) {// 搭讪过 进入聊天
                if (service.getUserInfo().isGirl()) {   //判断女性
                    p.getMsgCheck(dataBean.getUser_id() + "");
                } else {
                    ARouterUtils.toChatP2PActivity(dataBean.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                }
            } else { //没有搭讪, 进行搭讪
                p.accostUser(0, String.valueOf(dataBean.getUser_id()), CommonUtil.isBtnTextTrend(service, dataBean.getSex() == 1) ? 1 : 2);
            }
        } else if (id == R.id.tv_discuss_count || id == R.id.iv_comment) {  //评论
            Utils.runOnUiThread(() -> KeyboardUtils.open(mEtComment));
        } else if (id == R.id.btn_like || id == R.id.tv_like_count) {   //点赞
            if (dataBean.getIs_praise() == 0) {
                //点赞
                p.blogPraise(0, dataBean.getBlog_id());
            } else {
                toastTip("您之前已经点赞过该动态 不能再点赞了！");
            }
        } else if (id == R.id.tv_del) { //删除评论
            CustomPopWindow customPopWindow = new CustomPopWindow(this);
            customPopWindow.setContent("删除该动态?");
            customPopWindow.setRightButton("删除");
            customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSure() {
                    p.delTrend(0, dataBean.getBlog_id());
                }
            });
            customPopWindow.showPopupWindow();
        } else if (id == R.id.tv_send) {   //发送
            if (!ClickUtil.canOperate()) {
                return;
            }
            String comment = Utils.getText(mEtComment);
            if (!TextUtils.isEmpty(comment)) {
                CommonUtil.getToken(new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
                    @Override
                    public void onSure() {
                        getP().trendComment(dataBean.getBlog_id(), comment_id, comment);
                    }
                });
            }
        }
    }

    /**
     * 评论成功
     *
     * @param data data
     */
    @Override
    public void commentSuccess(boolean data) {
        if (data) {
            toastTip("评论成功");
            RxBus.getDefault().post(new CommentEvent(2, dataBean.getBlog_id()));
            com.blankj.utilcode.util.KeyboardUtils.hideSoftInput(getWindow());
            mEtComment.setText("");
            dynamicDetailcommentNum.setAnimationDuration(dataBean.getComments() == 0 ? 0 : Interfaces.TICKERVIEW_ANIMATION_LIKE);
            dataBean.setComments(dataBean.getComments() + 1);
            dynamicDetailcommentNum.setText(String.valueOf(dataBean.getComments()));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_TREND_COMMENT_SUCCESS));
        }
    }

    @Override
    public void attentionSuccess(boolean data) {
        if (dataBean != null) {
            dataBean.is_follow = data ? 1 : 0;
        }
        if (data) {
            toastTip("关注成功");
        } else {
            toastTip("取消关注成功");
        }
    }

    @Override
    public void accostUserSuccess(int position, AccostDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            if (data != null && data.gift != null) {
                Utils.playAccostAnimationAndSound(data.gift.name, data.gift.animation);
            }
            dataBean.setIs_accost(1);
            CommonUtil.sendAccostGirlBoy(service, dataBean.getUser_id(), data, 4);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, dataBean.getUser_id()));
        }
    }

    @Override
    public void praiseSuccess(int position, Boolean data) {
        if (data) {
            mLikeButton.onClick(null);
            dataBean.setIs_praise(1);
            RxBus.getDefault().post(new CommentEvent(1, dataBean.getBlog_id()));
            dynamicDetailpraise.setAnimationDuration(dataBean.getPraises() == 0 ? 0 : Interfaces.TICKERVIEW_ANIMATION_LIKE);
            dynamicDetailpraise.setText(String.valueOf(dataBean.getPraises() + 1));
        }
    }

    @Override
    public void deleteTrendSuccess(int position, Boolean data) {
        if (data) {
            toastTip("删除成功");
            RxBus.getDefault().post(new CommentEvent(-2, dataBean.getBlog_id()));
            finish();
        }
    }

    @Override
    public void getTrendSuccess(List<CircleBean> data) {

    }

    @Override
    public void getTrendError() {

    }

    @Override
    public void getInfoSucess(CircleBean data) {
        dataBean = data;
        setData();
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

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
        }
    }

    @Override
    public void getTrendFail(int code, String message) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) { //点击外部
                Utils.runOnUiThread(() -> com.blankj.utilcode.util.KeyboardUtils.hideSoftInput(mBot_input));
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            mBot_input.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = ScreenUtils.getScreenWidth();

            // 点击EditText的事件，忽略它。
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    @Override
    public void showLoadingDialog() {
        runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "发送中..."));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
        if (mRootview != null) {
            mRootview.removeAllViews();
            mRootview = null;
        }
        //Utils.finishAfterTransition(mActivity);
    }
}
