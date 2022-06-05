package com.tftechsz.im.mvp.iview;

import com.tftechsz.common.base.MvpView;

public interface IChatSettingView extends  MvpView {


  void getIsAttentionSuccess(boolean isAttention);

  void attentionSuccess(boolean isAttention);

  void blackSuccess(boolean isBlack);

  void cancelBlackSuccess(boolean isBlack);

}
