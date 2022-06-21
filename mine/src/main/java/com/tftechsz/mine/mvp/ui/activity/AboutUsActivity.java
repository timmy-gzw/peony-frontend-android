package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.entity.UpdateInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.UpdateDialog;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AboutAdapter;
import com.tftechsz.mine.mvp.IView.IAboutUsView;
import com.tftechsz.mine.mvp.presenter.AboutUsPresenter;

import java.util.List;


public class AboutUsActivity extends BaseMvpActivity<IAboutUsView, AboutUsPresenter> implements View.OnClickListener, IAboutUsView {

    @Autowired
    UserProviderService service;
    private TextView mTvVersion;
    private RecyclerView mRvAbout;
    private UpdateDialog dialog;
    private LinearLayout mBotLink;

    @Override
    public AboutUsPresenter initPresenter() {
        return new AboutUsPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("关于" + getString(R.string.app_name))
                .build();
        mTvVersion = findViewById(R.id.tv_version);
        mRvAbout = findViewById(R.id.rv_about);
        mBotLink = findViewById(R.id.bot_link);
        mRvAbout.setLayoutManager(new LinearLayoutManager(this));
        TextView tvAbout = findViewById(R.id.tv_about);
        tvAbout.setText(String.format("Copyright ©2021 %s\nAll Rights Reserved", getString(R.string.app_name)));
        initListener();

    }

    private void initListener() {


    }


    @Override
    protected int getLayout() {
        return R.layout.activity_about_us;
    }


    @Override
    protected void initData() {
        super.initData();
        mTvVersion.setText(getString(R.string.app_name) + ":" + AppUtils.getVersionName(this));
        ConfigInfo configInfo = service.getConfigInfo();
        if (null != configInfo) {
            AboutAdapter adapter = new AboutAdapter(configInfo.api.about_bot);
            mRvAbout.setAdapter(adapter);
            adapter.setOnItemClickListener((ad, view, position) -> {
                ConfigInfo.MineInfo mineInfo = configInfo.api.about_bot.get(position);
                if (null != mineInfo) {
                    String down = "peony://update_app";
                    if (mineInfo.link.contains(down)) {
                        p.getUpdateCheck();
                    } else {
                        CommonUtil.performLink(mActivity, mineInfo, position, 0);
                    }
                }
            });

            mBotLink.removeAllViews();
            if (configInfo.api != null && configInfo.api.about_bot != null && configInfo.api.about_bot.size() > 0) {
                List<ConfigInfo.MineInfo> about_bot = configInfo.api.about_bot;
                for (int i = 0, j = about_bot.size(); i < j; i++) {
                    ConfigInfo.MineInfo mineInfo = about_bot.get(i);
                    TextView textView = new TextView(mContext);
//                    textView.setTextColor(Color.parseColor("#797979"));
                    textView.setTextSize(12);
                    textView.setPadding(ConvertUtils.dp2px(6), ConvertUtils.dp2px(3), ConvertUtils.dp2px(6), ConvertUtils.dp2px(3));
                    if (!TextUtils.isEmpty(mineInfo.info))
                        textView.setText(Html.fromHtml(mineInfo.info));
//                    textView.setText(Html.fromHtml("<font color=\"#797979\">《<u>王者荣耀</u>》</font>"));
                    int finalI = i;
                    textView.setOnClickListener(v -> CommonUtil.performLink(mActivity, mineInfo, finalI, 0));
                    mBotLink.addView(textView);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_exit) {  //退出登录
        } else if (id == R.id.item_charge_setting) {   //收费设置
            startActivity(ChargeSettingActivity.class);
        } else if (id == R.id.item_black_list) {   // 黑名单
            startActivity(BlackListActivity.class);
        }
    }

    @Override
    public void getUpdateCheckSuccess(ConfigInfo.MineInfo data) {
//        UpdateManager.getInstance().downLoadApp(data.link,null);
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.is_force = data.is_force;
        updateInfo.info = data.info;
        updateInfo.link_type = data.link_type;
        updateInfo.version = data.version;
        updateInfo.link = data.link;
        showDialog(updateInfo);
    }

    /**
     * 显示更新弹窗
     */
    private void showDialog(final UpdateInfo updateInfo) {
        if (null == dialog)
            dialog = new UpdateDialog(AboutUsActivity.this, updateInfo);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
    }


}
