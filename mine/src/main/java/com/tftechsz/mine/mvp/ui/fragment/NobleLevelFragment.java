package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.NoblePrivilegeItemAdapter;
import com.tftechsz.mine.databinding.FraNobleLevelBinding;
import com.tftechsz.mine.entity.NobleItemClickEvent;
import com.tftechsz.mine.entity.dto.NoblePrivilegeDto;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.fragment
 * 描 述 : 贵族等级fragment
 */
public class NobleLevelFragment extends BaseMvpFragment implements OnItemClickListener {

    private ArrayList<NoblePrivilegeDto> mAllData;

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    public int getBindLayout() {
        return R.layout.fra_noble_level;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        com.tftechsz.mine.databinding.FraNobleLevelBinding bind = (FraNobleLevelBinding) getBind();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mAllData = (ArrayList<NoblePrivilegeDto>) bundle.getSerializable(Interfaces.EXTRA_ALL_DATA);
        }

        bind.recy.setLayoutManager(new GridLayoutManager(mContext, 3));
        NoblePrivilegeItemAdapter adapter = new NoblePrivilegeItemAdapter(mAllData);
        bind.recy.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        RxBus.getDefault().post(new NobleItemClickEvent(position));
    }
}
