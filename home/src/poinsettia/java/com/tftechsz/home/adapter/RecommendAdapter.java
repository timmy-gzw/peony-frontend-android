package com.tftechsz.home.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.VipUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.home.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 推荐适配器
 */
public class RecommendAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    private final int mType;
    private final UserProviderService service;
    private int isShow;
    private boolean isGa = false;//是否审核环境


    public RecommendAdapter(int type) {
        super(R.layout.item_recommend);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mType = type;
    }

    public RecommendAdapter(int type, boolean isGa) {
        super(isGa ? R.layout.item_recommend_ga : R.layout.item_recommend);
        this.isGa = isGa;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mType = type;
    }

    public void setShow(int isShow) {
        this.isShow = isShow;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, UserInfo item, @NotNull List<?> payloads) {
        /*if (payloads.isEmpty()) {
            convert(holder, item);
        } else*/
        setData(holder, item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo item) {
        setData(helper, item);
    }

    public void setData(BaseViewHolder helper, UserInfo item) {
        TextView tvName = helper.getView(R.id.tv_name);
        ImageView ivAvatar = helper.getView(R.id.iv_avatar);
        String name = StringUtils.handleText(item.getNickname(), Constants.MAX_NAME_LENGTH);

        helper.setGone(R.id.iv_real_people, item.getIs_real() != 1);  //是否真人
        if (isGa) {
            RecyclerView recyclerView = helper.getView(R.id.rv);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            List<String> dates;
            if(item.tags.contains("|")) {
                dates = Arrays.asList(item.tags.trim().split("\\|"));
            }else {
                dates = new ArrayList<>();
                dates.add(item.tags.trim());
            }
            recyclerView.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_home_tag, dates) {
                @Override
                protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
                    baseViewHolder.setText(R.id.tv,s);
                }
            });
            CommonUtil.setUserName(tvName, name, false);
            if (item.isVip()) {
                tvName.setTextColor(Utils.getColor(com.tftechsz.common.R.color.vip_color));
            } else {
                tvName.setTextColor(Utils.getColor(com.tftechsz.common.R.color.white));
            }
            helper.setGone(R.id.iv_vip,item.isVip());
            helper.setVisible(R.id.iv_online, item.getIs_online() == 1); //是否在线  1:在线
            GlideUtils.loadRoundImage(getContext(), ivAvatar, item.getIcon(), 16);
            helper.setImageResource(R.id.ll_accost, item.isAccost() ? R.mipmap.ic_home_ga_p2p : R.mipmap.ic_home_ga_accost);//私聊/搭讪
        } else {
            helper.setText(R.id.tv_info, item.tags);
            CommonUtil.setUserName(tvName, name, item.isVip());
            View bgFrame = helper.getView(R.id.bg_frame);

            if (mType == 1) {   //推荐
                helper.setVisible(R.id.iv_online, item.getIs_online() == 1); //是否在线  1:在线
                helper.setVisible(R.id.tv_distance, false);
            } else {   //附近
                helper.setVisible(R.id.iv_online, false);
                helper.setVisible(R.id.tv_distance, !TextUtils.isEmpty(item.tag_distance));
                helper.setText(R.id.tv_distance, item.tag_distance);
            }
            RecyclerView recycleView = helper.getView(R.id.recycleview);
            if (item.getPicture() != null && item.getPicture().size() > 0) { //有相册
                recycleView.setVisibility(View.VISIBLE);
                recycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                HomePictureAdapter adpater = new HomePictureAdapter(getContext());
                recycleView.setAdapter(adpater);
                adpater.setList(item.getPicture());
                adpater.setOnItemClickListener((adapter, view, position) -> {//点击图册
                    if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
                    if (!NetworkUtils.isConnected()) {
                        Utils.toast("暂无网络，请连接网络后重试！");
                        return;
                    }
                    ARouterUtils.toUserPicBrowserActivity(item.getUser_id(), position, null, item.isBoy());
                });
            } else {
                recycleView.setVisibility(View.GONE);
            }
            //bgFrame.setBackgroundResource(VipUtils.getPictureFrameBackground(item.picture_frame, true, false));
            VipUtils.setPersonalise(bgFrame, item.picture_frame, true, true);
            helper.setText(R.id.tv_sign, item.getDesc());

            GlideUtils.loadRoundImage(getContext(), ivAvatar, item.getIcon(), 21);
//        GlideUtils.loadUserIcon(ivAvatar, item.getIcon());
//        helper.setGone(R.id.iv_auth, item.getIs_self() != 1);  //是否实名
            helper.setText(R.id.tv_accost, item.isAccost() ? getContext().getString(R.string.private_chat) : getContext().getString(R.string.accost));//私聊/搭讪
            helper.setTextColor(R.id.tv_accost, item.isAccost() ? ContextCompat.getColor(getContext(), R.color.c_btn_p2p) : ContextCompat.getColor(getContext(), R.color.c_btn_accost));//私聊/搭讪
            helper.setBackgroundResource(R.id.ll_accost, item.isAccost() ? R.drawable.bg_p2p_btn : R.drawable.bg_accost_btn);//私聊/搭讪
            if (isShow == 1) {   //只显示私信   图片显示认证图片
                helper.setImageResource(R.id.iv_real_people, R.mipmap.ic_attestation);
            } else {
                helper.setImageResource(R.id.iv_real_people, R.mipmap.home_icon_real_people);
            }
        }
    }
}
