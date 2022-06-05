package com.tftechsz.family.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.family.BR;
import com.tftechsz.family.R;
import com.tftechsz.family.databinding.ItemUserShareListBinding;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.entity.UserShareDto;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */

public class UserShareListAdapter extends BaseQuickAdapter<UserShareDto.ListDTO, DataBindBaseViewHolder> {
    private int limit;

    public UserShareListAdapter() {
        super(R.layout.item_user_share_list);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, UserShareDto.ListDTO bean) {
        ItemUserShareListBinding bind = (ItemUserShareListBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();

        bind.setIsEnable(bean.intimacy_value >= limit); //只能邀请亲密度大于3℃的用户
        CommonUtil.setSexAndAge(getContext(), bean.sex, bean.age, bind.tvSex);
        GlideUtils.loadRoundImage(getContext(), bind.ivAvatar, bean.icon,100);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
