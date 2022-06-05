package com.tftechsz.mine.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.widget.VoiceImageView;

import java.io.File;

public class VoiceAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    private int mCurrentPlayAnimPosition = -1;//当前播放动画的位置

    public TextView duration;



    public VoiceAdapter() {
        super(R.layout.rv_list_voice);
    }



    @Override
    protected void convert(BaseViewHolder helper, File item) {
        //这里就不考虑语音长度了，实际开发中用到的Sdk有提供保存语音信息的bean
        VoiceImageView ivAudio = helper.getView(R.id.iv_voice);
        duration = helper.getView(R.id.tv_duration);
        if (mCurrentPlayAnimPosition == helper.getLayoutPosition()) {
            ivAudio.startPlay();
        } else {
            ivAudio.stopPlay();
        }
    }

    /**
     * 开始播放动画
     *
     * @param position
     */
    public void startPlayAnim(int position) {
        mCurrentPlayAnimPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 停止播放动画
     */
    public void stopPlayAnim() {
        mCurrentPlayAnimPosition = -1;
        notifyDataSetChanged();
    }
}
