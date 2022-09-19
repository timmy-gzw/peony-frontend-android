package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.LabelAdapter;
import com.tftechsz.mine.entity.dto.LabelDto;
import com.tftechsz.mine.mvp.IView.ILabelView;
import com.tftechsz.mine.mvp.presenter.LabelPresenter;

import java.util.List;

@Route(path = ARouterApi.ACTIVITY_CHOOSE_TAG)
public class LabelActivity extends BaseMvpActivity<ILabelView, LabelPresenter> implements ILabelView, View.OnClickListener {
    private RecyclerView mRvLabel;
    private TextView mTvSave, mTvChooseNum, mTvJump;
    private LabelAdapter mAdapter;
    private String mFrom;
    private final StringBuilder tagIdSB = new StringBuilder();
    private View ivBack;

    @Override
    protected int getLayout() {
        return R.layout.activity_label;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvLabel = findViewById(R.id.rv_label);
        mRvLabel.setLayoutManager(new LinearLayoutManager(this));
        mRvLabel.setItemViewCacheSize(20);
        ivBack = findViewById(R.id.toolbar_back_all);
        ivBack.setOnClickListener(this);
        mTvSave = findViewById(R.id.tv_save);
        mTvChooseNum = findViewById(R.id.toolbar_choose_title);
        mTvSave.setOnClickListener(this);
        mTvJump = findViewById(R.id.toolbar_tv_menu);
        mTvJump.setOnClickListener(this);
        getP().getTag();
    }


    @Override
    protected void initData() {
        super.initData();
        mFrom = getIntent().getStringExtra("from");
        if (TextUtils.equals(mFrom, "info")) {
            mTvJump.setVisibility(View.GONE);
        } else if (TextUtils.equals(mFrom, "init")) {
            ivBack.setVisibility(View.GONE);
            mTvSave.setText("立即交友");
        }
        mAdapter = new LabelAdapter();
        mRvLabel.setAdapter(mAdapter);
        mAdapter.addOnSelectListener(() -> {
            tagIdSB.setLength(0);
            selectedCount();
        });
    }

    private void selectedCount() {
        int number = 0;
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            for (int j = 0; j < mAdapter.getData().get(i).list.size(); j++) {
                if (mAdapter.getData().get(i).list.get(j).is_select == 1) {
                    if (tagIdSB.length() > 0) {
                        tagIdSB.append(",");
                    }
                    tagIdSB.append(mAdapter.getData().get(i).list.get(j).id);
                    number += 1;
                }
            }
        }
        mTvChooseNum.setText("(已选 " + number + "/15)");
    }

    @Override
    public LabelPresenter initPresenter() {
        return new LabelPresenter();
    }

    @Override
    public void getTagSuccess(List<LabelDto> data) {
        mAdapter.setList(data);
        selectedCount();
    }

    @Override
    public void setTagSuccess(Boolean data) {
        toastTip("保存成功");
        finishTag();
    }

    private void finishTag() {
        if (TextUtils.equals(mFrom, "init")) {
            ARouterUtils.toPathWithId(ARouterApi.MAIN_MAIN);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.tv_save) {
            if (tagIdSB.length() > 0) {
                getP().setTag(tagIdSB.toString());
            } else {
                finishTag();
            }
        } else if (id == R.id.toolbar_tv_menu) {
            finishTag();
        }
    }
}
