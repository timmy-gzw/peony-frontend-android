package com.tftechsz.common.nertcvoiceroom.util;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtil {


    private static final String TAG = "JsonUtil";

    public static JSONObject parse(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getUnescapeJson(String escapeJson){
        return null;
    }

}
