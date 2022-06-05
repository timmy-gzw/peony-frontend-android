package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.tftechsz.im.R;
import com.tftechsz.common.utils.Utils;

import razerdp.basepopup.BasePopupWindow;

/**
 *  消息更多弹窗
 */
public class ChatMorePopWindow extends BasePopupWindow implements View.OnClickListener {

    public ChatMorePopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {

        findViewById(R.id.tv_all_read).setOnClickListener(this);
        findViewById(R.id.tv_del).setOnClickListener(this);
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_chat_more);
        return v;
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
        if (id == R.id.tv_all_read) {  //标记全部已读
            NIMClient.getService(MsgService.class).clearAllUnreadCount();
            dismiss();
        } else if (id == R.id.tv_del) {
            if (listener != null)
                listener.deleteContact();
            dismiss();
        }
    }

    public interface OnSelectListener {
        void deleteContact();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
