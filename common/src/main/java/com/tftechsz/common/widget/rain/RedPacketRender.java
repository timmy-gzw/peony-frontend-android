package com.tftechsz.common.widget.rain;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.view.Surface;
import android.view.TextureView;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.R;
import com.tftechsz.common.nim.ChatSoundPlayer;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 红包雨渲染器
 */
public class RedPacketRender extends Thread implements TextureView.SurfaceTextureListener {

    public interface OnStateChangeListener {
        void onRun();

        void onOpen(int id);

        void onHalt();
    }

    private OnStateChangeListener mOnStateChangeListener;

    private static final String TAG = "RedPacketRender";

    private final Object mLock = new Object();        // guards mSurfaceTexture, mDone
    private SurfaceTexture mSurfaceTexture;
    private volatile boolean mDone;
    private final int mCount;
    private Map<Integer, Bitmap> mBitmapMap = new ConcurrentHashMap<>();

    private int mWidth;     // from SurfaceTexture
    private int mHeight;

    private Resources mResources;

    public RedPacketRender(Resources resources, int count) {
        super("TextureViewCanvas Renderer");
        mResources = resources;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿画笔
        mStandardBitmap = BitmapFactory.decodeResource(mResources, R.mipmap.chat_ic_rain_red);
        mCount = count;
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    @Override
    public void run() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onRun();
        }
        mDone = false;
        while (!mDone) {
            SurfaceTexture surfaceTexture = null;

            // Latch the SurfaceTexture when it becomes available.  We have to wait for
            // the TextureView to create it.
            synchronized (mLock) {
                while (!mDone && (surfaceTexture = mSurfaceTexture) == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ie) {
                        throw new RuntimeException(ie);     // not expected
                    }
                }
                if (mDone) {
                    break;
                }
            }
            LogUtil.e(TAG, "Got surfaceTexture=" + surfaceTexture);

