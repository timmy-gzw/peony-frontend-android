package com.tftechsz.common.widget.gift;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.GiftEffectDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.OrangeStrokeTextView;
import com.tftechsz.common.widget.PrivilegeEffectUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.os.Looper.getMainLooper;

public class GiftItemLayout extends LinearLayout implements Animation.AnimationListener {
    public final String TAG = GiftItemLayout.class.getSimpleName();

    Handler handler = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                state = GIFTITEM_DEFAULT;
                handler.removeCallbacksAndMessages(null);
                if (animListener == null) return;
                if (giftBean == null) return;
                animListener.giftAnimEnd(index, giftBean);
            }
        }
    };

    public static final int SHOW_TIME = 3000;
    public static final int GIFTITEM_DEFAULT = 0;
    public static final int GIFTITEM_SHOW = 1;

    /**
     * 当前显示状态
     */
    public int state = GIFTITEM_DEFAULT;
    /**
     * 当前显示位置
     */
    public int index;
    /**
     * 当前tag
     */
    public String tag;

    private GiftDto giftBean;

    private HeadImageView headImage;
    private TextView tvUserName;
    private TextView tvMessage;
    private ImageView ivgift;
    private OrangeStrokeTextView giftNum;
    private Context mContext;
    private LinearLayout bgLl;

    /**
     * 透明度动画(200ms), 连击动画(200ms)
     */
    private Animation translateAnim, numAnim;
    private GiftAnimListener animListener;

    public GiftItemLayout(Context context) {
        super(context);
        init(context, null);
    }

    public GiftItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GiftItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.item_send_gift, this);
        headImage = findViewById(R.id.cir_avatar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvMessage = findViewById(R.id.tv_message);
        ivgift = findViewById(R.id.iv_gift);
        giftNum = findViewById(R.id.gift_num);
        bgLl = findViewById(R.id.left_view);
        initTranslateAnim();
        initNumAnim();
        mContext = context;
        if (null == attrs) return;
        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.GiftItemLayout, 0, 0);
        if (null == typed) return;
        index = typed.getInteger(R.styleable.GiftItemLayout_gift_index, 0);
        typed.recycle();
    }

    /**
     * 设置礼物item显示的数据
     *
     * @param giftBean
     */
    @SuppressLint("SetTextI18n")
    public void setData(GiftDto giftBean) {
        this.giftBean = giftBean;
        tag = giftBean.getUserName() + giftBean.getName() + giftBean.getToUserName();
        tvUserName.setText(giftBean.userName);
        tvMessage.setText("送 " + giftBean.getToUserName());
        headImage.loadBuddyAvatar(giftBean.headUrl);
        giftNum.setText("×" + giftBean.group);
        int price = giftBean.coin * giftBean.group;
        if (giftBean.getBgResId() != 0) {
            bgLl.setBackgroundResource(giftBean.getBgResId());
        } else {
            GiftEffectDto dto = PrivilegeEffectUtils.getInstance().getEffectsWithPrice(price);
            bgLl.setBackgroundResource(dto.getComboRes());
        }
        GlideUtils.loadRouteImage(mContext, ivgift, giftBean.getImage());
    }

    @Override
    public void clearAnimation() {
        state = GIFTITEM_DEFAULT;
        super.clearAnimation();
        if (headImage != null) {
            headImage.clearAnimation();
        }
        if (giftNum != null) {
            giftNum.clearAnimation();
        }

        clearData();
    }

    public void reset() {
        state = GIFTITEM_DEFAULT;
    }

    public void clearData() {
        handler.removeCallbacksAndMessages(null);
        this.giftBean = null;
    }

    /**
     * 执行了礼物数量连接效果
     *
     * @param group
     */
    public void addCount(int group) {
        handler.removeMessages(0);
        if (giftBean == null) return;
        giftBean.group = giftBean.group + group;
        giftNum.setText("×" + giftBean.group);
        int price = giftBean.coin * giftBean.group;
        if (giftBean.getBgResId() != 0) {
            bgLl.setBackgroundResource(giftBean.getBgResId());
        } else {
            GiftEffectDto dto = PrivilegeEffectUtils.getInstance().getEffectsWithPrice(price);
            bgLl.setBackgroundResource(dto.getComboRes());
        }
        giftNum.startAnimation(numAnim);// 执行礼物数量动画

    }


    public void startAnimation() {
        headImage.startAnimation(translateAnim);
        state = GIFTITEM_SHOW;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == translateAnim) {// 头像渐变动画执行完毕
            headImage.clearAnimation();
            giftNum.startAnimation(numAnim);// 执行礼物数量动画
        } else {
            handler.sendEmptyMessageDelayed(0, SHOW_TIME);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    /**
     * 初始化位移动画
     */
    private void initTranslateAnim() {
        translateAnim = new TranslateAnimation(-300, 0, 0, 0);
        translateAnim.setDuration(200);
        translateAnim.setFillAfter(true);
        translateAnim.setAnimationListener(this);
    }

    /**
     * 初始化礼物数字动画
     */
    private void initNumAnim() {
        numAnim = new ScaleAnimation(1f, 1.6f, 1f, 1.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        numAnim.setDuration(200);
        numAnim.setAnimationListener(this);
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMyTag() {
        return tag;
    }

    public void setMyTag(String tag) {
        this.tag = tag;
    }

    public GiftAnimListener getAnimListener() {
        return animListener;
    }

    public void setAnimListener(GiftAnimListener animListener) {
        this.animListener = animListener;
    }
}
