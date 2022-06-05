package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.dto.JoinPartyDto;

import razerdp.basepopup.BasePopupWindow;

/**
 * 派对排名
 */
public class PartyHourRankPopWindow extends BasePopupWindow implements View.OnClickListener {
    private final TextView mTvHourContent;
    private JoinPartyDto mData;

    public PartyHourRankPopWindow(Context context) {
        super(context);
        setBackground(R.color.transparent);
        mTvHourContent = findViewById(R.id.tv_hour_content);
        findViewById(R.id.tv_look_rank).setOnClickListener(this);
    }


    public void setData(JoinPartyDto data) {
        mData = data;
        if (mData != null && mData.getRoom() != null) {
            if (mData.getRoom().getHour_rank() == 0) {
                mTvHourContent.setText("暂未上榜，再接再厉哦~");
            } else if (mData.getRoom().getHour_rank() == 1) {
                mTvHourContent.setText(new SpannableStringUtils.Builder().append("当前小时榜排名").append("第" + mData.getRoom().getHour_rank() + "名")
                        .setForegroundColor(Utils.getColor(R.color.red))
                        .append("，无人能敌")
                        .create());
            } else {
                mTvHourContent.setText(new SpannableStringUtils.Builder().append("当前小时榜排名").append("第" + mData.getRoom().getHour_rank() + "名")
                        .setForegroundColor(Utils.getColor(R.color.red))
                        .append("，距离上一名还差" + mData.getRoom().getHour_rank_before_diff() + "音量值")
                        .create());

            }
        }
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_party_hour_rank);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_look_rank) {  //查看榜单
            if (mData != null && mData.getRoom() != null) {
                BaseWebViewActivity.start(getContext(), "", mData.getRoom().getHour_rank_page(),false,0,17);
                dismiss();
            }
        }
    }
}
