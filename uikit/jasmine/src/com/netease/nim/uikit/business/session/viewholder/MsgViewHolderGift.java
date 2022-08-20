package com.netease.nim.uikit.business.session.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.module.list.MsgAdapter;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

/**
 * 个人礼物消息
 */
public class MsgViewHolderGift extends MsgViewHolderBase implements MsgAdapter.OnScrollListener  {

    private ImageView typeImage,mIvFromUserAvatar;
    private FrameLayout mFlGift;
    private FrameLayout mFlImage;
    private LinearLayout mLlContent,mLlTotal;
    private TextView tvSend,tvSendRight;
    private TextView mTvGift,mTvGiftNum,mTvFromUserName,mTvGiftBtn;
    private LinearLayout mLlLeft,mLlRight;
    private ImageView mIvFromUserAvatarRight;
    private TextView mTvFromUserNameRight;
    public MsgViewHolderGift(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_gift;
    }

    @Override
    public void inflateContentView() {
        mLlTotal = findViewById(R.id.message_item_avchat_content);
        typeImage = findViewById(R.id.message_item_img);
        mFlGift = findViewById(R.id.fl_gift);
        mLlContent = findViewById(R.id.ll_content);
        mFlImage = findViewById(R.id.fl_image);
        tvSend = findViewById(R.id.tv_send);
        tvSendRight = findViewById(R.id.tv_send_right);
        mTvGift = findViewById(R.id.tv_gift);
        mTvGiftNum = findViewById(R.id.tv_gift_num);
        mTvFromUserName = findViewById(R.id.tv_from_user_name);
        mIvFromUserAvatar = findViewById(R.id.iv_from_user_avatar);
        mTvGiftBtn = findViewById(R.id.tv_gift_btn);
        mLlLeft = findViewById(R.id.ll_left);
        mLlRight = findViewById(R.id.ll_right);
        mIvFromUserAvatarRight = findViewById(R.id.iv_from_user_avatar_right);
        mTvFromUserNameRight = findViewById(R.id.tv_from_user_name_right);
        getMsgAdapter().addOnScrollListener(this);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
        try {
            refreshContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void layoutByDirection() {
        if (isReceivedMessage()) {   //接收消息
            mLlTotal.setBackgroundResource(R.drawable.bg_chat_gift_left);
            setGravity(mLlContent, Gravity.START);
            setGravity(mFlImage, Gravity.END | Gravity.CENTER_VERTICAL);
            mLlContent.setPadding(0, 0, ScreenUtil.dip2px(0), 0);
            mLlContent.setGravity(Gravity.START);
            mFlGift.setPadding(ScreenUtil.dip2px(0), 0, 0, 0);
            typeImage.setPadding(ScreenUtil.dip2px(5), 0, 0, 0);
            mTvGift.setGravity(Gravity.END);
            mTvGiftBtn.setText("回赠礼物");
            tvSend.setText("送给你");
            mLlRight.setVisibility(View.GONE);
            mLlLeft.setVisibility(View.VISIBLE);
        } else {
            mLlTotal.setBackgroundResource(R.drawable.bg_chat_gift_right);
            setGravity(mFlImage, Gravity.START | Gravity.CENTER_VERTICAL);
            setGravity(mLlContent, Gravity.END );
            mLlContent.setGravity(Gravity.END);
            mTvGift.setGravity(Gravity.START);
            mFlGift.setPadding(0, 0, ScreenUtil.dip2px(0), 0);
            mLlContent.setPadding(ScreenUtil.dip2px(0), 0, 0, 0);
            typeImage.setPadding(0, 0, ScreenUtil.dip2px(5), 0);
            mTvGiftBtn.setText("继续送礼");
            tvSendRight.setText("送出礼物");
            mLlLeft.setVisibility(View.GONE);
            mLlRight.setVisibility(View.VISIBLE);
        }
    }

    private void refreshContent() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
        TextView targetTextView = isReceivedMessage()?mTvFromUserName:mTvFromUserNameRight;
        ImageView targetImageView = isReceivedMessage()?mIvFromUserAvatar:mIvFromUserAvatarRight;
        if (gift != null) {
            mTvGift.setText(gift.gift_info.name);
            mTvGiftNum.setText("x" + gift.gift_num);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(4)))
                    .dontAnimate();          //缓存全尺寸
            Glide.with(context)                             //配置上下文
                    .asDrawable()
                    .apply(options)
                    .load(gift.gift_info.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(typeImage);

            UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(isReceivedMessage()?chatMsg.from:chatMsg.to);
            if(null != userInfo.getName()) {
                targetTextView.setText(userInfo.getName());
            }

            if(null != userInfo.getAvatar()) {
                String avaterUrl = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + userInfo.getAvatar();
                Glide.with(context)                             //配置上下文
                        .asDrawable()
                        .apply(options)
                        .load(avaterUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .into(targetImageView);
            }
        }
    }

    private ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        String configInfo = sp.getString("configInfo", "");
        Gson gson = new Gson();
        ConfigInfo c = gson.fromJson(configInfo, ConfigInfo.class);
        return c;
    }

    @Override
    protected void bindHolder(BaseViewHolder holder) {
        super.bindHolder(holder);

    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getSessionListener() != null)
            NimUIKitImpl.getSessionListener().continueSendGift(message);
    }

    @Override
    protected boolean isShowHeadImage() {
        return true;
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    @Override
    public void getScrollPosition(float sx, RecyclerView recyclerView, int rvHeight, View view, int last) {


    }

}
