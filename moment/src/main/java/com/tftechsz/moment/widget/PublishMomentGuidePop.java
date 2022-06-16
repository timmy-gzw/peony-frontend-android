package com.tftechsz.moment.widget;

import android.Manifest;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.EventBusConstant;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.MomentImageAdapter;
import com.tftechsz.moment.mvp.IView.ITrendView;
import com.tftechsz.moment.mvp.presenter.TrendPresenter;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.util.KeyboardUtils;

/**
 * 包 名 : com.tftechsz.moment.widget
 * 描 述 : 发布动态引导弹窗
 */
public class PublishMomentGuidePop extends BaseBottomPop implements View.OnClickListener, ITrendView {

    private final FragmentActivity mActivity;
    private final CompositeDisposable mCompositeDisposable;
    private final List<LocalMedia> selectList = new ArrayList<>();
    private EditText etContent;
    private TextView tvContentPh;
    private View llContentPh;
    private RecyclerView recyclerView;
    private FrameLayout flContent;
    private TextView tvOtherWords;
    private TextView tvContentCount;
    private MomentImageAdapter adapter;
    //TODO 2022/6/16 替换动态引导文案
    private final String[] randomAccost = {
            "有来找另一半的人吗，可以互相认识一下",
            "22222222222222222222222222222222222222222",
            "3333333333333333",
            "4444444444444444",
            "5555555555555555555555555555555555555"
    };

    public TrendPresenter presenter;

    public PublishMomentGuidePop(FragmentActivity mActivity) {
        super(mActivity);
        mCompositeDisposable = new CompositeDisposable();
        this.mActivity = mActivity;
        initUI();
    }

    private void initUI() {
        EventBus.getDefault().register(this);
        presenter = new TrendPresenter();
        presenter.attachView(this);
        setOutSideDismiss(false);
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            KeyboardUtils.close(etContent);
            dismiss();
        });
        recyclerView = findViewById(R.id.rv_pic);
        flContent = findViewById(R.id.fl_publish_moment);
        flContent.setEnabled(false);
        tvOtherWords = findViewById(R.id.tv_other_words);
        tvOtherWords.setOnClickListener(this);
        tvContentCount = findViewById(R.id.tv_content_count);
        tvContentPh = findViewById(R.id.tv_content_ph);
        llContentPh = findViewById(R.id.ll_content_ph);
        llContentPh.setOnClickListener(v -> {
            setEtContent();
            etContent.requestFocus();
            KeyboardUtils.open(etContent);
        });
        etContent = findViewById(R.id.et_content);
        etContent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (llContentPh.getVisibility() == View.VISIBLE) {
                    setEtContent();
                }
                tvOtherWords.setVisibility(View.GONE);
                tvContentCount.setVisibility(View.VISIBLE);
            } else {
                tvOtherWords.setVisibility(View.VISIBLE);
                tvContentCount.setVisibility(View.GONE);
            }
        });
        Utils.setEditCardTextChangedListener(etContent, tvContentCount, 250, false);
        randomAccost();
        initRecyclerView();
        mCompositeDisposable.add(RxView.clicks(flContent)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                            if (selectList.size() == 0) {
                                toastTip("请添加图片后发布");
                                return;
                            }
                            if (selectList.size() > 9) {
                                toastTip("最多上传9张照片");
                                return;
                            }

                            if (presenter != null) {
                                presenter.beforeCheck(selectList, false);
                            }
                        }
                ));
    }

    private void setEtContent() {
        CharSequence text = tvContentPh.getText();
        llContentPh.setVisibility(View.GONE);
        etContent.setText(text);
        etContent.setSelection(text.length());
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new MomentImageAdapter(mActivity, this::showMediaSelector);
        adapter.setList(selectList);
        adapter.setSelectMax(Interfaces.PIC_SELCTED_NUM);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MomentImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    List<String> urls = new ArrayList<>();
                    for (int i = 0; i < selectList.size(); i++) {
                        String path = TextUtils.isEmpty(selectList.get(i).getPath()) ? selectList.get(i).getCompressPath() : selectList.get(i).getPath();
                        urls.add(path);
                    }
                    Mojito.with(mContext)
                            .urls(urls)
                            .position(position, 0, urls.size())
                            .views(recyclerView, R.id.iv)
                            .autoLoadTarget(false)
                            .setProgressLoader(DefaultPercentProgress::new)
                            .setIndicator(new NumIndicator(mActivity))
                            .start();
                }
            }

            @Override
            public void onItemDel(int pos) {
                checkPublishButtonEnable();
            }
        });
    }

    private void checkPublishButtonEnable() {
        flContent.setEnabled(selectList.size() > 0);
    }

    private void showMediaSelector() {
        mCompositeDisposable.add(new RxPermissions(mActivity)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, selectList, new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                selectList.clear();
                                selectList.addAll(result);
                                adapter.notifyDataSetChanged();
                                checkPublishButtonEnable();
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                    } else {
                        Utils.toast("请允许摄像头权限");
                    }
                })
        );
    }

    //上传到后端PHP
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(String event) {
        if (presenter != null && event != null && !event.equals("") && event.equals(EventBusConstant.UPLOADPUBLISHDATA)) {
            String content = etContent.getEditableText().toString();
            presenter.publish(content, selectList, false);
        }
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        EventBus.getDefault().unregister(this);
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        if (presenter != null) {
            presenter.detachView();
        }
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_publish_moment_guide);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_other_words) {
            randomAccost();
        }
    }

    private void randomAccost() {
        int i = new Random().nextInt(randomAccost.length);
        tvContentPh.setText(randomAccost[i]);
    }

    @Override
    public void showLoadingDialog() {
        if (mActivity != null) {
            GlobalDialogManager.getInstance().show(mActivity.getFragmentManager(), "正在发布...");
        }
    }

    @Override
    public boolean isLoadingDialogShow() {
        return GlobalDialogManager.getInstance().isShow();
    }

    @Override
    public void toastTip(String msg) {
        ToastUtil.showToast(BaseApplication.getInstance(), msg);
    }

    @Override
    public void hideLoadingDialog() {
        GlobalDialogManager.getInstance().dismiss();
    }

    @Override
    public void sendTrendSuccess(Boolean data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_SEND_TREND_SUCCESS));
        toastTip("发布成功,等待审核通过！");
        selectList.clear();
        dismiss();
    }

    @Override
    public void beforeCheckSuccess() {
        showLoadingDialog();
    }
}
