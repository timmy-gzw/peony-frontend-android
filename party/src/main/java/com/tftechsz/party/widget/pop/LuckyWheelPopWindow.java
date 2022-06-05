package com.tftechsz.party.widget.pop;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gongwen.marqueen.SimpleMF;
import com.gongwen.marqueen.SimpleMarqueeView;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.CoinBean;
import com.tftechsz.party.entity.CostBean;
import com.tftechsz.party.entity.PartyLuckWheelBean;
import com.tftechsz.party.entity.TurntableStartBeforeBean;
import com.tftechsz.party.entity.WheelResultBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;

import static com.tftechsz.common.Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN;
import static com.tftechsz.party.mvp.ui.activity.PartyRoomActivity.mNumber;

/**
 * 幸运转盘
 */
public class LuckyWheelPopWindow extends BaseBottomPop implements View.OnClickListener, LuckyResultPopWindow.ILResultPopCallBack, BuyLuckyStonePopWindow.ILBuyLuckPopCallBack {

    //图片摆手
    private ImageView mImgHeaderTop, mImgHeaderBottom;
    //中心指针
    private ImageView mImgCenter;
    //开始三个按钮
    private RelativeLayout mClickBtn1, mClickBtn2, mClickBtn3;
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    //设置选中边框
    private final List<ImageView> listsVisible = new ArrayList<>();
    //图片设置集合
    private final List<ImageView> listsImg = new ArrayList<>();
    //礼物名称设置集合
    private final List<TextView> listsTvName = new ArrayList<>();
    //金币设置集合
    private final List<TextView> listsTvCoin = new ArrayList<>();
    //初级场 高级场tab
    private ImageView mImgCjc, mImgGjc;
    //转盘背景
    private ImageView mImgBg2, mImgBs1, mImgBs2;
    private ConstraintLayout constraintLayout, mImgBg3;
    //跑马灯
    private ImageView mImgShan;
    //转盘-排行榜
    private RankingPopWindow rankingPopWindow;

    private final PartyApiService service;
    private final PartyApiService userService;
    private final CompositeDisposable mCompositeDisposable;
    private PartyLuckWheelBean partyLuckWheelBean;
    //开始按钮文字
    private TextView mTvCoinBtn1, mTvCoinText1, mTvCoinBtn2, mTvCoinText2, mTvCoinBtn3, mTvCoinText3;
    //充值弹窗
    private RechargePopWindow rechargePopWindow;
    private LinearLayout mLinBroadcastBg;
    /**
     * 0、初级场    1、高级场
     */
    private int mTagPosition;
    private TextView mTvCoinBottom;
    // 10s 自动停止开始动画-后台不通知
    private int mTimeout;
    //结果通知弹窗
    private LuckyResultPopWindow luckyResultPopWindow;
    private PartyRecordPopWindow partyLuckyRecordPopWindow;
    private final String roomId;
    //bg 幸运转盘
    private RelativeLayout mRelBgItem1, mRelBgItem2, mRelBgItem3, mRelBgItem4, mRelBgItem5,
            mRelBgItem6, mRelBgItem7, mRelBgItem8;

    //延迟时间
    private long mTimeDelay;
    //动画位置
    private static int mTagAniPosition;
    //第三圈加速
    private static int mTagCirclePosition;

    //结果通知停止快速转动，慢速一圈结束
    private boolean mFlagIsResult;

    private final ICallBackGiftResult iCallBackGiftResult;
    //停留礼物位置
    private int mIndexGift;
    private WheelResultBean wheelResultBean;
    private SimpleMarqueeView mTvContactWay;
    //开始动画中
    private boolean mFlagIsStart;
    private ImageView mImgHintBg1, mImgNotify, mImgRanking, mImgTitle, mImgClickRule, mImgRecord, mImgCoinAdd, mImgTabBg;//高级场闪光覆盖图片
    private boolean mIsActivityBg;//活动皮肤
    private boolean mInitView;//第一次初始化活动view
    private TextView mTvLuckyStone;
    private boolean mIsNetBeforeLoading;//before 请求中
    private LinearLayout mLinCoinLuckyStone;//幸运石背包
    private String mCoin;//金币余额
    private BuyLuckyStonePopWindow buyLuckyStonePopWindow;
    private ImageView mImgCoinLuckyStone;

    public LuckyWheelPopWindow(Context context, String roomId, ICallBackGiftResult iCallBackGiftResult, boolean flagIsActivityBg) {
        super(context);
        mContext = context;
        this.mIsActivityBg = flagIsActivityBg;
        this.roomId = roomId;
        this.iCallBackGiftResult = iCallBackGiftResult;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        userService = RetrofitManager.getInstance().createUserApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
        initRxBus();
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        if (mTvContactWay != null) {
            mTvContactWay.startFlipping();
        }

        WheelAdapter();
        startMarquee();
        net();
        coinNet();
        selectCostLuckyStone();
        setConRootHeight();
    }

