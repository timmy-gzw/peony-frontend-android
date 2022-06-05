package com.tftechsz.mine.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.NobleItemClickEvent;
import com.tftechsz.mine.entity.dto.NoblePrivilegeDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : 特权
 */
public class NoblePrivilegeAdapter extends BaseQuickAdapter<List<NoblePrivilegeDto>, BaseViewHolder> {
    public NoblePrivilegeAdapter() {
        super(R.layout.item_noble);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, List<NoblePrivilegeDto> bean) {
        RecyclerView recy = helper.getView(R.id.recy);
        recy.setLayoutManager(new GridLayoutManager(getContext(), 3));
        NoblePrivilegeItemAdapter adapter = new NoblePrivilegeItemAdapter(bean);
        recy.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                RxBus.getDefault().post(new NobleItemClickEvent(position));
            }
        });

    }
}
