package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.FateInfo;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface FateMsgView extends MvpView {

    void getFateMsgList (List<FateInfo> fateInfoList);
}
