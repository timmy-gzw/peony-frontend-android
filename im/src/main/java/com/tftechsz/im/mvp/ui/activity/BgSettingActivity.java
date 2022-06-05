package com.tftechsz.im.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.BgSetAdapter;
import com.tftechsz.im.databinding.ActBgSettingBinding;
import com.tftechsz.im.model.BgSetBean;
import com.tftechsz.im.model.event.BgSetEvent;
import com.tftechsz.im.mvp.iview.IBgSetView;
import com.tftechsz.im.mvp.presenter.BgSetPresenter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * 包 名 : com.tftechsz.im.mvp.ui.activity
 * 描 述 : 个性化设置
 */
public class BgSettingActivity extends BaseMvpActivity<IBgSetView, BgSetPresenter> implements IBgSetView {

    private ActBgSettingBinding mBind;
    private BgSetAdapter mAdapter;
    private String mUid;
    @Autowired
    UserProviderService service;

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_bg_setting);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mUid = getIntent().getStringExtra(Interfaces.EXTRA_UID);
        new ToolBarBuilder().setTitle("个性化设置").showBack(true).build();
        mBind.recy.setLayoutManager(new GridLayoutManager(mContext, 2));
        mAdapter = new BgSetAdapter();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            BgSetBean item = mAdapter.getItem(position);
            if (item.used != 1) {
                p.bgSet(mUid, item.id, item.bg, 1);
            }
        });
        View view = View.inflate(mContext, R.layout.base_empty_view, null);
        ImageView ivEmpty = view.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.mipmap.peony_zdydzf_kzt_png);
        TextView tvEmpty = view.findViewById(R.id.tv_empty);
        tvEmpty.setText("您跟TA还没有个性化装扮哦～");
        tvEmpty.setPadding(0, ConvertUtils.dp2px(10), 0, 0);
        tvEmpty.setTextColor(Utils.getColor(R.color.color_cccccc));
        mAdapter.setEmptyView(view);
        mBind.recy.setAdapter(mAdapter);
        mAdapter.setList(null);
        p.getBgConfig(mUid);

    }

    @Override
    public BgSetPresenter initPresenter() {
        return new BgSetPresenter();
    }

    @Override
    public void getBgConfigSuccess(List<BgSetBean> data) {
        if (data != null && data.size() > 0) {
            mAdapter.setList(data);
        }
    }

    @Override
    public void setBgSuccess(String bg) {
        String s = MMKVUtils.getInstance().decodeString(Interfaces.SP_CHAT_BG + service.getUserInfo().getUser_id());
        JSONObject json;
        if (!TextUtils.isEmpty(s)) {
            json = JSONObject.parseObject(s);
        } else {
            json = new JSONObject();
        }
        json.put(mUid, TextUtils.isEmpty(bg) ? "" : bg);
        Utils.logE("uid=" + mUid + "  bg=" + bg);
        MMKVUtils.getInstance().encode(Interfaces.SP_CHAT_BG + service.getUserInfo().getUser_id(), JSONObject.toJSONString(json));
        RxBus.getDefault().post(new BgSetEvent(bg));
        p.getBgConfig(mUid);
        Utils.toast("背景设置成功");
    }
}
