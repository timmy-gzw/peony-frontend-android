package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.dto.LiveHomeListDto;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface ILiveHomeView extends MvpView {

    void getLiveHomeSuccess(List<LiveHomeListDto> data);

    void getLiveHomeTokenSuccess(int position,String roomName,String token);
}
