package com.tftechsz.common.music.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * 包 名 : com.tftechsz.common.music.util
 * 描 述 : TODO
 */
public class SDUtils {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableSize() {
        if (isMounted()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获得可用的块的数量
            long count = stat.getAvailableBlocksLong();
            long size = stat.getBlockSizeLong();
            return count * size;
        }
        return 0;
    }


    /**
     * 判断SDCard是否挂载
     * Environment.MEDIA_MOUNTED,表示SDCard已经挂载
     * Environment.getExternalStorageState()，获得当前SDCard的挂载状态
     */
    private static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 获得SDCard 的路径,storage/sdcard
     * @return          路径
     */
    public static String getSDCardPath() {
        String path = null;
        if (isMounted()) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        return path;
    }

}
