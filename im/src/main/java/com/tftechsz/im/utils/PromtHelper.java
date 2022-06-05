package com.tftechsz.im.utils;

import android.content.Context;

import com.tftechsz.im.R;


public class PromtHelper {
    public static void voiceStopedPromt(Context context) {
        MediaPlayerHelper.getInstance(context).play(R.raw.audio_voice_stoped);
    }
}
