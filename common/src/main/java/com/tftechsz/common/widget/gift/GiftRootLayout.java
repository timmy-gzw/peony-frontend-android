package com.tftechsz.common.widget.gift;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.GiftDto;

import java.util.Comparator;
import java.util.TreeMap;

public class GiftRootLayout extends LinearLayout implements Animation.AnimationListener, GiftAnimListener {

    public final String TAG = GiftRootLayout.class.getSimpleName();

    public GiftItemLayout oneItemLayout, twoItemLayout, threeItemLayout;
    private Animation oneGiftItemInAnim, oneGiftItemOutAnim;
    private Animation twoGiftItemInAnim, twoGiftItemOutAnim;
    private Animation threeGiftItemInAnim, threeGiftItemOutAnim;
    private final TreeMap<Long, GiftDto> giftBeanTreeMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    });

    public GiftRootLayout(Context context) {
        super(context);
        init(context);
    }

    public GiftRootLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftRootLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GiftRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        oneGiftItemInAnim = AnimationUtils.loadAnimation(context, R.anim.gift_in);
        oneGiftItemInAnim.setFillAfter(true);
        oneGiftItemOutAnim = AnimationUtils.loadAnimation(context, R.anim.gift_out);
        oneGiftItemOutAnim.setFillAfter(true);

        twoGiftItemInAnim = AnimationUtils.loadAnimation(context, R.anim.gift_in);
        twoGiftItemInAnim.setFillAfter(true);
        twoGiftItemOutAnim = AnimationUtils.loadAnimation(context, R.anim.gift_out);
        twoGiftItemOutAnim.setFillAfter(true);

        threeGiftItemInAnim = AnimationUtils.loadAnimation(context, R.anim.gift_in);
        threeGiftItemInAnim.setFillAfter(true);
        threeGiftItemOutAnim = AnimationUtils.loadAnimation(context, R.anim.gift_out);
        threeGiftItemOutAnim.setFillAfter(true);

        oneGiftItemOutAnim.setAnimationListener(this);
        twoGiftItemOutAnim.setAnimationListener(this);
        threeGiftItemOutAnim.setAnimationListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed || getChildCount() == 0) return;
        oneItemLayout = (GiftItemLayout) findViewById(R.id.gift1);
        oneItemLayout.setAnimListener(this);
        twoItemLayout = (GiftItemLayout) findViewById(R.id.gift2);
        twoItemLayout.setAnimListener(this);
        threeItemLayout = (GiftItemLayout) findViewById(R.id.gift3);
        threeItemLayout.setAnimListener(this);
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        if (oneItemLayout != null) {
            oneItemLayout.reset();
            oneItemLayout.clearAnimation();
            oneItemLayout.setVisibility(INVISIBLE);
        }
        if (twoItemLayout != null) {
            twoItemLayout.reset();
            twoItemLayout.clearAnimation();
            twoItemLayout.setVisibility(INVISIBLE);
        }
        if (threeItemLayout != null) {
            threeItemLayout.reset();
            threeItemLayout.clearAnimation();
            threeItemLayout.setVisibility(INVISIBLE);
        }
    }

    public void loadGift(GiftDto giftBean) {
        if (giftBeanTreeMap == null || giftBean == null) return;
        String tag = giftBean.getUserName() + giftBean.getName() + giftBean.getToUserName();
        if (oneItemLayout == null) {
            return;
        }
        if (oneItemLayout.getState() == GiftItemLayout.GIFTITEM_SHOW && oneItemLayout.getMyTag().equals(tag)) {
            oneItemLayout.addCount(giftBean.getGroup());
            if (giftRootListener != null) {
                giftRootListener.showGiftAmin(giftBean, 1);
            }
            return;
        }
        if (twoItemLayout.getState() == GiftItemLayout.GIFTITEM_SHOW && twoItemLayout.getMyTag().equals(tag)) {
            twoItemLayout.addCount(giftBean.getGroup());
            if (giftRootListener != null) {
                giftRootListener.showGiftAmin(giftBean, 2);
            }
            return;
        }
        if (threeItemLayout.getState() == GiftItemLayout.GIFTITEM_SHOW && threeItemLayout.getMyTag().equals(tag)) {
            threeItemLayout.addCount(giftBean.getGroup());
            if (giftRootListener != null) {
                giftRootListener.showGiftAmin(giftBean, 3);
            }
            return;
        }
        addGift(giftBean);
    }

    public void addGift(GiftDto giftBean) {
        if (giftBeanTreeMap == null || giftBean == null) return;
        if (giftBeanTreeMap.size() == 0) {
            giftBeanTreeMap.put(giftBean.getSortNum(), giftBean);
            showGift();
            return;
        }
        for (Long key : giftBeanTreeMap.keySet()) {
            GiftDto result = giftBeanTreeMap.get(key);
            if (result == null) return;
            String tagNew = giftBean.getUserName() + giftBean.getName() + giftBean.getToUserName();
            String tagOld = result.getUserName() + result.getName() + result.getToUserName();

            LogUtil.e("=============11111111111111", tagNew + "=====" + tagOld);

            if (tagNew.equals(tagOld)) {
                giftBean.setGroup(result.getGroup() + 1);
                giftBeanTreeMap.remove(result.getSortNum());
                giftBeanTreeMap.put(giftBean.getSortNum(), giftBean);
                return;
            }
        }
        giftBeanTreeMap.put(giftBean.getSortNum(), giftBean);
    }

    public void showGift() {
        if (isEmpty()) return;
        if (oneItemLayout.getState() == GiftItemLayout.GIFTITEM_DEFAULT) {
            startAnim(oneItemLayout, oneGiftItemInAnim, 1);
        } else if (twoItemLayout.getState() == GiftItemLayout.GIFTITEM_DEFAULT) {
            startAnim(twoItemLayout, twoGiftItemInAnim, 2);
        } else if (threeItemLayout.getState() == GiftItemLayout.GIFTITEM_DEFAULT) {
            startAnim(threeItemLayout, threeGiftItemInAnim, 3);
        }
    }

    private void startAnim(GiftItemLayout giftItemLayout, Animation animation, int index) {
        GiftDto giftBean = getGift();
        if (giftBean == null) return;
        giftItemLayout.setVisibility(View.VISIBLE);
        giftItemLayout.setData(giftBean);
        giftItemLayout.startAnimation(animation);
        giftItemLayout.startAnimation();
        giftItemLayout.setVisibility(View.VISIBLE);
        if (giftRootListener != null) {
            giftRootListener.showGiftAmin(giftBean, index);
        }
    }

    public GiftDto getGift() {
        GiftDto giftBean = null;
        if (giftBeanTreeMap.size() != 0) {
            // 获取队列首个礼物实体
            giftBean = giftBeanTreeMap.firstEntry().getValue();
            // 移除队列首个礼物实体
            giftBeanTreeMap.remove(giftBeanTreeMap.firstKey());
        }
        return giftBean;
    }

    /**
     * 切换直播间-重置状态
     */
    public void resetItemLayoutState() {
        giftBeanTreeMap.clear();
        if (oneItemLayout != null) {
            oneItemLayout.state = GiftItemLayout.GIFTITEM_DEFAULT;
        }
        if (twoItemLayout != null) {
            twoItemLayout.state = GiftItemLayout.GIFTITEM_DEFAULT;
        }
        if (threeItemLayout != null) {
            threeItemLayout.state = GiftItemLayout.GIFTITEM_DEFAULT;
        }
    }

    /**
     * 礼物是否为空
     */
    public boolean isEmpty() {
        return giftBeanTreeMap == null || giftBeanTreeMap.size() == 0;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        showGift();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void giftAnimEnd(int position, GiftDto giftBean) {
        if (giftBean == null) return;
        giftBeanTreeMap.remove(giftBean.getSortNum());
        switch (position) {
            case 0:
                oneItemLayout.startAnimation(oneGiftItemOutAnim);
                LogUtil.e("GiftRootLayout1礼物显示完毕", giftBean.getUserName() + giftBean.getName() + "x" + giftBean.group);
                if (isEmpty() && giftRootListener != null) {
                    giftRootListener.showGiftInfo(giftBean);
                    giftRootListener.hideGiftAmin(1, giftBean.id);
                }
                break;
            case 1:
                twoItemLayout.startAnimation(twoGiftItemOutAnim);
                LogUtil.e("GiftRootLayout2礼物显示完毕", giftBean.getUserName() + giftBean.getName() + "x" + giftBean.group);
                if (isEmpty() && giftRootListener != null) {
                    giftRootListener.showGiftInfo(giftBean);
                    giftRootListener.hideGiftAmin(2, giftBean.id);
                }
                break;
            case 2:
                threeItemLayout.startAnimation(threeGiftItemOutAnim);
                LogUtil.e("GiftRootLayout3礼物显示完毕", giftBean.getUserName() + giftBean.getName() + "x" + giftBean.group);
                if (isEmpty() && giftRootListener != null) {
                    giftRootListener.showGiftInfo(giftBean);
                    giftRootListener.hideGiftAmin(3, giftBean.id);
                }
                break;
        }
    }

    public interface GiftRootListener {
        void showGiftInfo(GiftDto giftBean);

        void showGiftAmin(GiftDto giftBean, int index);

        void hideGiftAmin(int index, int giftId);
    }

    GiftRootListener giftRootListener;

    public void setPlayGiftEndListener(GiftRootListener giftRootListener) {
        this.giftRootListener = giftRootListener;

    }
}
