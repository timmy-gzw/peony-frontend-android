package com.tftechsz.im.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.tftechsz.im.R;
import com.tftechsz.im.model.VoiceInfoBean;
import com.tftechsz.im.widget.AudioPrintView;
import com.tftechsz.common.utils.FileUtils;
import com.tftechsz.common.utils.log.KLog;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * 描 述：语音播放器实现类.
 */
public class VoicePlayHelper {
    private static final String TAG = VoicePlayHelper.class.getSimpleName();

    private VoicePlayListener voicePlayListener;
    private VoicePlayer mVoicePlayer = null;
    private Context mContext = null;
    /**
     * 所播放语音对应的动画控件
     */
    private AudioPrintView mAudioPrintView;
    /**
     * 是否是发出的语音
     */
    private boolean mIsTo;

    protected String mFromId;

    protected VoiceInfoBean mVoiceInfoBean;

    public void setAudioStreamType(int mode) {
        mVoicePlayer.setAudioStreamType(mode);
        if (AudioManager.STREAM_VOICE_CALL == mode && isEntityVoicePlaying(mVoiceInfoBean)) {//进入听筒模式并有处于播放状态的语音则重播
            rePlayVoice();
        } else if (AudioManager.MODE_NORMAL == mode && isEntityVoicePlaying(mVoiceInfoBean)) {
            if (mAudioPrintView != null) {
                mAudioPrintView.start(mIsTo);
            }
        }
    }

