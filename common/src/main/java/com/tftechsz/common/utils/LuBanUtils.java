package com.tftechsz.common.utils;

import android.content.Context;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;

import me.jessyan.autosize.utils.LogUtils;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class LuBanUtils {
    private final static String FILE_COMPRESS = PathUtils.getExternalAppCachePath() + File.separator + "peony" + File.separator + "compress/";

    public interface OnCompress {
        void success(File f);

        void error(String msg);
    }

    /**
     * 尝试用鲁班压缩，压缩失败则返回原图
     *
     * @param context
     * @param file
     * @param l
     */
    public static void compress(Context context, File file, OnCompress l) {
        String name = file.getName();
        if (name.endsWith(".mp4") || name.endsWith(".3gp") || name.endsWith(".avi") || name.endsWith(".mpeg")
                || name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".wma") || name.endsWith(".flac") || name.endsWith(".aac")) {//视频不能执行鲁班压缩
            l.success(file);
            return;
        }
        FileUtils.createOrExistsDir(FILE_COMPRESS);
        Luban.with(context)
                .load(file)
                .ignoreBy(800)
                .setTargetDir(FILE_COMPRESS)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        LogUtils.e("luban  start");
                    }

                    @Override
                    public void onSuccess(File f) {
                        l.success(f);
                        LogUtils.e("luban  onSuccess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("luban  onError:" + e.getMessage());
                        //如果压缩失败 则原图返回
                        l.success(file);
                    }
                }).launch();
    }
}
