package com.tftechsz.mine.mvp.IView;

import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface IMinePhotoView extends MvpView {


    void uploadPhotoSuccess(List<LocalMedia> items,List<LocalMedia> mPhotoList, boolean data);

    void getPhotoSuccess(List<String> data);

}
