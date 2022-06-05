package com.tftechsz.party.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ChatMsg;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.tftechsz.common.widget.SharePopWindow;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PkResultAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * pk结果
 */
public class PkResultPopWindow extends BaseCenterPop implements View.OnClickListener {

    private final TextView mTvCancel, mTvSure, mTvTitle;
    private final SVGAImageView mSvPkResult;
    private SVGAParser svgaParser;
    private final RecyclerView mRvPkResult;
    private final LinearLayout mLlContent;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private SharePopWindow mPopWindowShare;
    private final UserProviderService service;
    private int id;
    private String roomName;

    public PkResultPopWindow(Context context, int id, String roomName) {
        super(context);
        this.id = id;
        this.roomName = roomName;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvSure = findViewById(R.id.tv_sure);
        mTvSure.setOnClickListener(this);
        mTvTitle = findViewById(R.id.tv_title);
        mRvPkResult = findViewById(R.id.rv_pk_result);
        mSvPkResult = findViewById(R.id.sv_pk_result);
        mLlContent = findViewById(R.id.ll_content);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null)
                    listener.onDismiss();
            }
        });
    }


    public void setSeat(List<VoiceRoomSeat> list, ChatMsg.PartyPkStop partyPkStop) {
        mLlContent.setVisibility(View.VISIBLE);
        mTvSure.setVisibility(View.VISIBLE);
        mTvCancel.setBackgroundResource(R.drawable.bg_gray_radius25);
        if (partyPkStop.win_site == 0) {    //0平局 1红方胜利 2蓝方胜利
            downSvg(partyPkStop.victory_static.draw);
            mLlContent.setVisibility(View.INVISIBLE);
        } else if (partyPkStop.win_site == 1) {
            mTvTitle.setText("- 恭喜红队获得胜利 -");
            downSvg(partyPkStop.victory_static.win);
        } else if (partyPkStop.win_site == 2) {
            downSvg(partyPkStop.victory_static.win);
            mTvTitle.setText("- 恭喜蓝队获得胜利 -");
        }
        List<VoiceRoomSeat> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUser_id() != 0)
                newList.add(list.get(i));
        }
        if (partyPkStop.win_site != 0) {
            boolean isSelfTeam = false;
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getUser_id() == service.getUserId()) {
                    isSelfTeam = true;
                }
            }
            //是否是自己的队伍胜利
            if (isSelfTeam) {
                mTvSure.setVisibility(View.VISIBLE);
            } else {
                mTvCancel.setBackgroundResource(R.drawable.bg_orange_radius25);
                mTvSure.setVisibility(View.GONE);
            }
        }
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mRvPkResult.setLayoutManager(layoutManager);
        PkResultAdapter adapter = new PkResultAdapter();
        mRvPkResult.setAdapter(adapter);
        adapter.setList(newList);
    }


    public void downSvg(String link) {
        if (null == svgaParser) svgaParser = new SVGAParser(mContext);
        String realName = DownloadHelper.getFileNameFromFileUrl(link);
        File file = new File(DownloadHelper.FILE_PATH + File.separator + realName.split("\\.")[0]);
        if (file.exists()) {
            loadSvg(file);
        } else {
            DownloadHelper.downloadGift(link, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    loadSvg(file);
                }

                @Override
                public void failed() {
                }

                @Override
                public void onProgress(int progress) {
                }
            });
        }
    }

    private void loadSvg(File file) {
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    mSvPkResult.setVideoItem(videoItem);
                    mSvPkResult.stepToFrame(0, true);
                }

                @Override
                public void onError() {

                }
            };
        }
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_pk_result);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_sure) {
            if (mPopWindowShare == null) {
                mPopWindowShare = new SharePopWindow((Activity) mContext);
            }
            mPopWindowShare.questData(Interfaces.SHARE_QUEST_TYPE_PARTY, id, roomName);
            dismiss();
        }
    }


    public interface OnSelectListener {
        void onDismiss();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
