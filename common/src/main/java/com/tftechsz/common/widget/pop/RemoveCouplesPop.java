package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.CoupleRelieveApplyInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 解除情侣关系
 */
public class RemoveCouplesPop extends BaseCenterPop implements View.OnClickListener {

    private final CompositeDisposable mCompositeDisposable;
    private View view;

    private int applyId;
    private CoupleRelieveApplyInfo coupleRelieveApplyInfo;

    public RemoveCouplesPop(Context context, int applyId) {
        super(context);
        this.applyId = applyId;
        mCompositeDisposable = new CompositeDisposable();
        view.findViewById(R.id.tv_think).setOnClickListener(this);
        view.findViewById(R.id.tv_accept).setOnClickListener(this);

    }

    public void getData(int applyId) {
        this.applyId = applyId;
        relieveCoupleInfos(applyId);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void initUI() {

        HeadImageView headImageView = view.findViewById(R.id.iv_left);
        HeadImageView headImageViewRight = view.findViewById(R.id.iv_right);

        ImageView imageView = view.findViewById(R.id.iv_left_head);
        ImageView imageViewRight = view.findViewById(R.id.iv_left_head_right);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(coupleRelieveApplyInfo.from_nickname);
        TextView tvNameRight = view.findViewById(R.id.tv_name_right);
        tvNameRight.setText(coupleRelieveApplyInfo.to_nickname);
        if (coupleRelieveApplyInfo.from_sex == 1) {
            GlideUtils.loadImage(mContext, headImageView, coupleRelieveApplyInfo.from_icon);   //头像
            GlideUtils.loadImage(mContext, headImageViewRight, coupleRelieveApplyInfo.to_icon);
        } else {
            GlideUtils.loadImage(mContext, headImageView, coupleRelieveApplyInfo.to_icon);
            GlideUtils.loadImage(mContext, headImageViewRight, coupleRelieveApplyInfo.from_icon);
        }

        imageView.setImageResource(R.mipmap.chat_bg_group_couple_head_boy);
        imageViewRight.setImageResource(R.mipmap.chat_bg_group_couple_head_girl);
    }

    @Override
    protected View createPopupById() {
        view = createPopupById(R.layout.pop_remove_couples);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_think) {
            relieveCouple(applyId, 1);
        } else if (v.getId() == R.id.tv_accept) {
            relieveCouple(applyId, 2);

        }
    }

    /**
     * 解除情侣
     */
    private void relieveCouple(int applyId, int type) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(PublicService.class).relieveCoupleHandler(applyId, type)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        dismiss();
                    }
                }));
    }


    /**
     * 匹配数据
     */
    private void relieveCoupleInfos(int applyId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(PublicService.class).coupleRelieveApply(applyId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CoupleRelieveApplyInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<CoupleRelieveApplyInfo> response) {
                        if (response == null || response.getData() == null) return;
                        RemoveCouplesPop.this.coupleRelieveApplyInfo = response.getData();
                        initUI();

                    }
                }));
    }

}
