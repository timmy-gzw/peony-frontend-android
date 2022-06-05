package com.tftechsz.family.widget.pop;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.family.R;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.List;

/**
 * 管理商务合作弹窗
 */
public class FamilyCooperationPopWindow extends BaseCenterPop implements View.OnClickListener {
    private final Activity mContext;
    private List<ConfigInfo.FeedbackContactNew> mContact;

    public FamilyCooperationPopWindow(Activity context) {
        super(context);
        mContext = context;
        setPopupFadeEnable(true);
        setPopupGravity(Gravity.CENTER);
    }

    public void initUI(List<ConfigInfo.FeedbackContactNew> contact) {
        mContact = contact;
        RecyclerView mRvFamilyManager = findViewById(R.id.rv_family_cooper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvFamilyManager.setLayoutManager(layoutManager);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        FamilyManagerAdapter adapter = new FamilyManagerAdapter(mContact);
        mRvFamilyManager.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", adapter.getItem(position).msg_copy);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            ToastUtil.showToast(mContext, "复制成功");
        });
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_opper);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        }
    }


    static class FamilyManagerAdapter extends BaseQuickAdapter<ConfigInfo.FeedbackContactNew, BaseViewHolder> {

        public FamilyManagerAdapter(@Nullable List<ConfigInfo.FeedbackContactNew> data) {
            super(R.layout.item_family_opper, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ConfigInfo.FeedbackContactNew item) {
            helper.setText(R.id.tv_content, item.msg);
        }
    }

}
