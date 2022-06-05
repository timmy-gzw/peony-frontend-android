package com.tftechsz.common.widget.pop;

import android.os.Bundle;

import com.loading.dialog.AndroidLoadingDialog;

/**
 * 解决IllegalStateException: Failure saving state: active Fragment has cleared
 */
public class AndroidLoadingDialogNew extends AndroidLoadingDialog {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }
}
