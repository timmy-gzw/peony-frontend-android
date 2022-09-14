package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PropertiesPopWindow extends BaseBottomPop {

    public PropertiesPopWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    private void initUI() {
        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_all_properties).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_properties_record).setOnClickListener(v -> dismiss());
    }


    public interface OnClicksListener {
        void click(int pos, String str);
    }

    public OnClicksListener listener;

    public void addOnClickListener(OnClicksListener listener) {
        this.listener = listener;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_properties);
    }

}
