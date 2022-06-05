package com.netease.nim.uikit.common.ui.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class AvatarVipFrameView extends RelativeLayout {
    private final int DEFAULT_WIDTH = (int) NimUIKit.getContext().getResources().getDimension(R.dimen.dp_size_75);
    private final int DEFAULT_MARGIN = (int) NimUIKit.getContext().getResources().getDimension(R.dimen.dp_size_7);
    private ImageView mBgFrame;
    private View ivOnline;
    private AvatarImageView mIvAvatar;
    private FrameLayout flAvatar;

    public AvatarVipFrameView(Context context) {
        super(context);
    }

    public AvatarVipFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_frame_avatar_vip_view, this);
        flAvatar = findViewById(R.id.fl_avatar);
        ivOnline = findViewById(R.id.iv_online);
        mBgFrame = findViewById(R.id.iv_frame);
        mIvAvatar = findViewById(R.id.iv_header);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VipImageView);
        if (typedArray != null) {
            int height = typedArray.getDimensionPixelSize(R.styleable.VipImageView_vip_height, DEFAULT_WIDTH);
            int width = typedArray.getDimensionPixelSize(R.styleable.VipImageView_vip_width, DEFAULT_WIDTH);
            int margin = typedArray.getDimensionPixelSize(R.styleable.VipImageView_vip_margin, DEFAULT_MARGIN);
            boolean is_round = typedArray.getBoolean(R.styleable.VipImageView_is_round, false);
            mIvAvatar.setRoundIcon(is_round);

            FrameLayout.LayoutParams iconParam = (FrameLayout.LayoutParams) mIvAvatar.getLayoutParams();
            iconParam.setMargins(margin, margin, margin, margin);
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) flAvatar.getLayoutParams();
            param.width = width;
            param.height = height;
            flAvatar.setLayoutParams(param);
            typedArray.recycle();
        }
    }

    public void setHeight(int width, int height) {
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) flAvatar.getLayoutParams();
        param.width = width;
        param.height = height;
        flAvatar.setLayoutParams(param);

    }

    public void setOnline(boolean online) {
        ivOnline.setVisibility(online ? VISIBLE : GONE);
    }


    public AvatarImageView getImageView() {
        return mIvAvatar;
    }


    public void setAvatar(Object contactId) {
        if (contactId instanceof String) {
            mIvAvatar.loadBuddyAvatar((String) contactId);
        } else if (contactId instanceof IMMessage) {
            mIvAvatar.loadBuddyAvatar((IMMessage) contactId);
        } else if (contactId instanceof Integer) {
            if (0 == (Integer) contactId) {
                mIvAvatar.setImageResource(0);
            } else {
                mIvAvatar.setImageResource(R.drawable.chat_ic_customer_service_secretary);
            }
        }
    }

    public void setPartyAvatar(Object contactId) {
        if (contactId instanceof String) {
            mIvAvatar.loadPartyChatListAvatar((String) contactId);
        }
    }


    public void setBgFrame(int pictureFrame) {
        if (pictureFrame == -1) {
            mBgFrame.setBackgroundResource(R.color.transparent);
        } else {
            VipUtils.setPersonalise(mBgFrame, -1, false, true);
        }
    }

    public void setChatBgFrame(int pictureFrame) {
        if (pictureFrame == -1) {
            mBgFrame.setBackgroundResource(R.color.transparent);
        } else {
            VipUtils.setPersonalise(mBgFrame, pictureFrame, false, true);
        }
      /*  if (TextUtils.isEmpty(picture_frame_url)) {
//            playGift("http://peony-public.oss-cn-shenzhen.aliyuncs.com/dress/123456.svga");
            return;
        }

        if (picture_frame_url.endsWith(".svga")) {
            playGift(picture_frame_url);
            return;
        }
        Glide.with(mBgFrame.getContext()).load(picture_frame_url).placeholder(R.drawable.bg_trans).into(mBgFrame);*/
    }

}
