package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.FamilyBoxDescAdapter;
import com.tftechsz.im.databinding.PopFamilyBoxDescBinding;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 包 名 : com.tftechsz.im.widget.pop
 * 描 述 : 家族宝箱活动说明
 */
public class FamilyBoxDescPop extends BaseCenterPop {

    private PopFamilyBoxDescBinding mBind;

    public FamilyBoxDescPop(Context context, String desc) {
        super(context);
        initUI(desc);
    }

    private void initUI(String desc) {
        mBind.btn.setOnClickListener(v -> dismiss());
        List<String> strings = JSON.parseArray(desc, String.class);
        mBind.recy.setLayoutManager(new LinearLayoutManager(getContext()));
        mBind.recy.setAdapter(new FamilyBoxDescAdapter(strings));
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_family_box_desc);
        mBind = DataBindingUtil.bind(view);
        return view;
    }
}
