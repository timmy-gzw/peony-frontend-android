package com.tftechsz.common.nim.model.impl;


import com.tftechsz.common.nim.model.UIService;

/**
 * 保存UIservice 给内部使用
 */
public class UIServiceManager {

    public UIService uiService;

    private UIServiceManager() {

    }

    public static UIServiceManager getInstance() {
        return UIServiceManagerHolder.uiServiceManagerHolder;
    }

    private static class UIServiceManagerHolder {
        public static final UIServiceManager uiServiceManagerHolder = new UIServiceManager();
    }

    public void setUiService(UIService uiService) {
        this.uiService = uiService;
    }

    public UIService getUiService() {
        return uiService;
    }
}
