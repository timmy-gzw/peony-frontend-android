package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.RealCameraBinding;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : TODO
 */
@Route(path = ARouterApi.ACTIVITY_REAL_CAMERA)
public class RealCameraActivity extends BaseMvpActivity {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    @Autowired
    UserProviderService service;

    //private SurfaceHolder mSurfaceHolder;
    private Handler childHandler;
    private String mCameraID;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;//摄像头设备
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;

    private RealCameraBinding mBind;
    public String FRONT_CAMERA;
    public String BACK_CAMERA;
    private File mFile;
    private int mCameraType; //0:人脸   1:身份证正面   2:反面
    private String[] mCameraIdList;

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.real_camera);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).transparentBar().statusBarDarkFont(false).init();
        mCameraType = getIntent().getIntExtra(Interfaces.EXTRA_TYPE, 0);
        mBind.setShowHintTip(mCameraType == 0 && service.getUserInfo().isGirl());
        mBind.setShowHintBg(mCameraType != 0 || service.getUserInfo().isGirl());
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBind.cancel.getLayoutParams();
        lp.setMargins(0, ImmersionBar.getStatusBarHeight(mActivity), 0, 0);
        mBind.cancel.setLayoutParams(lp);

        if (mCameraType == 0) {
            mBind.ivMidBack.setImageResource(R.drawable.peony_zrrz_frame_img);
        } else if (mCameraType == 1) {
            mBind.ivMidBack.setImageResource(R.mipmap.peony_sfzpz_rsm_png);
        } else if (mCameraType == 2) {
            mBind.ivMidBack.setImageResource(R.mipmap.peony_sfzpz_ghm_png);
        }

        mBind.surfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                setupCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });

        /*mSurfaceHolder = mBind.surfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        // mSurfaceView添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
                // 初始化Camera
                //获取摄像头权限
                initCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                // 释放Camera资源
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
            }
        });*/

        mBind.btnControl.setOnClickListener(v -> {
            if (!ClickUtil.canOperate()) return;
            takePicture();
        });
        mBind.reView.setOnClickListener(v -> {
            if (!ClickUtil.canOperate()) return;
            takePreview();
        });
        mBind.cancel.setOnClickListener(v -> finish());
        mBind.send.setOnClickListener(v -> {
            if (mFile != null) {
                Intent intent = new Intent();
                intent.putExtra(Interfaces.EXTRA_PATH, mFile.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    private void setupCamera() {
        mBind.btnControl.setClickable(true);
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        Handler mainHandler = new Handler(getMainLooper());

        if (mCameraType == 0) {
            ORIENTATIONS.append(Surface.ROTATION_0, 90);
            ORIENTATIONS.append(Surface.ROTATION_90, 0);
            ORIENTATIONS.append(Surface.ROTATION_180, 270);
            ORIENTATIONS.append(Surface.ROTATION_270, 180);
        } else {
            ORIENTATIONS.append(Surface.ROTATION_0, 0);
            ORIENTATIONS.append(Surface.ROTATION_90, 90);
            ORIENTATIONS.append(Surface.ROTATION_180, 180);
            ORIENTATIONS.append(Surface.ROTATION_270, 270);
        }
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraIdList = cameraManager.getCameraIdList();
            for (String s : mCameraIdList) {
                if (s.equals(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT))) {
                    BACK_CAMERA = s;
                    continue;
                }
                if (s.equals(String.valueOf(CameraCharacteristics.LENS_FACING_BACK))) {
                    FRONT_CAMERA = s;
                }
            }
            mCameraID = mCameraType == 0 ? FRONT_CAMERA : BACK_CAMERA;//后摄像头
            Utils.logE(JSON.toJSONString(mCameraIdList) + "  BACK_CAMERA= " + BACK_CAMERA + "    FRONT_CAMERA= " + FRONT_CAMERA + "   mCameraID:" + mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        CameraCharacteristics characteristics;
        try {
            characteristics = cameraManager.getCameraCharacteristics(mCameraID);
            //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] outputSizes = map.getOutputSizes(ImageFormat.JPEG);
            mPreviewSize = Collections.max(Arrays.asList(outputSizes),
                    (lhs, rhs) -> Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getHeight() * rhs.getWidth()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.logE("getWidth= " + mPreviewSize.getWidth() + " --  getHeight= " + mPreviewSize.getHeight());
        if (mPreviewSize == null) {
//        mImageReader = ImageReader.newInstance(mBind.surfaceView.getWidth(), mBind.surfaceView.getHeight(), ImageFormat.JPEG, 1);
            mImageReader = ImageReader.newInstance(ScreenUtils.getScreenWidth(), (int) (ScreenUtils.getScreenWidth() / 1.33), ImageFormat.JPEG, 1);
        } else {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBind.surfaceView.getLayoutParams();
            lp.width = -1;
            lp.height = (int) (ScreenUtils.getScreenWidth() * (mPreviewSize.getWidth() * 1f / mPreviewSize.getHeight()));
            mBind.surfaceView.setLayoutParams(lp);
            mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 1);
        }
        //可以在这里处理拍照得到的临时照片 例如，写入本地
        mImageReader.setOnImageAvailableListener(reader -> {
//                mCameraDevice.close();
//                mSurfaceView.setVisibility(View.INVISIBLE);
            // 拿到拍照照片数据

            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);//由缓冲区存入字节数组
            //保存

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

            if (bitmap != null) {
                if (mCameraID.equals(FRONT_CAMERA)) {
                    bitmap = Utils.convert(bitmap);
                    //前置摄像头拍的要先旋转180度
                    if (RomUtils.isXiaomi()) {
                        bitmap = adjustPhotoRotation(bitmap, 90);
                    } else {
                        bitmap = adjustPhotoRotation(bitmap, 180);
                    }
                } else {
                    //bitmap = adjustPhotoRotation(bitmap, -90);
                }
                mBind.send.setVisibility(View.VISIBLE);
                mBind.pb.setVisibility(View.GONE);
                mBind.rlBtn.setBackgroundResource(R.drawable.sp_primary_r_25);
                writeToFile(bitmap);
            }
            image.close();
        }, mainHandler);
        //获取摄像头管理
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开摄像头
            cameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        } catch (OutOfMemoryError ignored) {
        }
        return null;
    }

    private void writeToFile(Bitmap bitmap) {
        String file = getCacheDir() + File.separator + "real_" + System.currentTimeMillis() + ".jpeg";
        FileUtils.createOrExistsFile(file);
        mFile = new File(file);
        ImageUtils.save(bitmap, mFile, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 开始预览
     */
    private Size mPreviewSize;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview() {
        try {
            mBind.setIsPreview(true);
            mBind.rlBtn.setBackgroundResource(R.drawable.bg_orange_noenable);
            // 创建预览需要的CaptureRequest.Builder
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            SurfaceTexture surfaceTexture = mBind.surfaceView.getSurfaceTexture();
            if (mPreviewSize == null) {
                surfaceTexture.setDefaultBufferSize(ScreenUtils.getScreenWidth(), (int) (ScreenUtils.getScreenWidth() / 1.33));
            } else {
                surfaceTexture.setDefaultBufferSize(
                        (int) (ScreenUtils.getScreenWidth() * (mPreviewSize.getWidth() * 1f / mPreviewSize.getHeight()))
                        ,
                        ScreenUtils.getScreenWidth()
                );
            }
            //获取Surface显示预览数据
            Surface mSurface = new Surface(surfaceTexture);

            mPreviewRequestBuilder.addTarget(mSurface);
            // 创建CameraCaptureSession，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，
            // 当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        //mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.FLASH_MODE_OFF);
                        // 显示预览
                        CaptureRequest previewRequest = mPreviewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    toastTip("配置失败");
                    finish();
                }
            }, childHandler);
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 摄像头创建监听
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            takePreview();
          /*  toastTip("摄像头开启失败");
            StringBuilder sb = new StringBuilder();
            sb.append("摄像头list= ").append(JSON.toJSONString(mCameraIdList))
                    .append("；cameraId= ").append(camera.getId())
                    .append("；errorCode= ").append(111);
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .buriedPoint("摄像头开启失败", "click", "camera_open_fail", JSON.toJSONString(new BuriedPointExtendDto(sb.toString())), null);
            finish();*/
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice.close();
                RealCameraActivity.this.mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            toastTip("摄像头开启失败");
            StringBuilder sb = new StringBuilder();
            sb.append("摄像头list= ").append(JSON.toJSONString(mCameraIdList))
                    .append("；cameraId= ").append(camera.getId())
                    .append("；errorCode= ").append(error);
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("摄像头开启失败", "click", "camera_open_fail", JSON.toJSONString(new BuriedPointExtendDto(sb.toString())), null);
            finish();
        }
    };

    /**
     * 拍照
     */
    @SuppressLint("NewApi")
    private void takePicture() {
        if (mCameraDevice == null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            mBind.setIsPreview(false);
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);
            mCameraCaptureSession.close();
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraDevice != null)
            mCameraDevice.close();
        if (mCameraCaptureSession != null)
            mCameraCaptureSession.close();
    }
}
