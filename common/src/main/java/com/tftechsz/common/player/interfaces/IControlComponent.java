package com.tftechsz.common.player.interfaces;

import android.view.View;
import android.view.animation.Animation;

import com.tftechsz.common.player.controller.ControlWrapper;

import androidx.annotation.NonNull;

/**
 *  包 名 : com.standard.ui.palyer.interfaces

 *  描 述 : TODO
 */
public interface IControlComponent {

    void attach(@NonNull ControlWrapper controlWrapper);

    View getView();

    void onVisibilityChanged(boolean isVisible, Animation anim);

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void setProgress(int duration, int position);

    void onLockStateChanged(boolean isLocked);

}
