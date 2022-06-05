package com.tftechsz.im.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.api.NimUIKit;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.PullWiresDto;
import com.tftechsz.common.base.BaseListActivity;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.chat.ChatTimeUtils;

import java.util.List;

import io.reactivex.Flowable;

public class PullWiresRecordActivity extends BaseListActivity<PullWiresDto> implements View.OnClickListener {

    private ChatApiService service;
    private ImageView mIvRefresh;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        findViewById(R.id.toolbar_menu).setOnClickListener(this);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mIvRefresh = findViewById(R.id.iv_refresh);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_menu) {
            AnimationUtil.createRotateAnimation(mIvRefresh);
            refresh();
        }
    }

    @Override
    public void setData(List<PullWiresDto> datas, int page) {
        super.setData(datas, page);
        if (mIvRefresh != null) {
            mIvRefresh.postDelayed(() -> {
                mIvRefresh.clearAnimation();
            }, 600);
        }
        if (adapter == null) return;
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if (adapter.getData().get(position) != null) {
                ARouterUtils.toChatP2PActivity(String.valueOf(adapter.getData().get(position).to_user_id), NimUIKit.getCommonP2PSessionCustomization(), null);
            }
//            Intent intent = new Intent(PullWiresRecordActivity.this,FateMsgActivity.class);
//            intent.putExtra("PullWiresDto",adapter.getItem(position));
//            startActivity(intent);
        });
    }


    @Override
    public Flowable setNetObservable() {
        if (null == service)
            service = new RetrofitManager().createUserApi(ChatApiService.class);
        return service.getMsgList(4, page);
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    public int setItemLayoutRes() {
        return R.layout.item_pull_wires_message;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, PullWiresDto item, int position) {
        GlideUtils.loadRouteImage(this, helper.getView(R.id.iv_avatar), item.to_user_icon);
        helper.setText(R.id.tv_name, item.to_user_nickname)
                .setText(R.id.tv_content, item.msg_content);
        helper.setText(R.id.tv_time, ChatTimeUtils.getChatTime(item.created_at * 1000));
        helper.setVisible(R.id.view, helper.getLayoutPosition() != getDatas().size() - 1);
    }

    @Override
    public String setEmptyContent() {
        if (mIvRefresh != null) {
            mIvRefresh.postDelayed(() -> {
                mIvRefresh.clearAnimation();
            }, 600);
        }
        return "";
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_pull_wires_record;
    }


}
