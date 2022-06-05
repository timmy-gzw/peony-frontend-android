package com.tftechsz.common.utils;

import com.tencent.mmkv.MMKV;
import com.tftechsz.common.http.RetrofitManager;

/**
 *
 */
public class MMKVUtils {
    private static MMKV mmkv = null;

    private static MMKVUtils mmkvUtils;

    public static MMKVUtils getInstance() {
        if (mmkvUtils == null) {
            synchronized (RetrofitManager.class) {
                if (mmkvUtils == null) {
                    mmkvUtils = new MMKVUtils();
                }
            }
        }
        return mmkvUtils;
    }

    public MMKVUtils() {
        mmkv = MMKV.defaultMMKV(1, "tfpeony_sp");
    }


    public void encode(String key, Object value) {
        if (value instanceof String) {
            mmkv.encode(key, (String) value);
        } else if (value instanceof Integer) {
            mmkv.encode(key, (Integer) value);
        } else if (value instanceof Boolean) {
            mmkv.encode(key, (Boolean) value);
        } else if (value instanceof Float) {
            mmkv.encode(key, (Float) value);
        } else if (value instanceof Long) {
            mmkv.encode(key, (Long) value);
        } else {
            mmkv.encode(key, value.toString());
        }
    }

    public int decodeInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public long decodeLong(String key) {
        return mmkv.decodeLong(key, 0);
    }

    public double decodeDouble(String key) {
        return mmkv.decodeDouble(key, 0.00);
    }

    public String decodeString(String key) {

        return mmkv.decodeString(key, "");
    }

    public boolean decodeBoolean(String key) {

        return mmkv.decodeBool(key, false);
    }


    public void removeKey(String key) {
        mmkv.removeValueForKey(key);
    }


    public void clearAll() {
        mmkv.clearAll();
    }


}
