package com.tftechsz.mine.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.NobleBean;
import com.tftechsz.mine.entity.dto.NoblePrivilegeDto;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : 会员特权适配器 item
 */
public class NoblePrivilegeItemAdapter extends BaseQuickAdapter<NoblePrivilegeDto, BaseViewHolder> {

    public NoblePrivilegeItemAdapter(List<NoblePrivilegeDto> dto) {
        super(R.layout.item_noble_privilege, dto);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NoblePrivilegeDto bean) {
        NobleBean.PrivilegeDTO privilegeDTO = bean.privilege.get(helper.getLayoutPosition());
        ImageView icon = helper.getView(R.id.icon);
        TextView name = helper.getView(R.id.name);
        TextView tips = helper.getView(R.id.tips);
        if (bean.gradeDTO_privilege.contains(privilegeDTO.id)) {
            GlideUtils.loadRouteImage(getContext(), icon, String.format(bean.privilege_icon, privilegeDTO.id, "unlock"));
        } else {
            GlideUtils.loadRouteImage(getContext(), icon, String.format(bean.privilege_icon, privilegeDTO.id, "lock"));
        }
        name.setText(privilegeDTO.name);
        if (privilegeDTO.is_heat == 1) {
            tips.setText(String.format(privilegeDTO.tips, bean.heat));
        } else {
            tips.setText(privilegeDTO.tips);
        }

    }
}
