package com.tftechsz.moment.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.moment.R;
import com.tftechsz.moment.entity.CommentListItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

//动态评论适配器
public class TrendCommentAdapter extends BaseQuickAdapter<CommentListItem, BaseViewHolder> {

    public TrendCommentAdapter(@Nullable ArrayList<CommentListItem> data) {
        super(R.layout.item_trend_comment, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CommentListItem item) {
        ConstraintLayout constraintLayout = helper.getView(R.id.item_trend_comment_root);
        ImageView headIcon = helper.getView(R.id.iv_avatar);
        TextView nickName = helper.getView(R.id.tv_name);
        TextView content = helper.getView(R.id.tv_content);
        TextView time = helper.getView(R.id.tv_time);

        int radius = Utils.getDimensPx(getContext(), R.dimen.trend_avater_image_radius);
        GlideUtils.loadRoundImage(getContext(), headIcon, item.getIcon(),radius);
        CommonUtil.setUserName(nickName, item.getNickname(), item.isVip());
        if (!TextUtils.isEmpty(item.getReply_nickname())) {
            StringBuffer sb = new StringBuffer();
            sb.append("回复").append(item.getReply_nickname()).append(" : ").append(item.getContent());

            SpannableStringBuilder builder = new SpannableStringBuilder(sb);
            builder.setSpan(new MyClickableSpan(false, constraintLayout, 0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new MyClickableSpan(true, null, item.getReply_user_id()), 2, 2 + item.getReply_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new MyClickableSpan(false, constraintLayout, 0), 2 + item.getReply_nickname().length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.at)), 2, 2 + item.getReply_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            content.setText(builder);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setHighlightColor(Utils.getColor(R.color.transparent));
        } else {
            content.setText(item.getContent().trim());
        }
        time.setText(item.getCreated_at());
    }

    private static class MyClickableSpan extends ClickableSpan {
        private final boolean isClickedName;
        private final int userid;
        private final ConstraintLayout layout;

        public MyClickableSpan(boolean isClickedName, ConstraintLayout layout, int userid) {
            this.userid = userid;
            this.layout = layout;
            this.isClickedName = isClickedName;
        }


        @Override
        public void onClick(@NonNull View widget) {
            if (isClickedName) {
                ARouterUtils.toMineDetailActivity(String.valueOf(userid));
            } else {
                if (layout != null) {
                    layout.performClick();
                }
            }
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }
}
