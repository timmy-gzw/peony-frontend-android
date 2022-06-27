package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.netease.nim.uikit.common.util.DownloadHelper;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.databinding.PopSvgaPlayerBinding;
import com.tftechsz.common.entity.SVGAPlayerDto;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : TODO
 */
public class SVGAPlayerPop extends BaseCenterPop {
    private PopSvgaPlayerBinding mBind;
    private Handler safeHandle;
    private boolean mIsDown;
    private final Queue<SVGAPlayerDto> myGiftList = new ConcurrentLinkedQueue<>();
    protected SVGAParser svgaParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private boolean isLoading; //是否需要加载框

    public SVGAPlayerPop(Context context) {
        super(context);
        initUI();
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_svga_player);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        setOutSideDismiss(false);
//        setBackground(R.drawable.bg_trans); //背景透明
        startThread();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                safeHandle.removeCallbacksAndMessages(null);
            }
        });
    }

    public void addSvga(@Nullable String name, String animation) {
        addSvga(name, animation, false);
    }

    /**
     * @param name      svga名字
     * @param animation svga资源
     * @param isLoading 是否需要加载框
     */
    public void addSvga(@Nullable String name, String animation, boolean isLoading) {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(animation)) return;
        this.isLoading = isLoading;
        SVGAPlayerDto dto = new SVGAPlayerDto(animation);
        if (TextUtils.isEmpty(name)) {  //http://public-cdn1.peony125.com/dress/car/飞船.svga
            dto.setName(Utils.getFileName(animation).replace(".svga", "")); //飞船
        } else {
            dto.setName(name);
        }
        myGiftList.offer(dto);
        if (myGiftList.size() == 1) {  //说明是第一次加入队列, 发送handler消息
            safeHandle.sendEmptyMessage(Interfaces.EVENT_MESSAGE);
        }
    }


    private void startThread() {
        if (null == safeHandle) {
            safeHandle = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Interfaces.EVENT_MESSAGE) {
                        try {
                            playGift();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        safeHandle.sendEmptyMessageDelayed(Interfaces.EVENT_MESSAGE, 1000);
                    }
                }
            };
        }
        if (safeHandle.hasMessages(Interfaces.EVENT_MESSAGE)) return;
        safeHandle.sendEmptyMessage(Interfaces.EVENT_MESSAGE);
    }

    private void playGift() {
        if (mIsDown) return;
        if (myGiftList.isEmpty()) return;
        if (mBind.svgaImageView.isAnimating()) {
            return;
        }
        SVGAPlayerDto data = myGiftList.peek();
        if (data == null) {
            return;
        }
        if (null == svgaParser) svgaParser = new SVGAParser(getContext());
        File file = new File(DownloadHelper.FILE_PATH + File.separator + data.getName());
        myGiftList.poll();
        if (file.exists()) {
            loadGift(file);
        } else {
            mIsDown = true;
            if (isLoading) {
                GlobalDialogManager.getInstance().show(getContext().getFragmentManager(), "资源下载中,请稍后...");
            }
            DownloadHelper.downloadGift(data.getAnimation(), new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    loadGift(file);
                }

                @Override
                public void failed() {
                    if (isLoading) {
                        Utils.toast("资源下载失败, 请重试");
                    }
                    mIsDown = false;
                }

                @Override
                public void onProgress(int progress) {
                    mIsDown = true;
                }
            });
        }
        mBind.svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
//                mBind.ivGiftMask.setVisibility(View.GONE);
                mIsDown = false;
                if (myGiftList.size() == 0) {
                    dismiss();
                }
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
    }

    private void loadGift(File file) {
        GlobalDialogManager.getInstance().dismiss();
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    showPopupWindow();
//                    mBind.ivGiftMask.setVisibility(View.VISIBLE);
                    mBind.svgaImageView.setVideoItem(videoItem);
                    mBind.svgaImageView.stepToFrame(0, true);
                }

                @Override
                public void onError() {
                }
            };
        }
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
            svgaParser.setFileDownloader(new SVGAParser.FileDownloader());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBind.svgaImageView.clearAnimation();
        myGiftList.clear();
    }
}
