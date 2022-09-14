package com.tftechsz.mine.mvp.IView;


import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.LabelDto;

import java.util.List;

public interface ILabelView extends MvpView {

    void getTagSuccess(List<LabelDto> data);

    void setTagSuccess(Boolean data);
}
