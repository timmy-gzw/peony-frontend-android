package com.tftechsz.im.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.im.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;


public class OnlineListAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {

    public IOnLine iOnLine;
    UserProviderService userService;

    public OnlineListAdapter(IOnLine iOnLine) {
        super(R.layout.adapter_online_list);
        this.iOnLine = iOnLine;
        userService = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo item) {
        helper.setText(R.id.tv_name, item.getNickname());
        if (!item.isVip()) {
            helper.setTextColor(R.id.tv_name, Color.parseColor("#333333"));
        } else {
            helper.setTextColor(R.id.tv_name, Color.parseColor("#F6508D"));
        }
//        CommonUtil.setSexAndAge(getContext(), item.getSex(), item.getAge() + "", helper.getView(R.id.iv_sex));
        GlideUtils.loadCircleImage(helper.itemView.getContext(), helper.getView(R.id.img_header), item.getIcon());
        helper.getView(R.id.img_click_btn).setOnClickListener(v -> iOnLine.accost(item.getUser_id(), getItemPosition(item)));
        helper.getView(R.id.tv_click_btn).setOnClickListener(v -> iOnLine.chat(item.getUser_id()));
        TextView tvSex = helper.getView(R.id.iv_sex);
        int level = item.rich_level;
//        String userLevel = (String) ext.get("rich_level");
        tvSex.setBackgroundResource(GlideUtils.getResId(GlideUtils.getUserLevel(level), R.drawable.class));
        tvSex.setText(String.valueOf(level));

        if (userService.getUserInfo().isGirl()) {
            if (item.getSex() == 2) {
                helper.getView(R.id.tv_click_btn).setVisibility(View.GONE);
                helper.getView(R.id.img_click_btn).setVisibility(View.GONE);
            } else {
                helper.setImageResource(R.id.img_click_btn, R.mipmap.party_btn_greet);
                accostBtn(helper, item);
            }

        } else {
            if (item.getSex() == 1) {
                //看男用户无显示
                helper.getView(R.id.tv_click_btn).setVisibility(View.GONE);
                helper.getView(R.id.img_click_btn).setVisibility(View.GONE);
            } else {
                accostBtn(helper, item);
                helper.setImageResource(R.id.img_click_btn, R.mipmap.party_btn_accost);
            }
        }

        if (item.getUser_id() == userService.getUserId()) {
            helper.getView(R.id.tv_click_btn).setVisibility(View.GONE);
            helper.getView(R.id.img_click_btn).setVisibility(View.GONE);
        }
    }

    /**
     * 搭讪过 进入聊天
     */
    private void accostBtn(BaseViewHolder helper, UserInfo item) {
        if (item.getIs_accost() == 1) {
            helper.getView(R.id.tv_click_btn).setVisibility(View.VISIBLE);
            helper.getView(R.id.img_click_btn).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_click_btn).setVisibility(View.GONE);
            helper.getView(R.id.img_click_btn).setVisibility(View.VISIBLE);
        }
    }

    public interface IOnLine {
        void accost(int userId, int position);

        void chat(int userId);

        void say(int userId, int position);
    }
}
