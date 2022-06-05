package com.tftechsz.party.widget.pop;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.audio.NERtcCreateAudioMixingOption;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.music.constant.Constant;
import com.tftechsz.common.music.enums.PlayModeEnum;
import com.tftechsz.common.music.listener.OnPlayerEventListener;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.MusicPlayerPopAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.databinding.PopMusicPlayerBinding;
import com.tftechsz.party.entity.AudioIds;
import com.tftechsz.party.entity.MusicListDto;
import com.tftechsz.party.entity.dto.MusicActionDto;
import com.tftechsz.party.mvp.ui.activity.ScanMusicActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.party.mvp.ui.fragment
 * 描 述 : 音乐播放弹窗
 */
public class MusicPlayerPop extends BaseBottomPop implements View.OnClickListener, OnItemClickListener, OnItemLongClickListener, SeekBar.OnSeekBarChangeListener, OnItemChildClickListener, OnRefreshListener {

    private final Activity mActivity;
    private PopMusicPlayerBinding mBind;
    protected CompositeDisposable mCompositeDisposable;
    private List<AudioBean> mScanBeans = new ArrayList<>();
    private MusicPlayerPopAdapter mAdapter;
    private boolean isSmooth;
    private String mQua_url;

    public MusicPlayerPop(Activity context) {
        super(context);
        mActivity = context;
        initUI();
        initBus();
    }

    private void initUI() {
        mCompositeDisposable = new CompositeDisposable();
        mBind.ivAdd.setOnClickListener(this);
        mBind.btnScan.setOnClickListener(this);
        mBind.tips.setOnClickListener(this);
        mBind.ivEdit.setOnClickListener(this);
        mBind.editFinish.setOnClickListener(this);
        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MusicPlayerPopAdapter();
        mAdapter.addChildClickViewIds(R.id.iv_del);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        mBind.recy.setAdapter(mAdapter);
        mBind.musicStatePlay.setOnClickListener(this);
        mBind.musicStatusLoop.setOnClickListener(this);
        mBind.musicStateNext.setOnClickListener(this);
        mBind.musicStatePrev.setOnClickListener(this);
        mBind.musicVolume.setOnClickListener(this);
        mBind.seekBar.setOnSeekBarChangeListener(this);
        mBind.refresh.setOnRefreshListener(this);

        View inflate = View.inflate(mActivity, R.layout.base_empty_view, null);
        ImageView ivEmpty = inflate.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.mipmap.ic_empty2);
        TextView tvEmpty = inflate.findViewById(R.id.tv_empty);
        tvEmpty.setText("暂无歌曲，快去上传自己喜欢的歌曲吧～");
        mAdapter.setEmptyView(inflate);

        initPlayServiceListener();
        initPlayMode();

