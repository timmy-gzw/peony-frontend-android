package com.tftechsz.im.widget.pop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.ScreenUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseTopPop;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.IntmacyLevelAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.common.entity.IntimacyDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.im.model.dto.MultiIntmacyItem;
import com.tftechsz.im.utils.TopSmoothScroller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  亲密度消息弹窗
 */
public class ChatMessagePopWindow extends BaseTopPop implements View.OnClickListener {

    private CircleImageView ivTo, ivFrom;
    private TextView mTvUpLevel;  //升级提示
    private final UserProviderService service;
    private final String sessionId;
    private ChatApiService chatApiService;
    protected CompositeDisposable mCompositeDisposable;
    private Context context;
    private IntimacyDto mIntimacyDateTemp;
    private TextView mTvLevelName;
    private int mHeightWindow;//派对弹窗高度
    private ImageView mIvPushPull;
    private boolean isPush = false;//是否展开
    private ImageView mRvIntmacyPop;
    private View mBootom;
    private RecyclerView mRv;
    private IntmacyLevelAdapter adapter;

    public ChatMessagePopWindow(Context context, String sessionId) {
        super(context);
        this.context = context;
        this.sessionId = sessionId;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = new RetrofitManager().createUserApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        setPopupFadeEnable(true);
        initUI();
        setOutSideTouchable(false);
        this.setOnPopupWindowShowListener(() -> {
           Utils.runOnUiThreadDelayed(new Runnable() {
               @Override
               public void run() {
                   if(mRv != null && adapter != null && adapter.getmCurrentLevel()>1){
                       TopSmoothScroller smoothScroller = new TopSmoothScroller(mContext);
                       smoothScroller.setTargetPosition(adapter.getmCurrentLevel()-1);

                       mRv.getLayoutManager().startSmoothScroll(smoothScroller);
                   }
               }
           }, 500);
        });
    }


    private void initUI() {
        ivFrom = findViewById(R.id.iv_from);
        ivTo = findViewById(R.id.iv_to);
        mTvUpLevel = findViewById(R.id.tv_up_level);  //升级

        mIvPushPull = findViewById(R.id.iv_push_pull);
        mRvIntmacyPop = findViewById(R.id.intmacy_pop);
        mBootom = findViewById(R.id.bottom2);
        mTvLevelName = findViewById(R.id.img_algin_pcm_ablow10);
        findViewById(R.id.tv_strategy).setOnClickListener(v->{
            IntmacyUpgradePopWindow popWindow = new IntmacyUpgradePopWindow(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            popWindow.showPopupWindow();
        });
        mRv = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(linearLayoutManager);
        adapter = new IntmacyLevelAdapter();
        mRv.setAdapter(adapter);
        mBootom.setOnClickListener(v->{
            dismiss();
        });
        mIvPushPull.setOnClickListener(v->{
            if(!ClickUtil.canOperate())
                return;
            if(!isPush){
                isPush = true;
                tranlateAnimation(Utils.dp2px(context,200), 1, R.mipmap.icon_intmacy_pull);

            }else{
                isPush = false;
                tranlateAnimation(1, Utils.dp2px(context, 200), R.mipmap.icon_intamcy_push);
            }
        });
        initData();

    }

    private void tranlateAnimation(int i, int i2, int p) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(i, i2);
        valueAnimator.addUpdateListener(value -> {
            int h = (int) value.getAnimatedValue();
            mBootom.getLayoutParams().height = h;
            mBootom.requestLayout();
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIvPushPull.setImageResource(p);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    /**
     * 加载数据
     */
    private void initData() {
        if(service.getUserInfo().getSex() == 2){
            ivFrom.loadBuddyAvatar(String.valueOf(service.getUserId()));
            ivTo.loadBuddyAvatar(sessionId);
        }else{
            ivTo.loadBuddyAvatar(String.valueOf(service.getUserId()));
            ivFrom.loadBuddyAvatar(sessionId);
        }

    }


    /**
     * 获取亲密度信息
     */
    public void getIntimacy() {
        mCompositeDisposable.add(chatApiService.getIntimacy(sessionId).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<IntimacyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<IntimacyDto> response) {
                        setData(response.getData());
                    }

                }));
    }

    /**
     * 获取亲密度信息
     */
    public void setHeightWindow(int height) {
        this.mHeightWindow = height;


    }


