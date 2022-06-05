package com.tftechsz.family.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.CreateFamilyFunctionDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 包 名 : com.tftechsz.family.adapter
 * 描 述 : TODO
 */
public class CreateFamilyFunctionAdapter extends BaseQuickAdapter<CreateFamilyFunctionDto, BaseViewHolder> {
    public CreateFamilyFunctionAdapter(List<CreateFamilyFunctionDto> data) {
        super(R.layout.item_create_family_function, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CreateFamilyFunctionDto item) {
        ConstraintLayout root = helper.getView(R.id.root);
        ImageView img = helper.getView(R.id.img);
        TextView tv = helper.getView(R.id.tv);

        img.setImageResource(item.imgRes);
        tv.setText(item.name);

        int end = ConvertUtils.dp2px(0.5f), bot = ConvertUtils.dp2px(0.5f);
        if (helper.getLayoutPosition() + 1 % 3 == 0) {
            end = 0;
        }
        if (getItemCount() - (helper.getLayoutPosition() + 1) < 3) {
            bot = 0;
        }
        root.setPadding(0, 0, end, bot);
    }
}
