package com.tftechsz.mine.mvp.ui.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.ui.FaceUnityView;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.custom.CameraRenderer;
import com.tftechsz.common.custom.CameraUtils;
import com.tftechsz.common.custom.GlUtil;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.face.LifeCycleSensorManager;
import com.tftechsz.mine.R;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 美颜设置
 */
public class FaceUnitySettingActivity extends BaseMvpActivity implements CameraRenderer.OnRendererStatusListener
        , FURenderer.OnDebugListener, LifeCycleSensorManager.OnAccelerometerChangedListener,
        FURenderer.OnTrackStatusChangedListener, FURenderer.OnSystemErrorListener, CancelAdapt {
    private CameraRenderer mCameraRenderer;
    private FURenderer mFuRenderer;
    private MineService mineService;
    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        mineService = ARouter.getInstance().navigation(MineService.class);
        runOnUiThread(() -> {
            FURenderer.setup(this);
            GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
            glSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
            mCameraRenderer = new CameraRenderer(this, glSurfaceView, this);
            glSurfaceView.setRenderer(mCameraRenderer);
            glSurfaceView.setKeepScreenOn(true);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            FaceUnityView faceUnityView = findViewById(R.id.fu_view);
            int cameraFacing = FURenderer.CAMERA_FACING_FRONT;
            mFuRenderer = new FURenderer.Builder(this)
                    .setInputTextureType(FURenderer.INPUT_TEXTURE_EXTERNAL_OES)
                    .setCameraFacing(cameraFacing)
                    .setInputImageOrientation(CameraUtils.getCameraOrientation(cameraFacing))
                    .setRunBenchmark(true)
                    .setOnDebugListener(this)
                    .setOnTrackStatusChangedListener(this)
                    .setOnSystemErrorListener(this)
                    .build();
            faceUnityView.setModuleManager(mFuRenderer);
            LifeCycleSensorManager lifeCycleSensorManager = new LifeCycleSensorManager(this, getLifecycle());
            lifeCycleSensorManager.setOnAccelerometerChangedListener(this);
        });
        findViewById(R.id.tob_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_face_unity_setting;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraRenderer.onPause();
    }

    @Override
    public void onSurfaceCreated() {
        if (mFuRenderer != null) {
            mFuRenderer.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {

    }

    @Override
    public int onDrawFrame(byte[] nv21Byte, int texId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        if (mFuRenderer != null && texId > 0 && nv21Byte != null && nv21Byte.length > 0) {
            return mFuRenderer.onDrawFrameDualInput(nv21Byte, texId, cameraWidth, cameraHeight);
        }
        return 0;
    }

    @Override
    public void onSurfaceDestroy() {
        if (mFuRenderer != null) {
            mFuRenderer.onSurfaceDestroyed();
        }
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {
        if (mFuRenderer != null) {
            mFuRenderer.onCameraChanged(cameraFacing, cameraOrientation);
            if (mFuRenderer.getMakeupModule() != null) {
                mFuRenderer.getMakeupModule().setIsMakeupFlipPoints(cameraFacing == FURenderer.CAMERA_FACING_BACK ? 1 : 0);
            }
        }
    }

    @Override
    public void onTrackStatusChanged(int type, int status) {

    }

    @Override
    public void onSystemError(int code, String message) {
        mineService.trackEvent("faceUnity", "faceUnityOpen", ""+ code, message, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
            }
        });
    }

    @Override
    public void onFpsChanged(double fps, double callTime) {

    }

    @Override
    public void onAccelerometerChanged(float x, float y, float z) {
        if (Math.abs(x) > 3 || Math.abs(y) > 3) {
            if (Math.abs(x) > Math.abs(y)) {
                mFuRenderer.onDeviceOrientationChanged(x > 0 ? 0 : 180);
            } else {
                mFuRenderer.onDeviceOrientationChanged(y > 0 ? 90 : 270);
            }
        }
    }
}
