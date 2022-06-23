package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.GiftDto;

import java.util.List;

public interface IMineAboutMeView extends MvpView {

    void getGiftSuccess(List<GiftDto> data);

}
