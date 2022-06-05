package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.ARouterUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *  动态详情举报弹窗
 */
public class ReportPopWindow extends BaseCenterPop implements View.OnClickListener {


    private final int blogId;
    private final int fromType;  // 1:个人  2：动态 3：帮助反馈


    public ReportPopWindow(Context context, int blogId, int fromType) {
        super(context);
        this.blogId = blogId;
        this.fromType = fromType;
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_report);
    }

    private void initUI() {
        RecyclerView mRvReport = findViewById(R.id.rv_report);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvReport.setLayoutManager(layoutManager);
        List<String> list = new ArrayList<>();
        list.add("诈骗欺诈");
        list.add("恶意骚扰");
        list.add("色情信息");
        list.add("性别不符");
        list.add("垃圾广告");
        list.add("其他");
        ReportAdapter adapter = new ReportAdapter(list);
        mRvReport.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                int reportType = 0;
                if (list.get(position).equals("诈骗欺诈")) {
                    reportType = 0;
                } else if (list.get(position).equals("恶意骚扰")) {
                    reportType = 1;
                } else if (list.get(position).equals("色情信息")) {
                    reportType = 2;
                } else if (list.get(position).equals("性别不符")) {
                    reportType = 3;
                } else if (list.get(position).equals("垃圾广告")) {
                    reportType = 4;
                } else if (list.get(position).equals("其他")) {
                    reportType = 5;
                }
                ARouterUtils.toReportActivity(reportType, blogId, fromType);
                dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    /**
     * 举报适配器
     */
    public static class ReportAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ReportAdapter(@Nullable List<String> data) {
            super(R.layout.item_report, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            helper.setText(R.id.id_report, item);
            if (helper.getLayoutPosition() == getData().size() - 1) {
                helper.setGone(R.id.view, true);
            }
        }
    }

    public interface OnSelectListener {
        void shareType(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
