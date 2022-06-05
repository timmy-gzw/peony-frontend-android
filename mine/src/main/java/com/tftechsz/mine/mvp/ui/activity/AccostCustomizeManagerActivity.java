package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AccostCustomizeAdapter;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.dto.AccostCustomizeEvent;
import com.tftechsz.mine.entity.req.DelAccostSettingBean;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 自定义打招呼管理
 */
public class AccostCustomizeManagerActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView, View.OnClickListener {
    private RecyclerView mRecy;
    private TextView mDel;
    private AccostCustomizeAdapter mAdapter;

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_customize_manager;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("管理自定义招呼")
                .setRightText("完成", v -> Utils.finishAfterTransition(mActivity))
                .build();
        mRecy = findViewById(R.id.recy);
        mDel = findViewById(R.id.del);
    }

    @Override
    protected void initData() {
        mRecy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccostCustomizeAdapter(true);
        mRecy.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.editor);
        mDel.setOnClickListener(this);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            if (id == R.id.editor) {
                Intent intent = new Intent(mContext, AccostCustomizeEditActivity.class);
                intent.putExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_TYPE, 0);
                AccostSettingListBean item = mAdapter.getItem(position);
                if (item != null) {
                    intent.putExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_ID, item.id);
                    intent.putExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_CONTENT, item.text);
                }
                startActivity(intent);
            }
        });
        initBus();
        p.getAccostSettingList(AccostType.TEXT);
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(AccostCustomizeEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            switch (event.type) {
                                case 1: //更新
                                    p.getAccostSettingList(AccostType.TEXT);
                                    break;

                                case 2: //删除
                                    if (mAdapter.getCheckedIds().size() < mAdapter.getItemCount()) {
                                        p.getAccostSettingList(AccostType.TEXT);
                                    }
                                    break;
                            }
                        }
                ));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.del) {
            if (mAdapter.getCheckedIds().size() > 0) {
                p.delAccostSetting(0,new DelAccostSettingBean(mAdapter.getCheckedIds()));
            } else {
                toastTip("还未选择");
            }
        }
    }

    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {
        mAdapter.refresh(data);
    }

    @Override
    public void addAccostSettingSuccess() {

    }



    @Override
    public void addAccostSettingError() {

    }

    @Override
    public void updateAccostSettingSuccess(int position) {

    }

    @Override
    public void delAccostSettingSuccess(int position) {
        if (mAdapter.getCheckedIds().size() == mAdapter.getItemCount()) {//已经删完 finish
            finishAfterTransition();
        }
        RxBus.getDefault().post(new AccostCustomizeEvent(2));
    }
}
