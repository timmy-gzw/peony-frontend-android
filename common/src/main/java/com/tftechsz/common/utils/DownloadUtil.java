package com.tftechsz.common.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.tftechsz.common.base.BaseApplication;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class DownloadUtil {

    private String TAG = "DownloadUtil";
    private static DownloadUtil downloadUtil;
    private DownloadManager mDownloadManager;
    private DownloadManager.Request mRequest;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public synchronized void download(final String url, final String saveDir, final String fileName, final OnDownloadListener listener) {
        File saveDirFile;
        if (TextUtils.isEmpty(url))
            return;
        if (!TextUtils.isEmpty(saveDir)) {
            saveDirFile = new File(saveDir);
            if (!saveDirFile.exists() || !saveDirFile.isDirectory()) {
                saveDirFile.mkdirs();
            }
        }
        File file = new File(saveDir, fileName);
        if (file.exists()) {
            file.delete();
        }


        mDownloadManager = (DownloadManager) BaseApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        mRequest = new DownloadManager.Request(Uri.parse(url));

        mRequest.setTitle("peony");
        mRequest.setMimeType("application/vnd.android.package-archive");
        mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();

        //设置文件存放路径
        mRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long id = mDownloadManager.enqueue(mRequest);
        final DownloadManager.Query query = new DownloadManager.Query();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                android.database.Cursor cursor = mDownloadManager.query(query.setFilterById(id));
                if (cursor != null && cursor.moveToFirst()) {
                    if (cursor.getInt(
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/芍药.apk");
                        if (listener != null) {
                            listener.onDownloadSuccess(file);
                        }
                    }
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (listener != null) {
                        listener.onDownloading(bytes_downloaded, bytes_total);
                    }

                } else {
                    if (listener != null) {
                        listener.onDownloadFailed();
                    }
                }
                if (cursor != null)
                    cursor.close();
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
        mTimerTask.run();

    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File file = new File(saveDir);
        if (file.exists()) {
            file.delete();
        }
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */


        void onDownloading(long progress, long total);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

}
