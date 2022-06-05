package com.tftechsz.im.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

import com.tftechsz.common.utils.log.KLog;

import java.io.File;

public class MediaPlayerHelper {
    private final static String TAG = MediaPlayerHelper.class.getSimpleName();
    private static MediaPlayerHelper instance = null;

    private Context context = null;

    public static MediaPlayerHelper getInstance(Context context) {
        if (instance == null)
            instance = new MediaPlayerHelper(context);
        return instance;
    }

    private MediaPlayerHelper(Context context) {
        this.context = context;
    }

    public void play(int audio_res_id) {
        final MediaPlayer md = createMediaPlayer(audio_res_id);
        if (md != null)
            md.start();
    }

    public MediaPlayer createMediaPlayer(int audio_res_id) {
        try {
            final MediaPlayer md = MediaPlayer.create(context, audio_res_id);
            md.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    md.release();
                }
            });
            return md;
        } catch (Exception e) {
            KLog.e(TAG, e.getMessage() + e);
            return null;
        }
    }

    public void play(File f) {
        final MediaPlayer md = createMediaPlayer(f);
        if (md != null)
            md.start();
    }

    public MediaPlayer createMediaPlayer(File f) {
        try {
            final MediaPlayer md = MediaPlayer.create(context, Uri.fromFile(f));
            final long ss = System.currentTimeMillis();
            md.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    System.out.println("!!OOOOOOOOOOOOOOOOOOOOOO?" + (System.currentTimeMillis() - ss) + "，音频：" + md.getDuration());
                    md.release();
                }
            });
            return md;
        } catch (Exception e) {
            KLog.e(TAG, e.getMessage() + e);
            return null;
        }
    }

    public void play(Uri uri) {
        final MediaPlayer md = createMediaPlayer(uri);
        if (md != null)
            md.start();
    }

    public MediaPlayer createMediaPlayer(Uri uri) {
        try {
            final MediaPlayer md = MediaPlayer.create(context, uri);
            md.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    md.release();
                }
            });
            return md;

        } catch (Exception e) {
            KLog.e(TAG, e.getMessage() + e);
            return null;
        }
    }
}
