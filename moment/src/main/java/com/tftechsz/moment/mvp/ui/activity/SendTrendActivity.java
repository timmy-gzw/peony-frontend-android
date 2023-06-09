package com.tftechsz.moment.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.VideoInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.EventBusConstant;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.moment.R;
import com.tftechsz.moment.adapter.GridImageAdapter;
import com.tftechsz.moment.mvp.IView.ITrendView;
import com.tftechsz.moment.mvp.presenter.TrendPresenter;
import com.tftechsz.moment.other.DragListener;
import com.tftechsz.moment.other.FullyGridLayoutManager;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;
import net.mikaelzero.mojito.impl.SimpleMojitoViewCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SendTrendActivity extends BaseMvpActivity<ITrendView, TrendPresenter> implements View.OnClickListener, ITrendView {
    private final int REQUEST_CODE = 10000;
    private TextView tvPublish;
    private EditText mEditText;
    private RecyclerView mRecyclerViewGrallyPic;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private FrameLayout mFlVideo;
    private TextView tvDeleteText;
    private ImageView mIvVideo;
    private String videoPath;
    private boolean isSelVideo;
    private ItemTouchHelper mItemTouchHelper;
    private boolean needScaleBig = true;
    private boolean needScaleSmall = true;
    private DragListener mDragListener;
    private boolean isUpward;

    @Override
    protected int getLayout() {
        return R.layout.activity_send_trend;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public TrendPresenter initPresenter() {
        return new TrendPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mEditText = findViewById(R.id.publishContent);
        tvDeleteText = findViewById(R.id.tv_delete_text);
        TextView tvNumber = findViewById(R.id.tv_number);
        mFlVideo = findViewById(R.id.fl_video);
        mIvVideo = findViewById(R.id.iv_video);
        ImageView ivVideoDel = findViewById(R.id.iv_video_del);
        tvPublish = findViewById(R.id.pubish);
        tvPublish.setOnClickListener(this);
        mFlVideo.setOnClickListener(this);
        ivVideoDel.setOnClickListener(this);
        mRecyclerViewGrallyPic = findViewById(R.id.rv_report_pic);
        initWidget();
        Utils.setEditCardTextChangedListener(mEditText, tvNumber, 500, false);

        mCompositeDisposable.add(RxView.clicks(findViewById(R.id.pubish))
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                            if (selectList.size() == 0) {
                                toastTip("请添加图片/视频后发布");
                                return;
                            }
                            if (!isSelVideo && selectList.size() > 9) {
                                toastTip("最多上传9张照片");
                                return;
                            }
                            p.beforeCheck(selectList, isSelVideo);
                        }
                ));
    }

    @Override
    protected void initData() {
        setData(getIntent().getParcelableArrayListExtra(Interfaces.EXTRA_TREND));
    }

    private void setData(List<LocalMedia> images) {
        if (images == null || images.size() == 0) {
            return;
        }
        selectList.clear();
        if (images.size() == 1 && Utils.fileIsVideo(images.get(0).getMimeType())) {
            //ARouterUtils.toVideoTrimmerActivity(mActivity, images.get(0).getRealPath());
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
            setVideo(images.get(0));
        } else {
            selectList.addAll(images);
            adapter.setList(images);
            adapter.notifyDataSetChanged();
            isSelVideo = false;
        }
        checkPublishButtonEnable();
    }

    private final GridImageAdapter.onAddPicClickListener onAddPicClickListener = () -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                ChoosePicUtils.picMultiple(SendTrendActivity.this, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, selectList, true);
            } else {
                PermissionUtil.toAllFilePermissionSetting(this, REQUEST_CODE);
            }
        } else {
            final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions(this)
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    ChoosePicUtils.picMultiple(SendTrendActivity.this, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, selectList, true);
                                } else {
                                    PermissionUtil.showPermissionPop(this, getString(R.string.chat_open_storage_camera_permission));
                                }
                            })
                    );
                } else {
                    PermissionUtil.showPermissionPop(this, getString(R.string.chat_open_storage_camera_permission));
                }
            });
        }
    };

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerViewGrallyPic.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(Interfaces.PIC_SELCTED_NUM);
        mRecyclerViewGrallyPic.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
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
                            .views(mRecyclerViewGrallyPic, R.id.iv)
                            .autoLoadTarget(false)
                            .setProgressLoader(DefaultPercentProgress::new)
                            .setOnMojitoListener(new SimpleMojitoViewCallback() {
                                @Override
                                public void onClick(@NotNull View view, float x, float y, int position) {
                                    super.onClick(view, x, y, position);
                                }
                            })
                            .setIndicator(new NumIndicator(mActivity))
                            .start();
                }
            }

            @Override
            public void onItemDel(int pos) {
                selectList.remove(pos);
                checkPublishButtonEnable();
            }
        });

        adapter.setItemLongClickListener((holder, position, v) -> {
            //如果item不是最后一个，则执行拖拽
            needScaleBig = true;
            needScaleSmall = true;
            int size = adapter.getData().size();
            if (size != Interfaces.PIC_SELCTED_NUM) {
                mItemTouchHelper.startDrag(holder);
                return;
            }
            if (holder.getLayoutPosition() != size - 1) {
                mItemTouchHelper.startDrag(holder);
            }
        });

        mDragListener = new DragListener() {
            @Override
            public void deleteState(boolean isDelete) {
                if (isDelete) {
                    tvDeleteText.setText("松手即可删除");
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_let_go_delete, 0, 0);
                } else {
                    tvDeleteText.setText("拖动到此处删除");
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.picture_icon_delete, 0, 0);
                }

            }

            @Override
            public void dragState(boolean isStart) {
                int visibility = tvDeleteText.getVisibility();
                if (isStart) {
                    if (visibility == View.GONE) {
                        tvDeleteText.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
                        tvDeleteText.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (visibility == View.VISIBLE) {
                        tvDeleteText.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateInterpolator());
                        tvDeleteText.setVisibility(View.GONE);
                    }
                }
            }
        };


        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.setAlpha(0.7f);
                }
                return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //得到item原来的position
                try {
                    int fromPosition = viewHolder.getAdapterPosition();
                    //得到目标position
                    int toPosition = target.getAdapterPosition();
                    int itemViewType = target.getItemViewType();
                    if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                        if (fromPosition < toPosition) {
                            for (int i = fromPosition; i < toPosition; i++) {
                                Collections.swap(adapter.getData(), i, i + 1);
                            }
                        } else {
                            for (int i = fromPosition; i > toPosition; i--) {
                                Collections.swap(adapter.getData(), i, i - 1);
                            }
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition);
                        selectList = adapter.getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (null == mDragListener) {
                        return;
                    }
                    KeyboardUtils.hideSoftInput(mActivity);
                    if (needScaleBig) {
                        //如果需要执行放大动画
                        viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(100);
                        //执行完成放大动画,标记改掉
                        needScaleBig = false;
                        //默认不需要执行缩小动画，当执行完成放大 并且松手后才允许执行
                        needScaleSmall = false;
                    }
                    int sh = mRecyclerViewGrallyPic.getBottom() + tvDeleteText.getHeight();
                    int ry = tvDeleteText.getBottom() - sh;
                    if (dY >= ry) {
                        //拖到删除处
                        mDragListener.deleteState(true);
                        if (isUpward) {
                            //在删除处放手，则删除item
                            viewHolder.itemView.setVisibility(View.INVISIBLE);
                            int adapterPosition = viewHolder.getAdapterPosition();
                            adapter.delete(adapterPosition);
                            selectList.remove(adapterPosition);
                            checkPublishButtonEnable();
                            resetState();
                            return;
                        }
                    } else {//没有到删除处
                        if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {
                            //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                            mDragListener.dragState(false);
                        }
                        if (needScaleSmall) {//需要松手后才能执行
                            viewHolder.itemView.animate().scaleXBy(1f).scaleYBy(1f).setDuration(100);
                        }
                        mDragListener.deleteState(false);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : GridImageAdapter.TYPE_CAMERA;
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && mDragListener != null) {
                        mDragListener.dragState(true);
                    }
                    super.onSelectedChanged(viewHolder, actionState);
                }
            }

            @Override
            public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                needScaleSmall = true;
                isUpward = true;
                return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.setAlpha(1.0f);
                    super.clearView(recyclerView, viewHolder);
                    adapter.notifyDataSetChanged();
                    resetState();
                }
            }
        });

        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewGrallyPic);

    }

    /**
     * 重置
     */
    private void resetState() {
        if (mDragListener != null) {
            mDragListener.deleteState(false);
            mDragListener.dragState(false);
        }
        isUpward = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.fl_video) { //播放视频
            if (!TextUtils.isEmpty(videoPath)) {
                VideoInfo videoInfo = new VideoInfo(videoPath, true);
                Utils.startTrendVideoViewActivity(mActivity, mIvVideo, videoInfo);
            }
        } else if (id == R.id.iv_video_del) {
            //FileUtils.delete(videoPath);
            videoPath = "";
            selectList.clear();
            checkPublishButtonEnable();
            mFlVideo.setVisibility(View.GONE);
            mRecyclerViewGrallyPic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //FileUtils.delete(videoPath);
        if (selectList != null && selectList.size() > 0) {
            selectList.clear();
        }
        EventBus.getDefault().unregister(this);
    }

    //上传到后端PHP
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(String event) {
        if (event != null && !event.equals("") && event.equals(EventBusConstant.UPLOADPUBLISHDATA)) {
            p.publish(mEditText.getEditableText().toString(), selectList, isSelVideo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                    setData(images);
//                  selectList = PictureSelector.obtainMultipleResult(data);
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;

                case Interfaces.VIDEO_TRIM_REQUEST_CODE:
                    String video_Path = data.getStringExtra(Interfaces.EXTRA_TRIM_VIDEO_PATH);
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setRealPath(video_Path);
                    setVideo(localMedia);
                    break;
            }
        }
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                toastTip(getString(R.string.no_external_premiss));
            }
        }
    }

    private void checkPublishButtonEnable() {
//        String content = mEditText.getText().toString().trim();
        tvPublish.setEnabled(/*!TextUtils.isEmpty(content) || */selectList.size() > 0);
    }

    private void setVideo(LocalMedia localMedia) {
        selectList.add(localMedia);
        checkPublishButtonEnable();
        videoPath = !TextUtils.isEmpty(localMedia.getRealPath()) ? localMedia.getRealPath() : localMedia.getPath();
        int[] videoWidthHeight = Utils.getVideoWidthHeight(videoPath);
        mFlVideo.setVisibility(View.VISIBLE);
        mRecyclerViewGrallyPic.setVisibility(View.GONE);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) Utils.filterVideoWH(mIvVideo.getLayoutParams(),
                (int) (ScreenUtils.getScreenWidth() / Interfaces.VideoW), (int) (ScreenUtils.getScreenHeight() / Interfaces.VideoH), videoWidthHeight[0], videoWidthHeight[1]);
        mIvVideo.setLayoutParams(lp);
        GlideUtils.loadRoundImageRadius(mContext, mIvVideo, videoPath);
        isSelVideo = true;
    }

    /**
     * 发布动态成功
     */
    @Override
    public void sendTrendSuccess(Boolean data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_SEND_TREND_SUCCESS));
        toastTip("发布成功,等待审核通过！");
        selectList.clear();
        finish();
    }

    @Override
    public void beforeCheckSuccess() {
        GlobalDialogManager.getInstance().show(getFragmentManager(), "正在发布...");
    }
}
