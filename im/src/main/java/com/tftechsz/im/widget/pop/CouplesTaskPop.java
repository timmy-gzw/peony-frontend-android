package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.CouplesTaskAdapter;
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

import io.reactivex.disposables.CompositeDisposable;

/**
 * 情侣任务
 */
public class CouplesTaskPop extends BaseBottomPop {
    private final CompositeDisposable mCompositeDisposable;
    private CouplesTaskAdapter couplesTaskAdapter;
    private final String mFamilyId;
    private String userId;
    private MandatoryLoversPopWindow mandatoryLoversPopWindow;

    public CouplesTaskPop(Context context, String mFamilyId, String userId) {
        super(context);
        this.mFamilyId = mFamilyId;
        this.userId = userId;
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void initUI() {
        couplesTaskAdapter = new CouplesTaskAdapter((position, coupleTask) -> {
            if (coupleTask.status == 1) {
//领取
                CouplesTaskPop.this.sendCoupleLetter(mFamilyId, coupleTask, position);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.rel_couples_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(couplesTaskAdapter);
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_btn_dissolve_couple).setOnClickListener(v -> {
            if (mandatoryLoversPopWindow == null)
                mandatoryLoversPopWindow = new MandatoryLoversPopWindow(mContext, userId);
            mandatoryLoversPopWindow.refresh(userId);
            mandatoryLoversPopWindow.showPopupWindow();
            dismiss();
        });
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_couples_task);
    }

    public void initData(GroupCoupleDto coupleTask) {

        if (couplesTaskAdapter != null) {
            couplesTaskAdapter.getData();
            couplesTaskAdapter.getData().clear();
            couplesTaskAdapter.addData(coupleTask.couple_task);
            couplesTaskAdapter.notifyDataSetChanged();
        }

        HeadImageView mIvLeft = findViewById(R.id.iv_left);
        HeadImageView mIvRight = findViewById(R.id.iv_right);

        ImageView mIvLeftHead = findViewById(R.id.iv_left_head);
        ImageView mIvRightHead = findViewById(R.id.iv_right_head);
        if (coupleTask.sex == 1) {
            GlideUtils.loadImage(mContext, mIvLeft, coupleTask.icon);   //头像
            GlideUtils.loadImage(mContext, mIvRight, coupleTask.couple_icon);
        } else {
            GlideUtils.loadImage(mContext, mIvLeft, coupleTask.couple_icon);
            GlideUtils.loadImage(mContext, mIvRight, coupleTask.icon);
        }

        mIvLeftHead.setImageResource(R.mipmap.chat_bg_group_couple_head_boy);
        mIvRightHead.setImageResource(R.mipmap.chat_bg_group_couple_head_girl);

        TextView mTvCurrentLovers = findViewById(R.id.tv_current_lovers);
        TextView mTvSweet = findViewById(R.id.tv_sweet);
        if (coupleTask.is_couple) {  //有情侣
            mTvCurrentLovers.setText(new SpannableStringUtils.Builder().append("当前情侣：").append(coupleTask.couple_nickname).setForegroundColor(Utils.getColor(R.color.half_red)).create());
            mTvSweet.setText(new SpannableStringUtils.Builder().append("甜蜜值：").append(coupleTask.couple_sweet).setForegroundColor(Utils.getColor(R.color.half_red)).create());

        } else {
            mTvCurrentLovers.setText(new SpannableStringUtils.Builder().append("当前情侣：").append("暂无").setForegroundColor(Utils.getColor(R.color.half_red)).create());
            mTvSweet.setText(new SpannableStringUtils.Builder().append("甜蜜值：").append("暂无").setForegroundColor(Utils.getColor(R.color.half_red)).create());
        }
        TextView tvBody = findViewById(R.id.tv_body);
        tvBody.setText(coupleTask.couple_rule_desc);
    }

    /**
     * 领取任务
     */
    private void sendCoupleLetter(String family_id, GroupCoupleDto.CoupleTask coupleTask, int position) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getTask(family_id, String.valueOf(coupleTask.id))
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        //                        领取任务
                        if (couplesTaskAdapter != null) {
                            coupleTask.status = 2;
                            couplesTaskAdapter.updateDate(coupleTask, position);
                        }
                    }
                }));
    }


}
