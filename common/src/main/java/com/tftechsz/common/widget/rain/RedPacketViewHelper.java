package com.tftechsz.common.widget.rain;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.entity.BoxInfo;
import com.tftechsz.common.nim.ChatSoundPlayer;

import java.util.List;

import io.reactivex.annotations.NonNull;


/**
 * 红包雨动画帮助类。
 */

public class RedPacketViewHelper {


    public interface GiftRainListener {
        void startLaunch(); //开始发射

        void openRedPacket(int id);

        void startRain(); //开始红包雨

        void endRain(); //红包雨最后一帧结束
    }


    private Activity mActivity;

    private TextureView mGiftRainView; //红包雨承载控件（为保持扩展性，未对该View进行自定义）。
    private RedPacketRender mRedPacketRender; //红包雨渲染器。
    private boolean mIsGiftRaining; //是否在下红包雨（用于规避同时下多场红包雨）。

    private int mBoxId; //宝箱ID
    private GiftRainListener mGiftRainListener; //红包雨监听器。

    public RedPacketViewHelper(Activity activity) {
        mActivity = activity;
    }


    /**
     * 发射红包雨。
     *
     * @param boxId            这次发射的id
     * @param boxInfoList      红包雨列表
     * @param giftRainListener 红包雨监听器
     * @return 是否成功发射（只管有没有成功发射，不管最终是否顺利执行）。
     */
    public boolean launchGiftRainRocket(int boxId, List<BoxInfo> boxInfoList,
                                        GiftRainListener giftRainListener) {
        if (mIsGiftRaining || boxInfoList.isEmpty()) {
            return false;
        }
        mIsGiftRaining = true;
        mBoxId = boxId;
        mGiftRainListener = giftRainListener;
        mGiftRainListener.startLaunch();
        giftRain(boxInfoList);
        return true;
    }


    private void openBoom(int pos) {
        if (mRedPacketRender == null) {
            return;
        }
        mRedPacketRender.openBoom(pos);
    }

    /**
     * 红包雨
     */
    private void giftRain(@NonNull List<BoxInfo> boxInfoList) {
        mGiftRainView = new TextureView(mActivity);
        mGiftRainView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int pos = mRedPacketRender.getClickPosition((int) event.getRawX(), (int) event.getRawY());
                if (pos >= 0) {
                    openBoom(pos);
                }
            }
            return true;
        });
        mGiftRainView.setOpaque(false); //设置textureview透明，这样底下还可以显示其他组件。
        final ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        viewGroup.addView(mGiftRainView);

        mRedPacketRender = new RedPacketRender(mActivity.getResources(), boxInfoList.size());
        mRedPacketRender.setOnStateChangeListener(new RedPacketRender.OnStateChangeListener() {
            @Override
            public void onRun() {
                if (mGiftRainView == null || mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    mGiftRainView.setVisibility(View.VISIBLE);
                    mGiftRainListener.startRain();
                });
            }

            @Override
            public void onOpen(int id) {
                if (mGiftRainView == null || mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                mGiftRainListener.openRedPacket(id);
            }

            @Override
            public void onHalt() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    mGiftRainListener.endRain();
                    if (mGiftRainView != null) {
                        mGiftRainView.setVisibility(View.GONE);
                        mGiftRainView.setSurfaceTextureListener(null);
//                        mGiftRainView.setOnTouchListener(null);
                        viewGroup.removeView(mGiftRainView);
                        mGiftRainView = null;
                        mRedPacketRender = null;
                        //在所有红包雨的引用断开后，才置为false。
                        mIsGiftRaining = false;
                        LogUtil.e("DQ", "gift rain remove textureView");
                    }
                    ChatSoundPlayer.instance().stop();
                });
            }
        });
        mGiftRainView.setSurfaceTextureListener(mRedPacketRender);
        mRedPacketRender.start();
    }

    /**
     * 结束红包雨.
     */
    public void endGiftRain() {
        if (mRedPacketRender != null) {
            mRedPacketRender.halt();
        }
    }

}
