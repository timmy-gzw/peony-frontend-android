package com.netease.nim.uikit.business.session.viewholder;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.support.glide.ImageLoaderKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;

import java.util.HashMap;

/**
 * 游戏
 */
public class MsgViewHolderGame extends MsgViewHolderBase {

    private ImageView ivGame;
    private Handler handler;

    public MsgViewHolderGame(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_game;
    }

    @Override
    public void inflateContentView() {
        ivGame = findViewById(R.id.iv_game);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }


    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.Game game = JSONObject.parseObject(chatMsg.content, ChatMsg.Game.class);
        if (game != null) {
            int value = game.number;
            LogUtil.e("==============",message.getTime() + "=========" + System.currentTimeMillis());
            if (game.type == 1) {  //骰子
//                if (message.getTime() + 1500 >= System.currentTimeMillis()) {
//                    ImageLoaderKit.loadGif(context, ivGame, R.drawable.ic_game_dice);
//                    if (handler == null)
//                        handler = new Handler(Looper.getMainLooper());
//                    handler.postDelayed(() -> ivGame.setImageResource(ImageLoaderKit.getResId("ic_game_dice" + value, R.drawable.class)), 1500);
//                } else {
//                    ivGame.setImageResource(ImageLoaderKit.getResId("ic_game_dice" + value, R.drawable.class));
//                }
                play("ic_game_dice" + value,R.drawable.ic_game_dice);
            } else if (game.type == 2) { //猜拳
//                if (message.getTime() + 1500  >= System.currentTimeMillis()) {
//                    ImageLoaderKit.loadGif(context, ivGame, R.drawable.ic_game_guess);
//                    if (handler == null)
//                        handler = new Handler(Looper.getMainLooper());
//                    handler.postDelayed(() -> ivGame.setImageResource(ImageLoaderKit.getResId("ic_game_guess" + value, R.drawable.class)), 1500);
//                } else {
//                    ivGame.setImageResource(ImageLoaderKit.getResId("ic_game_guess" + value, R.drawable.class));
//                }

                play("ic_game_guess" + value,R.drawable.ic_game_guess);
            }
        }
    }


    private void saveStatus() {
        if (message != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ic_game", 1);
            message.setLocalExtension(map);
            NIMClient.getService(MsgService.class).updateIMMessage(message);
            refreshCurrentItem();
        }
    }


    private void play(String name, int pic) {
        if (message.getLocalExtension() != null) {
            Object obj = message.getLocalExtension().get("ic_game");
            if (obj != null) {
                ivGame.setImageResource(ImageLoaderKit.getResId(name, R.drawable.class));
            }
        } else {
            ImageLoaderKit.loadGif(context, ivGame, pic);
            if (handler == null)
                handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivGame.setImageResource(ImageLoaderKit.getResId(name, R.drawable.class));
                    saveStatus();
                }
            },1000);
        }
    }


    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }


}
