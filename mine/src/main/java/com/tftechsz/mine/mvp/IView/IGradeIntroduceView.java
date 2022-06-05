package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.GradeLevelDto;

public interface IGradeIntroduceView extends  MvpView {

    void getGradeIntroduceSuccess(GradeLevelDto data);


}
