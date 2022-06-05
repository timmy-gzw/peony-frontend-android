package com.tftechsz.mine.mvp.ui.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.MinePhotoAdapter;
import com.tftechsz.mine.mvp.IView.IMinePhotoView;
import com.tftechsz.mine.mvp.presenter.MinePhotoPresenter;
import com.tftechsz.mine.widget.pop.DelPicPopWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MinePhotoActivity extends BaseMvpActivity<IMinePhotoView, MinePhotoPresenter> implements IMinePhotoView, View.OnClickListener {

    private static final String EXTRA_PHOTOS = "extra_photos";
    private final int REQUEST_PHOTO = 10000;
    private MinePhotoAdapter mAdapter;
    private List<LocalMedia> mPhotoList;
    private List<LocalMedia> mUploadPhotoList;  //上传路径
    private RecyclerView mRvPhoto;
    private DelPicPopWindow mDelPicPopWindow;
    private int mMaxSize = 8;
    private final int mSize = 9;
    private int mType = 0;

    @Override
    public MinePhotoPresenter initPresenter() {
        return new MinePhotoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("我的相册")
                .build();
        mRvPhoto = findViewById(R.id.rv_photo);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRvPhoto.setLayoutManager(layoutManager);
        findViewById(R.id.tv_upload).setOnClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        List<String> mPhotos = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        //添加照片
        mPhotoList = new ArrayList<>();
        mUploadPhotoList = new ArrayList<>();
        if (null != mPhotos && mPhotos.size() > 0) {   //有相册传入
            for (String photo : mPhotos) {
                LocalMedia localMedia = new LocalMedia();
                localMedia.setPath(photo);
                mPhotoList.add(localMedia);
                mUploadPhotoList.add(localMedia);
            }
        } else {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath("ADD");
            mPhotoList.add(localMedia);
            mUploadPhotoList.add(localMedia);
            p.getSelfPhoto(mSize);
        }
        mMaxSize = mSize - mPhotoList.size();
        mAdapter = new MinePhotoAdapter();
        mRvPhoto.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((ad, view, position) -> {
            if (position == 0) {
                if (mMaxSize > 0) {
                    ChoosePicUtils.picSingle(MinePhotoActivity.this, true, new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            selectList = result;
                            if (selectList != null && selectList.size() > 0) {
                                StringBuilder sb = new StringBuilder();
                                p.uploadFile(sb, mUploadPhotoList, selectList, 0);
                            }
                            mMaxSize = mSize - mPhotoList.size();
                            mType = 0;
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                } else {
                    toastTip("最多上传8张照片");
                }
            } else {
                if (null == mDelPicPopWindow)
                    mDelPicPopWindow = new DelPicPopWindow(MinePhotoActivity.this);
                mDelPicPopWindow.setOnClickListener(new DelPicPopWindow.OnClickListener() {
                    @Override
                    public void onDeleteClick() {
                        if (mPhotoList.size() <= position) return;
                        mPhotoList.remove(position);
                        mUploadPhotoList.remove(position);
                        mType = 1;  //更新
                        p.updatePosition(mUploadPhotoList);
                    }
                });
                mDelPicPopWindow.showPopupWindow();
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFrlg = 0;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }
                return makeMovementFlags(dragFrlg, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                try {
                    //得到当拖拽的viewHolder的Position
                    int fromPosition = viewHolder.getAdapterPosition();
                    //拿到当前拖拽到的item的viewHolder
                    int toPosition = target.getAdapterPosition();
                    if (TextUtils.equals(mAdapter.getData().get(fromPosition).getPath(), "ADD")) {
                        return false;
                    }
                    if (TextUtils.equals(mAdapter.getData().get(toPosition).getPath(), "ADD")) {
                        return false;
                    }
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(mPhotoList, i, i + 1);
                            Collections.swap(mUploadPhotoList, i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            if (mPhotoList.size() > i + 1) {
                                Collections.swap(mPhotoList, i, i - 1);
                            }
                            if (mUploadPhotoList.size() > i + 1) {
                                Collections.swap(mUploadPhotoList, i, i - 1);
                            }
                        }
                    }
                    mAdapter.notifyItemMoved(fromPosition, toPosition);
                    mType = 1;  //更新
                    p.updatePosition(mUploadPhotoList);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑删除可以使用；
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            /**
             * 长按选中Item的时候开始调用
             * 长按高亮
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    //获取系统震动服务//震动70毫秒
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(70);
//                    mAdapter.setIsShowDelete(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原高亮
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                try {
                    Utils.runOnUiThread(() -> {
                        mAdapter.notifyDataSetChanged(); //完成拖动后刷新适配器，这样拖动后删除就不会错乱
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        helper.attachToRecyclerView(mRvPhoto);
    }


    List<LocalMedia> selectList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PHOTO) {
            selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                getP().uploadFile(sb, mUploadPhotoList, selectList, 0);
            }
            mMaxSize = mSize - mPhotoList.size();
            mType = 0;
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_mine_photo;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_upload) {
            ChoosePicUtils.picMultiple(MinePhotoActivity.this, mMaxSize, REQUEST_PHOTO);
        }
    }


    @Override
    public void uploadPhotoSuccess(List<LocalMedia> items, List<LocalMedia> photoList, boolean data) {
        if (data) {
            if (mType == 0) {
                mPhotoList.addAll(items);
                toastTip("上传成功");
            } else {
                toastTip("更新成功");
            }
            p.reLoad(mUploadPhotoList, photoList);
            mAdapter.setList(mPhotoList);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        } else {
            if (mType == 0)
                toastTip("上传失败");
            else
                toastTip("更新失败");
        }
        mMaxSize = mSize - mAdapter.getData().size();
    }

    @Override
    public void getPhotoSuccess(List<String> data) {
        if (null != data && data.size() > 0) {
            for (String photo : data) {
                LocalMedia local = new LocalMedia();
                local.setPath(photo);
                mPhotoList.add(local);
                mUploadPhotoList.add(local);
            }
        }
        mAdapter.setList(mPhotoList);
        mMaxSize = mSize - mAdapter.getData().size();
    }
}
