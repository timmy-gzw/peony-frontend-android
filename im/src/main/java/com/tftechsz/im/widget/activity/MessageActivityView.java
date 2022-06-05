package com.tftechsz.im.widget.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ui.widget.FixBugsViewPager;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.im.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.TimeUtils;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 活动轮播
 */
public class MessageActivityView<T> extends RelativeLayout {
    private final Context mContext;
    private final FixBugsViewPager vp;
    private final LinearLayout llPoint;
    private static final long AD_CHANGE_TIME = 5000;
    public boolean isRunning;
    private List<ChatMsg.Airdrop> mActivityList = new ArrayList<>();
    private CountBackUtils countBackUtils, countCoupleBagUtils;
    private final SVGAParser svgaParser;
    private ActivityViewApter mAdapter;
    List<View> views;
    @Autowired
    UserProviderService userProviderService;
    private View airdropView, coupleLetterView, redPackView, activityView, coupleBagView,coupleRelieveView;
    OnActItemClickListener onActItemClickListener;
    private boolean mFlagIsTeam;//是否群组
    private int userId;
    private String sessionId;

    public void setOnActItemClickListener(OnActItemClickListener l, boolean flag, String sessionId, int userId) {
        onActItemClickListener = l;
        this.userId = userId;
        this.sessionId = sessionId;
        this.mFlagIsTeam = flag;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1 && isRunning) {
                int currentPos = vp.getCurrentItem() + 1;
                if (currentPos >= vp.getChildCount()) {
                    currentPos = 0;
                }
                vp.setCurrentItem(currentPos);
                //sendEmptyMessageDelayed(1, AD_CHANGE_TIME);
            }
        }
    };

    public void stopAutoScroll() {
        isRunning = false;
        mHandler.removeMessages(1);
    }

    public void startAutoScroll() {
        if (isRunning) return;
        if (mActivityList != null && mActivityList.size() > 1) {
            isRunning = true;
            mHandler.sendEmptyMessageDelayed(1, AD_CHANGE_TIME);
        }
    }

    public MessageActivityView(Context context) {
        this(context, null);
    }

    public MessageActivityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MessageActivityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_message_activity, this, true);
        vp = view.findViewById(R.id.viewpager);
        llPoint = view.findViewById(R.id.ll_point);
        countBackUtils = new CountBackUtils();
        svgaParser = new SVGAParser(mContext);
    }

    public void addData(List<ChatMsg.Airdrop> airdrop) {
        if (airdrop == null || airdrop.size() == 0) return;
        mActivityList = airdrop;
        views = new ArrayList<>();
        setVisibility(VISIBLE);
//        if (mActivityList.size() > 1)
//            views.add(addView(mActivityList.get(mActivityList.size() - 1)));
        for (int i = 0; i < mActivityList.size(); i++) {
            views.add(addView(mActivityList.get(i)));
        }
//        if (mActivityList.size() > 1) {
//            views.add(addView(mActivityList.get(0)));
//        }
        mAdapter = new ActivityViewApter(views);
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(mAdapter.getCount());
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pPosition) {
                setCurrentPoint(pPosition);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (ViewPager.SCROLL_STATE_IDLE == arg0) {
                    startAutoScroll();
                } else {
                    stopAutoScroll();
                }
            }
        });
        //vp.setCurrentItem(0, false);
        startAutoScroll();
    }

    private View addView(final ChatMsg.Airdrop airdrop) {
        if (isContainsActivity(airdrop.type)) { //活动
            return getAirdropViewActivity(airdrop);
        }
        if (isGiftBag(airdrop.type)) {//空投
            return getAirdropView(airdrop);
        }
        if (isCoupleLetter(airdrop.type)) {//表白信
            return getCoupleLetter(airdrop);
        }
        if (isRedPacketRain(airdrop.type)) {//红包雨
            return getAirdropRedPacketView(airdrop);
        }
        if (isCouplePaster(airdrop.type)) { //贴片
            return getCouplePaster(airdrop);
        }
        if (isCoupleRelieveApply(airdrop.type)) { //解除情侣贴片
            return getCoupleRelieveApply(airdrop);
        }
        return null;
    }

    private boolean isGiftBag(String type) {//空投
        return !TextUtils.isEmpty(type) && TextUtils.equals(type, "gift_bag");
    }

    private boolean isCoupleLetter(String type) {//表白信
        return !TextUtils.isEmpty(type) && TextUtils.equals(type, "couple_letter");
    }

    private boolean isCouplePaster(String type) {//表白信贴片
        return !TextUtils.isEmpty(type) && TextUtils.equals(type, "couple_paster");
    }


    private boolean isRedPacketRain(String type) {//红包雨
        return !TextUtils.isEmpty(type) && TextUtils.equals(type, "red_packet_rain");
    }

    private boolean isContainsActivity(String type) {//活动
        return !TextUtils.isEmpty(type) && type.startsWith("activity_");
    }


    private boolean isCoupleRelieveApply(String type) {//表白信解除通知
        return !TextUtils.isEmpty(type) && TextUtils.equals(type, "couple_relieve_apply");
    }


    public void updateActivityData(ChatMsg.Airdrop airdrop) {
        stopAutoScroll();
        if (airdrop != null && airdrop.num == 0 && mActivityList != null && mActivityList.size() > 0) {
            if (hasData(airdrop)) {
                for (int i = mActivityList.size() - 1; i >= 0; i--) {
                    if (TextUtils.equals(airdrop.type, mActivityList.get(i).type)) {
                        mActivityList.remove(i);
                    }
                }
                if (isGiftBag(airdrop.type) && views != null && views.size() > 0) {  //空投
                    mAdapter.removeView(airdropView);
                } else if (isCoupleLetter(airdrop.type) && views != null && views.size() > 0) {  // 表白信
                    mAdapter.removeView(coupleLetterView);
                } else if (isRedPacketRain(airdrop.type) && views != null && views.size() > 0) {  // 红包雨
                    mAdapter.removeView(redPackView);
                } else if (isCouplePaster(airdrop.type) && views != null && views.size() > 0) {  // 表白信礼包贴片
                    mAdapter.removeView(coupleBagView);
                } else if (isCoupleRelieveApply(airdrop.type) && views != null && views.size() > 0) {  // 表白信礼包贴片
                    mAdapter.removeView(coupleRelieveView);
                }
                if (mAdapter.getCount() <= 0)
                    setVisibility(INVISIBLE);
                setPoint();
            }
            startAutoScroll();
            return;
        }

        if (airdrop == null || airdrop.num == 0) {
            return;
        }
        for (int i = 0; i < mActivityList.size(); i++) {
            if (TextUtils.equals(airdrop.type, mActivityList.get(i).type)) {
                mActivityList.set(i, airdrop);
            }
        }
        if (!hasData(airdrop)) {
            mActivityList.add(airdrop);
        }
        Collections.sort(mActivityList, comp);
        addData(mActivityList);
        setPoint();
    }

    private static final Comparator<ChatMsg.Airdrop> comp = (recent1, recent2) -> recent1.sortId - recent2.sortId;


    private boolean hasData(ChatMsg.Airdrop bannerBean) {
        boolean isType = false;
        for (int i = 0; i < mActivityList.size(); i++) {
            if (TextUtils.equals(mActivityList.get(i).type, bannerBean.type)) {
                mActivityList.get(i).animation = bannerBean.animation;
                mActivityList.get(i).num = bannerBean.num;
                if (bannerBean.start_time != 0) {
                    mActivityList.get(i).start_time = bannerBean.start_time;
                }
                isType = true;
                break;
            }
        }
        return isType;
    }

    /**
     * 获得红包布局
     */
    private View getAirdropRedPacketView(ChatMsg.Airdrop airdrop) {
        redPackView = inflate(mContext, R.layout.layout_activity_airdrop_red_packet, null);
        TextView tvAirdropNum = redPackView.findViewById(R.id.tv_airdrop_num);
        TextView tvOpenAirdrop = redPackView.findViewById(R.id.tv_airdrop);
        if (airdrop != null) {
            tvAirdropNum.setText(String.valueOf(airdrop.num));
            if (airdrop.start_time != 0) {
                tvOpenAirdrop.setVisibility(View.VISIBLE);
                tvOpenAirdrop.setText(TimeUtils.timeParse(airdrop.start_time));

                if (countBackUtils == null)
                    countBackUtils = new CountBackUtils();
                countBackUtils.countBack2(airdrop.start_time, new CountBackUtils.Callback2() {
                    @Override
                    public void countBacking(long time) {
                        if (airdrop != null) {
                            airdrop.start_time = (int) time;
                        }

                        tvOpenAirdrop.setText(TimeUtils.timeParse(time));
                    }

                    @Override
                    public void finish() {
                        if (airdrop != null) {
                            airdrop.start_time = 0;
                        }
                        tvOpenAirdrop.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        return redPackView;
    }


    //空投view
    private View getAirdropView(ChatMsg.Airdrop airdrop) {
        airdropView = inflate(mContext, R.layout.layout_activity_airdrop, null);
        TextView tvAirdropNum = airdropView.findViewById(R.id.tv_airdrop_num);
        TextView tvOpenAirdrop = airdropView.findViewById(R.id.tv_airdrop);
        RelativeLayout rlAirdrop = airdropView.findViewById(R.id.rl_airdrop);
        SVGAImageView svgaAirDrop = airdropView.findViewById(R.id.svga_airdrop);
        if (airdrop != null) {
            tvAirdropNum.setText(String.valueOf(airdrop.num));
            if (airdrop.start_time != 0) {
                playAirdrop(svgaAirDrop, "airdrops_send_small.svga");
                tvOpenAirdrop.setBackgroundResource(R.drawable.bg_half_tran_radius25);
                tvOpenAirdrop.setText(TimeUtils.timeParse(airdrop.start_time));
                if (countBackUtils == null)
                    countBackUtils = new CountBackUtils();
                countBackUtils.countBack(airdrop.start_time, new CountBackUtils.Callback() {
                    @Override
                    public void countBacking(long time) {
                        tvOpenAirdrop.setBackgroundResource(R.drawable.bg_half_tran_radius25);
                        tvOpenAirdrop.setText(TimeUtils.timeParse(time));
                    }

                    @Override
                    public void finish() {
                        playAirdrop(svgaAirDrop, "airdrops_receive.svga");
                        tvOpenAirdrop.setBackgroundResource(R.drawable.bg_airdrop_radius25);
                        tvOpenAirdrop.setText("抢空投");
                    }
                });
            } else {
                playAirdrop(svgaAirDrop, "airdrops_receive.svga");
            }
        }

        rlAirdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActItemClickListener != null)
                    onActItemClickListener.openAirdrop();
            }
        });
        return airdropView;
    }

    /**
     * 获得活动view
     */
    private View getAirdropViewActivity(ChatMsg.Airdrop airdrop) {
        activityView = inflate(mContext, R.layout.layout_activity_airdrop_activity, null);
        ImageView mActivityIcon = activityView.findViewById(R.id.activity_icon);

        if (airdrop != null) {
            mActivityIcon.setAlpha(airdrop.activity.alpha);
            GlideUtils.loadRoundImage(getContext(), mActivityIcon, airdrop.activity.icon);
        }

        activityView.findViewById(R.id.rel_click_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActItemClickListener != null)
                    com.tftechsz.common.utils.CommonUtil.performLink(MessageActivityView.this.getContext(), new ConfigInfo.MineInfo(airdrop.activity.link, airdrop.activity.option), 0, 22);

                if (mFlagIsTeam) {//过年活动
                    String teamId = NimUIKit.getTeamProvider().getTeamById(sessionId).getId();
                    try {
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("新年活动小秘书入口点击", "family_patch_click", "", JSON.toJSONString(new NavigationLogEntity(userId, airdrop.activity.link, 27,
                                        0, System.currentTimeMillis(), com.tftechsz.common.utils.CommonUtil.getOSName(), Constants.APP_NAME, "家族内新年活动贴片点击", "-1", Integer.parseInt(teamId))), null
                                );
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return activityView;
    }


    //情侣大礼包贴片
    private View getCouplePaster(ChatMsg.Airdrop airdrop) {
        coupleBagView = inflate(mContext, R.layout.layout_couple_gift_bag, null);
        TextView tvTime = coupleBagView.findViewById(R.id.tv_couple_time);
        if (countCoupleBagUtils == null)
            countCoupleBagUtils = new CountBackUtils();
        countCoupleBagUtils.countBack(airdrop.start_time, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                airdrop.start_time = (int) time;
                tvTime.setText(Utils.getLastTime(time));
            }

            @Override
            public void finish() {
            }
        });
        coupleBagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActItemClickListener != null)
                    onActItemClickListener.openCoupleBad(airdrop.family_id,airdrop.gift_bag_id);
            }
        });
        return coupleBagView;
    }


    //情侣解除通知
    private View getCoupleRelieveApply(ChatMsg.Airdrop airdrop) {
        coupleRelieveView = inflate(mContext, R.layout.layout_couple_relieve_apply, null);
        coupleRelieveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActItemClickListener != null)
                    onActItemClickListener.openRelieveApply(airdrop.apply_id);
            }
        });
        return coupleRelieveView;
    }


    private void playAirdrop(SVGAImageView svgaAirDrop, String name) {
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaAirDrop != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaAirDrop.setImageDrawable(drawable);
                    svgaAirDrop.startAnimation();
                }
            }
        }, null);
    }


    //表白信view
    private View getCoupleLetter(ChatMsg.Airdrop airdrop) {
        coupleLetterView = inflate(mContext, R.layout.layout_activity_couple_letter, null);
        TextView tvCoupleNum = coupleLetterView.findViewById(R.id.tv_couple_num);
        RelativeLayout rlAirdrop = coupleLetterView.findViewById(R.id.rl_couple);
        tvCoupleNum.setText(String.valueOf(airdrop.num));
        rlAirdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActItemClickListener != null)
                    onActItemClickListener.clickCoupleLetter();
            }
        });
        return coupleLetterView;
    }


    private static class ActivityViewApter extends PagerAdapter {

        private final List<View> mViewList;

        public ActivityViewApter(List<View> pArrayList) {
            this.mViewList = pArrayList;
        }

        @Override
        public int getCount() {
            if (mViewList != null) {
                return mViewList.size();
            }
            return 0;
        }

        public void removeView(View view) {
            mViewList.remove(view);
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
            if (mViewList != null && mViewList.size() > 0)
                container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int pPosition) {
            if (mViewList.get(pPosition).getParent() != null) {
                container.removeView(mViewList.get(pPosition));
            }
            container.addView(mViewList.get(pPosition), 0);
            return mViewList.get(pPosition);
        }

        @Override
        public boolean isViewFromObject(@NotNull View pView, @NotNull Object pObject) {
            return (pView == pObject);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (mViewList.contains(object)) {
                return mViewList.indexOf(object);
            } else {
                return POSITION_NONE;
            }
        }

    }


    /**
     * 设置底部的点
     */
    private void setPoint() {
        int count = mAdapter.getCount();
        llPoint.removeAllViews();
        if (count == 1) {
            return;
        }
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = DensityUtils.dp2px(BaseApplication.getInstance(), 3);
            params.leftMargin = DensityUtils.dp2px(BaseApplication.getInstance(), 3);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.selector_gift_point_bg);
            if (0 == i) {
                point.setEnabled(false);
            }
            llPoint.addView(point);
        }

    }

    private void setCurrentPoint(int position) {
        Log.e("tag", "position=" + position + "");
        int count = mAdapter.getCount();
        if (llPoint.getChildAt(position) != null)
            llPoint.getChildAt(position).setEnabled(false);
//        llPoint.getChildAt(mCurrentPagePosition).setEnabled(true);
        for (int i = 0; i < count; i++) {
            if (i != position) {
                if (llPoint.getChildAt(i) != null)
                    llPoint.getChildAt(i).setEnabled(true);
            }

        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }


    public void onDestroy() {
        if (coupleLetterView != null) {
            coupleLetterView = null;
        }
        if (airdropView != null) {
            airdropView = null;
        }
        if (countCoupleBagUtils != null)
            countCoupleBagUtils.destroy();
    }
}
