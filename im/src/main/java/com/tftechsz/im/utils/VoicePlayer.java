package com.tftechsz.im.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import java.util.Observer;


/**
 * 音频播放器封装类.
 * <p>
 * 此类支持设定是否从扬声器发声.
 * <p>
 * ## Bug FIX: 20180806 by Jack Jiang -----------------------------------------------------------
 * 为了解决在某些Android 7.0机型上播放AMR语音时，前几百毫秒无法播放的问题，已经去掉了本类了所有有关场声器设置的代码，
 * 因为Andriod的MediaPlayer类播放音频本身就是默认从扬声器出来，没有必要设置。且设置的话将导致在Android 7.0以上某
 * 些机型上因兼容性问题会导致语音开头播放无音的问题。
 * -------------------------------------------------------------------------------------------
 *
 * @author Jack Jiang(http://www.52im.net/space-uid-1.html)
 * @version 1.0
 * @since 2.3
 */
public class VoicePlayer {
    private final static String TAG = VoicePlayer.class.getSimpleName();

    private MediaPlayer mp = new MediaPlayer();

    private Context context = null;
    private Observer onCompletionObserver = null;
//	/** 是否使用扬声器播放 */
//	private boolean useSpeeker = false;
//	/** 当前音量（保存当前音量用于关闭扬声器时恢复系统音量） */
//	private int currVolume = 0;

//	public void play()
//	{
//		mp.start();
//	}

    /**
     * 构造方法.
     * 默认不使用扬声器发声.
     *
     * @param context
     */
    public VoicePlayer(final Context context, final Observer obsAfterCompletion) {
        this(context, false, obsAfterCompletion);
    }


    public void setAudioStreamType(int mode) {
        mp.setAudioStreamType(mode);
    }

    /**
     * 构造方法.
     *
     * @param context
     * @param _useSpeeker
     */
    public VoicePlayer(final Context context, boolean _useSpeeker, final Observer obsAfterCompletion) {
        this.context = context;
//		this.useSpeeker = _useSpeeker;
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onCompletionObserver != null)
                    onCompletionObserver.update(null, null);

//				if(VoicePlayer.this.useSpeeker)
//					// 关闭扬声器
//					ToolKits.closeSpeaker(context, currVolume);

                if (obsAfterCompletion != null)
                    obsAfterCompletion.update(null, null);
//				// 播放结束提示音
//				PromtHelper.voiceStopedPromt(context);
            }
        });
    }

    /**
     * 播放指定声音文件.
     * <b>注意：</b>本方法强制要求参数filePath不能为null，否则将抛出异常.
     *
     * @param filePath
     * @throws Exception 如果filePath为null或者播放时出现异常则抛出之
     */
    public void play(String filePath) throws Exception {
        if (filePath != null) {
            try {
                // 重置
                mp.reset();

                // 初始化
                mp.setDataSource(filePath);
                mp.prepare();

//				if(useSpeeker)
//				{
//					// 打开扬声器
//					currVolume = ToolKits.openSpeaker(context);
//				}
                // 开始播放
                mp.start();

            } catch (Exception e) {
                Log.w(TAG, "play(String)时出错了！", e);
                throw e;
            }
        } else {
            throw new Exception("filePath不能为null!");
        }
    }


//	/**
//	 * 暂停播放.
//	 *
//	 * @throws Exception
//	 */
//	public void pause() throws Exception
//	{
//		try
//		{
//			mp.pause();
//		}
//		catch (Exception e)
//		{
//			Log.w(TAG, "pause()时出错了！", e);
//			throw e;
//		}
//	}

//	public boolean isUseSpeeker()
//	{
//		return useSpeeker;
//	}
//	public void setUseSpeeker(boolean useSpeeker)
//	{
//		this.useSpeeker = useSpeeker;
//	}

    public void stop() throws Exception {
//		if(useSpeeker)
//			// 关闭扬声器
//			ToolKits.closeSpeaker(context, currVolume);

        try {
            if (mp != null && mp.isPlaying())
                mp.stop();
        } catch (Exception e) {
            Log.w(TAG, "stop()时出错了！", e);
            throw e;
        }
    }

    public void release() {
//		if(useSpeeker)
//			// 关闭扬声器
//			ToolKits.closeSpeaker(context, currVolume);

        try {
            mp.release();
        } catch (Exception e) {
            Log.w(TAG, "release()时出错了！", e);
        }
    }

    public void setOnCompletionObserver(Observer onCompletionObserver) {
        this.onCompletionObserver = onCompletionObserver;
    }
}