    /**
     * 群聊为群id,单聊为好友uId
     */
//    private String mId;
    public VoicePlayHelper(final Context context, String fromId) {
        mContext = context;
        this.mFromId = fromId;
        mVoicePlayer = new VoicePlayerEx(context, true, new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                // 播放结束提示音
//            PromtHelper.voiceStopedPromt(context);//todo 滴一声在播放结束回调的这个地方
                onPlayFinish();
            }
        });
        // 播放完成时就清除“正在播放”记录
        mVoicePlayer.setOnCompletionObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                clearPlayingStatus();
                if (mAudioPrintView != null && mAudioPrintView.isStart()) {
                    mAudioPrintView.stop();
                }
            }
        });
    }

    public String getFromId() {
        return mFromId;
    }

    public void setFromId(String fromId) {
        mFromId = fromId;
    }

    public void release() {
        // 执行音频的release，释放资源
        mVoicePlayer.release();
    }

    /**
     * 当前列表单元（语音留言消息）是否处于“播放中”.
     *
     * @param voiceInfoBean
     * @return true表示播放中，否则处于普通状态
     */
    public boolean isEntityVoicePlaying(VoiceInfoBean voiceInfoBean) {
        return mVoiceInfoBean != null && mVoiceInfoBean == voiceInfoBean;
    }

    public void setAudioPrintView(AudioPrintView audioPrintView, boolean isTo) {
        if (mAudioPrintView != null && mAudioPrintView.isStart()) {
            mAudioPrintView.stop();
        }
        mAudioPrintView = audioPrintView;
        mIsTo = isTo;
    }

    /**
     * 清空播放状态
     */
    public void clearPlayingStatus() {
        mVoiceInfoBean = null;
        mFromId = null;
    }

    public void rePlayVoice() {
        if (mVoiceInfoBean == null) {
            return;
        }
        playVoice(mVoiceInfoBean.getFileName(), mVoiceInfoBean.getSendId());
    }

    public boolean playByVoiceInfo(VoiceInfoBean voiceInfoBean) {
        if (mVoiceInfoBean != null) {
            return false;
        }
        if (voiceInfoBean == null) {
            return false;
        }
        this.mVoiceInfoBean = voiceInfoBean;
        return playVoice(voiceInfoBean.getFileName(), voiceInfoBean.getSendId());
    }

    /**
     * 播放语音留言文件.
     *
     * @param voiceFileName 语音文件名称
     * @param fromId        用户id还是群聊id
     * @return true 表示正常进入播放过程，否则表示没有成功（调用者将可以据此来即时恢复”播放中“状态等）
     */
    @SuppressLint("StaticFieldLeak")
    protected boolean playVoice(final String voiceFileName, String fromId) {

        boolean sucess = false;
        try {
            // 语音留言消息中的此字段存放的就是语音文件的信息json
            if (TextUtils.isEmpty(voiceFileName)) {
                return false;
            }

            String voiceFilePath = FileUtils.getVoicePath(fromId, voiceFileName);
            File voiceFile = new File(voiceFilePath);
            if (voiceFile.exists()) {
                try {
                    mVoicePlayer.play(voiceFileName);
                    if (mAudioPrintView != null) {
                        mAudioPrintView.start(mIsTo);
                    }
                    sucess = true;
                } catch (Exception e) {
                    KLog.e(TAG, mContext.getString(R.string.chat_playvoice_play_faild));
                    Toast.makeText(mContext, mContext.getString(R.string.chat_playvoice_play_faild), Toast.LENGTH_SHORT).show();
                }
            } else {
                loadDownVoiceAndPlay(mVoiceInfoBean.getFileUrl(), FileUtils.getVoiceDir(fromId));
                sucess = true;
//                if (fromId.equals(BaseApp.getInstance().getUserId())) {
//                    //属于发出的语音留言,不存在则为主动删除，目前不再下载，而只是提醒
//                    LogUtil.e(TAG, mContext.getString(R.string.chat_playvoice_play_faild2));
//                    TipHelper.showTip(R.string.chat_playvoice_play_faild2);
//                    if (mAudioPrintView != null && mAudioPrintView.isStart()) {
//                        mAudioPrintView.stop();
//                    }
//                    return sucess;
//                } else {//收到消息本地不存在则尝试网路下载
//                    loadDownVoiceAndPlay(FileUtils.getVoiceDir(fromId), voiceFileName);
//                }
            }

        } catch (Exception e) {
            KLog.e(TAG, "此消息不符合语音消息格式！");
        }
        return sucess;
    }

    @SuppressLint("StaticFieldLeak")
    public void loadDownVoiceAndPlay(final String fileUrl, final String dir) {
        new HttpFileDownloadHelper.DownloadAsyncRoot(mContext, fileUrl, dir) {//目前不启用转存
            @Override
            protected void onPreExecute() {
//                entity.getDownloadStatus().setStatus(ChatMsgEntity.DownloadStatus.PROCESSING);
                // 状态的改变同时也通知ui刷新显示
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
//                entity.getDownloadStatus().setProgress(progress[0]);
                // 状态的改变同时也通知ui刷新显示
            }

            @Override
            protected void onPostExecute_onSucess(String fileSavedPath) {
                try {// 成功下载了语音文件（则播放之）
                    if (null != voicePlayListener) {
                        voicePlayListener.onPlaying();
                    }
                    mVoicePlayer.play(fileSavedPath);

                    // 设置“播放中”状态（不同于playVoice中的同类方法，因为本代码在内部类中，无法由调用者来决定清空播放状态，所以要自已来干哦）
                    if (mAudioPrintView != null) {
                        mAudioPrintView.start(mIsTo);
                    }
//                    entity.getDownloadStatus().setStatus(ChatMsgEntity.DownloadStatus.PROCESS_OK);
                    // 状态的改变同时也通知ui刷新显示
                } catch (Exception e) {
                    if (null != voicePlayListener) {
                        voicePlayListener.onError();
                    }
                    Toast.makeText(mContext, R.string.chat_playvoice_play_faild3, Toast.LENGTH_SHORT).show();
                    // 播放失败则清空播放状态（不同于playVoice中的同类方法，因为本代码在
                    // 内部类中，无法由调用者来决定清空播放状态，所以要自已来干哦）
                    clearPlayingStatus();
                    if (mAudioPrintView != null && mAudioPrintView.isStart()) {
                        mAudioPrintView.stop();
                    }

                }
            }

            @Override
            protected void onPostExecute_onException(Exception exception) {
                if (null != voicePlayListener) {
                    voicePlayListener.onError();
                }
                KLog.e(TAG, mContext.getString(R.string.chat_playvoice_play_faild4));
//                Toast.makeText(mContext, mContext.getString(R.string.chat_playvoice_play_faild4), Toast.LENGTH_SHORT).show();
                // 播放失败则清空播放状态（不同于playVoice中的同类方法，因为本代码在
                // 内部类中，无法由调用者来决定清空播放状态，所以要自已来干哦）
                clearPlayingStatus();
                if (mAudioPrintView != null && mAudioPrintView.isStart()) {
                    mAudioPrintView.stop();
                }
                loadDownVoiceAndPlay(fileUrl, dir);
            }

        }.execute();


    }

    /**
     * 停止语音播放
     */
    public void stopVoice() {
        this.clearPlayingStatus();
        if (mAudioPrintView != null && mAudioPrintView.isStart()) {
            mAudioPrintView.stop();
        }
        try {
            if(mVoicePlayer!=null)
                mVoicePlayer.stop();
        } catch (Exception e) {
//            Log.w(TAG, e.getMessage());
        }
    }

    /**
     * 播放完毕回调
     */
    private void onPlayFinish() {
        if (voicePlayListener != null) {
            voicePlayListener.onCompletion();
        }
    }

    /**
     * 设置录音播放监听
     *
     * @param voicePlayListener
     */
    public void setVoicePlayListener(VoicePlayListener voicePlayListener) {
        this.voicePlayListener = voicePlayListener;
    }

    /**
     * 录音播放回调的接口定义
     */
    public interface VoicePlayListener {

        /**
         * 录音播放完毕时调用
         */
        void onCompletion();

        void onPlaying();

        void onError();
    }
}
