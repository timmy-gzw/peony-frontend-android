package com.tftechsz.mine.utils;

import android.util.Log;

import com.huawei.hms.common.util.Base64Utils;
import com.tftechsz.common.base.BaseApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 追踪缓存处理
 */
public class TrackUtils {

    private static ExecutorService sExecutor;
    private static String TRACK_FILE = "";
    private static String FILE_NAME="track.txt";
    private static volatile TrackUtils trackUtils;

    public static TrackUtils getInstance() {
        if (trackUtils == null) {
            synchronized (TrackUtils.class) {
                if (trackUtils == null) {
                    trackUtils = new TrackUtils();
                    sExecutor = Executors.newSingleThreadScheduledExecutor();
                    TRACK_FILE = BaseApplication.getInstance().getExternalCacheDir()
                            + File.separator + "track" + File.separator;
                }
            }
        }
        return trackUtils;
    }

    /**
     * 缓存追踪内容
     *
     * @param input 写入内容
     * @return
     */
    public boolean cacheTrackEvent(final String input) {
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                BufferedWriter bw = null;
                try {
                    // 构造给定文件名的FileWriter对象，并使用布尔值指示是否追加写入的数据。
                    File file = new File(TRACK_FILE);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    //创建文件，并写入内容
                    FileOutputStream outputStream = new FileOutputStream(TRACK_FILE+FILE_NAME,true);
                    outputStream.write(Base64Utils.encode(input.getBytes(StandardCharsets.UTF_8)).getBytes("UTF-8"));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取缓存追踪内容
     *
     * @return
     */
    public String getTrackCache() {
        Future<String> submit = sExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    String content = "";
                    String path = TRACK_FILE+FILE_NAME;
                    File file = new File(path);
                    InputStream inputStream = new FileInputStream(file);
                    if (inputStream != null) {
                        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            content += line + "\n";
                        }
                        inputStream.close();
                        return content;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 删除缓存
     */
    public void cleanCache() {
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String path = TRACK_FILE+FILE_NAME;
                File file = new File(path);
                if (file.exists()) {
                    return file.delete();
                }
                return false;
            }
        });

    }
}
