package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AccostPicAdapter;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.req.DelAccostSettingBean;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 招呼相册
 */
@Route(path = ARouterApi.ACTIVITY_ACCOST_SETTING_PIC)
public class AccostPicActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView, View.OnClickListener {

    private TextView mTopHint;
    private RecyclerView mRecy;
    private TextView mEmpty;
    private TextView mUpload;
    private AccostPicAdapter mAdapter;
    private List<AccostSettingListBean> mList = new ArrayList<>();

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_pic;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle(Interfaces.ACCOST_PIC).build();
        mTopHint = findViewById(R.id.ar_top_hint);
        mRecy = findViewById(R.id.recy);
        mEmpty = findViewById(R.id.accost_setting_empty);
        mUpload = findViewById(R.id.upload);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mTopHint.setText("照片越多，越容易让Ta喜欢你哦~");
        mEmpty.setText("暂无图片，去上传靓照吧~");
        mRecy.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new AccostPicAdapter();
        mAdapter.addChildClickViewIds(R.id.del);
        mRecy.setAdapter(mAdapter);
        mUpload.setOnClickListener(this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<String> urls = new ArrayList<>();
            for (AccostSettingListBean bean : mList) {
                urls.add(bean.url);
            }
            Mojito.with(mContext)
                    .urls(urls)
                    .position(position, 0, urls.size())
                    .views(mRecy, R.id.icon)
                    .autoLoadTarget(false)
                    .setIndicator(new NumIndicator(mActivity))
                    .setProgressLoader(DefaultPercentProgress::new)
                    .start();
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.del) {
                ArrayList<Integer> ids = new ArrayList<>();
                ids.add(mAdapter.getItem(position).id);
                p.delAccostSetting(position, new DelAccostSettingBean(ids));
            }
        });
        p.getAccostSettingList(AccostType.PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.upload) {
            if (mAdapter.getItemCount() >= 9) {
                toastTip("相册已达上限,不能继续添加了!");
                return;
            }
            final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    ChoosePicUtils.picSingle(mActivity, new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            if (result != null && result.size() > 0) {
                                                String path = TextUtils.isEmpty(result.get(0).getRealPath()) ? result.get(0).getPath() : result.get(0).getRealPath();
                                                GlobalDialogManager.getInstance().show(getFragmentManager(), "正在上传...");
                                                UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE,
                                                        UploadHelper.PATH_USER_ACCOST, UploadHelper.TYPE_IMAGE, new File(path), new UploadHelper.OnUploadListener() {
                                                            @Override
                                                            public void onStart() {
                                                            }

                                                            @Override
                                                            public void onLoading(long cur, long total) {

                                                            }

                                                            @Override
                                                            public void onSuccess(String url) {
                                                                p.addAccostSetting(AccostType.PICTURE, new AccostSettingListBean(url, ""));
                                                            }

                                                            @Override
                                                            public void onError() {
                                                                GlobalDialogManager.getInstance().dismiss();
                                                            }
                                                        });
                                            } else {
                                                toastTip("请重新选中图片");
                                            }
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                                } else {
                                    PermissionUtil.showPermissionPop(this, getString(R.string.chat_open_storage_permission));
                                }
                            })
                    );
                }
            });
        }
    }


    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {
        mList = data;
        if (data != null && data.size() > 0) {
            mAdapter.setList(data);
            mRecy.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        } else {
            mRecy.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addAccostSettingSuccess() {
        GlobalDialogManager.getInstance().dismiss();
        p.getAccostSettingList(AccostType.PICTURE);
    }


    @Override
    public void addAccostSettingError() {
        GlobalDialogManager.getInstance().dismiss();
    }

    @Override
    public void updateAccostSettingSuccess(int position) {

    }

    @Override
    public void delAccostSettingSuccess(int position) {
        Utils.logE("delAccostSettingSuccess");
        mAdapter.removeAt(position);
        if (mAdapter.getItemCount() > 0) {
            mRecy.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        } else {
            mRecy.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
        //mList.remove(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mList != null) {
            mList.clear();
            mList = null;
        }
    }
}
