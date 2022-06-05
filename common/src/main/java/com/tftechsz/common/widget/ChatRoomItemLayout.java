package com.tftechsz.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.netease.nim.uikit.api.NimUIKit;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;

import io.reactivex.disposables.CompositeDisposable;

public class ChatRoomItemLayout extends LinearLayout  {
    public final String TAG = ChatRoomItemLayout.class.getSimpleName();
    private ImageView mIvRoomAvatar;
    private TextView mTvRoomName,mTvRoomContent;
    private Context mContext;
    private ChatHistoryDto.RoomInfo mRoomInfo;  //聊天室信息
    protected CompositeDisposable mCompositeDisposable;
    public ChatRoomItemLayout(Context context) {
        super(context);
        init(context);
    }

    public ChatRoomItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatRoomItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_chat_room, this);
        mCompositeDisposable = new CompositeDisposable();
        mIvRoomAvatar = findViewById(R.id.ci_room_avatar);
        mTvRoomName = findViewById(R.id.tv_room_name);
        mTvRoomContent = findViewById(R.id.tv_room_content);
        ConstraintLayout clRoom = findViewById(R.id.rl_room);
        clRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });


    }


    public void setData(ChatHistoryDto.RoomInfo data){
        mRoomInfo = data;
        GlideUtils.loadRoundImage(mContext,mIvRoomAvatar,data.icon,6);
        mTvRoomName.setText(data.room_name);
        mTvRoomContent.setText(data.room_des);

    }



    /**
     * 进入聊天室
     */
    private void joinRoom() {
        if(mRoomInfo == null)
            return;
        mCompositeDisposable.add(new RetrofitManager().createChatRoomApi(PublicService.class).joinRoom(mRoomInfo.room_id)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ARouterUtils.toChatTeamActivity(mRoomInfo.room_id, NimUIKit.getCommonTeamSessionCustomization(), null,1);
                    }
                }));

    }


}
