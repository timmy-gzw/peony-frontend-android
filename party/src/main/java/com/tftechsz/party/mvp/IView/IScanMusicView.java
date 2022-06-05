package com.tftechsz.party.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.party.entity.MusicCountBean;

/**
 * 包 名 : com.tftechsz.party.mvp.IView
 * 描 述 : TODO
 */
public interface IScanMusicView extends MvpView {
    void getCountSuccess(MusicCountBean data);

    void uploadSuccess();
}