            // Render frames until we're told to stop or the SurfaceTexture is destroyed.
            doAnimation();
        }

        LogUtil.e(TAG, "Renderer thread exiting");
    }

    private final List<RedPacket> mRedPackets = new CopyOnWriteArrayList<>();
    private final Bitmap mStandardBitmap;
    private final Random mRandom = new Random();
    private Paint mPaint;
    private static final int INVISIBLE_Y = 5000; //不可见的y坐标（用于防误判，可以拉回）。
    private static final int SLEEP_TIME = 20; //多少毫秒一帧
    private static final float BOOM_PER_TIME = 2; //爆炸物多少帧刷新一次
    private static int BLOCK_SPEED = 20; //红包每一帧的移动距离

    private void doAnimation() {
        // Create a Surface for the SurfaceTexture.
        Surface surface = null;
        synchronized (mLock) {
            SurfaceTexture surfaceTexture = mSurfaceTexture;
            if (surfaceTexture == null) {
                LogUtil.d(TAG, "ST null on entry");
                return;
            }
            surface = new Surface(surfaceTexture);
        }

        long lastNano = 0;
        mRedPackets.clear();

        BLOCK_SPEED = (int) (SLEEP_TIME * mStandardBitmap.getHeight() / (200 * 1.3f));

        int xLength = mWidth - mStandardBitmap.getWidth();
        int ribbonXLength = mWidth - mStandardBitmap.getWidth() * 35 / 130;

        int centerX = xLength * 16 / 30;
        int leftX = xLength * 7 / 30;
        int rightX = xLength * 5 / 6;

        int visibleY = -mStandardBitmap.getHeight();
        //第一个红包的位置
        int firstY = mStandardBitmap.getHeight() * 7 / 10;
        int yLength = mHeight;
        int boomWidth = mStandardBitmap.getWidth() * 188 / 130;
        int boomHeight = mStandardBitmap.getHeight() * 200 / 150;

        int boomDx = (boomWidth - mStandardBitmap.getWidth()) / 2;
        int boomDy = (boomHeight - mStandardBitmap.getHeight()) / 2;


        int diff = 0;
        int maxDiff = Math.max(0, (xLength - mStandardBitmap.getWidth() * 3) / 6);
        for (int i = 0; i < mCount; i++) {
            RedPacket redPacket;
            if (i >= 3) {
                diff = mRandom.nextInt(maxDiff * 2 + 1) - maxDiff;
            }
            switch (i % 3) {
                //右
                case 1:
                    redPacket = (new RedPacket(rightX + diff, firstY - yLength * i / 10 + diff));
                    break;
                //左
                case 2:
                    redPacket = (new RedPacket(leftX + diff, firstY - yLength * i / 10 + yLength / 9 + diff));
                    break;
                //中间
                default:
                    redPacket = (new RedPacket(centerX + diff, firstY - yLength * i / 10 + diff));
                    break;
            }
            redPacket.setImageRes(R.mipmap.chat_ic_rain_red);
            redPacket.setIndex(i);
            mRedPackets.add(redPacket);
            RedPacket ribbon = new RedPacket((int) (ribbonXLength * mRandom.nextFloat()),
                    firstY - yLength * i / 10 - mRandom.nextInt(100));
            ribbon.setImageRes(R.mipmap.chat_ic_rain_star);
            ribbon.setType(RedPacket.TYPE_RIBBON);
            mRedPackets.add(ribbon);
        }


        while (!mDone) {
            final long startNano = System.nanoTime();
            Canvas canvas = null;
            try {
                canvas = surface.lockCanvas(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (canvas == null) {
                LogUtil.d(TAG, "lockCanvas() failed");
                break;
            }
            try {
                // just curious
                if (canvas.getWidth() != mWidth || canvas.getHeight() != mHeight) {
                    LogUtil.d(TAG, "WEIRD: width/height mismatch");
                }
                // Draw the entire window.  If the dirty rect is set we should actually
                // just be drawing into the area covered by it -- the system lets us draw
                // whatever we want, then overwrites the areas outside the dirty rect with
                // the previous contents.  So we've got a lot of overdraw here.
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                boolean hasShowRedPacket = false;
                long nano = System.nanoTime();
                int dirY;
                if (lastNano == 0) {
                    dirY = BLOCK_SPEED;
                } else {
                    float dirMills = (nano - lastNano) / 1000_000F;
                    dirY = Math.round(dirMills * BLOCK_SPEED / SLEEP_TIME);
                }
                lastNano = nano;
                for (RedPacket redPacket : mRedPackets) {
                    int y = 0;
                    if (redPacket.getType() == RedPacket.TYPE_PACKET || redPacket.getType() == RedPacket.TYPE_RIBBON) {
                        switch (redPacket.getIndex() % 4) {
                            case 1:
                                y = redPacket.nextY(dirY + 1);
                                break;
                            case 2:
                                y = redPacket.nextY(dirY + 2);
                                break;
                            case 3:
                                y = redPacket.nextY(dirY + 3);
                                break;
                            default:
                                y = redPacket.nextY(dirY);
                                break;
                        }

                    } else {
                        y = redPacket.nextY(y);
                    }
                    int x = redPacket.nextX(0);
                    if (redPacket.getType() == RedPacket.TYPE_RIBBON) {
                        y = redPacket.nextY((int) (dirY) - 10);
                    }

                    if (y > visibleY && y < mHeight) {
                        final int typeIndex = redPacket.addTypeIndex(1) - 1;
                        switch (redPacket.getType()) {
                            case RedPacket.TYPE_BOOM:
                                final int boomIndex = (int) (typeIndex / BOOM_PER_TIME);
                                if (boomIndex < RedPacketRes.BOOM_LIST.length) {
                                    redPacket.setImageRes(RedPacketRes.BOOM_LIST[boomIndex]);
                                    if (typeIndex == 0) {
                                        //校准位置
                                        x = redPacket.nextX(-boomDx);
                                        y = redPacket.nextY(-boomDy);
                                    }
                                } else {
                                    redPacket.nextY(INVISIBLE_Y);
                                }
                                break;
                            case RedPacket.TYPE_PACKET_OPEN:
                                if (typeIndex == 0) {
                                    redPacket.setImageRes(RedPacketRes.NO_EMOTION);
                                }
                                if (typeIndex > 600 / SLEEP_TIME) {
                                    redPacket.setType(RedPacket.TYPE_BOOM);
                                }
                                break;

                        }
                        if (redPacket.getType() == RedPacket.TYPE_BOOM) {
                            canvas.drawBitmap(getBitmapFromRes(redPacket.getImageRes()), redPacket.getmX(), redPacket.getmY(), mPaint);
                        } else {
                            canvas.drawBitmap(getBitmapFromRes(redPacket.getImageRes()), x, y, mPaint);
                        }
                        hasShowRedPacket = true;
                    }
                }
                if (!hasShowRedPacket) {
                    mRedPackets.clear();
                    halt();
                }
            } finally {
                try {
                    surface.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException iae) {
                    LogUtil.d(TAG, "unlockCanvasAndPost failed: " + iae.getMessage());
                    break;
                }
            }
            long costNano = System.nanoTime() - startNano;
            long sleepMills = SLEEP_TIME - costNano / 1000_000;
            if (sleepMills > 0) {
                SystemClock.sleep(sleepMills);
            }
        }

        surface.release();
        mRedPackets.clear();
        mBitmapMap.clear();
    }

    private Bitmap getBitmapFromRes(int imageRes) {
        Bitmap bitmap;
        //缓存策略
        if (mBitmapMap.containsKey(imageRes)) {
            bitmap = mBitmapMap.get(imageRes);
        } else {
            bitmap = BitmapFactory.decodeResource(mResources, imageRes);
            mBitmapMap.put(imageRes, bitmap);
        }
        return bitmap;
    }


    /**
     * 红包雨点击事件，返回点击的item.
     *
     * @param x 点击的x坐标
     * @param y 点击的y坐标
     * @return 返回点中的红包position。（若未点中则返回-1）
     */
    public int getClickPosition(int x, int y) {
        if (!mDone && mStandardBitmap != null && mRedPackets.size() > 0) {
            for (RedPacket redPacket : mRedPackets) {
                if (redPacket.isClickable()
                        && redPacket.isInArea(x, y, mStandardBitmap.getWidth(), mStandardBitmap.getHeight())) {
                    redPacket.setType(RedPacket.TYPE_PACKET_OPEN);
                    return redPacket.getIndex();
                }
            }
        }
        return -1;
    }

    /**
     * 炸掉红包
     */
    public void openBoom(int index) {
        RedPacket redPacket = findRedPacket(index);
        if (redPacket != null) {
            redPacket.setType(RedPacket.TYPE_BOOM);
            ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.RAIN_CLICK);
            if (mOnStateChangeListener != null) {
                mOnStateChangeListener.onOpen(index);
            }
        }
    }


    private RedPacket findRedPacket(int index) {
        //0,2,4,6...
        int pos = index * 2;
        RedPacket redPacket = null;
        if (mRedPackets.size() > pos) {
            redPacket = mRedPackets.get(pos);
        }

        if (redPacket == null || redPacket.getIndex() != index) {
            //实在没拿对，那只能遍历了。
            for (RedPacket packet : mRedPackets) {
                if (packet.getIndex() == index) {
                    return packet;
                }
            }
        }
        return redPacket;
    }

    /**
     * Tells the thread to stop running.
     */
    public void halt() {
        synchronized (mLock) {
            mDone = true;
            mLock.notify();
        }
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onHalt();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        LogUtil.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");
        mWidth = width;
        mHeight = height;
        synchronized (mLock) {
            mSurfaceTexture = st;
            mLock.notify();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        LogUtil.d(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");
        mWidth = width;
        mHeight = height;
    }

    @Override   // will be called on UI thread
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        LogUtil.d(TAG, "onSurfaceTextureDestroyed");

        synchronized (mLock) {
            mSurfaceTexture = null;
        }
        return true;
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
        //Log.d(TAG, "onSurfaceTextureUpdated");
    }
}
