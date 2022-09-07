package com.tftechsz.mine.mvp.ui.activity;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.mine.R;

@Route(path = ARouterApi.ACTIVITY_YOUTH_MODEL)
public class YouthModelActivity extends BaseMvpActivity implements View.OnClickListener {

    private TextView mTvYouthModel;

    @Override
    protected int getLayout() {
        return R.layout.activty_youth_model;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        TextView tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("青少年模式");
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mTvYouthModel = findViewById(R.id.tv_youth_model);
        mTvYouthModel.setOnClickListener(this);
        String pass = MMKVUtils.getInstance().decodeString(Constants.YOUTH_MODE_PASS);
        mTvYouthModel.setText(!TextUtils.isEmpty(pass) ? "关闭青少年模式" : "开启青少年模式");
        TextView tvYouthModel = findViewById(R.id.tv_youth_model_content);
        TextView tvYouthModel1 = findViewById(R.id.tv_youth_model_content1);
        tvYouthModel.setText(String.format(getString(R.string.youtu_mode_content), getString(R.string.app_name),getString(R.string.app_name)));
        tvYouthModel1.setText(String.format(getString(R.string.youtu_mode_content1), getString(R.string.app_name)));
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_youth_model) {
            Intent intent = new Intent(YouthModelActivity.this, YouthModelPassActivity.class);
            startActivityForResult(intent, 1000);
        } else if (view.getId() == R.id.toolbar_back_all) {
            back();
        }
    }

    //双击退出app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void back(){
        String pass = MMKVUtils.getInstance().decodeString(Constants.YOUTH_MODE_PASS);
        if (!TextUtils.isEmpty(pass))
            AppManager.getAppManager().finishAllActivity();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            mTvYouthModel.setText("关闭青少年模式");
        }
    }
}
