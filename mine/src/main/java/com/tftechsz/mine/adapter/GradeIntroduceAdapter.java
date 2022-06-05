package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GradeIntroduceInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 等级
 */
public class GradeIntroduceAdapter extends BaseQuickAdapter<GradeIntroduceInfo, BaseViewHolder> {
    public GradeIntroduceAdapter(@Nullable List<GradeIntroduceInfo> data) {
        super(R.layout.item_grade_introduce, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GradeIntroduceInfo item) {
        helper.setImageResource(R.id.iv_image,item.bg);
        helper.setText(R.id.tv_name,item.title);

    }
}
