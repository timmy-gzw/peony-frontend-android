package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.CoupleRelieveInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 强制解除情侣弹窗
 */
public class MandatoryLoversPopWindow extends BaseBottomPop implements View.OnClickListener {

    private HeadImageView mIvLeft, mIvRight;

    private TextView mTvSweet, mTvCurrentLovers;


    private ImageView mIvLeftHead, mIvRightHead;
    private TextView mTvBody;
    private final CompositeDisposable mCompositeDisposable;
    private CustomPopWindow customPopWindow;
    private TextView tvApplyMandatoryHint;
    //申请中 和 暂未申请
    private RelativeLayout mRelRootBtn, mRelRootBtnTime;
    private CountBackUtils mCountBack2Box;

    private CoupleRelieveInfo coupleRelieveInfo;
    private String userId;

    public MandatoryLoversPopWindow(Context context, String userId) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        this.userId = userId;
        initView();

    }

    public void refresh(String userId) {
        this.userId = userId;
        //调用接口获得数据 GroupCoupleDto 要刷新
        getCoupleRelieveInfo();
    }

    private void initView() {
        mTvBody = findViewById(R.id.tv_body);
        mIvLeft = findViewById(R.id.iv_left);
        mIvRight = findViewById(R.id.iv_right);
        mIvLeftHead = findViewById(R.id.iv_left_head);
        mIvRightHead = findViewById(R.id.iv_right_head);
        mRelRootBtn = findViewById(R.id.rel_click_apply);
        mRelRootBtnTime = findViewById(R.id.rel_click_apply_time);

        mTvCurrentLovers = findViewById(R.id.tv_current_lovers);
        mTvSweet = findViewById(R.id.tv_sweet);


//        mIvTip = findViewById(R.id.iv_tip);

        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        mRelRootBtn.setOnClickListener(v -> showConfirmPop(1));

        findViewById(R.id.img_click_1).setOnClickListener(v -> dismiss());
        mRelRootBtnTime.setOnClickListener(v -> {
            if (coupleRelieveInfo == null) {
                getCoupleRelieveInfo();
                return;
            }
//取消申请
            relieveCouple(coupleRelieveInfo.apply_id, 9);
        });

        findViewById(R.id.img_click_3).setOnClickListener(v -> showConfirmPop(2));
        tvApplyMandatoryHint = findViewById(R.id.tv_apply_mandatory_hint_time);

    }

    /**
     * 解除申请
     */
    private void relieveCouple(int applyId, int type) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).relieveCoupleHandler(applyId, type)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        dismiss();
                    }
                }));
    }

    public void setData() {

//        mIvTip.setImageResource(R.mipmap.chat_bg_group_couple_tip);

        if (coupleRelieveInfo.sex == 1) {
            GlideUtils.loadImage(mContext, mIvLeft, coupleRelieveInfo.icon);   //头像
            GlideUtils.loadImage(mContext, mIvRight, coupleRelieveInfo.couple_user_icon);
        } else {
            GlideUtils.loadImage(mContext, mIvLeft, coupleRelieveInfo.couple_user_icon);
            GlideUtils.loadImage(mContext, mIvRight, coupleRelieveInfo.icon);
        }

        mIvLeftHead.setImageResource(R.mipmap.chat_bg_group_couple_head_boy);
        mIvRightHead.setImageResource(R.mipmap.chat_bg_group_couple_head_girl);
        mTvCurrentLovers.setText(new SpannableStringUtils.Builder().append("当前情侣：").append(coupleRelieveInfo.couple_user_nickname).setForegroundColor(Utils.getColor(R.color.half_red)).create());
        mTvSweet.setText(new SpannableStringUtils.Builder().append("甜蜜值：").append(coupleRelieveInfo.couple_sweet + "").setForegroundColor(Utils.getColor(R.color.half_red)).create());

        mTvBody.setText(coupleRelieveInfo.couple_rule_desc);
    }


    /**
     * 解除
     */
    private void relieveCouple(int type) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).coupleRelieveApply(userId, type)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        MandatoryLoversPopWindow.this.dismiss();
                    }

                }));
    }

    /**
     * 获取解除情侣信息
     */
    private void getCoupleRelieveInfo() {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).coupleRelieveInfo()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CoupleRelieveInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<CoupleRelieveInfo> response) {
                        if (response == null || response.getData() == null) return;
                        coupleRelieveInfo = response.getData();
                        setData();
                        if (coupleRelieveInfo.apply_id == 0) {
                            mRelRootBtnTime.setVisibility(View.GONE);
                            mRelRootBtn.setVisibility(View.VISIBLE);
                        } else {
                            mRelRootBtnTime.setVisibility(View.VISIBLE);
                            mRelRootBtn.setVisibility(View.GONE);
                            if (mCountBack2Box == null)
                                mCountBack2Box = new CountBackUtils();
                            mCountBack2Box.countBack(coupleRelieveInfo.apply_time, new CountBackUtils.Callback() {
                                @Override
                                public void countBacking(long time) {
                                    tvApplyMandatoryHint.setText(Utils.getLastTime(time));
                                }

                                @Override
                                public void finish() {
                                    if (mRelRootBtnTime != null) {
                                        mRelRootBtnTime.setVisibility(View.GONE);
                                        mRelRootBtn.setVisibility(View.VISIBLE);
                                    }

                                }

                            });
                        }


                    }
                }));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_mandatory_lovers);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    /**
     * @param type 1 解除情侣 2  强制解除
     */
    private void showConfirmPop(int type) {
        if (coupleRelieveInfo == null) {
            getCoupleRelieveInfo();
            return;
        }
        if (type == 1) {
            relieveCouple(type);
        } else {//强制解除情侣，需要支付1260金币哦，确认要强制解除吗？
            if (customPopWindow == null)
                customPopWindow = new CustomPopWindow(mContext);
            customPopWindow.setRightButton("确认");
            customPopWindow.setRightColor(R.color.half_red);

            customPopWindow.setContentText(coupleRelieveInfo.coin_desc);
            customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSure() {
                    if (!NetworkUtil.isNetworkAvailable(mContext)) {
                        Utils.toast("网络好像出现了问题");
                        return;
                    }
                    MandatoryLoversPopWindow.this.dismiss();
                    //强制解除
                    relieveCouple(type);
                }
            });
            customPopWindow.showPopupWindow();
        }
       /* if (type == 1)
            relieveCouple(mData.user_id);
        else
            sendCoupleLetter(mData.user_id);*/
    }

    @Override
    public void onClick(View v) {

    }
}
