package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.RealCheckDto;

public interface IRealAuthView extends MvpView {

    void uploadRealAvatarSuccess(Boolean data);

    void uploadAvatarSuccess(String data);

    void facedetectCheckSuccess(RealCheckDto data);

    void recheckSuccess(RealCheckDto data);
}
