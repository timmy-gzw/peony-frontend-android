package com.tftechsz.common.utils;

import android.os.Environment;

import java.io.File;

public class UpdateManager {

    public static UpdateManager instance;
    // 创建项目目录文件夹
    private String path = Environment.getExternalStorageDirectory().getPath() + "/peony/Download";
    // 手机自带下载目录文件夹
    private File downFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public UpdateManager() {
    }

    public static synchronized UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    public void downLoadApp(String url, final DownLoadAppProcessListener listener) {
        DownloadUtil.get().download(url, downFile.getPath(), "peony.apk", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (listener != null) {
                    listener.onSuccess(file);
                }
            }

            @Override
            public void onDownloading(long progress,long total) {
                if (listener != null) {
                    listener.onLoading(progress,total);
                }
            }

            @Override
            public void onDownloadFailed() {
                if (listener != null) {
                    listener.onFailed();
                }
            }
        });
    }

    public interface DownLoadAppProcessListener {
        void onSuccess(File file);

        void onLoading(long progress, long total);

        void onFailed();
    }

}