    /**
     * 查询幸运石
     */
    private void selectCostLuckyStone() {
        mCompositeDisposable.add(userService
                .selectCostLuckyStone(mTagPosition + 1)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CostBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<CostBean> response) {
                        if (response != null && response.getData() != null) {
                            mTvLuckyStone.setText((mTagPosition == 0 ? "初级幸运石 " : "高级幸运石 ") + response.getData().cost);
                        }
                    }
                }));

    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT) {
                                wheelResultBean = JSON.parseObject(event.code, WheelResultBean.class);
                                mFlagIsResult = true;
                                mTimeout = 0;
                                if (handler != null) {
                                    handler.removeMessages(2);
                                    mTimeout = 0;
                                    //停止位置放在handler里面动画
                                }
                                //查询金币
                                coinNet();
                                selectCostLuckyStone();
                            } else if (event.type == NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN) {
                                if (isShowing()) {
                                    //再来一次
                                    if (event.code.equals("10")) {
                                        startBeforePrepare(2);
                                    } else if (event.code.equals("100")) {
                                        startBeforePrepare(3);
                                    } else {
                                        startBeforePrepare(1);
                                    }
                                }

                            }
                        }
                ));


    }


    /**
     * 是否圣诞活动
     */
    public void setIsActivityBg(boolean isActivityBg) {
        this.mIsActivityBg = isActivityBg;
    }

    private void net() {
        mCompositeDisposable.add(service
                .getLuckConfig()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyLuckWheelBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyLuckWheelBean> response) {
                        LuckyWheelPopWindow.this.partyLuckWheelBean = response.getData();
                        WheelAdapter();
                        //设置通知栏目
                        //SimpleMarqueeView<T>，SimpleMF<T>：泛型T指定其填充的数据类型，比如String，Spanned等
                        SimpleMF<String> marqueeFactory = new SimpleMF(mContext);
                        marqueeFactory.setData(response.getData().reward_list);
                        mTvContactWay.setMarqueeFactory(marqueeFactory);
                        mTvContactWay.startFlipping();
                    }


                }));

    }


    private void coinNet() {
        if (mCompositeDisposable == null || userService == null) {
            return;
        }
        mCompositeDisposable.add(userService
                .getCoinConfig("coin")
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CoinBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<CoinBean> response) {
                        CoinBean partyLuckWheelBean = response.getData();
                        mCoin = partyLuckWheelBean.coin;
                        mTvCoinBottom.setText(partyLuckWheelBean.coin + "");
                    }

                }));

    }

    /**
     * 设置动态距离
     */
    private void setLayoutPar() {
        ConstraintLayout.LayoutParams layoutParamsCjc = (ConstraintLayout.LayoutParams) mImgCjc.getLayoutParams();
        layoutParamsCjc.leftMargin = DensityUtils.dp2px(mContext, mIsActivityBg ? 19 : 34);
        mImgCjc.setLayoutParams(layoutParamsCjc);

        ConstraintLayout.LayoutParams layoutParamsGjc = (ConstraintLayout.LayoutParams) mImgGjc.getLayoutParams();
        layoutParamsGjc.rightMargin = DensityUtils.dp2px(mContext, mIsActivityBg ? 19 : 34);
        mImgGjc.setLayoutParams(layoutParamsGjc);
    }

    /**
     * 初级场数据
     */
    private void WheelAdapter() {
        mTagPosition = 0;
        if (mImgCjc == null) {
            return;
        }
        selectCostLuckyStone();
        mImgCoinLuckyStone.setImageResource(R.drawable.party_lucky_stone);
        Shader shader = new LinearGradient(0, 0, 0, mTvLuckyStone.getLineHeight(),
                Color.parseColor("#ffffff"), Color.parseColor("#A6D9E3"), Shader.TileMode.REPEAT);
        mTvLuckyStone.getPaint().setShader(shader);
        loadImageNew(mImgCjc, getResources(1, 1), mIsActivityBg ? R.drawable.activity_party_pop_chujichang : R.drawable.party_pop_chujichang);
        loadImageNew(mImgGjc, getResources(2, 2), mIsActivityBg ? R.drawable.activity_party_pop_gaojichang : R.drawable.party_pop_gaojichang);
        setLayoutPar();
        mImgNotify.setImageResource(mIsActivityBg ? R.drawable.activity_party_laba_pop : R.drawable.party_laba_pop);

        constraintLayout.setBackgroundResource(getResources(3, 1));

        mImgBg2.setImageResource(getResources(4, 1));

        mImgBg3.setBackgroundResource(getResources(6, 1));
        mImgCenter.setImageResource(getResources(5, 1));

        mClickBtn1.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_1 : R.drawable.party_btn_bg_1);
        mClickBtn2.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_1 : R.drawable.party_btn_bg_1);
        mClickBtn3.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_2 : R.drawable.party_btn_bg_2);

        mImgBs1.setImageResource(R.drawable.party_bashou_top);
        mImgBs2.setImageResource(R.drawable.party_bashou_bottom);
        mImgShan.setBackgroundResource(R.drawable.ani_marquee);

        mTvCoinBtn1.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone : R.drawable.party_lucky_stone), null, null, null);
        mTvCoinText2.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone : R.drawable.party_lucky_stone), null, null, null);
        mTvCoinText3.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone : R.drawable.party_lucky_stone), null, null, null);
        mTvCoinBtn1.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_lv : R.color.shadow_3c3fce_def);
        mTvCoinText2.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_lv : R.color.shadow_3c3fce_def);
        mTvCoinText3.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);

        mTvCoinBtn1.setTextColor(Color.parseColor(mIsActivityBg ? "#006066" : "#ffffff"));
        mTvCoinText2.setTextColor(Color.parseColor(mIsActivityBg ? "#006066" : "#ffffff"));
        mTvCoinText3.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));
        mTvCoinText1.setTextColor(Color.parseColor(mIsActivityBg ? "#006066" : "#ffffff"));
        mTvCoinBtn2.setTextColor(Color.parseColor(mIsActivityBg ? "#006066" : "#ffffff"));
        mTvCoinBtn3.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));

        mTvCoinText1.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_lv : R.color.shadow_3c3fce_def);
        mTvCoinBtn2.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_lv : R.color.shadow_3c3fce_def);
        mTvCoinBtn3.setShadowLayer(1, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);
        mLinBroadcastBg.setBackgroundResource(getResources(7, 1));
        mImgHintBg1.setVisibility(View.GONE);

        if (partyLuckWheelBean != null && partyLuckWheelBean.gift_list != null) {
            if (partyLuckWheelBean.gift_list.size() > 0) {
                int size = partyLuckWheelBean.gift_list.get(0).size();
                for (int i = 0; i < size; i++) {
                    GlideUtils.loadImage(mContext, listsImg.get(i), partyLuckWheelBean.gift_list.get(0).get(i).image);
                    listsTvName.get(i).setText(partyLuckWheelBean.gift_list.get(0).get(i).name);
                    listsTvCoin.get(i).setText(partyLuckWheelBean.gift_list.get(0).get(i).coin + "金币");
                    listsTvCoin.get(i).setTextColor(mIsActivityBg ? Color.parseColor("#33B6AE") : Color.parseColor("#6F88E0"));
                }
            }
        }
        if (partyLuckWheelBean != null && partyLuckWheelBean.cost_map != null) {
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(0) != null && partyLuckWheelBean.cost_map.get(0).size() > 0) {
                mTvCoinBtn1.setText(partyLuckWheelBean.cost_map.get(0).get(0).cost);
                mTvCoinText1.setTag(partyLuckWheelBean.cost_map.get(0).get(0).number);
                mTvCoinText1.setText(partyLuckWheelBean.cost_map.get(0).get(0).desc);
            }
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(0) != null && partyLuckWheelBean.cost_map.get(0).size() > 1) {
                mTvCoinText2.setText(partyLuckWheelBean.cost_map.get(0).get(1).cost + "个");
                mTvCoinText2.setTag(partyLuckWheelBean.cost_map.get(0).get(1).number);
                mTvCoinBtn2.setText(partyLuckWheelBean.cost_map.get(0).get(1).desc);
            }
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(0) != null && partyLuckWheelBean.cost_map.get(0).size() > 2) {
                mTvCoinText3.setText(partyLuckWheelBean.cost_map.get(0).get(2).cost + "个");
                mTvCoinText3.setTag(partyLuckWheelBean.cost_map.get(0).get(2).number);
                mTvCoinBtn3.setText(partyLuckWheelBean.cost_map.get(0).get(2).desc);
            }
        }
        mTvContactWay.setBackgroundColor(mIsActivityBg ? Color.parseColor("#0D5F6E") : Color.parseColor("#002074"));
        setBgItem();
        startMarquee();
        if (iCallBackGiftResult != null) {
            iCallBackGiftResult.tagItem(1);
        }

        mImgRanking.setImageResource(getResources(8, mIsActivityBg ? 1 : 2));
        mImgTitle.setImageResource(getResources(9, mIsActivityBg ? 1 : 2));
        mImgClickRule.setImageResource(getResources(10, mIsActivityBg ? 1 : 2));
        mImgRecord.setImageResource(getResources(11, mIsActivityBg ? 1 : 2));
        mImgCoinAdd.setImageResource(getResources(12, mIsActivityBg ? 1 : 2));
        mImgTabBg.setImageResource(getResources(13, mIsActivityBg ? 2 : 1));
        if (!mInitView && listsVisible != null && listsVisible.size() > 0 && mIsActivityBg) {
            int size = listsVisible.size();
            for (int i = 0; i < size; i++) {
                listsVisible.get(i).setImageResource(R.drawable.activity_party_ani_selectbg);
            }
            mInitView = true;
        }
    }


    /**
     * 高级场数据
     */
    private void WheelAdapterGjc() {
        mTagPosition = 1;
        mImgCoinLuckyStone.setImageResource(R.drawable.party_lucky_stone_gj);

        selectCostLuckyStone();
        loadImageNew(mImgCjc, getResources(1, 2), mIsActivityBg ? R.drawable.activity_party_gjc_tab1 : R.drawable.party_gjc_tab1);
        loadImageNew(mImgGjc, getResources(2, 1), mIsActivityBg ? R.drawable.activity_party_gjc_tab2 : R.drawable.party_gjc_tab2);
        Shader shader = new LinearGradient(0, 0, 0, mTvLuckyStone.getLineHeight(),
                Color.parseColor("#ffffff"), Color.parseColor("#D365FF"), Shader.TileMode.REPEAT);
        mTvLuckyStone.getPaint().setShader(shader);
        constraintLayout.setBackgroundResource(getResources(3, 2));
        mImgNotify.setImageResource(mIsActivityBg ? R.drawable.activity_party_laba_pop : R.drawable.party_laba_pop);
        mImgBg3.setBackgroundResource(getResources(6, 2));
        mImgBg2.setImageResource(getResources(4, 2));
        mImgCenter.setImageResource(getResources(5, 2));
        mClickBtn1.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_2 : R.drawable.party_btn_bg_2);
        mClickBtn2.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_2 : R.drawable.party_btn_bg_2);
        mClickBtn3.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_btn_bg_2_gjc : R.drawable.party_btn_bg_2_gjc);
        mImgBs1.setImageResource(R.drawable.party_bashou_top_tjc);
        mImgBs2.setImageResource(R.drawable.party_bashou_bottom_gjc);
        mImgShan.setBackgroundResource(R.drawable.ani_marquee_gjc);
        mTvCoinBtn1.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone_gj : R.drawable.party_lucky_stone_gj), null, null, null);
        mTvCoinText2.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone_gj : R.drawable.party_lucky_stone_gj), null, null, null);
        mTvCoinText3.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(mIsActivityBg ? R.drawable.party_lucky_stone_gj : R.drawable.party_lucky_stone_gj), null, null, null);
        mTvCoinBtn1.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);
        mTvCoinText2.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);
        mTvCoinText3.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_red : R.color.shadow_3c3fce_lan);
        mTvCoinBtn1.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));
        mTvCoinText2.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));
        mTvCoinText3.setTextColor(Color.parseColor(mIsActivityBg ? "#FFD0A2" : "#ffffff"));
        mTvCoinText1.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));
        mTvCoinBtn2.setTextColor(Color.parseColor(mIsActivityBg ? "#BE4715" : "#ffffff"));
        mTvCoinBtn3.setTextColor(Color.parseColor(mIsActivityBg ? "#FFD0A2" : "#ffffff"));
        mTvCoinText1.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);
        mTvCoinBtn2.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_huang : R.color.shadow_3c3fce_zi);
        mTvCoinBtn3.setShadowLayer(3, 0, 1, mIsActivityBg ? R.color.shadow_3c3fce_red : R.color.shadow_3c3fce_lan);
        mLinBroadcastBg.setBackgroundResource(getResources(7, 2));
        mImgHintBg1.setVisibility(View.VISIBLE);
        if (partyLuckWheelBean != null && partyLuckWheelBean.gift_list != null) {
            if (partyLuckWheelBean.gift_list.size() > 1) {
                int size = partyLuckWheelBean.gift_list.get(1).size();
                for (int i = 0; i < size; i++) {
                    GlideUtils.loadImage(mContext, listsImg.get(i), partyLuckWheelBean.gift_list.get(1).get(i).image);
                    listsTvName.get(i).setText(partyLuckWheelBean.gift_list.get(1).get(i).name);
                    listsTvCoin.get(i).setText(partyLuckWheelBean.gift_list.get(1).get(i).coin + "金币");
                    listsTvCoin.get(i).setTextColor(mIsActivityBg ? Color.parseColor("#FFA89E") : Color.parseColor("#976AE0"));
                }
            }
        }
        if (partyLuckWheelBean != null && partyLuckWheelBean.cost_map != null) {
            if (partyLuckWheelBean.cost_map.size() > 1 && partyLuckWheelBean.cost_map.get(1) != null && partyLuckWheelBean.cost_map.get(1).size() > 0) {
                mTvCoinBtn1.setText(partyLuckWheelBean.cost_map.get(1).get(0).cost + "个");
                mTvCoinText1.setTag(partyLuckWheelBean.cost_map.get(1).get(0).number);
                mTvCoinText1.setText(partyLuckWheelBean.cost_map.get(1).get(0).desc);
            }
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(1) != null && partyLuckWheelBean.cost_map.get(1).size() > 1) {
                mTvCoinText2.setText(partyLuckWheelBean.cost_map.get(1).get(1).cost + "个");
                mTvCoinText2.setTag(partyLuckWheelBean.cost_map.get(1).get(1).number);
                mTvCoinBtn2.setText(partyLuckWheelBean.cost_map.get(1).get(1).desc);
            }
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(1) != null && partyLuckWheelBean.cost_map.get(1).size() > 2) {
                mTvCoinText3.setText(partyLuckWheelBean.cost_map.get(1).get(2).cost + "个");
                mTvCoinText3.setTag(partyLuckWheelBean.cost_map.get(1).get(2).number);
                mTvCoinBtn3.setText(partyLuckWheelBean.cost_map.get(1).get(2).desc);
            }
        }

        mTvContactWay.setBackgroundColor(mIsActivityBg ? Color.parseColor("#145B3C") : Color.parseColor("#2A0070"));
        setBgItem();
        startMarquee();
        if (iCallBackGiftResult != null) {
            iCallBackGiftResult.tagItem(2);
        }

        mImgRanking.setImageResource(getResources(8, mIsActivityBg ? 1 : 2));
        mImgTitle.setImageResource(getResources(9, mIsActivityBg ? 3 : 2));
        mImgClickRule.setImageResource(getResources(10, mIsActivityBg ? 1 : 2));
        mImgRecord.setImageResource(getResources(11, mIsActivityBg ? 1 : 2));
        mImgCoinAdd.setImageResource(getResources(12, mIsActivityBg ? 1 : 2));
        mImgTabBg.setImageResource(getResources(13, mIsActivityBg ? 3 : 1));

    }

    private void setBgItem() {
        mRelBgItem1.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem2.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem3.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem4.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem5.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem6.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem7.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
        mRelBgItem8.setBackgroundResource(mTagPosition == 0 ? (mIsActivityBg ? R.drawable.activity_party_gift_bg : R.drawable.party_gift_bg) : (mIsActivityBg ? R.drawable.activity_party_gift_bg_gjc : R.drawable.party_gift_bg_gjc));
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_partywhell);
    }

    @SuppressLint("CutPasteId")
    private void initUI() {
        mImgCoinLuckyStone = findViewById(R.id.img_coin_lucky_stone);
        mLinCoinLuckyStone = findViewById(R.id.lin_click_coins_lucky_stone);
        mTvLuckyStone = findViewById(R.id.tv_coin_lucky_stone);
        mImgTabBg = findViewById(R.id.img_align_1);
        mImgCoinAdd = findViewById(R.id.img_add_coin);
        mImgRecord = findViewById(R.id.img_click_record);
        mImgClickRule = findViewById(R.id.img_click_rule);
        mImgTitle = findViewById(R.id.align_img_textxyzp);
        mImgRanking = findViewById(R.id.img_click_raking);
        mImgNotify = findViewById(R.id.img_notify_bro);
        mImgHintBg1 = findViewById(R.id.img_align_41);
        mLinBroadcastBg = findViewById(R.id.lin_align_pop_party_while);

        mLinCoinLuckyStone.setOnClickListener(this);
        findViewById(R.id.lin_click_coins).setOnClickListener(this);
        findViewById(R.id.lin_click_bag).setOnClickListener(this);
        mTvCoinText1 = findViewById(R.id.tv_click_money_1);
        mTvCoinBtn1 = findViewById(R.id.tv_coin_btn1);
        mTvCoinBottom = findViewById(R.id.tv_coin);
        mRelBgItem1 = findViewById(R.id.layout_gift1);
        mRelBgItem2 = findViewById(R.id.layout_gift2);
        mRelBgItem3 = findViewById(R.id.layout_gift3);
        mRelBgItem4 = findViewById(R.id.layout_gift4);
        mRelBgItem5 = findViewById(R.id.layout_gift5);
        mRelBgItem6 = findViewById(R.id.layout_gift6);
        mRelBgItem7 = findViewById(R.id.layout_gift7);
        mRelBgItem8 = findViewById(R.id.layout_gift8);

        mTvCoinText2 = findViewById(R.id.tv_coin_btn2);
        mTvCoinBtn2 = findViewById(R.id.tv_click_money_2);

        mTvCoinText3 = findViewById(R.id.tv_coin_btn3);
        mTvCoinBtn3 = findViewById(R.id.tv_click_money_3);

        mClickBtn1 = findViewById(R.id.rel_click_btn1);
        mClickBtn2 = findViewById(R.id.rel_click_btn2);
        mClickBtn3 = findViewById(R.id.rel_click_btn3);
        mClickBtn1.setOnClickListener(this);
        mClickBtn2.setOnClickListener(this);
        mClickBtn3.setOnClickListener(this);
        mImgShan = findViewById(R.id.img_flashing);

        findViewById(R.id.img_click_rule).setOnClickListener(this);
        findViewById(R.id.img_click_record).setOnClickListener(this);
        findViewById(R.id.img_click_raking).setOnClickListener(this);
        mImgCenter = findViewById(R.id.img_gift_center);
        mImgBs1 = findViewById(R.id.img_header_party_top);
        mImgBs2 = findViewById(R.id.img_header_party_bottom);
        constraintLayout = findViewById(R.id.con_root);
        constraintLayout.post(() -> {
            setConRootHeight();
        });
        mImgCjc = findViewById(R.id.img_align_2);
        mImgGjc = findViewById(R.id.img_align_32);
        mImgGjc.setOnClickListener(this);
        mImgCjc.setOnClickListener(this);
        mImgBg2 = findViewById(R.id.img_align_4);
        mImgBg3 = findViewById(R.id.img_align_5);
        mImgHeaderTop = findViewById(R.id.img_header_party_top);
        mImgHeaderBottom = findViewById(R.id.img_header_party_bottom);
        initImg();
        mTvContactWay = findViewById(R.id.tv_contact_way);
    }

    /**
     * 兼容高度显示超出问题
     */
    private void setConRootHeight() {
        try {
            if (constraintLayout.getHeight() != 0) {
                if (ScreenUtil.getDisplayHeight() - ScreenUtil.getStatusBarHeight(mContext) < constraintLayout.getHeight()) {
                    if (constraintLayout.getLayoutParams() != null) {
                        ViewGroup.LayoutParams layoutParams = constraintLayout.getLayoutParams();
                        layoutParams.height = DensityUtils.dp2px(mContext, 615);
                        constraintLayout.setLayoutParams(layoutParams);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initImg() {

        ImageView mImgGift1 = findViewById(R.id.img_gift1);
        ImageView mImgGiftVisible1 = findViewById(R.id.img_visible_gift1);
        TextView mTvGiftName1 = findViewById(R.id.tv_giftname);
        TextView mTvGiftCoin1 = findViewById(R.id.tv_gift_coin1);

        listsImg.add(mImgGift1);
        listsTvName.add(mTvGiftName1);
        listsTvCoin.add(mTvGiftCoin1);

        ImageView mImgGift2 = findViewById(R.id.img_gift2);
        ImageView mImgGiftVisible2 = findViewById(R.id.img_visible_gift2);
        TextView mTvGiftName2 = findViewById(R.id.tv_giftname2);
        TextView mTvGiftCoin2 = findViewById(R.id.tv_gift_coin2);

        listsImg.add(mImgGift2);
        listsTvName.add(mTvGiftName2);
        listsTvCoin.add(mTvGiftCoin2);

        ImageView mImgGift3 = findViewById(R.id.img_gift3);
        ImageView mImgGiftVisible3 = findViewById(R.id.img_visible_gift3);
        TextView mTvGiftName3 = findViewById(R.id.tv_giftname3);
        TextView mTvGiftCoin3 = findViewById(R.id.tv_gift_coin3);

        listsImg.add(mImgGift3);
        listsTvName.add(mTvGiftName3);
        listsTvCoin.add(mTvGiftCoin3);

        ImageView mImgGift5 = findViewById(R.id.img_gift5);

        ImageView mImgGiftVisible5 = findViewById(R.id.img_visible_gift5);
        TextView mTvGiftName5 = findViewById(R.id.tv_giftname5);
        TextView mTvGiftCoin5 = findViewById(R.id.tv_gift_coin5);

        listsImg.add(mImgGift5);
        listsTvName.add(mTvGiftName5);
        listsTvCoin.add(mTvGiftCoin5);

        ImageView mImgGift8 = findViewById(R.id.img_gift8);
        ImageView mImgGiftVisible8 = findViewById(R.id.img_visible_gift8);
        TextView mTvGiftName8 = findViewById(R.id.tv_giftname8);
        TextView mTvGiftCoin8 = findViewById(R.id.tv_gift_coin8);

        listsImg.add(mImgGift8);
        listsTvName.add(mTvGiftName8);
        listsTvCoin.add(mTvGiftCoin8);

        ImageView mImgGift7 = findViewById(R.id.img_gift7);
        ImageView mImgGiftVisible7 = findViewById(R.id.img_visible_gift7);
        TextView mTvGiftName7 = findViewById(R.id.tv_giftname7);
        TextView mTvGiftCoin7 = findViewById(R.id.tv_gift_coin7);

        listsImg.add(mImgGift7);
        listsTvName.add(mTvGiftName7);
        listsTvCoin.add(mTvGiftCoin7);


        ImageView mImgGift6 = findViewById(R.id.img_gift6);
        ImageView mImgGiftVisible6 = findViewById(R.id.img_visible_gift6);
        TextView mTvGiftName6 = findViewById(R.id.tv_giftname6);
        TextView mTvGiftCoin6 = findViewById(R.id.tv_gift_coin6);

        listsImg.add(mImgGift6);
        listsTvName.add(mTvGiftName6);
        listsTvCoin.add(mTvGiftCoin6);

        ImageView mImgGift4 = findViewById(R.id.img_gift4);
        ImageView mImgGiftVisible4 = findViewById(R.id.img_visible_gift4);
        TextView mTvGiftName4 = findViewById(R.id.tv_giftname4);
        TextView mTvGiftCoin4 = findViewById(R.id.tv_gift_coin4);

        listsImg.add(mImgGift4);
        listsTvName.add(mTvGiftName4);
        listsTvCoin.add(mTvGiftCoin4);

        listsVisible.add(mImgGiftVisible1);
        listsVisible.add(mImgGiftVisible2);
        listsVisible.add(mImgGiftVisible3);
        listsVisible.add(mImgGiftVisible5);
        listsVisible.add(mImgGiftVisible8);
        listsVisible.add(mImgGiftVisible7);
        listsVisible.add(mImgGiftVisible6);
        listsVisible.add(mImgGiftVisible4);

    }


    /**
     * 跑马灯
     */
    private void startMarquee() {
        if (mImgShan != null) {
            AnimationDrawable animationDrawable = (AnimationDrawable) mImgShan.getBackground();
            if (animationDrawable != null) {
                animationDrawable.start();
            }
        }
    }

    /**
     * 停止跑马灯
     */
    private void stopMarquee() {
        if (mImgShan != null) {
            AnimationDrawable animationDrawable = (AnimationDrawable) mImgShan.getBackground();
            if (animationDrawable != null) {
                animationDrawable.stop();

            }
        }

    }

    public void startAnima() {
        stopAni();
        mFlagIsStart = true;
        handler.sendEmptyMessage(1);
        mImgHeaderTop.setVisibility(View.INVISIBLE);
        mImgHeaderBottom.setVisibility(View.VISIBLE);
    }

    public void stopAni() {
        mFlagIsStart = false;
        mTimeDelay = 80;
        mTagCirclePosition = 1;
        if (mIndexGift != 0) {
            mTagAniPosition = mIndexGift;
        } else
            mTagAniPosition = 0;
        mIndexGift = 0;
        handler.removeMessages(1);
        mImgHeaderTop.setVisibility(View.VISIBLE);
        mImgHeaderBottom.setVisibility(View.INVISIBLE);
        handler.sendEmptyMessageDelayed(5, 1000);
    }


    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {//动画
                for (int i = 0; i < 8; i++) {
                    if (wheelResultBean != null && wheelResultBean.index != 0 && partyLuckWheelBean.gift_list != null) {
                        try {
                            if (partyLuckWheelBean.gift_list.get(0) != null && mTagPosition == 0 && partyLuckWheelBean.gift_list.get(0).get(i).id == wheelResultBean.index) {
                                mIndexGift = i;
                            } else if (partyLuckWheelBean.gift_list.get(1) != null && mTagPosition == 1 && partyLuckWheelBean.gift_list.get(1).get(i).id == wheelResultBean.index) {
                                mIndexGift = i;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    listsVisible.get(i).setVisibility(mTagAniPosition == i ? View.VISIBLE : View.GONE);
                }

                //当前定位的动画位置
                boolean isFlag = mTagAniPosition == mIndexGift;

                if (mTagAniPosition == 7) {
                    mTagAniPosition = 0;
                    mTagCirclePosition++;
                } else
                    mTagAniPosition++;

                if (mTagCirclePosition > 2) {
                    if (mTimeDelay < 300) {//慢速的增加时间
                        mTimeDelay += 20;
                    } else
                        mTimeDelay = 300;
                } else {
                    mTimeDelay = 50;
                }
                if (mFlagIsResult && mTagCirclePosition > 2 && isFlag) {
                    mFlagIsResult = false;
                    iCallBackGiftResult.stopAniShowPop();
                    stopAni();
                } else {
                    handler.sendEmptyMessageDelayed(1, mTimeDelay);
                }

            } else if (5 == msg.what) {//隐藏背景
                //重置 选中动画背景
                for (int i = 0; i < 8; i++) {
                    listsVisible.get(i).setVisibility(View.GONE);
                }

            } else {//10s内自动停止
                if (mTimeout == 10) {
                    mTimeout = 0;
                    mFlagIsResult = false;
                    stopAni();
                    handler.removeMessages(2);
                    showDialogResult();
                } else {
                    mTimeout++;
                    handler.sendEmptyMessageDelayed(2, 1000);
                }

            }

        }
    };


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lin_click_coins) {

            if (null == rechargePopWindow)
                rechargePopWindow = new RechargePopWindow(LuckyWheelPopWindow.this.mContext, 4, 2, !TextUtils.isEmpty(roomId) ? Integer.parseInt(roomId) : 0);
            rechargePopWindow.getCoin();
            rechargePopWindow.requestData();
            rechargePopWindow.setFormType(2);
            rechargePopWindow.showPopupWindow();
        } else if (id == R.id.img_click_rule) {
            if (partyLuckWheelBean != null) {
                //规则
                BaseWebViewActivity.start(mContext, "幸运转盘规则", partyLuckWheelBean.rule_url, 0, 10);
            } else {
                net();
                dismiss();
            }
        } else if (id == R.id.rel_click_btn1) {
            startBeforePrepare(1);
        } else if (id == R.id.rel_click_btn2) {
            startBeforePrepare(2);
        } else if (id == R.id.rel_click_btn3) {
            startBeforePrepare(3);
        } else if (id == R.id.img_click_record) {
            if (partyLuckyRecordPopWindow == null) {
                partyLuckyRecordPopWindow = new PartyRecordPopWindow(mContext, mIsActivityBg,iCallBackGiftResult);
            }
            partyLuckyRecordPopWindow.showPopupWindow();

        } else if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.img_click_raking) {
            //榜单
            if (rankingPopWindow == null) {
                rankingPopWindow = new RankingPopWindow(mContext, mIsActivityBg, iCallBackGiftResult);
            }
            rankingPopWindow.showPopupWindow();

//            dismiss();
        } else if (id == R.id.img_align_2) {//初级场
            if (mTagPosition == 0 || mFlagIsStart) {
                return;
            }

            WheelAdapter();
        } else if (id == R.id.img_align_32) {//高级场
            if (mTagPosition == 1 || mFlagIsStart) {
                return;
            }

            WheelAdapterGjc();
        } else if (id == R.id.lin_click_bag) {
            dismiss();
            //背包
            iCallBackGiftResult.openBag();
        } else if (id == R.id.lin_click_coins_lucky_stone) {
            //幸运石背包
            if (buyLuckyStonePopWindow == null) {
                buyLuckyStonePopWindow = new BuyLuckyStonePopWindow(mContext);
            }
            if (LuckyWheelPopWindow.this.partyLuckWheelBean != null) {
                if (LuckyWheelPopWindow.this.partyLuckWheelBean.recharge_map != null && LuckyWheelPopWindow.this.partyLuckWheelBean.recharge_map.size() > 1) {
                    List<TurntableStartBeforeBean.BuyBean> list = new ArrayList<>();
                    TurntableStartBeforeBean.BuyBean map = new TurntableStartBeforeBean.BuyBean();
                    map.image = LuckyWheelPopWindow.this.partyLuckWheelBean.recharge_map.get(mTagPosition == 0 ? 0 : 1).buy_image;
                    list.add(map);
                    map = new TurntableStartBeforeBean.BuyBean();
                    map.image = LuckyWheelPopWindow.this.partyLuckWheelBean.recharge_map.get(mTagPosition == 0 ? 0 : 1).give_image;
                    list.add(map);
                    buyLuckyStonePopWindow.setAdapter(list, mCoin, LuckyWheelPopWindow.this.partyLuckWheelBean.recharge_map.get(mTagPosition == 0 ? 0 : 1).cost, mTagPosition + 1, this);
                    buyLuckyStonePopWindow.showPopupWindow();
                } else {
                    net();
                }
            }

        }

    }


    /**
     * 转盘之前提示弹窗
     */
    private void startBeforePrepare(int tag) {
        if (mIsNetBeforeLoading) {
            return;
        }
        //单机次数
        String number = "";
        if (partyLuckWheelBean != null && partyLuckWheelBean.cost_map != null) {
            if (partyLuckWheelBean.cost_map.size() > 0 && partyLuckWheelBean.cost_map.get(mTagPosition) != null && partyLuckWheelBean.cost_map.get(mTagPosition).size() > tag - 1) {
                number = partyLuckWheelBean.cost_map.get(mTagPosition).get(tag - 1).cost;
            }

        }

        mIsNetBeforeLoading = true;
        mCompositeDisposable.add(service
                .startBefore(mTagPosition + 1, number)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<TurntableStartBeforeBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<TurntableStartBeforeBean> response) {
                        if (response != null && response.getData() != null) {
                            if (response.getData().is_auto == 0) {
                                if (luckyResultPopWindow == null) {
                                    luckyResultPopWindow = new LuckyResultPopWindow(mContext, null, true);
                                }
                                luckyResultPopWindow.setAdapterTurnStart(response.getData().content, LuckyWheelPopWindow.this, mTagPosition + 1, tag);
                                luckyResultPopWindow.showPopupWindow();
                                //mIsNetBeforeLoading = false;在回调里面
                            } else {
                                if (buyLuckyStonePopWindow != null) {
                                    buyLuckyStonePopWindow.dismiss();
                                }
                                switch (tag) {
                                    case 1:
                                        clickAward1();
                                        break;
                                    case 2:
                                        clickAward2();
                                        break;
                                    case 3:
                                        clickAward3();
                                        break;
                                }
                                mIsNetBeforeLoading = false;
                            }
                        }

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mIsNetBeforeLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIsNetBeforeLoading = false;
                    }
                }));
    }

    /**
     * 单机100次
     */
    private void clickAward3() {
        if (mTvCoinText3.getTag() != null && mTvCoinText3.getTag() instanceof Integer) {
            if (mFlagIsStart) {
                return;
            }
            if (!ClickUtil.canOperate()) {
                return;
            }
            startTurntable((Integer) mTvCoinText3.getTag());
            if (iCallBackGiftResult != null) {
                iCallBackGiftResult.tagDrawNum(mTagPosition + 1, 3);
            }
        }
    }

    /**
     * 单机10次
     */
    private void clickAward2() {
        if (mTvCoinText2.getTag() != null && mTvCoinText2.getTag() instanceof Integer) {
            if (mFlagIsStart) {
                return;
            }
            if (!ClickUtil.canOperate()) {
                return;
            }
            startTurntable((Integer) mTvCoinText2.getTag());
            if (iCallBackGiftResult != null) {
                iCallBackGiftResult.tagDrawNum(mTagPosition + 1, 2);
            }
        }
    }

    /**
     * 单机一次
     */
    private void clickAward1() {
        if (mTvCoinText1.getTag() != null && mTvCoinText1.getTag() instanceof Integer) {
            if (mFlagIsStart) {
                return;
            }
            if (!ClickUtil.canOperate()) {
                return;
            }
            startTurntable((Integer) mTvCoinText1.getTag());
            if (iCallBackGiftResult != null) {
                iCallBackGiftResult.tagDrawNum(mTagPosition + 1, 1);
            }
        }
    }

    /**
     * 开始转
     *
     * @param number
     */
    private void startTurntable(int number) {
        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance())) {
            return;
        }
        mNumber = number;
        startAnima();
        int type = mTagPosition + 1;
        mCompositeDisposable.add(service
                .startTurntable(roomId, type, String.valueOf(number))
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        //10s内 收到通知弹出来 礼物 ，否则提示错误 停止动画
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        stopAni();
                        mTagAniPosition = 0;
                        for (int i = 0; i < 8; i++) {
                            if (listsVisible != null) {
                                listsVisible.get(i).setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        for (int i = 0; i < 8; i++) {
                            if (listsVisible != null) {
                                listsVisible.get(i).setVisibility(View.GONE);
                            }
                        }

                        stopAni();
                        showDialogResult();
                    }
                }));

    }

    /**
     * 显示错误结果通知
     */
    private void showDialogResult() {
        if (luckyResultPopWindow == null) {
            luckyResultPopWindow = new LuckyResultPopWindow(mContext, null, false);
        }
        luckyResultPopWindow.setAdapter(null, 0);
        luckyResultPopWindow.showPopupWindow();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        if (mTvContactWay != null) {
            mTvContactWay.stopFlipping();
        }
        stopMarquee();
        mImgHeaderTop.setVisibility(View.VISIBLE);
        mImgHeaderBottom.setVisibility(View.INVISIBLE);
        if (handler != null) {
            stopAni();
            handler.removeMessages(1);
            handler.removeCallbacksAndMessages(null);
        }

        if (singleThreadExecutor != null) {
            singleThreadExecutor.shutdown();
        }

        mFlagIsResult = false;
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
    public void ok(boolean isCheck, int type) {
        mCompositeDisposable.add(service
                .setAuto(mTagPosition + 1, isCheck ? 1 : 0)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        switch (type) {
                            case 1:
                                clickAward1();
                                break;
                            case 2:
                                clickAward2();
                                break;
                            case 3:
                                clickAward3();
                                break;
                        }
                        mIsNetBeforeLoading = false;
                    }


                }));

    }

    @Override
    public void onCallBack() {//要重新恢复请求
        mIsNetBeforeLoading = false;
    }

    @Override
    public void ok(int type, int number) {
        mCompositeDisposable.add(service
                .buyTurntable(type, roomId, number)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response != null && response.getData() != null && response.getData()) {
                            selectCostLuckyStone();
                            coinNet();
                        }
                    }


                }));

    }

    public interface ICallBackGiftResult {
        void stopAniShowPop();

        void openBag();

        void tagItem(int position);

        /**
         * 抽奖按钮点击
         */
        void tagDrawNum(int position, int tag);

        /**
         * 榜单曝光
         */
        void rankingCallback();

        /**
         * 抽奖记录曝光
         */
        void recordCallback();
    }


    /**
     * 切换 资源
     *
     * @param tagMenu       1:初级场  2：高级场 3：背景 4:下部分背景 5：圆心 6:中间背景2 7：通知背景 8:榜单
     * @param currentTagRes 1-（1初级选中  2初级未选中 ）
     */
    public int getResources(int tagMenu, int currentTagRes) {
        int drawable = 0;
        if (tagMenu == 1) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_pop_chujichang : R.drawable.party_pop_chujichang) : currentTagRes == 2 ? (mIsActivityBg ? R.drawable.activity_party_gjc_tab1 : R.drawable.party_gjc_tab1) : R.drawable.party_pop_chujichang;
        } else if (tagMenu == 2) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_gjc_tab2 : R.drawable.party_gjc_tab2) : currentTagRes == 2 ? (mIsActivityBg ? R.drawable.activity_party_pop_gaojichang : R.drawable.party_pop_gaojichang) : R.drawable.party_gjc_tab2;
        } else if (tagMenu == 3) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_bg_luckywheel_pop : R.drawable.party_bg_luckywheel_pop) : (mIsActivityBg ? R.drawable.activity_party_bg_luckywheel_pop_gjc : R.drawable.party_bg_luckywheel_pop_gjc);
        } else if (tagMenu == 4) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_bg_pop_content : R.drawable.party_bg_pop_content) : (mIsActivityBg ? R.drawable.activity_party_bg_pop_content_gjc : R.drawable.party_bg_pop_content_gjc);
        } else if (tagMenu == 5) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_pop_bgcenterimg : R.drawable.party_pop_bgcenterimg) : (mIsActivityBg ? R.drawable.activity_party_pop_bgcenterimg_gjc : R.drawable.party_pop_bgcenterimg_gjc);
        } else if (tagMenu == 6) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_party_zhuanpan_bg_inside : R.drawable.party_zhuanpan_bg_inside) : (mIsActivityBg ? R.drawable.activity_party_zhuanpan_bg_inside_gjc : R.drawable.party_zhuanpan_bg_inside_gjc);
        } else if (tagMenu == 7) {
            drawable = currentTagRes == 1 ? (mIsActivityBg ? R.drawable.activity_bg_party_0d5f6e_cor12 : R.drawable.bg_party_002074_cor12) : (mIsActivityBg ? R.drawable.activity_bg_party_12c364_cor12 : R.drawable.bg_party_2a0070_cor12);
        } else if (tagMenu == 8) {
            drawable = currentTagRes == 1 ? R.drawable.activity_party_bangdan : R.drawable.party_bangdan;
        } else if (tagMenu == 9) {
            drawable = currentTagRes == 3 ? R.drawable.activity_party_xingyunzhuanpan_text_pop_gjc : (currentTagRes == 1 ? R.drawable.activity_party_xingyunzhuanpan_text_pop : R.drawable.party_xingyunzhuanpan_text_pop);
        } else if (tagMenu == 10) {
            drawable = currentTagRes == 1 ? R.drawable.activity_party_rule : R.drawable.party_rule;
        } else if (tagMenu == 11) {
            drawable = currentTagRes == 1 ? R.drawable.activity_party_record : R.drawable.party_record;
        } else if (tagMenu == 12) {
            drawable = currentTagRes == 1 ? R.drawable.activity_party_addcoin : R.drawable.party_addcoin;
        } else if (tagMenu == 13) {
            drawable = currentTagRes == 1 ? R.drawable.party_bg_henggang_luckwheelpop : currentTagRes == 2 ? R.drawable.activity_party_bg_henggang_luckwheelpop_cj : R.drawable.activity_party_bg_henggang_luckwheelpop_gj;
        }
        return drawable;
    }


    /**
     * 加载资源文件
     */
    public void loadImageNew(ImageView iv, int url, int resPlace) {

        RequestOptions options = RequestOptions
                .noTransformation()
                .placeholder(resPlace)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(iv);
    }
}
