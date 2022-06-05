package com.netease.nim.uikit.business.session.viewholder;

import android.media.AudioManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.audio.MessageAudioControl;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

import java.util.List;

/**
 * 用户信息
 */
public class MsgViewHolderUserInfo extends MsgViewHolderBase {

    private TextView mTvUserInfo, mTvSign, mTvAudioTime, mTvHome, mTvAudioTip;
    private AvatarImageView ivHead;
    private LinearLayout mLlAudio;
    private View mViewBottom;
    private TextView mTvTip;
    private FrameLayout mFlTip;
    private ImageView mIvAudio, mIvSex;
    private RecyclerView mRvPhoto;
    private AudioPlayer mIjkPlayer;

    public MsgViewHolderUserInfo(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_user_info;
    }

    @Override
    public void inflateContentView() {
        mLlAudio = findViewById(R.id.ll_audio);
        mTvUserInfo = findViewById(R.id.tv_user_info);
        mTvAudioTime = findViewById(R.id.audio_time);
        mTvAudioTip = findViewById(R.id.tv_audio_tip);
        mTvSign = findViewById(R.id.tv_sign);
        mIvAudio = findViewById(R.id.iv_audio);
        ivHead = findViewById(R.id.iv_head);
        mIvSex = findViewById(R.id.iv_sex);
        mRvPhoto = findViewById(R.id.rv_photo);
        mViewBottom = findViewById(R.id.view_bottom);
        mTvTip = findViewById(R.id.tv_content);
        mFlTip = findViewById(R.id.fl_title);
        mTvHome = findViewById(R.id.tv_home);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        mRvPhoto.setLayoutManager(gridLayoutManager);
        mIjkPlayer = MessageAudioControl.getInstance(context).getAudioPlayer();
        mIjkPlayer.setOnPlayListener(new OnPlayListener() {
            @Override
            public void onPrepared() {
                RequestOptions requestOptions = new RequestOptions().centerCrop();
                Glide.with(context)
                        .asGif()
                        .load(R.drawable.mine_ic_voice_playing)
                        .apply(requestOptions)
                        .into(mIvAudio);

            }

            @Override
            public void onCompletion() {
                mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
            }

            @Override
            public void onInterrupt() {
                mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
            }

            @Override
            public void onError(String error) {
                mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
            }

            @Override
            public void onPlaying(long curPosition) {
            }
        });
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.AccostCard card = JSON.parseObject(chatMsg.content, ChatMsg.AccostCard.class);
        if (card != null) {
            ivHead.loadAvatar(card.icon, 11);
            //录音时长
            mLlAudio.setVisibility(!TextUtils.isEmpty(card.voice) ? View.VISIBLE : View.GONE);
            mTvAudioTip.setVisibility(!TextUtils.isEmpty(card.voice) ? View.VISIBLE : View.GONE);
            mTvAudioTime.setText(card.voice_time);
            mIvSex.setImageResource(card.sex == 1 ? R.drawable.nim_ic_boy : R.drawable.nim_ic_girl);
            mTvUserInfo.setText(card.tags);
            if(!TextUtils.isEmpty(card.tips)){
                mFlTip.setVisibility(View.VISIBLE);
                SpannableStringBuilder span = ChatMsgUtil.getTipContent(card.tips,"", (ChatMsgUtil.OnSelectListener) content -> {
                    String webview = "webview://";
                    String peony = "peony://";
                    if (content.contains(webview)) {  //打开webview
                        NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 2, content.substring(webview.length() + 1, content.length() - 1));
                    } else if (content.contains(peony)) {   //打开原生页面
                        NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 1, content.substring(peony.length() + 1, content.length() - 1));
                    }
                });
                mTvTip.setText(span);
            }else {
                mFlTip.setVisibility(View.GONE);
            }
            //签名
            mTvSign.setText(TextUtils.isEmpty(card.desc) ? "这个人很懒，暂时没有个性签名～" : card.desc);
            //有照片
            if (card.quick_picture != null && card.quick_picture.size() > 0) {
                mTvHome.setVisibility(View.VISIBLE);
                mViewBottom.setVisibility(View.VISIBLE);
                mRvPhoto.setVisibility(View.VISIBLE);
                PhotoAdapter adapter = new PhotoAdapter(mRvPhoto, card.quick_picture);
                mRvPhoto.setAdapter(adapter);
            } else {
                mTvHome.setVisibility(View.GONE);
                mViewBottom.setVisibility(View.GONE);
                mRvPhoto.setVisibility(View.GONE);
            }
            mIvAudio.setOnClickListener(v -> {
                if (mIjkPlayer == null) {
                    mIjkPlayer = new AudioPlayer(context);
                }
                mIjkPlayer.setDataSource(card.voice);
                if (mIjkPlayer != null) {
                    if (mIjkPlayer.isPlaying()) {
                        mIjkPlayer.stop();
                        mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
                    } else {
                        mIjkPlayer.start(AudioManager.STREAM_MUSIC);
                    }
                }
            });
        }
        findViewById(R.id.message_item_user).setOnClickListener(v -> {
            if (NimUIKitImpl.getSessionListener() != null && context != null && message != null)
                NimUIKitImpl.getSessionListener().onCardClicked(context, message);
        });
    }


    public class PhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PhotoAdapter(RecyclerView recyclerView, List<String> data) {
            super(recyclerView, R.layout.nim_message_item_user_info_photo, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item, int position, boolean isScrolling) {
            ImageView typeImage = helper.getView(R.id.message_item_img);
            RequestOptions options = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(6)))
                    .dontAnimate();          //缓存全尺寸
            Glide.with(context)              //配置上下文
                    .asDrawable()
                    .apply(options)
                    .load(item)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(typeImage);

        }
    }


    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
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
