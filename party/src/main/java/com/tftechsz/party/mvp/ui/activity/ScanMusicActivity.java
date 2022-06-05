package com.tftechsz.party.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.music.util.FileMusicScanManager;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.MusicScanAdapter;
import com.tftechsz.party.databinding.ActScanMusicBinding;
import com.tftechsz.party.entity.MusicCountBean;
import com.tftechsz.party.mvp.IView.IScanMusicView;
import com.tftechsz.party.mvp.presenter.IScanMusicPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 包 名 : com.tftechsz.party.mvp.ui.fragment
 * 描 述 : 扫描音乐
 */
@Route(path = ARouterApi.ACTIVITY_SCAN_MUSIC)
public class ScanMusicActivity extends BaseMvpActivity<IScanMusicView, IScanMusicPresenter> implements IScanMusicView, View.OnClickListener, OnItemChildClickListener {

    private ActScanMusicBinding mBind;
    private MusicScanAdapter mAdapter;
    private List<AudioBean> mScanBeans;
    private final Object lock = new Object();
    private TextView mTvEmpty;
    private final List<AudioBean> uploadServerList = new ArrayList<>();
    private ArrayList<AudioBean> mAudioBeans = new ArrayList<>();

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(mActivity, R.layout.act_scan_music);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("添加音乐").showBack(true).setRightText("扫描本地", v -> {
            if (!mBind.getIsScan()) {
                initPermission(new Random().nextInt(1500) + 500);
            } else {
                Utils.toast("正在扫描中, 请稍后再试");
            }
        }).build();
//        ImmersionBar.with(mActivity).keyboardEnable(true).setOnKeyboardListener((isPopup, keyboardHeight) -> mBind.btn.setVisibility(isPopup ? View.GONE : View.VISIBLE)).init();
        if (getIntent() != null) {
            ArrayList<AudioBean> serializableExtra = (ArrayList<AudioBean>) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
            if (serializableExtra != null) {
                mAudioBeans = serializableExtra;
            }
        }

        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MusicScanAdapter();
        View empty = View.inflate(mContext, R.layout.base_empty_view, null);
        mTvEmpty = empty.findViewById(R.id.tv_empty);
        mAdapter.setEmptyView(empty);
        mAdapter.onAttachedToRecyclerView(mBind.recy);
        mAdapter.addChildClickViewIds(R.id.root);
        mAdapter.setOnItemChildClickListener(this);
        mBind.recy.setAdapter(mAdapter);
        mAdapter.setList(null);
        mBind.editDel.setOnClickListener(this);
        mBind.btn.setOnClickListener(this);

