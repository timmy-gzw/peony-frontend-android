package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.GiftDto;

import java.util.ArrayList;

public interface IMineAboutMeView extends MvpView {

    void getGiftSuccess(ArrayList<GiftDto> data);

}
