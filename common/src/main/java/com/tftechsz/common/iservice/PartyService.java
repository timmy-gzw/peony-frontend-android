package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 个人真人
 */
public interface PartyService extends IProvider {

    boolean isRunFloatService();

    void stopFloatService();

    boolean isOnSeat();

    void finishPartyActivity();

    boolean isRunActivity();
}
