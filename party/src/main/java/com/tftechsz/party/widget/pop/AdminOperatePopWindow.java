package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;

/**
 * 管理员弹窗
 */
public class AdminOperatePopWindow extends BaseCenterPop implements View.OnClickListener {
    private int mLock;
    private final TextView mLockSeat, mTvOnSeat, mLockSeat1;

    public AdminOperatePopWindow(Context context) {
        super(context);
        mTvOnSeat = findViewById(R.id.tv_on_seat);
        mLockSeat1 = findViewById(R.id.tv_lock_seat1);
        mLockSeat1.setOnClickListener(this);
        mTvOnSeat.setOnClickListener(this);  //上麦
        mLockSeat = findViewById(R.id.tv_lock_seat);
        mLockSeat.setOnClickListener(this); //锁定麦位
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_admin_operate);
    }


    public void setStatus(int lock, int fightPattern, boolean isOnSeat) {
        this.mLock = lock;
        if (fightPattern == 2) {   //pk模式
            if (isOnSeat) {
                mLockSeat1.setVisibility(View.VISIBLE);
                mLockSeat.setVisibility(View.INVISIBLE);
                mTvOnSeat.setVisibility(View.INVISIBLE);
                if (mLock == 1) {  //锁定了
                    mLockSeat1.setText("解锁麦位");
                } else {
                    mLockSeat1.setText("锁定麦位");
                }
            } else {
                setNormal();
            }
        } else {
            setNormal();
        }
    }


    private void setNormal() {
        mLockSeat1.setVisibility(View.INVISIBLE);
        mLockSeat.setVisibility(View.VISIBLE);
        mTvOnSeat.setVisibility(View.VISIBLE);
        if (mLock == 1) {
            mTvOnSeat.setText("解锁并上麦");
            mLockSeat.setText("解锁");
        } else {
            mTvOnSeat.setText("上麦");
            mLockSeat.setText("锁定麦位");
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_on_seat) {  //上麦
            if (listener != null)
                listener.onSeat();
            dismiss();
        } else if (id == R.id.tv_lock_seat || id == R.id.tv_lock_seat1) {  //锁定麦位
            if (listener != null)
                listener.lockSeat(mLock == 1 ? 0 : 1);
            dismiss();
        }
    }


    public interface OnSelectListener {
        void onSeat();

        void lockSeat(int lock);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
