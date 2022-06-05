package com.tftechsz.im.utils;

import android.content.Context;

import java.util.Observable;
import java.util.Observer;

public class VoicePlayerEx extends VoicePlayer
{

	public VoicePlayerEx(Context context, Observer obsAfterCompletion )
	{
		super(context, obsAfterCompletion);
	}

	public VoicePlayerEx(final Context context, boolean _useSpeeker, Observer obsAfterCompletion)
	{
		super(context, _useSpeeker, obsAfterCompletion);
	}

	private static Observer createObsAfterCompletion(final Context context)
	{
		return new Observer(){
			@Override
			public void update(Observable observable, Object data)
			{
				// 播放结束提示音
				PromtHelper.voiceStopedPromt(context);
			}
		};
	}


}
