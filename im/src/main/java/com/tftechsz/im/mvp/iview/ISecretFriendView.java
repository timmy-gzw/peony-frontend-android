package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface ISecretFriendView extends MvpView {

    void getIntimacyListSuccess(List<ContactInfo> data);


}
