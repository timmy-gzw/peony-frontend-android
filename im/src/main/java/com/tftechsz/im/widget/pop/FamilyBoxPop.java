package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.im.R;
import com.tftechsz.im.databinding.PopFamilyBoxBinding;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.FamilyBoxDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import androidx.databinding.DataBindingUtil;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.im.widget.pop
 * 描 述 : TODO
 */
public class FamilyBoxPop extends BaseCenterPop {
    private CountBackUtils mCountBack2Box;
    private PopFamilyBoxBinding mBind;
    private final CompositeDisposable mCompositeDisposable;

    public FamilyBoxPop(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void initUI() {
        mBind.del.setOnClickListener(v -> dismiss());
        mBind.ivPrestige.setOnClickListener(v -> {
            String desc = SPUtils.getInstance().getString(Interfaces.SP_FAMILY_BOX_ACTIVITY_DESC);
            if (TextUtils.isEmpty(desc)) {
                return;
            }
            new FamilyBoxDescPop(getContext(), desc).showPopupWindow();
        });
        mBind.btn.setOnClickListener(v -> {
            if (!ClickUtil.canOperate()) return;
            mBind.btn.setEnabled(false);
            mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                    .rushFamilyBox()
                    .compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<FamilyBoxDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<FamilyBoxDto> response) {
                            FamilyBoxDto dto = response.getData();
                            if (dto != null) {
                                mBind.lottie.playAnimation();
                                mBind.lottie.postDelayed(() -> {
                                    dismiss();
                                    new FamilyBoxCoinPop(getContext()).setCoin(dto.coin).showPopupWindow();
                                }, 1000);
                            }
                        }
                    }));
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mBind.lottie.cancelAnimation();
                mBind.lottie.setProgress(0);
            }
        });

    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_family_box);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    public void setData(ChatMsg.FamilyBox familyBox) {
        if (mBind.lottie.isAnimating()) { //如果正在播动画的时候不更新按钮状态
            return;
        }

        mBind.tvTask1.setText(new SpannableStringUtils.Builder().append("发言人数 ")
                .append(String.valueOf(familyBox.realUsersCount))
                .setForegroundColor(Utils.getColor(R.color.family_box_color))
                .append("/")
                .append(String.valueOf(familyBox.defaultUsersCount)).create());
        mBind.tvTask2.setText(new SpannableStringUtils.Builder().append("金币消耗 ")
                .append(String.valueOf(familyBox.realCoins))
                .setForegroundColor(Utils.getColor(R.color.family_box_color))
                .append("/")
                .append(String.valueOf(familyBox.defaultCoins)).create());
        mBind.tips.setText(familyBox.tips);
        switch (familyBox.status) {
            case 0: //倒计时
                mBind.lottie.setProgress(0);
                mBind.setIsClick(false);
                mBind.btn.setText(Utils.getLastTime(familyBox.count_down));
                if (mCountBack2Box == null)
                    mCountBack2Box = new CountBackUtils();
                if (familyBox.count_down > 0) {
                    mCountBack2Box.countBack(familyBox.count_down, new CountBackUtils.Callback() {
                        @Override
                        public void countBacking(long time) {
                            mBind.btn.setText(Utils.getLastTime(time));
                        }

                        @Override
                        public void finish() {
                            mBind.btn.setText(Utils.getLastTime(0));
                        }

                    });
                }
                break;

            case 1://开抢宝箱
                mBind.lottie.setProgress(0);
                mBind.setIsClick(true);
                if (mCountBack2Box != null && mCountBack2Box.isTiming()) {
                    mCountBack2Box.destroy();
                }
                mBind.btn.setText("抢宝箱");
                break;

            case 2:
                if (!mBind.lottie.isAnimating())
                    mBind.lottie.setProgress(1);
                mBind.setIsClick(false);
                if (mCountBack2Box != null && mCountBack2Box.isTiming()) {
                    mCountBack2Box.destroy();
                }
                mBind.btn.setText("您已领取, 请等待下一轮");
                break;

            case 3:
                if (!mBind.lottie.isAnimating())
                    mBind.lottie.setProgress(1);
                mBind.setIsClick(false);
                if (mCountBack2Box != null && mCountBack2Box.isTiming()) {
                    mCountBack2Box.destroy();
                }
                mBind.btn.setText("已抢完, 请等待下一轮");
                break;
        }
    }
}
