package com.tftechsz.moment.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.moment.R;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.moment.adapter
 * 描 述 : TODO
 */
public class ReportContactAdapter extends BaseQuickAdapter<ConfigInfo.FeedbackContactNew, BaseViewHolder> {
    public ReportContactAdapter() {
        super(R.layout.item_report_contact);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, ConfigInfo.FeedbackContactNew bean) {
        ImageView icon = helper.getView(R.id.icon);
        TextView msg = helper.getView(R.id.tv_msg);
        TextView copy = helper.getView(R.id.tv_copy);
        GlideUtils.loadImage(getContext(), icon, bean.icon);
        msg.setText(bean.msg);
        copy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", bean.msg_copy);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Utils.toast("复制成功");
        });
        copy.setVisibility(TextUtils.isEmpty(bean.msg_copy) ? View.GONE : View.VISIBLE);
    }
}
