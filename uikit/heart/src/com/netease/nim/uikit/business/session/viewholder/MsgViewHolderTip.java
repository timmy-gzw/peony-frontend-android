package com.netease.nim.uikit.business.session.viewholder;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

import java.util.List;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderTip extends MsgViewHolderBase {
    protected TextView bodyTextView;
    private LinearLayout mLlleft, mLlTitle;
    private LinearLayout mClCall;
    private RelativeLayout mRlBox;
    private TextView mtvTitle;
    private LinearLayout mLlAudio, mLlVideo;

    public MsgViewHolderTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.chat_item_center_custom;
    }


    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.tv_content);
        mLlleft = findViewById(R.id.ll_left);
        mLlTitle = findViewById(R.id.ll_title);
        mClCall = findViewById(R.id.cl_call);
        mRlBox = findViewById(R.id.rl_box);
        mtvTitle = findViewById(R.id.tv_title);
        mLlAudio = findViewById(R.id.ll_audio);
        mLlVideo = findViewById(R.id.ll_video);
    }

    @Override
    public void bindContentView() {
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        setReSendGone();
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        mLlTitle.setPadding(0, (int) context.getResources().getDimension(R.dimen.dp_10), (int) context.getResources().getDimension(R.dimen.dp_size_42), (int) context.getResources().getDimension(R.dimen.dp_10));
        mLlleft.setVisibility(View.VISIBLE);
        mClCall.setVisibility(View.GONE);
        mtvTitle.setVisibility(View.GONE);
        mRlBox.setBackground(context.getResources().getDrawable(R.drawable.bg_black_tran30_radius6));
        if (chatMsg == null)
            return;
        ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
        String content = "";
        bodyTextView.setTextColor(context.getResources().getColor(R.color.white));
        bodyTextView.setTextSize(COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_12));
        if (tips.des.contains("跟她语音聊天") && tips.des.contains("跟她视频聊天")) {
            mLlleft.setVisibility(View.GONE);
            mtvTitle.setVisibility(View.VISIBLE);
            mRlBox.setBackground(context.getResources().getDrawable(R.drawable.ic_tip_top_right));
            mLlTitle.setPadding((int) context.getResources().getDimension(R.dimen.dp_size_12), (int) context.getResources().getDimension(R.dimen.dp_size_12), (int)context.getResources().getDimension(R.dimen.dp_size_12), (int)context.getResources().getDimension(R.dimen.dp_size_12));
            mClCall.setVisibility(View.VISIBLE);
            List<String> tipContent = ChatMsgUtil.getTipContent(tips.des);
            mLlVideo.setOnClickListener(v -> {
                open(tipContent.get(1));
            });
            mLlAudio.setOnClickListener(v -> {
                open(tipContent.get(0));
            });
            String[] split = tips.des.split("\\n<tag");
            content = split[0];
            bodyTextView.setText(content);
            bodyTextView.setTextSize(COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_10));
            bodyTextView.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            SpannableStringBuilder span = ChatMsgUtil.getTipContent(/*"        " + */tips.des, "", new ChatMsgUtil.OnSelectListener() {
                @Override
                public void onClick(String content) {
                    open(content);
                }
            });
            bodyTextView.setText(span);
        }
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void open(String content) {
        String webview = "webview://";
        String peony = "peony://";
        if (content.contains(webview)) {  //打开webview
            NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 2, content.substring(webview.length() + 1, content.length() - 1));
        } else if (content.contains(peony)) {   //打开原生页面
            NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 1, content.substring(peony.length() + 1, content.length() - 1));
        }
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
