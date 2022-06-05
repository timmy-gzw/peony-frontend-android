package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.mine.entity.NoteValueConfirmDto;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface INoteValueShopView extends MvpView {
    void getNoteValueSuccess(IntegralDto data);

    void exchangeNoteValueSuccess();

    void convertConfirmSuccess(NoteValueConfirmDto data);
}
