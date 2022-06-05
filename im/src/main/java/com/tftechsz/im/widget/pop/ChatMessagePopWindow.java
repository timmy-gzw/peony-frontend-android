package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.common.entity.IntimacyDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.math.BigDecimal;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  亲密度消息弹窗
 */
public class ChatMessagePopWindow extends BaseCenterPop implements View.OnClickListener {

    private CircleImageView ivTo, ivFrom;
    private TextView mTvUpLevel, mTvHintText, mTvHinttext2;  //升级提示
    private final UserProviderService service;
    private final String sessionId;
    private ChatApiService chatApiService;
    protected CompositeDisposable mCompositeDisposable;
    private LinearLayout mLinViewRoot;
    private Context context;
    private IntimacyDto mIntimacyDateTemp;
    private TextView mTvLevText;
    private TextView mTvSex;
    private int mHeightWindow;//派对弹窗高度
    private NestedScrollView nestedScrollView;

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
    }


    private void initUI() {
        nestedScrollView = findViewById(R.id.nsv_pcm);
        mTvHinttext2 = findViewById(R.id.algintv_qmdtj1);
        mTvHintText = findViewById(R.id.tv_align_pcm2);
        mTvLevText = findViewById(R.id.tv_pcm_clstexttop);
        ivFrom = findViewById(R.id.iv_from);
        ivTo = findViewById(R.id.iv_to);
        mTvUpLevel = findViewById(R.id.tv_up_level);  //升级
        mLinViewRoot = findViewById(R.id.rv_award);
        mTvSex = findViewById(R.id.tv_sex_pcm);

        findViewById(R.id.img_close_pcm).setOnClickListener(v -> dismiss());

        initData();

    }

    /**
     * 加载数据
     */
    private void initData() {
        if (service.getUserInfo().isBoy()) {
            mTvSex.setText(service.getUserInfo().isBoy() ? "送她礼物" : "送他礼物");
        }
        ivTo.loadBuddyAvatar(String.valueOf(service.getUserId()));
        ivFrom.loadBuddyAvatar(sessionId);

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

        if (mHeightWindow != 0) {//弹窗模式 兼容高度
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nestedScrollView.getLayoutParams();
            if (mHeightWindow < 1200) {
                layoutParams.height = DensityUtils.dp2px(mContext, 200);
            }
            nestedScrollView.setLayoutParams(layoutParams);
        }

    }


    public void setData(IntimacyDto data) {
        if (data != null) {
            mIntimacyDateTemp = data;
            mTvUpLevel.setText("亲密度 " + data.intimacy);
            try {
//                mTvHintText.setText(data.message_intimacy);
                if (data.level != 10) {
                    double d = Double.parseDouble(mIntimacyDateTemp.intimacy.split("℃")[0]);
                    double s = (mIntimacyDateTemp.intimacy_config.get(data.level + 1).min - d);
                    BigDecimal bigDecimal = new BigDecimal(s);
                    double result = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    mTvHintText.setText("还差" + result + "℃, 升级至LV." + (data.level + 1) + mIntimacyDateTemp.intimacy_config.get(data.level + 1).title);
                }
                mTvHinttext2.setText(data.content);
                mTvLevText.setText("LV." + (data.level) + " " + mIntimacyDateTemp.intimacy_config.get(data.level).title);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setinTimacyList();
            setIntimacy();
        }

    }

    private void setIntimacy() {
        TextView mTvName = findViewById(R.id.tv_iia_namenew);
        EditText mTvTip = findViewById(R.id.tv_iia_tipnew);
        RelativeLayout relativeLayout = findViewById(R.id.rel_root_iia2);
        ImageView mImgHeart = findViewById(R.id.img_iianew);

        mTvTip.setText(mIntimacyDateTemp.message_friend);
        mTvName.setText(mIntimacyDateTemp.message_title);
        if (mIntimacyDateTemp.is_friend == 1) {
            mTvName.setTextColor(Color.parseColor("#ffffff"));
            mTvTip.setTextColor(Color.parseColor("#ffffff"));
            relativeLayout.setBackgroundResource(R.drawable.bg_colorf66097_to_f593b7_radius8);
            mImgHeart.setImageResource(R.mipmap.peony_js_icon);
        } else {
            mTvName.setTextColor(Color.parseColor("#ff886471"));
            mTvTip.setTextColor(Color.parseColor("#ffc2b5ba"));
            relativeLayout.setBackgroundResource(R.drawable.bg_f9f9f9_radius14);
        }
    }

    private void setinTimacyList() {
        if (mIntimacyDateTemp.intimacy_config == null)
            return;
        if (mLinViewRoot.getChildCount() > 0) {
            mLinViewRoot.removeAllViews();
        }
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
            mLinViewRoot.addView(view, layoutParams);
        }
        //10级之后手动设置 文字
        if (mIntimacyDateTemp.level == 10) {
            mTvHintText.setText("更多等级和奖励正在紧张筹备中");
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
