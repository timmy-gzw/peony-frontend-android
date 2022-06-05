package com.tftechsz.common.nim.model.impl;

import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.tftechsz.common.nim.model.NERTCCallingDelegate;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 收拢的Delegate
 */
public class NERTCInternalDelegateManager implements NERTCCallingDelegate {

    private CopyOnWriteArrayList<WeakReference<NERTCCallingDelegate>> mWeakReferenceList;

    public NERTCInternalDelegateManager() {
        mWeakReferenceList = new CopyOnWriteArrayList<>();
    }

    public void addDelegate(NERTCCallingDelegate listener) {
        WeakReference<NERTCCallingDelegate> listenerWeakReference = new WeakReference<>(listener);
        mWeakReferenceList.add(listenerWeakReference);
    }

    public void removeDelegate(NERTCCallingDelegate listener) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            if (reference != null && reference.get() == listener) {
                mWeakReferenceList.remove(reference);
            }
        }
    }

    @Override
    public void onError(int errorCode, String userId, String errorMsg) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onError(errorCode,userId, errorMsg);
            }
        }
    }

    @Override
    public void onInvitedByUser(InvitedEvent invitedEvent, String callId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onInvitedByUser(invitedEvent,callId);
            }
        }
    }

    @Override
    public void onInvitedByUser(String fromId, String callId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onInvitedByUser(fromId,callId);
            }
        }
    }



    @Override
    public void onUserEnter(long userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserEnter(userId);
            }
        }
    }

    @Override
    public void onJoinChannel(long channelId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onJoinChannel(channelId);
            }
        }
    }


    @Override
    public void onUserHangup(long userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserHangup(userId);
            }
        }
    }

    @Override
    public void onRejectByUserId(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onRejectByUserId(userId);
            }
        }
    }

    @Override
    public void onAcceptByUserId(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onAcceptByUserId(userId);
            }
        }
    }

    @Override
    public void onUserBusy(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserBusy(userId);
            }
        }
    }

    @Override
    public void onUserLeave(String userId, String reason) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserLeave(userId,reason);
            }
        }
    }

    @Override
    public void onCancelByUserId(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onCancelByUserId(userId);
            }
        }
    }

    @Override
    public void onJoinRoomFailed(int code) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onJoinRoomFailed(code);
            }
        }
    }

    @Override
    public void onCameraAvailable(long userId, boolean isVideoAvailable) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onCameraAvailable(userId, isVideoAvailable);
            }
        }
    }

    @Override
    public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserNetworkQuality(stats);
            }
        }
    }


    @Override
    public void onError(int code) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onError(code);
            }
        }
    }

    @Override
    public void onAudioAvailable(long userId, boolean isVideoAvailable) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onAudioAvailable(userId, isVideoAvailable);
            }
        }
    }

    @Override
    public void timeOut(int type) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.timeOut(type);
            }
        }
    }
}
