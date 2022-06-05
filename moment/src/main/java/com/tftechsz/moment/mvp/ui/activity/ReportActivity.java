package com.tftechsz.moment.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.GridImageAdapter;
import com.tftechsz.moment.adapter.ReportContactAdapter;
import com.tftechsz.moment.mvp.IView.IReportView;
import com.tftechsz.moment.mvp.presenter.ReportPresenter;
import com.tftechsz.moment.other.FullyGridLayoutManager;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route(path = ARouterApi.ACTIVITY_REPORT)
public class ReportActivity extends BaseMvpActivity<IReportView, ReportPresenter> implements View.OnClickListener, IReportView {
    private EditText mEditText;
    private RecyclerView mRecyclerViewGrallyPic;
    private final List<LocalMedia> selectList = new ArrayList<>();
    //保存获取到的阿里云oss图片Url
    private final ArrayList<String> uploadServerUrlList = new ArrayList<>();
    private GridImageAdapter adapter;
    private final ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    private CountDownLatch mCountDownLatch;
    private int reportType;
    private int blogId;
    private int fromType;  // 1:个人  2：动态 3：帮助反馈
    private TextView mTvTitle;

    //联系方式
    private LinearLayout mLlContactWay;
    private LinearLayout mLlOnlineCustom;

    @Autowired
    UserProviderService service;
    private RecyclerView mRecyclerView;
    private ReportContactAdapter mAdapter;
    private TextView mFeedbackTitle;


    @Override
    public ReportPresenter initPresenter() {
        return new ReportPresenter();
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mEditText = findViewById(R.id.publishContent);
        mTvTitle = findViewById(R.id.toolbar_title);
        mRecyclerViewGrallyPic = findViewById(R.id.rv_report_pic);
        mFeedbackTitle = findViewById(R.id.feedback_title);
        mLlContactWay = findViewById(R.id.ll_contact_way);
        mRecyclerView = findViewById(R.id.recy);
        mLlOnlineCustom = findViewById(R.id.ll_online_custom);
        mLlOnlineCustom.setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        initWidget();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ReportContactAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void initData() {
        super.initData();
        mLlContactWay.setVisibility(View.GONE);
        if (getIntent() != null) {
            reportType = getIntent().getIntExtra("report_type", 0);
            blogId = getIntent().getIntExtra("blogId", 0);
            fromType = getIntent().getIntExtra("fromType", 0);
        }
        if (fromType == 3) {
            mTvTitle.setText("意见反馈");
            mEditText.setHint("请输入你的意见和建议");
            ConfigInfo configInfo = service.getConfigInfo();
            if (configInfo != null && configInfo.sys != null) {
                if (configInfo.sys.is_show_im_feedback == 1) {   // 显示
                    mLlOnlineCustom.setVisibility(View.VISIBLE);
                } else {
                    mLlOnlineCustom.setVisibility(View.GONE);
                }
//                mLlContactWay.setVisibility(View.VISIBLE);
//                mFeedbackTitle.setText(configInfo.sys.feedback_title);
//                if (configInfo.sys.feedback_contact_new != null) {
//                    mAdapter.setList(configInfo.sys.feedback_contact_new);
//                }
            }
        }
    }

    private final GridImageAdapter.onAddPicClickListener onAddPicClickListener = () -> ChoosePicUtils.picMultiple(ReportActivity.this, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, adapter.getList());

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerViewGrallyPic.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        //adapter.setList(selectList);
        adapter.setSelectMax(Interfaces.PIC_SELCTED_NUM);
        mRecyclerViewGrallyPic.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
//                    ArrayList<UserViewInfo> userViewInfos = new ArrayList<>();
                ArrayList<String> urls = new ArrayList<>();
                for (int i = 0; i < adapter.getList().size(); i++) {
                    String path = TextUtils.isEmpty(adapter.getItem(i).getPath()) ? adapter.getItem(i).getCompressPath() : adapter.getItem(i).getPath();
                    //userViewInfos.add(new UserViewInfo(path));
                    urls.add(path);
                }
//                    Intent intent = new Intent(mContext, MyPicViewActivity.class);
//                    intent.putParcelableArrayListExtra(Interfaces.EXTRA_TREND_PIC_LIST, userViewInfos);
//                    intent.putExtra(Interfaces.EXTRA_TREND_PIC_INDEX, position);
//                    startActivity(intent);

                Mojito.with(mContext)
                        .urls(urls)
                        .position(position, 0, urls.size())
                        .views(mRecyclerViewGrallyPic, R.id.iv)
                        .autoLoadTarget(false)
                        .setIndicator(new NumIndicator(mActivity))
                        .setProgressLoader(DefaultPercentProgress::new)
                        .start();
            }

