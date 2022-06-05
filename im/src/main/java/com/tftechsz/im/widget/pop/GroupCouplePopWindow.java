package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.GroupCoupleDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 组情侣弹窗
 */
public class GroupCouplePopWindow extends BaseBottomPop implements View.OnClickListener {

    private HeadImageView mIvLeft, mIvRight;
    private ImageView mTvOperation;
    private TextView mTvSweet, mTvCurrentLovers;
    private TextView mTvTip;

    private ImageView mIvLeftHead, mIvRightHead;
    private GroupCoupleDto mData;
    private final CompositeDisposable mCompositeDisposable;
    private CustomPopWindow customPopWindow;
    private TextView mTvBtnDissolveCouple, mTvBody;
    private ICallBack iCallBack;
    private String mUserIdOther;
    private MandatoryLoversPopWindow mandatoryLoversPopWindow;

    public GroupCouplePopWindow(Context context, String mUserIdOther) {
        super(context);
        this.mUserIdOther = mUserIdOther;
        mCompositeDisposable = new CompositeDisposable();
        initView();
    }


    private void initView() {
        mTvBtnDissolveCouple = findViewById(R.id.tv_btn_dissolve_couple);
        mIvLeft = findViewById(R.id.iv_left);
        mIvRight = findViewById(R.id.iv_right);
        mIvLeftHead = findViewById(R.id.iv_left_head);
        mIvRightHead = findViewById(R.id.iv_right_head);
        mTvCurrentLovers = findViewById(R.id.tv_current_lovers);
        mTvSweet = findViewById(R.id.tv_sweet);
        mTvOperation = findViewById(R.id.tv_operation);
        mTvTip = findViewById(R.id.tv_tip);
        mTvOperation.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mTvBtnDissolveCouple.setOnClickListener(this);
        mTvBody = findViewById(R.id.tv_body);
    }


    public void initData(GroupCoupleDto data, ICallBack iCallBack) {
        mData = data;
        this.iCallBack = iCallBack;
        mTvBody.setText(data.couple_rule_desc);
//        mIvTip.setImageResource(R.mipmap.chat_bg_group_couple_tip);

        mTvOperation.setEnabled(true);
        mTvOperation.setImageResource(R.mipmap.chat_couples_biaobai_couples);

        if (data.sex == 1) {
            GlideUtils.loadImage(mContext, mIvLeft, data.icon);   //头像
            GlideUtils.loadImage(mContext, mIvRight, data.couple_icon);
        } else {
            GlideUtils.loadImage(mContext, mIvLeft, data.couple_icon);
            GlideUtils.loadImage(mContext, mIvRight, data.icon);
        }

        mIvLeftHead.setImageResource(R.mipmap.chat_bg_group_couple_head_boy);
        mIvRightHead.setImageResource(R.mipmap.chat_bg_group_couple_head_girl);
        if (data.is_couple) {  //有情侣
            mTvCurrentLovers.setText(new SpannableStringUtils.Builder().append("当前情侣：").append(data.couple_nickname).setForegroundColor(Utils.getColor(R.color.half_red)).create());
            mTvSweet.setText(new SpannableStringUtils.Builder().append("甜蜜值：").append(data.couple_sweet).setForegroundColor(Utils.getColor(R.color.half_red)).create());
            if (data.is_couple_to_me) {  //跟我是情侣
//                mIvTip.setImageResource(R.mipmap.chat_bg_group_couple_tip_1);
                mTvTip.setText(new SpannableStringUtils.Builder().append("你们已经在一起").append(data.couple_day).setForegroundColor(Utils.getColor(R.color.half_red)).append("天了～").create());
                mTvOperation.setImageResource(R.mipmap.chat_couples_jiechu_couples);
                mTvBtnDissolveCouple.setVisibility(View.VISIBLE);

            } else {  //不是情侣
//                mIvTip.setImageResource(R.mipmap.chat_bg_group_couple_tip_2);
                mTvTip.setText(new SpannableStringUtils.Builder().append("你跟TA的甜蜜值为").append(String.valueOf(data.couple_sweet_to_me)).setForegroundColor(Utils.getColor(R.color.half_red)).create());

            }
        } else {
            mTvCurrentLovers.setText(new SpannableStringUtils.Builder().append("当前情侣：").append("暂无").setForegroundColor(Utils.getColor(R.color.half_red)).create());
            mTvSweet.setText(new SpannableStringUtils.Builder().append("甜蜜值：").append("暂无").setForegroundColor(Utils.getColor(R.color.half_red)).create());
            mTvTip.setText(new SpannableStringUtils.Builder().append("送表白礼物，跟").append("TA表白").setForegroundColor(Utils.getColor(R.color.half_red)).append("～").create());
        }
    }


    /**
     * 发送表白信
     */
    private void sendCoupleLetter(int userId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).sendCoupleLetter(userId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        dismiss();
                    }
                }));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_group_couple);
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
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.tv_operation || id == R.id.tv_btn_dissolve_couple) {
            if (mData == null) return;
            if (mData.is_couple_to_me) {  //跟我是情侣
                showConfirmPop(1);
            } else {
                showConfirmPop(2);
            }

        }
    }

    /**
     * @param type 1 解除情侣 2  发起
     */
    private void showConfirmPop(int type) {
        if (type == 1) {
            if (mandatoryLoversPopWindow == null)
                mandatoryLoversPopWindow = new MandatoryLoversPopWindow(mContext, mUserIdOther);
            mandatoryLoversPopWindow.refresh(mUserIdOther);
            mandatoryLoversPopWindow.showPopupWindow();
            dismiss();
            return;
        }
        if (Integer.parseInt(mData.couple_sweet_to_me) < Integer.parseInt(mData.couple_sweet)) {
            iCallBack.callBacks();
            return;
        }
        if (customPopWindow == null)
            customPopWindow = new CustomPopWindow(mContext);
        customPopWindow.setRightButton("确认");
        customPopWindow.setRightColor(R.color.half_red);
        SpannableStringBuilder span = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(mData.desc);
        span.append(stringBuffer);
        int start = stringBuffer.toString().indexOf(mData.nickname);
        if (start >= 0) {
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6A9D")), start, start + mData.nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        customPopWindow.setContentText(span);
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                sendCoupleLetter(mData.user_id);
            }
        });
        customPopWindow.showPopupWindow();
    }

    public interface ICallBack {
        void callBacks();
    }

}
