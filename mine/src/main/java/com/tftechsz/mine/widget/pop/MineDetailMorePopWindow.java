package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 举报，拉黑弹窗
 */
public class MineDetailMorePopWindow extends BasePopupWindow implements View.OnClickListener {

    private TextView mTvBlack, mTvUnfollow;
    private View dividerUnfollow;
    private String userId;
    private boolean isBlack;

    public MineDetailMorePopWindow(Context context, String userId) {
        super(context);
        this.userId = userId;
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mTvUnfollow = findViewById(R.id.tv_unfollow);
        dividerUnfollow = findViewById(R.id.divider_unfollow);
        mTvUnfollow.setOnClickListener(this);
        mTvBlack = findViewById(R.id.tv_black);
        mTvBlack.setOnClickListener(this);
        findViewById(R.id.tv_report).setOnClickListener(this);
        mTvBlack.setText(UserInfoHelper.isInBlackList(userId) ? "取消拉黑" : "拉黑");
        isBlack = UserInfoHelper.isInBlackList(userId);
    }

    public void setAttentionVisible(boolean isShow) {
        if (mTvUnfollow != null && dividerUnfollow != null) {
            mTvUnfollow.setVisibility(isShow ? View.VISIBLE : View.GONE);
            dividerUnfollow.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_mine_detail_more);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, true);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_enter_anim);
    }


    @Override
    protected Animation onCreateDismissAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, false);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_exit_anim);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_black) {  //拉黑
            if (listener != null)
                listener.blackUser(isBlack);
            dismiss();
        } else if (id == R.id.tv_report) {
            if (listener != null)
                listener.reportUser();
            dismiss();
        } else if (id == R.id.tv_unfollow) {
            if (listener != null) listener.unfollow();
            dismiss();
        }
    }

    public interface OnSelectListener {
        void reportUser();

        void blackUser(boolean isBlack);

        void unfollow();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
