package com.tftechsz.family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.entity.dto.FamilyMemberGroupDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.sticky.BaseViewHolder;
import com.tftechsz.common.widget.sticky.GroupedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组成员
 */
public class FamilyMemberGroupAdapter extends GroupedRecyclerViewAdapter {

    private final List<FamilyMemberGroupDto> mGroups;
    private final UserProviderService service;
    private int mFamilyId;
    private int mFrom;
    private int orderType;

    public FamilyMemberGroupAdapter(Context context, List<FamilyMemberGroupDto> groups, int familyId, int from) {
        super(context);
        mGroups = groups;
        mFamilyId = familyId;
        mFrom = from;
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<FamilyMemberDto> children = mGroups.get(groupPosition).getData();
        return children == null ? 0 : children.size();
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        if (TextUtils.isEmpty(mGroups.get(groupPosition).getTitle()))
            return false;
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.item_family_member_group;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_family_member_child;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        FamilyMemberGroupDto entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_content, entity.getTitle());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder helper, int groupPosition, int childPosition) {
        FamilyMemberDto item = mGroups.get(groupPosition).getData().get(childPosition);
        CheckBox checkbox = helper.get(R.id.checkbox);
        TextView tvType = helper.get(R.id.tv_type);
        ImageView ivAvatar = helper.get(R.id.iv_avatar);
        View view = helper.get(R.id.view);
        GlideUtils.loadRoundImage(mContext, ivAvatar, item.icon, 100);
        helper.setText(R.id.tv_name, item.nickname);
        CommonUtil.setSexAndAge(mContext, item.sex, item.age, helper.get(R.id.tv_sex));
        String offer = item.offer + "日贡献";
        if (orderType == 1) {
            offer = item.offer + "日贡献";
        } else if (orderType == 2) {
            offer = item.offer + "周贡献";
        } else if (orderType == 3) {
            offer = item.offer + "总贡献";
        }
        helper.setText(R.id.iv_today_contribution, offer);  //贡献值
        helper.setText(R.id.tv_active_time, item.active_time);
        helper.setVisible(R.id.tv_active_time, !TextUtils.isEmpty(item.active_time));
        view.setVisibility(childPosition == mGroups.get(groupPosition).getData().size() - 1 ? View.INVISIBLE : View.VISIBLE);
        helper.setVisible(R.id.tv_type_name, service.getUserId() == item.user_id);
        UIUtils.setFamilyTitle(tvType, item.role_id);
        if (selectedMode) { //如果是编辑模式
            helper.setVisible(R.id.iv_arrow, false);
            helper.setVisible(R.id.tv_change, false);
            checkbox.setVisibility(service.getUserId() != item.user_id ? View.VISIBLE : View.GONE);
            checkbox.setChecked(item.isSelected == 1);

        } else {
            checkbox.setVisibility(View.GONE);
            if (mFrom == 3) {
                helper.setVisible(R.id.iv_arrow, false);
                helper.setVisible(R.id.tv_change, service.getUserId() != item.user_id);
            } else {
                helper.setVisible(R.id.iv_arrow, service.getUserId() != item.user_id);
                helper.setVisible(R.id.tv_change, false);
            }
        }
        if (service.getUserInfo() != null && service.getUserInfo().family_id != mFamilyId) {
            helper.setVisible(R.id.iv_arrow, false);
            helper.setVisible(R.id.iv_today_contribution, false);
        }
        helper.get(R.id.iv_arrow).setOnClickListener(v -> {
            if (listener != null) {
                listener.clickMore(groupPosition, childPosition, item.user_id, item.nickname, item.role_id);
            }
        });
        helper.get(R.id.tv_change).setOnClickListener(v -> {
            if (listener != null) {
                listener.changeMaster(groupPosition, childPosition, item.user_id);
            }
        });
    }

    boolean selectedMode;

    public void setSelectedMode(boolean b) {
        selectedMode = b;
        notifyDataChanged();
    }

    public interface OnSelectListener {
        void clickMore(int groupPosition, int childPosition, int userId, String nickname, int roleId);

        void changeMaster(int groupPosition, int childPosition, int userId);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
