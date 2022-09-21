package com.tftechsz.mine.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LabelDto;
import com.tftechsz.mine.entity.dto.LabelInfoDto;

import org.jetbrains.annotations.NotNull;


public class LabelAdapter extends BaseQuickAdapter<LabelDto, BaseViewHolder> {

    public LabelAdapter() {
        super(R.layout.item_label);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LabelDto labelDto) {
        baseViewHolder.setText(R.id.tv_title, labelDto.title);
        RecyclerView rvLabel = baseViewHolder.findView(R.id.rv_label);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext(), FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        if (rvLabel != null) {
            rvLabel.setLayoutManager(layoutManager);
            LabelContentAdapter adapter = new LabelContentAdapter();
            rvLabel.setAdapter(adapter);
            adapter.setList(labelDto.list);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                int number = 0;
                int size = getData().size();
                for (int i = 0; i < size; i++) {
                    int size2 = getData().get(i).list.size();
                    for (int j = 0; j < size2; j++) {
                        if (getData().get(i).list.get(j).is_select == 1) {
                            number += 1;
                        }
                    }
                }
                LabelInfoDto item = adapter.getData().get(position);
                if (item.is_select == 1) {
                    item.setIs_select(0);
                    adapter.setData(position, item);
                } else {
                    if (number >= 15) {
                        ToastUtil.showToast(getContext(),"最多可选择15个标签哦~");
                        return;
                    } else {
                        item.setIs_select(1);
                        adapter.setData(position, item);
                    }
                }
                notifyDataSetChanged();
                if (listener != null)
                    listener.select();
            });
        }
    }

    public interface OnSelectListener {
        void select();
    }

    public OnSelectListener listener;

    public void addOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
