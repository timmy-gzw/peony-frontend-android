package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class RecommendValuePop extends BaseCenterPop {


    private final ChatMsg.RecommendValue mData;

    public RecommendValuePop(Context context, ChatMsg.RecommendValue recommendValue) {
        super(context);
        mData = recommendValue;
        TextView tvTitle = findViewById(R.id.tv_title);
        RecyclerView rvRecommend = findViewById(R.id.rv_recommend);
        rvRecommend.setLayoutManager(new LinearLayoutManager(mContext));
        RecommendAdapter adapter = new RecommendAdapter();
        rvRecommend.setAdapter(adapter);
        adapter.setList(mData.content);
        tvTitle.setText(mData.title);
        findViewById(R.id.tv_reset).setOnClickListener(v -> {
            if (null != mData.button) {
                String event = mData.button.event;
                if (event.contains(Interfaces.LINK_WEBVIEW)) {  //打开webview
                    BaseWebViewActivity.start(BaseApplication.getInstance(), "", event.substring(Interfaces.LINK_WEBVIEW.length()), 0, 0);
                }
            }
            dismiss();
        });
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());


    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_recommend_value);
    }


    static class RecommendAdapter extends BaseQuickAdapter<ChatMsg.RecommendContent, BaseViewHolder> {

        public RecommendAdapter() {
            super(R.layout.item_recommend_value);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatMsg.RecommendContent r) {
            TextView tvContent = baseViewHolder.getView(R.id.tv_content);
            GlideUtils.loadImage(getContext(), baseViewHolder.getView(R.id.iv_icon), r.icon);
            if (r.text != null && r.text.size() > 0) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                for (ChatMsg.RecommendContentText contentText : r.text) {
                    builder.append(contentText.desc);
                    int start = builder.toString().indexOf(contentText.desc);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor(contentText.color)), start, start + contentText.desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvContent.setText(builder);
                tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            }
            View view = baseViewHolder.getView(R.id.view);
            if (baseViewHolder.getLayoutPosition() == getData().size() - 1) {
                view.setVisibility(View.GONE);
            }

        }
    }


}
