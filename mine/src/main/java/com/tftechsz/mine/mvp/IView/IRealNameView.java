package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.OcrCheckDto;
import com.tftechsz.common.entity.RealCheckDto;

public interface IRealNameView extends MvpView {

    void uploadRealNameInfoSuccess(Boolean data);

    void uploadCardSuccess(String data,int code);

    void uploadRealNameInfoError();

    void ocrCheckSuccess(OcrCheckDto data);

    void uploadRealNameInfoNewSuccess(RealCheckDto data);
}
