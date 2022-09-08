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

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 动态-评论回复pop
 */
public class CommentPopWindow extends BaseBottomPop {
    private final Context mContext;
    private int userId, blogUserId;

    UserProviderService service;
    private CommentAdapter mAdapter;

    public CommentPopWindow(Context context) {
        super(context);
        mContext = context;
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    private void initUI() {
        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        RecyclerView rv = findViewById(R.id.rv_comment);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        List<String> list = new ArrayList<>();
//        list.add("回复");
        list.add("复制");
        if (blogUserId == service.getUserId() || userId == service.getUserId()) {
            list.add("删除");
        } else {
            list.add("举报");
        }

        if (mAdapter == null) {
            mAdapter = new CommentAdapter(list);
            rv.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (listener != null) {
                    listener.click(position, mAdapter.getItem(position));
                }
                dismiss();
            });
        } else {
            mAdapter.setList(list);
        }
    }

    public void putInfo(int blogUserId, int userId) {
        this.blogUserId = blogUserId;
        this.userId = userId;
        initUI();
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
        return createPopupById(R.layout.pop_comment);
    }

    public static class CommentAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public CommentAdapter(@Nullable List<String> data) {
            super(R.layout.item_comment_pop, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            helper.setText(R.id.id_report, item);
            if (helper.getLayoutPosition() == getData().size() - 1) {
                helper.setGone(R.id.view, true);
            } else {
                helper.setVisible(R.id.view, true);
            }
            if ("删除".equals(item)) {
                helper.setTextColor(R.id.id_report, Utils.getColor(R.color.colorPrimary));
            } else {
                helper.setTextColor(R.id.id_report, Utils.getColor(R.color.color_normal));
            }
        }
    }
}
