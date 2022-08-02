package com.tftechsz.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class NetworkUtil {
    public static String url = "http://www.baidu.com";
    public static int NET_CNNT_BAIDU_OK = 1; // NetworkAvailable
    public static int NET_CNNT_BAIDU_TIMEOUT = 2; // no NetworkAvailable
    public static int NET_NOT_PREPARE = 3; // Net no ready
    public static int NET_ERROR = 4; //net error
    private static int TIMEOUT = 3000; // TIMEOUT


    public static boolean getConnectivityStatus(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return false;
                }
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities == null) return false;
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                NetworkInfo networkInfo = getActiveNetworkInfo(context);
                if (networkInfo == null) {
                    return false;
                }

                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                    case ConnectivityManager.TYPE_ETHERNET:
                    case ConnectivityManager.TYPE_MOBILE:
                    case ConnectivityManager.TYPE_MOBILE_MMS:
                    case ConnectivityManager.TYPE_MOBILE_SUPL:
                    case ConnectivityManager.TYPE_MOBILE_DUN:
                    case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    case ConnectivityManager.TYPE_WIMAX:
                        return true;
                    default:
                        return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * check NetworkAvailable
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        if (null == info || !info.isAvailable())
            return false;
        return true;
    }

    /**
     * 获取可用的网络信息
     *
     * @param context
     * @return
     */
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * getLocalIpAddress
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String ret = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ret = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * 返回当前网络状态
     *
     * @param context
     * @return
     */
    public static int getNetState(Context context) {
        try {

            NetworkInfo networkinfo = getActiveNetworkInfo(context);
            if (networkinfo != null) {
                if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                    if (!connectionNetwork())
                        return NET_CNNT_BAIDU_TIMEOUT;
                    else
                        return NET_CNNT_BAIDU_OK;
                } else {
                    return NET_NOT_PREPARE;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return NET_ERROR;
    }

    /**
     * ping "http://www.baidu.com"
     *
     * @return
     */
    static private boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL(url)
                    .openConnection();
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException e) {
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
            }
            httpUrl = null;
        }
        return result;
    }

    /**
     * check is3G
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        NetworkInfo activeNetInfo =
                getActiveNetworkInfo(context);
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * isWifi
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        NetworkInfo activeNetInfo = getActiveNetworkInfo(context);
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * is2G
     *
     * @param context
     * @return boolean
     */
    public static boolean is2G(Context context) {
        NetworkInfo activeNetInfo = getActiveNetworkInfo(context);
        if (activeNetInfo != null
                && (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
            return true;
        }
        return false;
    }

}
