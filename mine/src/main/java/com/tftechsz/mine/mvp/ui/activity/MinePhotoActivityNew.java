package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.app.Service;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.NetworkUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.MinePhotoAdapterNew;
import com.tftechsz.mine.entity.dto.MinePhotoDto;
import com.tftechsz.mine.mvp.IView.IMinePhotoViewNew;
import com.tftechsz.mine.mvp.presenter.MinePhotoPresenterNew;
import com.tftechsz.mine.widget.pop.DelPicPopWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(path = ARouterApi.ACTIVITY_MINE_PHOTO)
public class MinePhotoActivityNew extends BaseMvpActivity<IMinePhotoViewNew, MinePhotoPresenterNew> implements IMinePhotoViewNew, View.OnClickListener {

    private MinePhotoAdapterNew mAdapter;
    private RecyclerView mRvPhoto;
    private DelPicPopWindow mDelPicPopWindow;
    private List<MinePhotoDto> mTemp = new ArrayList<>();
    private TextView mBtnUpload;
    @Autowired
    UserProviderService service;

    @Override
    public MinePhotoPresenterNew initPresenter() {
        return new MinePhotoPresenterNew();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("个人相册")
                .build();
        mRvPhoto = findViewById(R.id.rv_photo);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRvPhoto.setLayoutManager(layoutManager);
        mBtnUpload = findViewById(R.id.tv_upload);
        mBtnUpload.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter = new MinePhotoAdapterNew();
        mRvPhoto.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((ad, view, position) -> {
            if (mAdapter.getItemViewType(position) == Interfaces.TYPE_CAMERA) {
                if (mAdapter.getItemCount() < 9) {
                    final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                        if (agreeToRequest) {
                            mCompositeDisposable.add(new RxPermissions(this)
                                    .request(permissions)
                                    .subscribe(aBoolean -> {
                                        if (aBoolean) {
                                            ChoosePicUtils.picSingle(MinePhotoActivityNew.this, true, new OnResultCallbackListener<LocalMedia>() {
                                                @Override
                                                public void onResult(List<LocalMedia> result) {
                                                    if (result != null && result.size() > 0) {
                                                        mBtnUpload.setEnabled(true);
                                                        p.updatePhoto(result.get(0));
                                                    } else {
                                                        toastTip(getString(R.string.picture_choose_fail));
                                                    }
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            });
                                        } else {
                                            PermissionUtil.showPermissionPop(this, getString(R.string.chat_open_storage_camera_permission));
                                        }
                                    }));
                        } else {
                            PermissionUtil.showPermissionPop(this, getString(R.string.chat_open_storage_camera_permission));
                        }
                    });
                } else {
                    toastTip("最多上传8张照片");
                }
            } else {
                MinePhotoDto item = mAdapter.getItem(position);
                if (item.isShow()) {
                    toastTip("审核中无法操作");
                    return;
                }
                if (null == mDelPicPopWindow) {
                    mDelPicPopWindow = new DelPicPopWindow(MinePhotoActivityNew.this);
                }
                mDelPicPopWindow.setOnClickListener(() -> {
                    p.remove(item.getUrl());
                    mBtnUpload.setEnabled(isUpdate());
                });
                mDelPicPopWindow.showPopupWindow();
            }
        });

        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Utils.logE("onItemLongClick");
            return false;
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                Utils.logE("isLongPressDragEnabled");
                return true;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑删除可以使用；
            }

            @Override
            public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
                Utils.logE("getMovementFlags");
                int itemViewType = viewHolder.getItemViewType();
                if (NetworkUtils.isConnected() && itemViewType != Interfaces.TYPE_CAMERA) {
                    viewHolder.itemView.setAlpha(0.7f);
                    return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                            | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
                }
                return makeMovementFlags(0, 0);
            }

            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                Utils.logE("onMove");
                try {
                    //得到当拖拽的viewHolder的Position
                    int fromPosition = viewHolder.getAdapterPosition();
                    //拿到当前拖拽到的item的viewHolder
                    int toPosition = target.getAdapterPosition();
                    if (NetworkUtils.isConnected() && target.getItemViewType() != Interfaces.TYPE_CAMERA) {
                        if (fromPosition < toPosition) {
                            for (int i = fromPosition; i < toPosition; i++) {
                                Collections.swap(mAdapter.getData(), i, i + 1);
                            }
                        } else {
                            for (int i = fromPosition; i > toPosition; i--) {
                                Collections.swap(mAdapter.getData(), i, i - 1);
                            }
                        }
                        mAdapter.notifyItemMoved(fromPosition, toPosition);
                        return true;
                    }
                    //p.updatePosition(mUploadPhotoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int itemViewType = viewHolder.getItemViewType();
                if (NetworkUtils.isConnected() && itemViewType != Interfaces.TYPE_CAMERA) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            /**
             * 长按选中Item的时候开始调用
             * 长按高亮
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                Utils.logE("onSelectedChanged");
                int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : Interfaces.TYPE_CAMERA;
                if (NetworkUtils.isConnected() && itemViewType != Interfaces.TYPE_CAMERA) {
                    mBtnUpload.setEnabled(true);
                    //获取系统震动服务//震动70毫秒
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(70);
                    viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(100).start();
                    super.onSelectedChanged(viewHolder, actionState);
                }
            }

            /**
             * 手指松开的时候还原高亮
             */
            @Override
            public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
                Utils.logE("clearView");
                if (viewHolder.getItemViewType() != Interfaces.TYPE_CAMERA) {
                    viewHolder.itemView.animate().scaleXBy(1f).scaleYBy(1f).setDuration(100).start();
                    viewHolder.itemView.setAlpha(1.0f);
                    super.clearView(recyclerView, viewHolder);
                    mAdapter.notifyDataSetChanged();
                    mBtnUpload.setEnabled(isUpdate());

                    if (isUpdate()) {
                        p.pictureSort(mAdapter.getData());
                    }
                }
            }
        });
        helper.attachToRecyclerView(mRvPhoto);
        p.getPhoto();
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_mine_photo;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_upload) {
            // p.uploadFile();
        }
    }

    private boolean isUpdate() {
        boolean isUpdate = false;
        if (mTemp.size() == mAdapter.getItemCount()) {//相等 可能没改
            for (int i = 0, j = mAdapter.getItemCount(); i < j; i++) {
                MinePhotoDto item = mAdapter.getItem(i);
                if (!TextUtils.equals(item.getUrl(), mTemp.get(i).getUrl())) {//只要不一样 就说明改了
                    isUpdate = true;
                    break;
                }
            }
        } else {
            isUpdate = true;
        }
        return isUpdate;
    }

    @Override
    public void uploadPhotoSuccess() {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        p.getPhoto();
    }

    @Override
    public void getPhotoSuccess(List<MinePhotoDto> data) {
        List<MinePhotoDto> dtos = new ArrayList<>();
        if (data != null) {
            dtos.addAll(data);
        }
        if (dtos.size() < 8) {
            MinePhotoDto mAddLocalMedia = new MinePhotoDto("ADD", 0);
            dtos.add(mAddLocalMedia);
        }
        mTemp = dtos;
        mAdapter.setList(mTemp);
        mBtnUpload.setEnabled(false);
    }

    @Override
    public void sortPhotoSuccess() {
        toastTip("位置更新成功");
        p.getPhoto();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTemp != null) {
            mTemp.clear();
            mTemp = null;
        }
    }
}
