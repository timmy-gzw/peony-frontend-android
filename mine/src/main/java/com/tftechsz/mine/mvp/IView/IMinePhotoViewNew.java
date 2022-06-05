package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.MinePhotoDto;

import java.util.List;

public interface IMinePhotoViewNew extends MvpView {

    void uploadPhotoSuccess();

    void getPhotoSuccess(List<MinePhotoDto> data);

    void sortPhotoSuccess();
}
