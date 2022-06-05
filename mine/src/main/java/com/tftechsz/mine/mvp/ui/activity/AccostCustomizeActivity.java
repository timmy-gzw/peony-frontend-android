package com.tftechsz.mine.mvp.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AccostCustomizeAdapter;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.dto.AccostCustomizeEvent;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 自定义打招呼
 */
@Route(path = ARouterApi.ACTIVITY_ACCOST_SETTING_CUSTOMIZE)
public class AccostCustomizeActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView, View.OnClickListener {
    private TextView mTopHint;
    private RecyclerView mRecy;
    private TextView mAdd;
    private TextView mEmpty;
    private AccostCustomizeAdapter mAdapter;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_customize;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle(Interfaces.ACCOST_CUSTOMIZE)
                .setRightText("管理", v -> {
                    if (mAdapter.getData().size() == 0) {
                        toastTip("你还没有自定义招呼, 快去添加吧");
                        return;
                    }
                    Intent intent = new Intent(mContext, AccostCustomizeManagerActivity.class);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent);
                    } else {
                        List<Pair<View, String>> pairs = new ArrayList<>();
                        pairs.add(new Pair<>(mAdd, getString(R.string.transition_aac_add)));
                        pairs.add(new Pair<>(mRecy, getString(R.string.transition_aac_recy)));
                        Utils.addTransitionBar(mActivity, pairs);
                        Pair<View, String>[] pairArr = new Pair[pairs.size()];
                        for (int i = 0; i < pairs.size(); i++) {
                            pairArr[i] = pairs.get(i);
                        }
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mActivity, pairArr).toBundle());
                    }
                })
                .build();

        mTopHint = findViewById(R.id.ar_top_hint);
        mRecy = findViewById(R.id.recy);
        mEmpty = findViewById(R.id.accost_setting_empty);
        mAdd = findViewById(R.id.add);
    }

    @Override
    protected void initData() {
        mTopHint.setText("添加3条以上自定义招呼时，会优先推荐给男用户哦~");
        mEmpty.setText("添加打招呼快捷语，收到回复的概率更高哦~");
        mRecy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccostCustomizeAdapter(false);
        mRecy.setAdapter(mAdapter);
        mAdd.setOnClickListener(this);
        initBus();
        p.getAccostSettingList(AccostType.TEXT);
    }

    private void initBus() {
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(RxBus.getDefault().toObservable(AccostCustomizeEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            switch (event.type) {
                                case 0: //添加
                                case 2: //删除
                                    p.getAccostSettingList(AccostType.TEXT);
                                    break;

                                case 1: //更新
                                    for (int i = 0, j = mAdapter.getItemCount(); i < j; i++) {
                                        AccostSettingListBean item = mAdapter.getItem(i);
                                        if (item != null && item.id == event.id) {
                                            item.text = event.text;
                                            mAdapter.setData(i, item);
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }
                ));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            if (mAdapter.getItemCount() >= 9) {
                toastTip("招呼已达上限,不能继续添加了!");
                return;
            }
            Intent intent = new Intent(mContext, AccostCustomizeEditActivity.class);
            intent.putExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_TYPE, 1);
            startActivity(intent);
        }
    }

    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {
        if (data != null && data.size() > 0) {
            mAdapter.setList(data);
            mEmpty.setVisibility(View.GONE);
            mRecy.setVisibility(View.VISIBLE);
        } else {
            mRecy.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
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

    }
}
