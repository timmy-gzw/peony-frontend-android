package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderFamilyNobilityTip extends MsgViewHolderBase {
    protected TextView bodyTextView;
    private ImageView ivLevel;

    public MsgViewHolderFamilyNobilityTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_item_family_nobility_tip;
    }


    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.tv_content);
        ivLevel = findViewById(R.id.iv_level);
    }

    @Override
    public void bindContentView() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.NobilityLevelUp levelUp = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
        if (levelUp != null) {
            ivLevel.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate();          //缓存全尺寸
            Glide.with(context)                             //配置上下文
                    .asDrawable()
                    .apply(options)
                    .load(levelUp.badge)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(ivLevel);
            SpannableStringBuilder spannableString = new SpannableStringBuilder(levelUp.msg);
            int start = spannableString.toString().indexOf(levelUp.nickname);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#EEC600")), start, start + levelUp.nickname.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            bodyTextView.setText(spannableString);
        }
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }


}