    public void setData(IntimacyDto data) {
        if (data != null) {
            mIntimacyDateTemp = data;
            mTvUpLevel.setText("亲密度 " + data.intimacy);
            mTvLevelName.setText("LV."+data.level+" "+data.message_title);
            try {
//                mTvHintText.setText(data.message_intimacy);
                if (data.level != 10) {
                    double d = Double.parseDouble(mIntimacyDateTemp.intimacy.split("℃")[0]);
                    double s = (mIntimacyDateTemp.intimacy_config.get(data.level + 1).min - d);
                    BigDecimal bigDecimal = new BigDecimal(s);
                    double result = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<MultiIntmacyItem> lists = new ArrayList<>();
            for (IntimacyDto.IntimacyConfigDTO intimacyConfigDTO : data.intimacy_config) {
                lists.add(new MultiIntmacyItem(intimacyConfigDTO));
            }
            adapter.setLastItemDto(lists.get(lists.size()-1));
            adapter.setmCurrentLevel(data.level);
            adapter.setList(lists);
//            mRv.scrollToPosition(data.level-1);

            setinTimacyList();
            setIntimacy();
        }

    }

    private void setIntimacy() {

    }

    private void setinTimacyList() {
        if (mIntimacyDateTemp.intimacy_config == null)
            return;
        //亲密度-等级奖励
        for (int i = 0; i < mIntimacyDateTemp.intimacy_config.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_intimacy_award, null);
            RelativeLayout relativeLayout = view.findViewById(R.id.rel_root_iia);
            TextView mTvName = view.findViewById(R.id.tv_iia_name);
            EditText mTvTip = view.findViewById(R.id.tv_iia_tip);
            TextView mTvVisible = view.findViewById(R.id.tv_current_textvisible);
            ImageView mImgiia = view.findViewById(R.id.img_iia);
            mTvVisible.setVisibility(View.GONE);
            if (mIntimacyDateTemp.intimacy_config.get(i).max == 0) {
                mImgiia.setImageResource(R.mipmap.peony_ccxy_icon);
                mTvName.setText(mIntimacyDateTemp.intimacy_config.get(i).title);
                relativeLayout.setBackgroundResource(R.drawable.shape_cor0_solidffeff6);
                mTvName.setTextColor(Color.parseColor("#ff886471"));
                mTvTip.setTextColor(Color.parseColor("#ffc2b5ba"));
            } else {
                if (mIntimacyDateTemp.level == i) {
                    //当前等级
                    mImgiia.setImageResource(R.mipmap.peony_js_icon);
                    mTvName.setText("LV." + (i) + " " + mIntimacyDateTemp.intimacy_config.get(i).title + " " + mIntimacyDateTemp.intimacy);
                    mTvName.setTextColor(Color.parseColor("#ffffff"));
                    mTvTip.setTextColor(Color.parseColor("#ffffff"));
                    relativeLayout.setBackgroundResource(R.drawable.bg_colorf66097_to_f593b7_radius8);
                    mTvVisible.setVisibility(View.VISIBLE);
                } else {
                    mTvName.setTextColor(Color.parseColor("#ff886471"));
                    mTvTip.setTextColor(Color.parseColor("#ffc2b5ba"));
                    if (mIntimacyDateTemp.level + 1 == i) {
                        //增加文字差了多少升级
                        try {
                            double d = Double.parseDouble(mIntimacyDateTemp.intimacy.split("℃")[0]);
                            double s = (mIntimacyDateTemp.intimacy_config.get(i).min - d);
                            BigDecimal bigDecimal = new BigDecimal(s);
                            double result = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                            mTvName.setText("LV." + (i) + " " + mIntimacyDateTemp.intimacy_config.get(i).title + " " + "差" + result + "℃升级");
                        } catch (NumberFormatException e) {
                            mTvName.setText("LV." + (i) + " " + mIntimacyDateTemp.intimacy_config.get(i).title);
                        }
                    } else {
                        mTvName.setText("LV." + (i) + " " + mIntimacyDateTemp.intimacy_config.get(i).title);
                    }

                    if (i < mIntimacyDateTemp.level) {
                        relativeLayout.setBackgroundResource(R.drawable.shape_cor0_solidffeff6);
                        mImgiia.setImageResource(R.mipmap.peony_js_icon);
                    } else {
                        relativeLayout.setBackgroundResource(R.drawable.bg_f9f9f9_radius14);
                        mImgiia.setImageResource(R.mipmap.peony_wjs_icon);
                    }

                }

            }

            mTvTip.setText(mIntimacyDateTemp.intimacy_config.get(i).tips);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin += 20;
        }
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_chat_message);
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
    public void onClick(View v) {
        int id = v.getId();

    }

    public interface OnSelectListener {
        void shareType(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