        getMusicList();
        setOnPopupWindowShowListener(new OnPopupWindowShowListener() {
            @Override
            public void onShowing() {
            }
        });

        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            setEdit(true);
            return false;
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setEdit(false);
            }
        });
    }


    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_music_player);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault()
                .toObservable(CommonEvent.class)
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_PARTY_MUSIC) {
                                getMusicList();
                            }
                        }
                ));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_scan || id == R.id.iv_add) {
            Intent intent = new Intent(mActivity, ScanMusicActivity.class);
            if (mScanBeans != null && !mScanBeans.isEmpty()) {
                intent.putExtra(Interfaces.EXTRA_DATA, (Serializable) mScanBeans);
            }
            mActivity.startActivity(intent);
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//            } else {
//                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mBind.root, "music");
//                mActivity.startActivity(intent, optionsCompat.toBundle());
//            }
        } else if (id == R.id.tips) {
            BaseWebViewActivity.start(mContext, "", mQua_url, 0, 0);
        } else if (id == R.id.iv_edit) {//编辑
            setEdit(true);
        } else if (id == R.id.edit_finish) {//编辑完成
            setEdit(false);
        } else if (id == R.id.music_state_play) {//播放/暂停
            if (BaseMusicHelper.get().getPlayService() != null)
                BaseMusicHelper.get().getPlayService().playPause();
        } else if (id == R.id.music_state_next) {//下一首
            if (BaseMusicHelper.get().getPlayService() != null)
                BaseMusicHelper.get().getPlayService().next(true);
        } else if (id == R.id.music_state_prev) {//上一首
            if (BaseMusicHelper.get().getPlayService() != null)
                BaseMusicHelper.get().getPlayService().prev(true);
        } else if (id == R.id.music_status_loop) {//循环模式
            switchPlayMode();
        } else if (id == R.id.music_volume) {//音量
            //获取系统的Audio管理者
            AudioManager mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
            //最大音量
            //当前音量
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mBind.getIsEdit()) {
            setEdit(false);
            return false;
        }
        return super.onBackPressed();
    }

    private void setEdit(boolean isEdit) {
        mBind.setIsEdit(isEdit);
        mAdapter.setEdit(isEdit);
        mBind.refresh.setEnableRefresh(!isEdit);//编辑中不可以刷新
        helper.attachToRecyclerView(isEdit ? mBind.recy : null);

        mAdapter.setOnItemLongClickListener(isEdit ? null : new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                setEdit(true);
                return false;
            }
        });
    }

    private void switchPlayMode() {
        int playMode = SPUtils.getInstance(Constant.SP_NAME).getInt(Constant.PLAY_MODE, 0);
        PlayModeEnum mode = PlayModeEnum.valueOf(playMode);
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SINGLE;
                Utils.toast("已切换到单曲循环播放");
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                Utils.toast("已切换到顺序播放");
                break;
            default:
                break;
        }
        SPUtils.getInstance(Constant.SP_NAME).put(Constant.PLAY_MODE, mode.value());
        initPlayMode();
    }

    private void initPlayMode() {
        int playMode = SPUtils.getInstance(Constant.SP_NAME).getInt(Constant.PLAY_MODE, 0);
        PlayModeEnum mode = PlayModeEnum.valueOf(playMode);
        switch (mode) {
            case LOOP:
                mBind.musicStatusLoop.setImageResource(R.drawable.music_loop);
                break;
            case SINGLE:
                mBind.musicStatusLoop.setImageResource(R.drawable.music_state_signle);
                break;
        }
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (mBind.getIsEdit()) {//如果在编辑状态, 是不允许点击item播放的
            return;
        }
        if (BaseMusicHelper.get().getPlayService() != null) {
            if (BaseMusicHelper.get().getPlayService().getPlayingPosition() == position) {//如果点击的是当条item
                BaseMusicHelper.get().getPlayService().playPause();
            } else {
                BaseMusicHelper.get().getPlayService().play(position);
            }
        }
    }

    @Override
    public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        return true;
    }


    private final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFrlg = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFrlg, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            try {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
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
                BaseMusicHelper.get().setMusicList(mAdapter.getData());
                BaseMusicHelper.get().getPlayService().updatePlayingPosition();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //侧滑删除可以使用；
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        /**
         * 长按选中Item的时候开始调用
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                //获取系统震动服务//震动70毫秒
                Vibrator vib = (Vibrator) mActivity.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(70);
//                    mAdapter.setIsShowDelete(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原高亮
         */
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            Utils.runOnUiThread(() -> {
                mAdapter.notifyDataSetChanged(); //完成拖动后刷新适配器，这样拖动后删除就不会错乱
            });
            updateSort();

        }
    });


    /**
     * 初始化服务播放音频播放进度监听器
     * 这个是要是通过监听即时更新主页面的底部控制器视图
     * 同时还要同步播放详情页面mPlayFragment的视图
     */
    public void initPlayServiceListener() {
        Utils.logE("切换歌曲: " + "=======");
        if (BaseMusicHelper.get().getPlayService() == null) {
            return;
        }
        BaseMusicHelper.get().getPlayService().setOnPlayEventListener(new OnPlayerEventListener() {
            /**
             * 切换歌曲
             * 主要是切换歌曲的时候需要及时刷新界面信息
             */
            @Override
            public void onChange(int pos, AudioBean music) {
                Utils.logE("切换歌曲: " + music.getTitle());
                // mBind.musicStatePlay.setImageResource(R.drawable.music_state_play);
                mBind.setShowProgress(true);
                mBind.seekBar.setMax((int) music.getDuration());
                mBind.timeMax.setText(Utils.getLastTime1(music.getDuration()));
                mBind.seekBar.setProgress(0);
                mBind.seekBar.setSecondaryProgress(0);
                mBind.timeProgress.setText("00:00");

                if (!BaseMusicHelper.get().getPlayService().hasLocalFile(music.getPath()) && !NetworkUtils.isConnected()) { //如果本地没有缓存并且者网络未连接
                    int playingPosition = BaseMusicHelper.get().getPlayService().getPlayingPosition();
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        AudioBean item = mAdapter.getItem(i);
                        if (i == playingPosition) {
                            item.setPlayPause();
                        } else {
                            item.setPlaying(false);
                        }
                        mAdapter.setData(i, item);
                    }
                    Utils.toast("网络好像出现了一些问题, 请检查后重试哦");
                    mBind.musicStatePlay.setImageResource(R.drawable.music_state_play);
                    return;
                }
                NERtcCreateAudioMixingOption option = new NERtcCreateAudioMixingOption();
                option.path = BaseMusicHelper.get().getPlayService().hasLocalFile(music.getPath()) ? BaseMusicHelper.get().getPlayService().getLocalFile(music.getPath()) : music.getPath(); ////混音文件路径
                option.playbackEnabled = true;    //是否本地播放
                option.playbackVolume = 12;      //本地播放音量
                option.sendEnabled = true;        //是否编码发送
                option.sendVolume = 8;          //发送音量
                option.loopCount = 1;             //循环次数
                int ret = NERtcEx.getInstance().startAudioMixing(option);
                //开始混音任务

                if (ret == NERtcConstants.ErrorCode.OK) {
                    //创建混音任务成功
                } else {
                    //创建混音任务失败

                }

            }
            /**
             * 继续播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerStart() {
                mBind.musicStatePlay.setImageResource(R.drawable.music_state_pause);
                int playingPosition = BaseMusicHelper.get().getPlayService().getPlayingPosition();

                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    AudioBean item = mAdapter.getItem(i);
                    item.setPlaying(i == playingPosition);
                    mAdapter.setData(i, item);
                }
                NERtcEx.getInstance().resumeAudioMixing();
            }

            /**z
             * 暂停播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerPause() {
                mBind.musicStatePlay.setImageResource(R.drawable.music_state_play);
                int playingPosition = BaseMusicHelper.get().getPlayService().getPlayingPosition();
                mAdapter.getItem(playingPosition).setPlayPause();
                LottieAnimationView lottieAnimationView = (LottieAnimationView) mAdapter.getViewByPosition(playingPosition, R.id.lottie_bg);
                if (lottieAnimationView != null) {
                    lottieAnimationView.pauseAnimation();
                }
                NERtcEx.getInstance().pauseAudioMixing();
            }

            /**
             * 更新进度
             * 主要是播放音乐或者拖动进度条时，需要更新进度
             */
            @Override
            public void onUpdateProgress(int progress) {
                mBind.setShowProgress(true);
                if (!isSmooth) { //不在拖动的时候才更新进度
                    mBind.seekBar.setProgress(progress);
                }
                if (TextUtils.isEmpty(Utils.getText(mBind.timeMax))) {//如果max为空, 则重新设置一遍
                    onPlayerStart();
                    long duration = BaseMusicHelper.get().getPlayService().getPlayingMusic().getDuration();
                    mBind.seekBar.setMax((int) duration);
                    mBind.timeMax.setText(Utils.getLastTime1(duration));
                }
                mBind.timeProgress.setText(Utils.getLastTime1(progress));
            }

            /**
             * 缓冲进度
             */
            @Override
            public void onBufferingUpdate(int percent) {
                mBind.seekBar.setSecondaryProgress(percent);
            }

            @Override
            public void onTimer(long remain) {

            }

            //播放错误的回调
            @Override
            public void onError(MediaPlayer mp, int what, int extra) {
                Utils.toast("播放错误,开始尝试下一首");
            }
        });
    }

    /**
     * 拖动中数值的时候
     *
     * @param fromUser 是否是由用户操作的
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    /**
     * 当按下的时候
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSmooth = true;
    }

    /**
     * 当松开的时候
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        BaseMusicHelper.get().getPlayService().seekTo(seekBar.getProgress());
        isSmooth = false;

    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        if (view.getId() == R.id.iv_del) {
            deleteItem(position);
        }
    }

    /**
     * 获取音乐列表
     */
    private void getMusicList() {
        RetrofitManager.getInstance().createUserApi(PartyApiService.class)
                .getMusicList()
                .compose(BasePresenter.applySchedulers())
                .subscribe(new ResponseObserver<BaseResponse<MusicListDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<MusicListDto> data) {
                        if (data == null || data.getData() == null) {
                            mBind.refresh.finishRefresh(false);
                            return;
                        }
                        mBind.refresh.finishRefresh(true);
                        mScanBeans = data.getData().music_list;

                        List<AudioBean> auditSuccessList = getAuditSuccessList();
                        mAdapter.setList(auditSuccessList);
                        mBind.setHasData(auditSuccessList.size() > 0);
                        BaseMusicHelper.get().setMusicList(auditSuccessList);
                        mBind.titleNum.setText(String.format("（共%s首）", auditSuccessList.size()));
                        mQua_url = data.getData().qua_url;
                        mBind.tips.setVisibility(!TextUtils.isEmpty(mQua_url) ? View.VISIBLE : View.GONE);
                        /*刷新后定位当前播放的歌曲位置*/
                        BaseMusicHelper.get().getPlayService().updatePlayingPosition();
                        int playingPosition = BaseMusicHelper.get().getPlayService().getPlayingPosition();
                        if (BaseMusicHelper.get().getPlayService().isPlaying() || BaseMusicHelper.get().getPlayService().isPausing()) {//如果正在播或者已经暂停才需要更新index
                            for (int i = 0; i < mAdapter.getData().size(); i++) {
                                AudioBean item = mAdapter.getItem(i);
                                if (i == playingPosition) {
                                    if (BaseMusicHelper.get().getPlayService().isPlaying()) {
                                        item.setPlaying(true);
                                    } else {
                                        item.setPlayPause();
                                    }
                                    mAdapter.setData(i, item);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mBind.refresh.finishRefresh(false);
                    }
                });
    }

    /**
     * @return 审核成功的列表
     */
    private List<AudioBean> getAuditSuccessList() {
        List<AudioBean> list = new ArrayList<>();
        if (mScanBeans == null) return list;
        for (AudioBean bean : mScanBeans) {
            if (bean.getStatus() == 1) {// 1审核通过 0审核中 2未通过
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 列表排序
     */
    private void updateSort() {
        List<String> ids = new ArrayList<>();
        if (mScanBeans == null) return;
        for (AudioBean bean : mAdapter.getData()) {
            ids.add(bean.getId());
        }
        RetrofitManager.getInstance().createUserApi(PartyApiService.class)
                .sortMusicList(new AudioIds(ids))
                .compose(BasePresenter.applySchedulers())
                .subscribe(new ResponseObserver<BaseResponse<MusicActionDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<MusicActionDto> data) {
                        if (data == null) return;
                        Utils.toast("排序成功");
                    }
                });
    }

    /**
     * 删除其中的列表
     *
     * @param position
     */
    private void deleteItem(int position) {
        if (!ClickUtil.canOperate()) {
            return;
        }
        AudioBean item = mAdapter.getItem(position);
        List<String> ids = new ArrayList<>();
        ids.add(item.getId());
        RetrofitManager.getInstance().createUserApi(PartyApiService.class)
                .deleteMusicList(new AudioIds(ids))
                .compose(BasePresenter.applySchedulers())
                .subscribe(new ResponseObserver<BaseResponse<MusicActionDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<MusicActionDto> data) {
                        if (data == null) return;
                        if (item.isPlaying() || item.isPlayPause()) {//如果需要删除的 与正在播放的一样
                            BaseMusicHelper.get().getPlayService().stop();
                            mBind.setShowProgress(false);
                        }
                        mAdapter.remove(item);
                        mScanBeans.remove(position);
                        BaseMusicHelper.get().setMusicList(mAdapter.getData());
                        BaseMusicHelper.get().getPlayService().updatePlayingPosition();
                        if (mAdapter.getData().size() == 0) {//删除到没有数据了 ,退出编辑模式
                            setEdit(false);
                        }
                        mBind.setHasData(mAdapter.getData().size() > 0);
                        mBind.titleNum.setText(String.format("（共%s首）", mAdapter.getData().size()));
                        Utils.toast("删除成功");
                    }
                });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getMusicList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
