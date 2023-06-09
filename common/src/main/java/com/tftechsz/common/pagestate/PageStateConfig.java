package com.tftechsz.common.pagestate;

import android.view.View;

public abstract class PageStateConfig {

    public abstract void onRetry(View retryView);

    public void onEmtptyViewClicked(View emptyView) {
        onRetry(emptyView);
    }

    public boolean isFirstStateLoading(){
        return false;
    }

    public String emptyMsg(){
        return "";
    }

    public int customLoadingLayoutId() {
        return PageStateManager.BASE_LOADING_LAYOUT_ID;
    }

    public int customErrorLayoutId() {
        return PageStateManager.BASE_RETRY_LAYOUT_ID;
    }

    public int customEmptyLayoutId() {
        return PageStateManager.BASE_EMPTY_LAYOUT_ID;
    }

    public boolean showProgress(View emptyView,int progress){
        return false;
    }

    public void onExit() {

    }



}
