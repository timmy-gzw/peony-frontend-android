package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.LabelAdapter;
import com.tftechsz.mine.entity.dto.LabelDto;
import com.tftechsz.mine.mvp.IView.ILabelView;
import com.tftechsz.mine.mvp.presenter.LabelPresenter;

import java.util.List;

public class LabelActivity extends BaseMvpActivity<ILabelView, LabelPresenter> implements ILabelView, View.OnClickListener {
    private RecyclerView mRvLabel;
    private TextView mTvSave, mTvChooseNum, mTvJump;
    private LabelAdapter mAdapter;
    private String mFrom;

    @Override
    protected int getLayout() {
        return R.layout.activity_label;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvLabel = findViewById(R.id.rv_label);
        mRvLabel.setLayoutManager(new LinearLayoutManager(this));
        mRvLabel.setItemViewCacheSize(20);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
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
        } else {
            mTvSave.setText("立即交友");
        }
        mAdapter = new LabelAdapter();
        mRvLabel.setAdapter(mAdapter);
        mAdapter.addOnSelectListener(() -> {
            StringBuilder stringBuffer = new StringBuilder();
            int number = 0;
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                for (int j = 0; j < mAdapter.getData().get(i).list.size(); j++) {
                    if (mAdapter.getData().get(i).list.get(j).is_select == 1) {
                        stringBuffer.append(mAdapter.getData().get(i).list.get(j).id);
                        stringBuffer.append(",");
                        number += 1;
                    }
                }
            }
            mTvChooseNum.setText("(已选 " + number + "/15)");
        });
    }

    @Override
    public LabelPresenter initPresenter() {
        return new LabelPresenter();
    }

    @Override
    public void getTagSuccess(List<LabelDto> data) {
        mAdapter.setList(data);
    }

    @Override
    public void setTagSuccess(Boolean data) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.tv_save) {

        } else if (id == R.id.toolbar_tv_menu) {

        }
    }
}
