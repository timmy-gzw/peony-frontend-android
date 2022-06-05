package com.tftechsz.common.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.netease.nim.uikit.common.util.DownloadHelper;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.base.BaseApplication;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SvgaUtils {
    private Context context;
    private ArrayList<String> stringList;
    private SVGAImageView svgaImage;
    private SVGAParser parser;

    public SvgaUtils(Context context, SVGAImageView svgaImage) {
        this.context = context;
        this.svgaImage = svgaImage;
        svgaImage.setLoops(1);
    }

    public void repeat() {
        svgaImage.setLoops(0);
    }

    /**
     * 初始化数据
     */
    public void initAnimator() {
        initAnimator(null);
    }

    public void initAnimator(SVGACallback callback) {
        parser = new SVGAParser(context);
        stringList = new ArrayList<>();
        //监听大动画的控件周期

        svgaImage.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                if (callback != null) {
                    callback.onPause();
                }
                Log.e("setCallback", "onPause");
            }

            @Override
            public void onFinished() {
                if (callback != null) {
                    callback.onFinished();
                }
                //当动画结束，如果数组容器大于0，则移除容器第一位的数据，轮询播放动画。
                if (stringList != null && stringList.size() > 0) {
                    stringList.remove(0);
                    //如果移除之后的容器大于0，则开始展示新一个的大动画
                    if (stringList != null && stringList.size() > 0) {
                        try {
                            parseSVGA();//解析加载动画
                        } catch (Exception e) {

                        }
                    } else {
                        stopSVGA();
                    }
                } else {
                    stopSVGA();
                }
            }

            @Override
            public void onRepeat() {
                if (callback != null) {
                    callback.onRepeat();
                }
                Log.e("setCallback", "onRepeat=" + stringList.size());
                stopSVGA();
            }

            @Override
            public void onStep(int i, double v) {
                if (callback != null) {
                    callback.onStep(i, v);
                }
            }
        });
    }

    private SVGAParser.ParseCompletion mParseCompletionCallback;

    public void getFile(String fileName, String url) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tfpeony/" + fileName);
        if (file.exists()) {
            sendPlay(file);
        } else {
            List<String> source = new ArrayList<>();
            source.add(url);
            DownloadHelper.download(source, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    if (file.exists())
                        sendPlay(file);
                }

                @Override
                public void failed() {

                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    private void sendPlay(File file) {
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(SVGAVideoEntity svgaVideoEntity) {

                    playSVGA(svgaVideoEntity);
                }

                @Override
                public void onError() {

                }
            };
        }
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            parser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void playSVGA(SVGAVideoEntity svgaVideoEntity) {
        if (svgaImage != null) {

            svgaImage.setVideoItem(svgaVideoEntity);
            svgaImage.startAnimation();

        }
    }

    /**
     * 显示动画
     */
    public void startAnimator(String svgaName) {
        stringList.add(stringList.size(), svgaName + ".svga");
        //如果礼物容器列表的数量是1，则解析动画，如果数量不是1，则此处不解析动画，在上一个礼物解析完成之后加载再动画
        if (stringList.size() == 1) {
            parseSVGA();
        }
    }

    /**
     * 显示动画
     */
    public void startAnimatorUrl(String svgaName) {
        stringList.add(stringList.size(), svgaName);
        //如果礼物容器列表的数量是1，则解析动画，如果数量不是1，则此处不解析动画，在上一个礼物解析完成之后加载再动画
        if (stringList.size() == 1) {
            parseSVGA();
        }
    }

    /**
     * 停止动画
     */
    private void stopSVGA() {
        if (svgaImage.isAnimating() && stringList.size() == 0) {
            svgaImage.stopAnimation();
        }
    }

    /**
     * 解析加载动画
     */
    private void parseSVGA() {
        if (stringList.size() > 0) {
            try {

                parser.parse(stringList.get(0), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                        //解析动画成功，到这里才真正的显示动画
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                        svgaImage.setImageDrawable(drawable);
                        svgaImage.startAnimation();
                    }

                    @Override
                    public void onError() {
                        //如果动画数组列表大于0,移除第一位的动画,继续循环解析
                        if (stringList.size() > 0) {
                            stringList.remove(0);
                            parseSVGA();
                        } else {
                            stopSVGA();
                        }
                    }
                });
            } catch (Exception e) {
            }
        } else {
            stopSVGA();
        }
    }


    /**
     * 播放svga动画
     */
    public static void playAirdrop(SVGAParser svgaParser, SVGAImageView svgaAirDrop, String name) {
        if (svgaParser == null)
            svgaParser = new SVGAParser(BaseApplication.getInstance());
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {
            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaAirDrop != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaAirDrop.setImageDrawable(drawable);
                    svgaAirDrop.startAnimation();
                }
            }
        }, null);
    }
}
