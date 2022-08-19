package com.tftechsz.mine.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.sticky.BaseViewHolder;
import com.tftechsz.common.widget.sticky.GroupedRecyclerViewAdapter;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.ExchangeRecord;
import com.tftechsz.mine.entity.dto.ExchangeRecordDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
public class IntegralGroupAdapter extends GroupedRecyclerViewAdapter {

    private final List<ExchangeRecord> mGroups;
    private final int mType;     //清单  0 积分清单  1 金币清单  2兑换记录  3 聊天卡消耗记录  4音符清单

    public IntegralGroupAdapter(Context context, ArrayList<ExchangeRecord> groups, int type) {
        super(context);
        mGroups = groups;
        mType = type;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ExchangeRecordDto> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.item_head_time;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        if (mType == 2 || mType == 5) {
            return R.layout.item_exchange_record;
        }
        return R.layout.item_integral_detailed;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        ExchangeRecord entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_time, entity.getHeader());
    }

    @Override
    public void onBindFooterViewHolder(com.tftechsz.common.widget.sticky.BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(com.tftechsz.common.widget.sticky.BaseViewHolder helper, int groupPosition, int childPosition) {
        ExchangeRecordDto item = mGroups.get(groupPosition).getChildren().get(childPosition);
        View view = helper.get(R.id.view);

        if (mType == 2 || mType == 5) {   // 兑换记录
            TextView tvStatus = helper.get(R.id.tv_status);
            helper.setText(R.id.tv_time, item.created_at).setText(R.id.tv_integral, item.integral);
            //0.申请中，1.已兑换，2.不允许兑换
            if (item.status == 0) {
                tvStatus.setText("审核中…");
            } else if (item.status == 1) {
                tvStatus.setText("兑换成功");
            } else if (item.status == 2) {
                tvStatus.setText("兑换失败");
            }
            TextView tvContent = helper.get(R.id.tv_title);
            setContent(tvContent, item.title);
            view.setVisibility(childPosition == mGroups.get(groupPosition).getChildren().size() - 1 ? View.INVISIBLE : View.VISIBLE);
            ImageView ivIcon = helper.get(R.id.ic_icon);
            if (ivIcon != null) {
                GlideUtils.loadImage(mContext, ivIcon, item.image_small);
            }
        } else {
            TextView tvContent = helper.get(R.id.tv_content);
            helper.setText(R.id.tv_time, item.created_at)
                    .setText(R.id.tv_integral, item.cost)
                    .setText(R.id.tv_balance, item.cost_balance);  // 余额
            int color = !TextUtils.isEmpty(item.cost) && item.cost.contains("-") ? ContextCompat.getColor(mContext, R.color.green) : ContextCompat.getColor(mContext, R.color.c_ff4951);
            helper.setTextColor(R.id.tv_integral, color);
            setContent(tvContent, item.title);
        }
    }


    private void setContent(TextView tvContent, String msg) {
        SpannableStringBuilder span = ChatMsgUtil.getTipContent(msg, "", content -> {
            String webview = "webview://";
            String peony = "peony://";
            if (content.contains(webview)) {  //打开webview
                NimUIKitImpl.getSessionTipListener().onTipMessageClicked(mContext, 2, content.substring(webview.length() + 1, content.length() - 1));
            } else if (content.contains(peony)) {   //打开原生页面
                NimUIKitImpl.getSessionTipListener().onTipMessageClicked(mContext, 1, content.substring(peony.length() + 1, content.length() - 1));
            }
        });
        tvContent.setText(span);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
