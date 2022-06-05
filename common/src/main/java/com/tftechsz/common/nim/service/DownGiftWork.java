package com.tftechsz.common.nim.service;


import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ZipUtils;
import com.tftechsz.common.constant.Interfaces;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.MD5Util;
import com.tftechsz.common.utils.Utils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DownGiftWork extends Worker {

    public DownGiftWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String nobleZip = getInputData().getString(Interfaces.WORKER_NOBLE_SVGA);
        if (!TextUtils.isEmpty(nobleZip)) {
            doWorkZip(nobleZip);
        }

        String bubbleZip = getInputData().getString(Interfaces.WORKER_BUBBLE_PNG);
        if (!TextUtils.isEmpty(bubbleZip)) {
            doWorkZip(bubbleZip);
        }
        return Result.success();
    }

    private void doWorkZip(String data) {
        if (!TextUtils.isEmpty(data)) {
            Utils.logE("doWork地址: " + data);
            String nobleFolder = DownloadHelper.FILE_PATH + File.separator;
            File file = new File(nobleFolder + MD5Util.toMD516(data) + ".zip");
            if (!file.exists()) {
                Utils.logE("开始下载...");
                DownloadHelper.downloadZip(data, new DownloadHelper.DownloadListener() {
                    @Override
                    public void completed() {
                        try {
                            Utils.logE("下载完成: " + file.getPath());
                            ZipUtils.unzipFile(file.getPath(), nobleFolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed() {
                    }

                    @Override
                    public void onProgress(int progress) {
                    }
                });
            } else {
                Utils.logE("文件存在:" + file.getPath());
            }
        }
    }
}
