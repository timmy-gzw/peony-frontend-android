package com.tftechsz.common.widget.pop;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.blankj.utilcode.util.FileUtils;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.databinding.PopNobilityLevelUpBinding;
import com.tftechsz.common.entity.NobilityLevelUpPopDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 贵族升级弹窗 - 组合svga动画  新
 */
public class NobilityLevelUpPop extends BaseCenterPop {

    private PopNobilityLevelUpBinding mBind;
    private NobilityLevelUpPopDto dto;
    private SVGAParser svgaParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private String mSvg2;

    public NobilityLevelUpPop() {
        super(Utils.getApp());
        setOutSideDismiss(false);
        init();
    }


    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_nobility_level_up);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    public void setDto(NobilityLevelUpPopDto dto) {
        this.dto = dto;
        GlideUtils.loadRouteImage(BaseApplication.getInstance(), mBind.bgPic, dto.bg_pic, R.drawable.bg_trans);
        GlideUtils.loadRouteImage(BaseApplication.getInstance(), mBind.topPic, dto.top_pic, R.drawable.bg_trans);
        GlideUtils.loadRouteImage(getContext(), mBind.botPic, dto.bottom_pic, R.drawable.bg_trans);
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(5 * 1000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        mBind.bgPic.setAnimation(rotate);

        String svg1 = DownloadHelper.FILE_PATH + File.separator + Utils.getFileName(dto.svg1).replace(".svga", "");
        mSvg2 = DownloadHelper.FILE_PATH + File.separator + Utils.getFileName(dto.svg2).replace(".svga", "");
        if (FileUtils.isFileExists(svg1) && FileUtils.isFileExists(mSvg2)) { //文件存在, 播放
            mBind.svgImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBind.svgImage.setLoops(-1);
                    playAnimation(new File(mSvg2));
                }
            }, 1650);
            playAnimation(new File(svg1));
        } else {
            ArrayList<String> urls = new ArrayList<>();
            if (!FileUtils.isFileExists(svg1)) {
                urls.add(dto.svg1);
            }
            if (!FileUtils.isFileExists(mSvg2)) {
                urls.add(dto.svg2);
            }
            DownloadHelper.download(urls, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    if (FileUtils.isFileExists(svg1) && FileUtils.isFileExists(mSvg2)) {
                        mBind.svgImage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBind.svgImage.setLoops(-1);
                                playAnimation(new File(mSvg2));
                            }
                        }, 1650);
                        playAnimation(new File(svg1));
                    }
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

    private void playAnimation(File file) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void init() {
        mBind.del.setOnClickListener(v -> dismiss());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mBind.svgImage.stopAnimation();
            }
        });

        if (null == svgaParser)
            svgaParser = new SVGAParser(getContext());
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    mBind.svgImage.setVideoItem(videoItem);
                    mBind.svgImage.stepToFrame(0, true);
                    showPopupWindow();
                }

                @Override
                public void onError() {
                }
            };
        }
        mBind.svgImage.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onRepeat() {
            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
    }

}
