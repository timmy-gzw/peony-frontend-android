package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.ChargeInfoDto;
import com.tftechsz.mine.entity.dto.ChargeItemDto;
import com.tftechsz.mine.mvp.IView.IChargeSettingView;
import com.tftechsz.mine.mvp.presenter.ChargeSettingPresenter;

import java.util.List;


public class ChargeSettingActivity extends BaseMvpActivity<IChargeSettingView, ChargeSettingPresenter> implements View.OnClickListener, IChargeSettingView {


    private final String MSG_COIN = "msg_coin"; //设置的聊天金币数
    private final String VOICE_COIN = "voice_coin";//设置的语音金币数
    private final String VIDEO_COIN = "video_coin";   //设置的视频金币数
    private final String IS_VOICE = "is_voice";    //是否开启语音收费：0.不开启，1.开启 默认开启
    private final String IS_VIDEO = "is_video"; //是否开启视频收费：0.不开启，1.开启 默认开启

    private CommonItemView mItemMessagePrice;  //消息价格
    private CommonItemView mItemVoicePrice;   //语音价格
    private CommonItemView mItemVideoPrice;  //视频价格
    private CommonItemView mItemVoiceSetting;  //语音设置价格
    private CommonItemView mItemVideoSetting;  //视频设置价格
    private TextView mTvTips;

    private RelativeLayout mRlSetting;

    private ChargeInfoDto mData;
    private ChargeItemDto mChargeItem;


    @Override
    public ChargeSettingPresenter initPresenter() {
        return new ChargeSettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("收费设置").setRightText("说明", v -> {
            if (mData != null)
                BaseWebViewActivity.start(ChargeSettingActivity.this, "", mData.url, 0, 6);
        })
                .build();
        mRlSetting = findViewById(R.id.rl_charge_setting);
        mItemMessagePrice = findViewById(R.id.item_message_price);
        mItemVoicePrice = findViewById(R.id.item_voice_price);
        mItemVideoPrice = findViewById(R.id.item_video_price);
        mItemVideoSetting = findViewById(R.id.item_video_setting);
        mItemVoiceSetting = findViewById(R.id.item_voice_setting);
        mTvTips = findViewById(R.id.tv_tip);
        initListener();
    }

    private void initListener() {
        mItemMessagePrice.setOnClickListener(this);
        mItemVoicePrice.setOnClickListener(this);
        mItemVideoPrice.setOnClickListener(this);
        mItemVoiceSetting.setOnClickListener(this);
        mItemVideoSetting.setOnClickListener(this);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_charge_setting;
    }


    @Override
    protected void initData() {
        super.initData();
        p.getChargeInfo();
        p.getChargeItem();  //价格ITEM
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mData == null)
            return;
        if (id == R.id.item_message_price) {  //消息价格
            p.messagePrice(this, mChargeItem, 1, mRlSetting, mData.msg_coin);   //消息
        } else if (id == R.id.item_voice_price) {  //语音价格
            p.messagePrice(this, mChargeItem, 2, mRlSetting, mData.voice_coin);   //语音
        } else if (id == R.id.item_video_price) {  //视频价格
            p.messagePrice(this, mChargeItem, 3, mRlSetting, mData.video_coin);   //视频
        } else if (id == R.id.item_video_setting) {  //视频开关设置
            p.updateChargeInfo(IS_VIDEO, mData.is_video == 1 ? 0 : 1, "");
        } else if (id == R.id.item_voice_setting) {  //语音开关设置
            p.updateChargeInfo(IS_VOICE, mData.is_voice == 1 ? 0 : 1, "");
        }
    }


    @Override
    public void getChargeSuccess(ChargeInfoDto data) {
        mData = data;
        mItemMessagePrice.setRightText(data.msg_coin == 0 ? "免费" : data.msg_coin + "金币/条");
        mItemVoicePrice.setRightText(data.voice_coin == 0 ? "免费" : data.voice_coin + "金币/分钟");
        mItemVideoPrice.setRightText(data.video_coin == 0 ? "免费" : data.video_coin + "金币/分钟");
        mItemVoiceSetting.getMySwitch().setChecked(data.is_voice == 1);
        mItemVideoSetting.getMySwitch().setChecked(data.is_video == 1);
        mTvTips.setText(data.desc);


    }

    @Override
    public void updateChargeSuccess(String field, int value, Boolean data, String content) {
        //设置的聊天金币数
        if (TextUtils.equals(field, MSG_COIN) && data) {
            mItemMessagePrice.setRightText(content);
            mData.msg_coin = value;
        }
        //设置的语音金币数
        if (TextUtils.equals(field, VOICE_COIN)) {
            mItemVoicePrice.setRightText(content);
            mData.voice_coin = value;
        }
        //设置的视频金币数
        if (TextUtils.equals(field, VIDEO_COIN)) {
            mItemVideoPrice.setRightText(content);
            mData.video_coin = value;
        }
        //语音
        if (TextUtils.equals(field, IS_VOICE)) {
            if (data && mData.is_voice == 1) {
                mData.is_voice = 0;
                mItemVoiceSetting.getMySwitch().setChecked(false);
            } else if (data && mData.is_voice == 0) {
                mData.is_voice = 1;
                mItemVoiceSetting.getMySwitch().setChecked(true);
            }
        }
        //视频
        if (TextUtils.equals(field, IS_VIDEO)) {
            if (data && mData.is_video == 1) {
                mData.is_video = 0;
                mItemVideoSetting.getMySwitch().setChecked(false);
            } else if (data && mData.is_video == 0) {
                mData.is_video = 1;
                mItemVideoSetting.getMySwitch().setChecked(true);
            }
        }
    }

    @Override
    public void getChargeItemSuccess(ChargeItemDto data) {
        mChargeItem = data;
    }

    /**
     * 设置价格
     *
     * @param list
     * @param chargeItem
     * @param type
     * @param position
     */
    @Override
    public void selectPrice(List<String> list, ChargeItemDto chargeItem, int type, int position) {
        String item = list.get(position);
        if (type == 1) {   //消息类型
            for (int i = 0; i < chargeItem.msg.size(); i++) {
                if (TextUtils.equals(item, chargeItem.msg.get(i).name)) {
                    p.updateChargeInfo(MSG_COIN, chargeItem.msg.get(i).coin, item);
                }
            }
        } else if (type == 2) {
            for (int i = 0; i < chargeItem.voice.size(); i++) {
                if (TextUtils.equals(item, chargeItem.voice.get(i).name)) {
                    p.updateChargeInfo(VOICE_COIN, chargeItem.voice.get(i).coin, item);
                }
            }
        } else if (type == 3) {
            for (int i = 0; i < chargeItem.video.size(); i++) {
                if (TextUtils.equals(item, chargeItem.video.get(i).name)) {
                    p.updateChargeInfo(VIDEO_COIN, chargeItem.video.get(i).coin, item);
                }
            }
        }
    }


}
