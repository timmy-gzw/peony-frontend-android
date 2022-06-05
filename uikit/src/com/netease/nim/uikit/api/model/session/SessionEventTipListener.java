package com.netease.nim.uikit.api.model.session;

import android.content.Context;

/**
 * 会话窗口消息列表一些点击事件的响应处理函数
 */
public interface SessionEventTipListener {

    // 自定义消息点击时间  1:原生  2 web view
    void onTipMessageClicked(Context context, int type ,String cotent);

}