            @Override
            public void onItemDel(int pos) {
              /*  if (selectList != null && selectList.size() > 0)
                    selectList.remove(pos);*/
            }
        });
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_send_report;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.submit) {
            if (TextUtils.isEmpty(mEditText.getText().toString())) {
                toastTip("请您输入内容后操作");
                return;
            }
            if (fromType == 3) {
                if (adapter.getList().size() <= 0) {
                    p.feedback(mEditText, mEditText.getEditableText().toString(), "");
                } else {
                    upload();
                }
            } else {
                if (adapter.getList().size() <= 0) {
                    toastTip("请您选择图片后操作");
                    return;
                }
                upload();
            }

        } else if (id == R.id.ll_online_custom) {
            ConfigInfo configInfo = service.getConfigInfo();
            if (configInfo != null && configInfo.sys != null) {
                BaseWebViewActivity.start(this, "在线客服", configInfo.sys.im_feedback_webview, 0, 3);
            }
        }
    }


    private void upload() {
        if (!ClickUtil.canOperate()) return;
        new Thread(() -> {
            mCountDownLatch = new CountDownLatch(adapter.getList().size());
            //发布文字和图片
            for (LocalMedia item : adapter.getList()) {
                mFixedThreadPool.execute(() -> report(item));
            }
        }).start();
    }

    public void report(LocalMedia item) {
//        String path = TextUtils.isEmpty(item.getPath()) ? item.getCompressPath() :item.getPath();
        String path = TextUtils.isEmpty(item.getRealPath()) ? item.getPath() : item.getRealPath();
        File file = new File(path);
        showLoadingDialog();
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                uploadServerUrlList.add(url);
                mCountDownLatch.countDown();
                if (mCountDownLatch.getCount() == 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < uploadServerUrlList.size(); i++) {
                        sb.append(uploadServerUrlList.get(i));
                        sb.append(",");
                    }
                    uploadToBendServer(mEditText, reportType, blogId, mEditText.getEditableText().toString(), sb.toString().substring(0, sb.length() - 1));
                }
            }

            @Override
            public void onError() {
                hideLoadingDialog();
            }
        });
    }


    /**
     * "content": "你好",    //动态内容文字
     * "media": "http://www.baidu.com",  //音频、视频url
     * "type": 1,  //1-视频 2-音频 3-图片
     */
    public void uploadToBendServer(EditText mEditText, int type_id, int blog_id, String message, String images) {
        if (fromType == 1) {  //举报用户
            p.reportUser(mEditText, type_id, blog_id, message, images);
        } else if (fromType == 2) {  //举报动态
            p.report(mEditText, type_id, blog_id, message, images);
        } else {  //用户反馈
            p.feedback(mEditText, message, images);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
                adapter.setList(PictureSelector.obtainMultipleResult(data));
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.clearAndUnregister();
            adapter = null;
        }
        if (uploadServerUrlList != null) {
            uploadServerUrlList.clear();
        }
    }

    @Override
    public void commitSuccess(String msg) {
        if (adapter != null) {
            adapter.getList().clear();
        }
        finish();
    }
}