        mBind.recy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    KeyboardUtils.hideSoftInput(mActivity);
                }
            }
        });

        mBind.search.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {//禁止空格
            if (source.equals(" ")) return "";
            else return null;
        }});
        mBind.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBind.editDel.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
                beginSearch(s.toString().trim());
            }
        });
        initPermission(500);
    }

    /**
     * 初始化权限
     */
    private void initPermission(long sleepTime) {
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        scanMusic(sleepTime);
                    } else {
                        PermissionUtil.showPermissionPop(mActivity, "未获取应用存储权限,音乐扫描功能无法正常使用。打开应用设置页以修改应用权限");
                    }
                }));
    }

    /**
     * 搜索本地语音
     *
     * @param searchString 搜索输入框content
     */
    private void beginSearch(String searchString) {
        if (mScanBeans.size() == 0) {
            return;
        }
        if (searchString.length() == 0) {
            mTvEmpty.setText("未获取到歌曲文件");
            mAdapter.setList(mScanBeans);
            return;
        }

        List<AudioBean> searchDates = new ArrayList<>();
        for (int i = 0; i < mScanBeans.size(); i++) {
            AudioBean item = mScanBeans.get(i);
            if (item.getFileName().toLowerCase().contains(searchString.toLowerCase())) {
                searchDates.add(item);
            }
        }
        if (searchDates.size() > 0) {
            mAdapter.setList(searchDates);
        } else {
            mTvEmpty.setText("未搜索到歌曲文件");
            mAdapter.setList(null);
        }
    }

    /**
     * 扫描本地音乐
     */
    private void scanMusic(long sleepTime) {
        mBind.searchLottie.playAnimation();
        mBind.setIsScan(true);
        Observable.create((ObservableOnSubscribe<List<AudioBean>>) emitter -> {
            List<AudioBean> audioBeans = FileMusicScanManager.getInstance().scanMusic(mActivity, mAudioBeans);
            Thread.sleep(sleepTime);
            emitter.onNext(audioBeans);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())//在IO线程执行数据库处理操作
                .observeOn(AndroidSchedulers.mainThread())//在UI线程显示图片;
                .subscribe(new Observer<List<AudioBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<AudioBean> beans) {
                        mScanBeans = beans;
                        Collections.sort(mScanBeans, (o1, o2) -> o1.getStatus() - o2.getStatus());
                        mTvEmpty.setText("未获取到歌曲文件");
                        mAdapter.setList(mScanBeans);
                        mBind.setIsScan(false);
                        mBind.setHaseData(beans.size() > 0);
                        mBind.rlEdt.setVisibility(mScanBeans.size() > 0 ? View.VISIBLE : View.GONE);
                        mBind.btn.setVisibility(mScanBeans.size() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public IScanMusicPresenter initPresenter() {
        return new IScanMusicPresenter();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 获取选中的列表
     *
     * @return 选中的列表
     */
    private List<AudioBean> getCheckedList() {
        List<AudioBean> checkBean = new ArrayList<>();
        for (AudioBean bean : mAdapter.getData()) {
            if (bean.isChecked() && bean.getStatus() != 1 && bean.getStatus() != 0) {
                checkBean.add(bean);
            }
        }
        return checkBean;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_del) {
            mBind.search.setText(null);
        } else if (id == R.id.btn) {
            List<AudioBean> checkedList = getCheckedList();
            if (checkedList.isEmpty()) {
                toastTip("请选择您要上传的音乐文件");
                return;
            }
            p.getUploadMusicCount();
        }
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        int id = view.getId();
        if (id == R.id.root) {
            ImageView checkbox = (ImageView) mAdapter.getViewByPosition(position, R.id.checkbox);
            if (checkbox != null) {
                AudioBean item = mAdapter.getItem(position);//被点击的那个实体bean
                if (!item.isUpload()) { //只有没有上传的才做事件处理
                    if (!item.isChecked()) {//如果没被选中
                        if (getCheckedList().size() >= Interfaces.MAX_SELECTED) { //如果大于等于5个
                            Utils.toast(String.format("最多一次性选择%s个", Interfaces.MAX_SELECTED));
                        } else {//设置选中状态
                            checkbox.setImageResource(R.mipmap.ic_check_selector);
                            item.setChecked(true);
                            mBind.setHasSelected(true);
                        }
                    } else {//设置未选中状态
                        checkbox.setImageResource(R.mipmap.ic_check_normal);
                        item.setChecked(false);
                        mBind.setHasSelected(getCheckedList().size() > 0);
                    }
                }
            }
        }
    }

    @Override
    public void getCountSuccess(MusicCountBean data) {
        List<AudioBean> checkedList = getCheckedList();
        if (data.upload_music_num != 0 && data.upload_music_num >= checkedList.size()) {
            upload(checkedList);
        } else {
            toastTip(String.format("每个用户最多只能上传%s首歌哦", data.music_max_count));
        }
    }

    @Override
    public void uploadSuccess() {
        List<AudioBean> checkedList = new ArrayList<>();
        for (int i = 0, j = mAdapter.getData().size(); i < j; i++) {
            AudioBean item = mAdapter.getItem(i);
            if (item.isChecked()) {
                item.setStatus(0);//把选中的列表变成审核中
                item.setChecked(false);//把选中状态还原
                checkedList.add(item);
                mAdapter.setData(i, item);
            }
        }
        uploadServerList.clear();
        mAudioBeans.addAll(checkedList);
        mBind.setHasSelected(false);
        toastTip("上传成功, 请等待审核");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_PARTY_MUSIC));
        // scanMusic(0);
    }

    /**
     * 开始上传文件
     *
     * @param lists 选中的集合
     */
    private void upload(List<AudioBean> lists) {
        showLoadingDialog();
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final Handler handler = new Handler(Looper.myLooper());

        class Task implements Runnable {
            final AudioBean bean;

            Task(AudioBean bean) {
                this.bean = bean;
            }

            @Override
            public void run() {
                UploadHelper.getInstance(BaseApplication.getInstance())
                        .doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_MUSIC, UploadHelper.TYPE_AUDIOS, new File(bean.getPath()), new UploadHelper.OnUploadListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onLoading(long cur, long total) {

                            }

                            @Override
                            public void onSuccess(String url) {
                                Utils.logE("url上传成功: " + url);
                                synchronized (lock) {
                                    bean.setFileHash(FileUtils.getFileMD5ToString(bean.getPath()).toLowerCase());
                                    bean.setPath(url);
                                    bean.setTitle(bean.getFileName());
                                    uploadServerList.add(bean);
                                    if (!taskList.isEmpty()) {
                                        Runnable runnable = taskList.pop();
                                        handler.post(runnable);
                                    } else {
                                        p.uploadMusic(uploadServerList);
                                        GlobalDialogManager.getInstance().dismiss();
                                        //完成之后的个人操作
                                        //EventBus.getDefault().post(EventBusConstant.UPLOADPUBLISHDATA);
                                    }
                                }
                            }


                            @Override
                            public void onError() {
                                GlobalDialogManager.getInstance().dismiss();
                            }
                        });
            }
        }
        //循环遍历原始路径 添加至linklist中
        for (AudioBean bean : lists) {
            taskList.add(new Task(bean));
        }
        handler.post(taskList.pop());

//        GlobalDialogManager.getInstance().show(getFragmentManager(), "正在上传...");


    }
}
