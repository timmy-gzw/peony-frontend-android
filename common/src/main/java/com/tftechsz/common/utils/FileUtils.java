package com.tftechsz.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.log.KLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;


public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();
    //内存卡路径
    public static String STORAGE_BASE_DIR;
    //外存卡路径
    public static String STORAGE_BASE_SD_DIR;
    public static final int DIR_TYPE_DOWNLOAD = 1;
    public static final int DIR_TYPE_IMAGE = 2;
    public static final int DIR_TYPE_VIDEO = 3;
    public static final int DIR_TYPE_VOICE = 4;
    public static final String STORAGE_EXTERNAL_PATH = "smartim";

    // 下载目录
    public final static String STORAGE_DOWNLOAD_DIR = File.separator + "downloads" + File.separator;
    public final static String STORAGE_IMAGE = File.separator + "image" + File.separator;
    public final static String STORAGE_VIDEO = File.separator + "video" + File.separator;
    public final static String STORAGE_VOICE = File.separator + "voice" + File.separator;
    public final static String STORAGE_FILE = File.separator + "file" + File.separator;
    public final static String STORAGE_GLIDE = File.separator + "glide";

    private static final long MB = 1024 * 1024;

    static {
        STORAGE_BASE_SD_DIR = new File(Environment.getExternalStorageDirectory(), STORAGE_EXTERNAL_PATH).getAbsolutePath();
        STORAGE_BASE_DIR = BaseApplication.getInstance().getFilesDir().getAbsolutePath() + "/" + STORAGE_EXTERNAL_PATH;
        Log.i(TAG, "STORAGE_BASE_DIR=>" + STORAGE_BASE_DIR);
        Log.i(TAG, "STORAGE_BASE_SD_DIR=>" + STORAGE_BASE_SD_DIR);
    }

    /**
     * 根据类型获取路径目录
     *
     * @param type
     * @return
     */
    public static String getDirByType(int type) {
        String fileName;
        switch (type) {
            case DIR_TYPE_IMAGE:
                fileName = STORAGE_IMAGE;
                break;
            case DIR_TYPE_DOWNLOAD:
                fileName = STORAGE_DOWNLOAD_DIR;
                break;
            case DIR_TYPE_VIDEO:
                fileName = STORAGE_VIDEO;
                break;
            case DIR_TYPE_VOICE:
                fileName = STORAGE_VOICE;
                break;
            default:
                fileName = STORAGE_EXTERNAL_PATH;
                break;
        }
        return createDirByPath(fileName);
    }

    protected static String createDirByPath(String fileName) {
        String filePath = null;
        if (isExistSDCard()) {
            if (TextUtils.isEmpty(STORAGE_BASE_SD_DIR)) {
                STORAGE_BASE_SD_DIR = new File(Environment.getExternalStorageDirectory(), STORAGE_EXTERNAL_PATH).getAbsolutePath();
            }
            filePath = STORAGE_BASE_SD_DIR + fileName;
        } else {
            if (TextUtils.isEmpty(STORAGE_BASE_DIR)) {
                STORAGE_BASE_DIR = BaseApplication.getInstance().getFilesDir().getAbsolutePath() + "/" + STORAGE_EXTERNAL_PATH;
            }
            filePath = STORAGE_BASE_DIR + fileName;
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
//        return file.getAbsolutePath()+"/";
        return file.getAbsolutePath();
    }

    /**
     * @param name 文件夹名 例如： “img”、“my/img”
     * @return path
     */
    public static String createChatDirByName(String name) {
        return createDirByPath(File.separator + "chat" + File.separator + name);
    }

    public static String createChatImage(String mId, String fileName) {
        return createChatDirByName(mId + STORAGE_IMAGE) + fileName;
    }

    public static String createDownDirByName(String name) {
        return createDirByPath(STORAGE_DOWNLOAD_DIR + name);
    }

    public static String createAvatarDirByName(String name) {
        return createDirByPath(getDirByType(DIR_TYPE_IMAGE) + name);
    }

    public static String getDownPath(String fileName) {
        return getDirByType(DIR_TYPE_DOWNLOAD) + fileName;
    }

    public static String getVideoPath(String fileName) {
        return getDirByType(DIR_TYPE_VIDEO) + fileName;
    }

    public static String getVoicePath(String fromId, String fileName) {
        return getVoiceDir(fromId) + fileName;
    }

    public static String getVoiceDir(String dirName) {
        return createChatDirByName(dirName + STORAGE_VOICE);
    }

    public static String getImagePath(String fileName) {
        return getDirByType(DIR_TYPE_IMAGE) + fileName;
    }


    /**
     * 第三方固件上级获取路径需要加“file://”
     *
     * @param fileName
     * @return
     */
    public static String getSensorUpgradePath(String fileName) {
        return "file://" + getDirByType(DIR_TYPE_DOWNLOAD) + fileName;
    }

    /**
     * 是否存在外置内存卡
     *
     * @return
     */
    public static boolean isExistSDCard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            Log.i(TAG, "isExistSDCard = true");
            return true;
        } else {
            Log.i(TAG, "isExistSDCard = false");
            return false;
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            KLog.e(TAG, "删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                KLog.e(TAG, "删除单个文件" + fileName + "成功！");
                return true;
            } else {
                KLog.e(TAG, "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            KLog.e(TAG, "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            KLog.e(TAG, "删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            KLog.e(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.d(TAG, "删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param fileName 文件名称及后缀(例如：xx.xml)
     * @return
     */
    public static String getFromAssetsByFileName(String fileName) {
        InputStreamReader inputReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            inputReader = new InputStreamReader(BaseApplication.getInstance().getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                stringBuffer.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            KLog.e(TAG, "获取文件大小不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    public static File saveBmp2Gallery(Context context, Bitmap bmp, String picName) {
        String fileName = null;
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        File file = null;
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".jpg");
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, outStream);
            }
//            //保存图片后发送广播通知更新数据库
//            Uri uri = Uri.fromFile(file);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
////通知相册更新
//MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
//                bmp, fileName, null);
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        mContext.sendBroadcast(intent);
        return file;
    }

    public static String readTxtFile(File file ) {
        StringBuilder content = new StringBuilder(); //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content.append(line);
                }
                instream.close();
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content.toString();
    }

}
