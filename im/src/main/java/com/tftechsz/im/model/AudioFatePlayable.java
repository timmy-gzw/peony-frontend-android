package com.tftechsz.im.model;

import android.text.TextUtils;

import com.netease.nim.uikit.business.session.audio.AudioMessagePlayable;
import com.netease.nim.uikit.common.media.audioplayer.Playable;

public class AudioFatePlayable implements Playable {

    private FateInfo message;

    public FateInfo getMessage() {
        return message;
    }

    public AudioFatePlayable(FateInfo playableMessage) {
        this.message = playableMessage;
    }

    @Override
    public long getDuration() {
        return message.getMsg_content().getDur();
    }

    @Override
    public String getPath() {
        return message.getMsg_content().getUrl();
    }

    @Override
    public boolean isAudioEqual(Playable audio) {
        if (AudioMessagePlayable.class.isInstance(audio)) {
            return !TextUtils.isEmpty(message.getMsg_content().getUrl()) && message.getMsg_content()
                    .getUrl().equalsIgnoreCase(audio.getPath());
        } else {
            return false;
        }
    }
}
