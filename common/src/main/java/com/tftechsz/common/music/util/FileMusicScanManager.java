package com.tftechsz.common.music.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tftechsz.common.entity.AudioBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.party.mvp.ui
 * 描 述 : TODO
 */
public class FileMusicScanManager {
    private static final long fileMaxSize = 1024 * 1024 * 20;
    private static final long fileMinTime = 1000 * 10;
    private static final long fileMaxTime = 1000 * 60 * 6;
    private static FileMusicScanManager mInstance;
    private static final Object mLock = new Object();

    public static FileMusicScanManager getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileMusicScanManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * ----------------------------------扫描歌曲------------------------------------------------
     **/

    private static final String SELECTION = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " +
            MediaStore.Audio.AudioColumns.DURATION + " >= ?";


    /**
     * 扫描歌曲
     */
    @NonNull
    public List<AudioBean> scanMusic(Context context, ArrayList<AudioBean> beans) {
        List<AudioBean> musicList = new ArrayList<>();
        String mFilterSize = SPUtils.getInstance().getString("filter_size", "0");
        String mFilterTime = SPUtils.getInstance().getString("filter_time", "0");

        long filterSize = parseLong(mFilterSize) * 1024;
        long filterTime = parseLong(mFilterTime) * 1000;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                },
                SELECTION,
                new String[]{String.valueOf(filterSize), String.valueOf(filterTime)},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor == null) {
            return musicList;
        }

        ArrayList<String> hash = new ArrayList<>();
        for (AudioBean bean : beans) {
            hash.add(bean.getFileHash());
        }
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (!isFly() && isMusic == 0) {
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

            //限制格式
            if (TextUtils.isEmpty(fileName) || !(fileName.endsWith("mp3") || fileName.endsWith("m4a") || fileName.endsWith("wma"))) {
                continue;
            }
            //限制大小 20M
            if (fileSize > fileMaxSize) {
                continue;
            }
            //限制时长  大于6分钟  || 小于10秒
            if (duration > fileMaxTime || duration < fileMinTime) {
                continue;
            }

            AudioBean music = new AudioBean();
            music.setId(id);
            music.setType(AudioBean.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);

            String fileHash = FileUtils.getFileMD5ToString(path).toLowerCase();
            if (hash.contains(fileHash)) {
                AudioBean bean = beans.get(hash.indexOf(fileHash));
                if (bean.getStatus() != 2) {
                    music.setUpload(true);
                    music.setStatus(bean.getStatus());
                }
            }
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }


    private long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private boolean isFly() {
        String flyFlag = getSystemProperty("ro.build.display.id");
        return !TextUtils.isEmpty(flyFlag) && flyFlag.toLowerCase().contains("fly");
    }


    private String getSystemProperty(String key) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String) getMethod.invoke(classType, key);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }


}
