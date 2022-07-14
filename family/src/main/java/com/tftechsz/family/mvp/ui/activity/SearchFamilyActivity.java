package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.family.R;
import com.tftechsz.family.mvp.IView.ISearchFamilyView;
import com.tftechsz.family.mvp.presenter.SearchFamilyPresenter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.utils.Utils;

import razerdp.util.KeyboardUtils;

/**
 * 搜索家族
 */
public class SearchFamilyActivity extends BaseMvpActivity<ISearchFamilyView, SearchFamilyPresenter> implements View.OnClickListener, ISearchFamilyView {

    private EditText mEtSearch;
    private String mKeyword;

    @Override
    public SearchFamilyPresenter initPresenter() {
        return new SearchFamilyPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search_family;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).fitsSystemWindows(true).keyboardEnable(true).init();
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        findViewById(R.id.toolbar_tv_menu).setOnClickListener(this);
        mEtSearch = findViewById(R.id.et_search);
        Utils.setFocus(mEtSearch);
        KeyboardUtils.open(mEtSearch, 200);
    }

    @Override
    protected void initData() {
        super.initData();
        mEtSearch.setOnKeyListener((v, keyCode, event) -> {        // 开始搜索
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                KeyboardUtils.close(this);
                //搜索逻辑
                mPage = 1;
                mKeyword = mEtSearch.getText().toString();
                p.searchFamily(mKeyword);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finishAfterTransition();
        } else if (id == R.id.toolbar_tv_menu) {
            mKeyword = Utils.getText(mEtSearch);
            if (!TextUtils.isEmpty(mKeyword)) {
                p.searchFamily(mKeyword);
            }
        }
    }


    @Override
    public void getSearchSuccess(FamilyIdDto data) {
    }
}
