package com.tftechsz.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.UpdateInfo;
import com.tftechsz.common.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.RequiresApi;
import zlc.season.rxdownload4.utils.HttpUtilKt;

/**
 * 更新dialog
 */
public class UpdateDialog extends Dialog {
    private UpdateInfo updateInfo;
    private ImageView mIvProgress;
    private FrameLayout mFlProgress;
    private ProgressBar mPbProgress;
    private OnSureClick mSureClick;
    private TextView mTvContent, mTvSure, mTvVersion;
    private ImageView mIvClose;
    private File mApkFile;
    private Activity mContext;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private boolean isDownSuccess = false;
    private int progress;// 当前进度
    private boolean interceptFlag = false;// 用户取消下载
    private long currentTime;
    private final int REQUEST_CODE_APP_INSTALL = 10001;
    private String downloadApkDir;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    isDownSuccess = false;
                    mIvClose.setVisibility(View.INVISIBLE);
                    mFlProgress.setVisibility(View.VISIBLE);
                    mTvSure.setVisibility(View.GONE);
                    mTvSure.setText("立即体验");
                    int imageStart = 0;
                    if (mPbProgress.getProgress() > 0) {
                        int right = (int) ((mPbProgress.getWidth() - mPbProgress.getPaddingLeft() - mPbProgress.getPaddingRight()) / (mPbProgress.getMax() * 1.0f) * mPbProgress.getProgress() - DensityUtils.dp2px(mContext, 3) + mPbProgress.getPaddingLeft());
                        imageStart = (right - DensityUtils.dp2px(mContext, 3));
                    }
                    if ((imageStart + mIvProgress.getWidth()) >= mPbProgress.getWidth() - mPbProgress.getPaddingRight()) {
                        imageStart = mPbProgress.getWidth() - mPbProgress.getPaddingRight() - mIvProgress.getWidth();
                    }
                    mIvProgress.setX(imageStart);
//                    mIvProgress.setX((progress * (mPbProgress.getWidth() / 100f)) - DensityUtils.dp2px(mContext, 45));
                    mPbProgress.setProgress(progress);
                    if (mContext == null || mContext.isFinishing() || mContext.isDestroyed()) {
                        return;
                    }
                    break;

                case DOWN_OVER:
                    isDownSuccess = true;
                    mFlProgress.setVisibility(View.INVISIBLE);
                    mTvSure.setVisibility(View.VISIBLE);
                    mTvSure.setText("立即安装");
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = isHasInstallPermissionWithO(mContext);
                        if (!hasInstallPermission) {
                            startInstallPermissionSettingActivity(mContext);
                            return;
                        } else {
                            installApk();
                        }
                    } else {*/

                    //}
                    installApk();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public UpdateDialog(Activity context, UpdateInfo updateInfo) {
        super(context, R.style.dialog_custom);
        this.mContext = context;
        this.updateInfo = updateInfo;
        setContentView(R.layout.dialog_update);
        initView();
        initListener();
        setCancelable(false);
        Window dialogWindow = this.getWindow();
        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);
    }

    private void initView() {
        mTvContent = findViewById(R.id.tv_update_content);
        mTvSure = findViewById(R.id.tv_update);
        mTvVersion = findViewById(R.id.tv_version);
        mIvClose = findViewById(R.id.iv_close);
        mFlProgress = findViewById(R.id.fl_progress);
        mPbProgress = findViewById(R.id.pb_progress);
        mIvProgress = findViewById(R.id.iv_progress);
        initData();
    }


    private void initData() {
        setContent(updateInfo.info);
        setVersion(updateInfo.version);
        showClose(updateInfo.is_force);   //是否强制更新
        downloadApkDir = mContext.getCacheDir() + File.separator + "downloads" + File.separator;
        FileUtils.createOrExistsDir(downloadApkDir);
    }

    private void initListener() {
        mTvSure.setOnClickListener(v -> {
            if(isDownSuccess){
                installApk();
            }else {
                if (updateInfo.link_type == 1) {   //浏览器打开
                    Uri uri = Uri.parse(updateInfo.link.replace("browser://", ""));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
//                downloadApk();
                } else if (updateInfo.link_type == 2) {
                    downloadApk();
                } else {
                    String packName = mContext.getPackageName();
                    AppUtils.toMarket(mContext, packName, null);
                }
            }
        });
        mIvClose.setOnClickListener(v -> {
            if (mSureClick != null)
                mSureClick.onCancel();
            dismiss();
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isHasInstallPermissionWithO(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 开启设置安装未知来源应用权限界面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
    }


    public void downloadApk() {
        //updateInfo.link = "https://public-cdn.peony125.com/download_apk/prod_market/1/PEONY_OFFICE_sign.apk";
        mApkFile = new File(downloadApkDir + HttpUtilKt.getFileNameFromUrl(updateInfo.link));
        Thread downloadThread = new Thread(mDownloadApkRunnable);
        downloadThread.start();
//        Disposable subscribe = RxDownloadKt.download(new Task(updateInfo.link, HttpUtilKt.getFileNameFromUrl(updateInfo.link),
//                HttpUtilKt.getFileNameFromUrl(updateInfo.link), downloadApkDir, ""))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(p -> {
//                    Utils.logE("下载进度： " + p.percent());
//                    progress = (int) p.percent();
//                    if (progress == 100) {
//                        mHandler.sendEmptyMessage(DOWN_OVER);
//                    } else {
//                        mHandler.sendEmptyMessage(DOWN_UPDATE);
//                    }
//                });
    }


    private final Runnable mDownloadApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(updateInfo.link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                FileOutputStream outStream = new FileOutputStream(mApkFile);
                int count = 0;
                byte[] buf = new byte[1024];
                do {
                    int numRead = ins.read(buf);
                    count += numRead;
                    long nowPro = (int) (((float) count / length) * 100);
                    if (nowPro - progress > 1) {
                        progress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                    }
                    if (numRead <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numRead);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_APP_INSTALL) {
            mHandler.sendEmptyMessage(DOWN_OVER);
        }
    }

    private void installApk() {
        com.blankj.utilcode.util.AppUtils.installApp(mApkFile);
        /*Uri uri;
        Intent intentInstall = new Intent();
        intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInstall.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { // 7.0
            uri = Uri.fromFile(mApkFile);
        } else { // Android 7.0 以上
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", mApkFile);
            intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        // 安装应用
        intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intentInstall);
        mHandler.postDelayed(() -> {
            AppManager.getAppManager().finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }, 1000);*/
    }

    public void setIsCancel(boolean isCancel) {
        this.setCancelable(isCancel);  // 是否可以撤销
    }

    /**
     * 设置文字
     */
    public void setVersion(String content) {
        mTvVersion.setText(content);
    }

    public void showClose(int isForce) {
        mIvClose.setVisibility(isForce == 0 ? View.VISIBLE : View.INVISIBLE);
    }


    public void setContent(String content) {
        mTvContent.setText(content);
    }

    //确定监听
    public interface OnSureClick {
        void onCancel();
    }

    public void setOnSureClick(OnSureClick click) {
        this.mSureClick = click;
    }
}
