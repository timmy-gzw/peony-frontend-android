package com.tftechsz.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;

public class ImgUtils {
    static onFinishListner listner;

    public static  void setOnFinishListner(onFinishListner finishListner){
        listner=finishListner;
    }

    public interface onFinishListner{
         void downLoadPictureFinish();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp,Dialog dialog)  {
        final Bitmap[] bitmap = new Bitmap[1];
        bitmap[0] = bmp;
//        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                bitmap[0] = resource;
//            }
//        });
        File appDir = new File(Environment.getExternalStorageDirectory(), "peony");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "peony"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2200);
                    dialog.dismiss();
                    listner.downLoadPictureFinish();
                 }catch (InterruptedException e){
                 }finally {
                    bmp.recycle();
                }
            }
        }.start();


        /** 把文件插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }**/
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

    }


}
