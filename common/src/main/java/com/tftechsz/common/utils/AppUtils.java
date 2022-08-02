package com.tftechsz.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 跟App相关的辅助类
 */
public class AppUtils {


    private final static String key = "JiEcBoaHyd";//方法一，密匙必须为16位
    private static final String ivParameter = "1234567890123456";//默认偏移
    private static final String WAYS = "AES";
    private static String MODE = "";
    private static boolean isPwd = false;
    private static final int pwdLenght = 16;
    private static final String val = "0";

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 判断微信客户端是否存在
     *
     * @return true安装, false未安装
     */
    public static boolean isWeChatAppInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID);
        int wxSdkVersion = api.getWXAppSupportAPI();
        if (api.isWXAppInstalled() && wxSdkVersion >= com.tencent.mm.opensdk.constants.Build.OPEN_BUSINESS_VIEW_SDK_INT) {
            return true;
        } else {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equalsIgnoreCase("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 判断是否安装某个包名应用
     */
    public static boolean isInstallPackage(String packageName) {
        final PackageManager packageManager = BaseApplication.getInstance().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断手机是否root
     */
    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";

        if (new File(binPath).exists() && isCanExecute(binPath)) {
            return true;
        }
        return new File(xBinPath).exists() && isCanExecute(xBinPath);
    }

    private static boolean isCanExecute(String filePath) {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("ls -l " + filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    public static List<PackageInfo> getAllPackageInfo() {
        final PackageManager packageManager = BaseApplication.getInstance().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        return pinfo;
    }

    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /***
     * 字节跳动支付统计
     * @param channle 支付渠道
     * @param paly 金额
     */
    public synchronized static void bytedancePurchase(String channle, int paly) {
//        if(!SPUtils.getBoolean(Constants.NOT_FIRST_PLAY)) {
//            SPUtils.put(Constants.NOT_FIRST_PLAY,true);
//            //内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
//            GameReportHelper.onEventPurchase("", "", getIMEI(BaseApplication.getInstance()), 1,
//                    channle, "¥", true, paly);
//        }
    }


    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return Build.BRAND + "-" + model;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 跳转应用商店.
     *
     * @param context   {@link Context}
     * @param appPkg    包名
     * @param marketPkg 应用商店包名
     * @return {@code true} 跳转成功 <br> {@code false} 跳转失败
     */
    public static boolean toMarket(Context context, String appPkg, String marketPkg) {
        try {
            Uri uri = null;
            if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
                uri = Uri.parse("samsungapps://ProductDetail/" + appPkg);
            } else {
                uri = Uri.parse("market://details?id=" + appPkg);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
                intent.setPackage(marketPkg);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        String version = (String) SPUtils.get(Constants.KEY_SYSTEM_VERSION, "");
        if (TextUtils.isEmpty(version)) {
            String sv = Build.VERSION.RELEASE;
            SPUtils.put(Constants.KEY_SYSTEM_VERSION, sv);
            return sv;
        } else {
            return version;
        }
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        String model = (String) SPUtils.get(Constants.KEY_SYSTEM_MODEL, "");
        if (TextUtils.isEmpty(model)) {
            String m = android.os.Build.MODEL;
            SPUtils.put(Constants.KEY_SYSTEM_MODEL, m);
            return m;
        } else {
            return model;
        }
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        String brand = (String) SPUtils.get(Constants.KEY_SYSTEM_BRAND, "");
        if (TextUtils.isEmpty(brand)) {
            String b = android.os.Build.BRAND;
            SPUtils.put(Constants.KEY_SYSTEM_BRAND, b);
            return b;
        } else {
            return brand;
        }
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context ctx) {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = "";
            if (checkPermission(ctx, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            return device_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                result = rest == PackageManager.PERMISSION_GRANTED;
            } catch (Throwable e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }


    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String string2Md5(String value) {
        String MD5 = "";
        if (null == value) return MD5;
        try {
            MessageDigest mD = MessageDigest.getInstance("MD5");
            MD5 = byteArrayToHexString(mD.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MD5 == null) MD5 = "";
        return MD5;
    }

    //IMEI：
    public static String doRead(Context context) {
        String imei = getIMEI(context);
        return string2Md5(imei);
    }

    //Android_ID
    public static String getOrigAndroidID(Context context) {
        String aid = "";
        try {
            aid = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return aid;
    }

    //MAC
    public static String getOrigMacAddr(Context context) {
        String macAddress = "";
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wm != null ? wm.getConnectionInfo() : null;
            macAddress = wInfo != null ? wInfo.getMacAddress() : null;
            if (macAddress != null) {
                macAddress = string2Md5(macAddress.replaceAll(":", "").toUpperCase());
            }
        } catch (Exception e) {
        }
        if (macAddress == null) {
            macAddress = "";
        }
        return macAddress;
    }

    // SimulateIDFA
    public static String getSimulateIDFA(Context context) {
        return doRead(context) + ";" + getOrigMacAddr(context) + ";" + getOrigAndroidID(context);
    }

    public static String getDevUUID(Context context) {
        return getDevUUID(context, getSimulateIDFA(context));
    }

    public static String getDevUUID(Context context, String simulateIDFA) {
        // get dev uuid from SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("peony", Context.MODE_PRIVATE);
        String userIdFromSp = sp.getString("uuid", "");
        // get dev uuid from file
        String userIdFromFile = "";
        try {
            String userIdFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/txrtmp/spuid";
            File userIdFile = new File(userIdFilePath);
            if (userIdFile.exists()) {
                FileInputStream fin = new FileInputStream(userIdFile);
                int length = fin.available();
                if (length > 0) {
                    byte[] buffer = new byte[length];
                    fin.read(buffer);
                    userIdFromFile = new String(buffer, StandardCharsets.UTF_8);
                }
                fin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userId = "";
        if (!userIdFromSp.isEmpty()) userId = userIdFromSp;
        if (!userIdFromFile.isEmpty()) userId = userIdFromFile;

        if (userId.isEmpty()) {
            userId = string2Md5(simulateIDFA + UUID.randomUUID().toString());
        }
        if (userIdFromFile.isEmpty()) {
            userIdFromFile = userId;
            // set dev uuid to file
            try {
                String userIdDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/peony";
                File userIdDir = new File(userIdDirPath);
                if (!userIdDir.exists()) userIdDir.mkdir();
                String userIdFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/peony/uuid";
                File userIdFile = new File(userIdFilePath);
                if (!userIdFile.exists()) userIdFile.createNewFile();
                FileOutputStream fout = new FileOutputStream(userIdFile);
                byte[] bytes = userId.getBytes();
                fout.write(bytes);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!userIdFromSp.equals(userIdFromFile)) {
            // set dev uuid to SharedPreferences
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("key_user_id", userId);
            editor.commit();
        }

        return userId;
    }


    public static boolean stackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningTaskInfo> recentTaskInfos = manager.getRunningTasks(1);
        if (recentTaskInfos != null && recentTaskInfos.size() > 0) {
            ActivityManager.RunningTaskInfo taskInfo = recentTaskInfos.get(0);
            return taskInfo.baseActivity.getPackageName().equals(packageName) && taskInfo.numActivities > 1;
        }

        return false;
    }

    private static final String SHARED_PREFERENCES_NAME = "deviceid_cache";
    //.开头隐藏文件和隐藏文件夹
    private static final String FILE_PATH = "data/.cache/.mp06bfSdnER";

    private static final String LOACL_UUID = "my_device_localuuid";
    private static final String LOACL_MAC = "my_device_local_mac";
    private static final String LOACL_IMEI = "my_device_local_imei";
    private static final String LOACL_DEVICE_IMEI = "my_device_local_device_imei";


    private static final String LOACL_DEVICE_ID = "my_device_loacl_device_id";


    //获取设备唯一id
    public static String getDeviceId() {
        String localDeviceId = getLocalDeviceId();
        if (!TextUtils.isEmpty(localDeviceId)) {
            return localDeviceId;
        }
        //先获取获取默认的imei
        String diviceid = getimei();
        //如果为空获取MAC地址
        if (TextUtils.isEmpty(diviceid)) {
            diviceid = getMacid();
        }
        //如果还为空则生成并保存一个唯一的UUID
        if (TextUtils.isEmpty(diviceid)) {
            diviceid = getLocalUUID();
        }
        saveDeviceId(diviceid);
        return diviceid;
    }

    private static String getLocalDeviceId() {
        String savaString = getSavaString(LOACL_DEVICE_ID, "");
        if (TextUtils.isEmpty(savaString)) {
            String s = readSDFile();
            if (!TextUtils.isEmpty(s)) {
                savaString(LOACL_DEVICE_ID, s);
                return s;
            }
        } else {
            return savaString;
        }

        return null;
    }

    private static void saveDeviceId(String diviceid) {
        savaString(LOACL_DEVICE_ID, diviceid);
        //加密保存
        saveFile(encrypt(diviceid));
    }

    private static void saveFile(String str) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录的hello.text
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + FILE_PATH + File.separator + getFileName();
        } else {  // 系统下载缓存根目录的hello.text
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + FILE_PATH + File.separator + getFileName();
        }
        try {
            File file = new File(filePath);
            //不存在則重新保存
            if (!file.exists()) {
                Log.d("DeviceIDUtils", "saveFile filePath:" + filePath);
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();
            } else {
                Log.d("DeviceIDUtils", "saveFile 文件已存在:" + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName() {
        return ".fd990eff10b3157097bfaa0fac56caa9";
    }

    private static String readSDFile() {
        try {
            String filePath = null;
            boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (hasSDCard) { // SD卡根目录的hello.text
                filePath = Environment.getExternalStorageDirectory().toString() + File.separator + FILE_PATH + File.separator + getFileName();
            } else {  // 系统下载缓存根目录的hello.text
                filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + FILE_PATH + File.separator + getFileName();
            }
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();

            byte[] buffer = new byte[length];
            fis.read(buffer);
            String res = new String(buffer, StandardCharsets.UTF_8);

            fis.close();
            Log.d("DeviceIDUtils", "readSDFile filePath:" + filePath);
            Log.d("DeviceIDUtils", "readSDFile res:" + res);
            //解密
            return decrypt(res);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String getLocalUUID() {
        String localuuid = getSavaString(LOACL_UUID, "");
        if (TextUtils.isEmpty(localuuid)) {
            localuuid = UUID.randomUUID().toString().replace("-", "");
            savaString(LOACL_UUID, localuuid);
        }
        return localuuid;
    }

    private static String getMacid() {
        String WLANMAC = getSavaString(LOACL_MAC, "");
        if (!TextUtils.isEmpty(WLANMAC)) {
            return WLANMAC;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            WLANMAC = getMac60();
        } else {
            WifiManager wm = (WifiManager) BaseApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WLANMAC = wm.getConnectionInfo().getMacAddress();
            if (TextUtils.isEmpty(WLANMAC) || "02:00:00:00:00:00".equals(WLANMAC)) {
                WLANMAC = getMac60();
            }
        }

        //在Android6.0的版本以后用原来的getMacAddress()方法获取手机的MAC地址都为：02:00:00:00:00:00这个固定的值
        if ("02:00:00:00:00:00".equals(WLANMAC)) {
            WLANMAC = null;
        }

        if (!TextUtils.isEmpty(WLANMAC)) {
            WLANMAC = WLANMAC.replaceAll(":", "");
            savaString(LOACL_MAC, WLANMAC);
        }
        return WLANMAC;
    }


    private static String getMac60() {
        String mac = getMac60_1();
        if (TextUtils.isEmpty(mac) || "02:00:00:00:00:00".equals(mac)) {
            mac = getMac60_2();
        }
        return mac;
    }

    private static String getMac60_1() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }

        //转换成小写
        if (!TextUtils.isEmpty(macSerial)) {
            macSerial = macSerial.toLowerCase();
        }
        return macSerial;
    }

    private static String getMac60_2() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                String mac = res1.toString();
                //转换成小写
                if (!TextUtils.isEmpty(mac)) {
                    mac = mac.toLowerCase();
                }
                return mac;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }


    //通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现
    private static String getDiviceInfoIMEI() {
        String device_imei = getSavaString(LOACL_DEVICE_IMEI, "");
        if (!TextUtils.isEmpty(device_imei)) {
            return device_imei;
        }

        device_imei = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        if (!TextUtils.isEmpty(device_imei)) {
            savaString(LOACL_DEVICE_IMEI, device_imei);
        }
        return device_imei;
    }

    private static String getimei() {
        String imei = getSavaString(LOACL_IMEI, "");
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }

        try {
            TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                savaString(LOACL_IMEI, imei);
            }
            return imei;
        } catch (Exception e) {

        }
        return null;
    }

    private static String getSavaString(String key, String defValue) {
        SharedPreferences sp = BaseApplication.getInstance().getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    private static void savaString(String key, String value) {
        SharedPreferences sp = BaseApplication.getInstance().getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();//提交保存键值对

    }


    //****************************************************   以下是加密算法  *********************************************************************//


    /**
     * @param
     * @return AES加密算法加密
     * @throws Exception
     */
    private static String encrypt(String cleartext) {
        if (cleartext == null) {
            return null;
        }
        try {
            String seed = key;
            byte[] encryptResultStr = new byte[0];
            encryptResultStr = encrypt(cleartext, seed, 0);
            return new String(encryptResultStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param encrypted
     * @return AES加密算法解密
     * @throws Exception
     */
    private static String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }
        try {
            String seed = key;
            String temp = decrypt(encrypted, seed, 0);
            return temp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private enum AESType {

        ECB("ECB", "0"), CBC("CBC", "1"), CFB("CFB", "2"), OFB("OFB", "3");
        private final String k;
        private final String v;

        AESType(String k, String v) {
            this.k = k;
            this.v = v;
        }

        public String key() {
            return this.k;
        }

        public String value() {
            return this.v;
        }

        public static AESType get(int id) {
            AESType[] vs = AESType.values();
            for (int i = 0; i < vs.length; i++) {
                AESType d = vs[i];
                if (d.key().equals(id))
                    return d;
            }
            return AESType.CBC;
        }

    }

    private static String selectMod(int type) {
        // ECB("ECB", "0"), CBC("CBC", "1"), CFB("CFB", "2"), OFB("OFB", "3");
        String modeCode = "PKCS5Padding";
        switch (type) {
            case 0:
                isPwd = false;
                MODE = WAYS + "/" + AESType.ECB.key() + "/" + modeCode;

                break;
            case 1:
                isPwd = true;
                MODE = WAYS + "/" + AESType.CBC.key() + "/" + modeCode;
                break;
            case 2:
                isPwd = true;
                MODE = WAYS + "/" + AESType.CFB.key() + "/" + modeCode;
                break;
            case 3:
                isPwd = true;
                MODE = WAYS + "/" + AESType.OFB.key() + "/" + modeCode;
                break;

        }

        return MODE;

    }

    // 加密
    private static byte[] encrypt(String sSrc, String sKey, int type)
            throws Exception {
        sKey = toMakekey(sKey, pwdLenght, val);
        Cipher cipher = Cipher.getInstance(selectMod(type));
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, WAYS);

        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        if (!isPwd) {// ECB 不用密码
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        }

        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码。
    }

    // 解密
    private static String decrypt(String sSrc, String sKey, int type)
            throws Exception {
        sKey = toMakekey(sKey, pwdLenght, val);
        try {
            byte[] raw = sKey.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, WAYS);
            Cipher cipher = Cipher.getInstance(selectMod(type));
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            if (!isPwd) {// ECB 不用密码
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            }
            byte[] encrypted1 = Base64.decode(sSrc.getBytes(), Base64.DEFAULT);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    //key
    private static String toMakekey(String str, int strLength, String val) {

        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(str).append(val);
                str = buffer.toString();
                strLen = str.length();
            }
        }
        return str;
    }

}
