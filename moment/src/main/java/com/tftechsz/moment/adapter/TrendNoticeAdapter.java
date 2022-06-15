package com.tftechsz.moment.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.moment.R;
import com.tftechsz.moment.mvp.entity.NoticeBean;

import java.util.List;

/**
 * 动态适配器
 */
public class TrendNoticeAdapter extends BaseQuickAdapter<NoticeBean, BaseViewHolder> {
    @Autowired
    UserProviderService service;

    public TrendNoticeAdapter(@Nullable List<NoticeBean> data) {
        super(R.layout.item_trend_notice, data);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NoticeBean item) {
        if (item == null)
            return;
        ImageView icon = helper.getView(R.id.iv_icon);
        ImageView image = helper.getView(R.id.iv_image);
        TextView name = helper.getView(R.id.tv_name);
        TextView type = helper.getView(R.id.tv_from_type);
        TextView content = helper.getView(R.id.tv_content);
        TextView time = helper.getView(R.id.tv_time);

        GlideUtils.loadRoundImageRadius(getContext(), icon, item.getIcon());
        GlideUtils.loadRoundImageRadius(getContext(), image, item.getImage());
        CommonUtil.setUserName(name, item.getNickname(), item.isVip());

        switch (item.getFrom_type()) {
            case "reply-comment":
                type.setText("回复了你");
                break;

            case "comment":
                //case "delete-comment":
                type.setText("评论了你");
                break;

            case "praise":
                type.setText("赞了你");
                break;

            default:
                type.setText("");
                break;
        }

        if (item.getStatus() == 0) { //判断删除状态
            content.setBackgroundResource(R.drawable.sp_eee_c_2);
            content.setTextSize(12);
        } else {
            content.setBackgroundResource(0);
            content.setTextSize(18);
            content.setPadding(0, 0, 0, 0);
        }

        if (!TextUtils.isEmpty(item.getContent())) {
            content.setVisibility(View.VISIBLE);
            content.setText(item.getContent());
        } else {
            content.setVisibility(View.GONE);
        }
        time.setText(item.getCreated_at());
    }
}
