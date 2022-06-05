package com.tftechsz.im.adapter;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.im.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.iservice.MineService;

import java.util.List;

public class NoticeAdapter extends BaseQuickAdapter<IMMessage, BaseViewHolder> {

    private final String mChatUser;

    public NoticeAdapter(@Nullable List<IMMessage> data, String chatUser) {
        super(R.layout.item_activity_notice, data);
        this.mChatUser = chatUser;

    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, IMMessage item) {
        TextView tvContent = helper.getView(R.id.tv_content);
        helper.setText(R.id.tv_time, TimeUtil.getTimeShowString(item.getTime(), false));
        helper.setBackgroundResource(R.id.iv_type, TextUtils.equals(mChatUser, Constants.CUSTOMER_SERVICE) ? R.mipmap.ic_customer_service : R.mipmap.ic_event_notification);
        SpannableStringBuilder span = ChatMsgUtil.getTipContent(item.getContent(), "", new ChatMsgUtil.OnSelectListener() {
            @Override
            public void onClick(String content) {
                String webview = "webview://";
                String peony = "peony://";
                if (content.contains(webview)) {  //打开webview
                    NimUIKitImpl.getSessionTipListener().onTipMessageClicked(getContext(), 2, content.substring(webview.length() + 1, content.length() - 1));
                } else if (content.contains(peony)) {   //打开原生页面
                    NimUIKitImpl.getSessionTipListener().onTipMessageClicked(getContext(), 1, content.substring(peony.length() + 1, content.length() - 1));
                }
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("新年活动小秘书链接点击", "secretary_link_click", "", JSON.toJSONString(new NavigationLogEntity(-1,
                                1, System.currentTimeMillis(), com.tftechsz.common.utils.CommonUtil.getOSName(), Constants.APP_NAME)), null
                        );

            }
        });
        tvContent.setText(span);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
