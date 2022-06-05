package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.dto.AirdropDetailDto;
import com.tftechsz.common.base.MvpView;

public interface IAirdropDetailView extends MvpView {

    void getAirdropDetailSuccess(AirdropDetailDto data);

}
