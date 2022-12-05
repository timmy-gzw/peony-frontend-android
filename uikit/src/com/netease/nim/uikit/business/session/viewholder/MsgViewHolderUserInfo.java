package com.netease.nim.uikit.business.session.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;


/**
 * 用户信息
 */
public class MsgViewHolderUserInfo extends MsgViewHolderBase {

    private LinearLayout mLlHometown, mLlTag, mLlConstellation, mLlAll;
    private TextView mTvHometown, mTvConstellation;
    private ImageView mIvReal, mIvSelf;
    private RecyclerView mRvPhoto, mRvTag;
    private RecyclerView mRvUserinfo;

    public MsgViewHolderUserInfo(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_user_info;
    }

    @Override
    public void inflateContentView() {
        mLlHometown = findViewById(R.id.ll_hometown);
        mLlTag = findViewById(R.id.ll_tag);
        mLlConstellation = findViewById(R.id.ll_constellation);
        mTvHometown = findViewById(R.id.tv_hometown);
        mTvConstellation = findViewById(R.id.tv_constellation);
        mLlAll = findViewById(R.id.ll_all);
        mIvReal = findViewById(R.id.iv_real);
        mIvSelf = findViewById(R.id.iv_self);
        mRvPhoto = findViewById(R.id.rv_photo);
        mRvUserinfo = findViewById(R.id.rv_userinfo);
        mRvTag = findViewById(R.id.rv_tag);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        mRvPhoto.setLayoutManager(gridLayoutManager);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context, FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        mRvUserinfo.setLayoutManager(layoutManager);

        FlexboxLayoutManager layoutManager1 = new FlexboxLayoutManager(context, FlexDirection.ROW);
        layoutManager1.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager1.setAlignItems(AlignItems.FLEX_START);
        mRvTag.setLayoutManager(layoutManager1);

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
            //有照片
            if (card.picture != null && card.picture.size() > 0) {
                mRvPhoto.setVisibility(View.VISIBLE);
                PhotoAdapter adapter = new PhotoAdapter();
                adapter.setList(card.picture);
                mRvPhoto.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        if (NimUIKitImpl.getSessionListener() != null && context != null && message != null)
                            NimUIKitImpl.getSessionListener().onCardPhotoClicked(context, message, position, card.picture.get(position), card.picture);
                    }
                });
            } else {
                mRvPhoto.setVisibility(View.GONE);
            }
            UserInfoAdapter adapter = new UserInfoAdapter();
            adapter.setList(card.tags);
            mRvUserinfo.setAdapter(adapter);

            if (card.tag_list != null && card.tag_list.size() > 0) {
                TagAdapter tagAdapter = new TagAdapter();
                tagAdapter.setList(card.tag_list);
                mRvTag.setAdapter(tagAdapter);
                mLlTag.setVisibility(View.VISIBLE);
            } else {
                mLlTag.setVisibility(View.GONE);
            }
            mIvReal.setVisibility(card.is_real == 0 ? View.GONE : View.VISIBLE);
            mIvSelf.setVisibility(card.is_self == 0 ? View.GONE : View.VISIBLE);
            mLlConstellation.setVisibility(TextUtils.isEmpty(card.star_sign) ? View.GONE : View.VISIBLE);
            mTvConstellation.setText(card.star_sign);
            mLlHometown.setVisibility(TextUtils.isEmpty(card.hometown) ? View.GONE : View.VISIBLE);
            mTvHometown.setText(card.hometown);
            mLlAll.setVisibility(TextUtils.isEmpty(card.star_sign) && TextUtils.isEmpty(card.hometown) ? View.GONE : View.VISIBLE);

        }

        findViewById(R.id.message_item_user).setOnClickListener(v -> {
            if (NimUIKitImpl.getSessionListener() != null && context != null && message != null)
                NimUIKitImpl.getSessionListener().onCardClicked(context, message);
        });
    }


    public class PhotoAdapter extends BaseQuickAdapter<String, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

        public PhotoAdapter() {
            super(R.layout.nim_message_item_user_info_photo);
        }

        @Override
        protected void convert(@NonNull com.chad.library.adapter.base.viewholder.BaseViewHolder helper, String s) {
            ImageView typeImage = helper.getView(R.id.message_item_img);
            RequestOptions options = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(16)))
                    .dontAnimate();          //缓存全尺寸
            Glide.with(context)              //配置上下文
                    .asDrawable()
                    .placeholder(R.drawable.ic_default_avatar)
                    .apply(options)
                    .load(s)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(typeImage);
        }
    }


    public static class UserInfoAdapter extends BaseQuickAdapter<String, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

        public UserInfoAdapter() {
            super(R.layout.nim_message_item_user_info_user);
        }

        @Override
        protected void convert(@NonNull com.chad.library.adapter.base.viewholder.BaseViewHolder helper, String s) {
            TextView tvInfo = helper.getView(R.id.tv_info);
            tvInfo.setText(s);
        }

    }


    public static class TagAdapter extends BaseQuickAdapter<String, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

        public TagAdapter() {
            super(R.layout.nim_message_item_user_tag);
        }

        @Override
        protected void convert(@NonNull com.chad.library.adapter.base.viewholder.BaseViewHolder helper, String s) {
            TextView tvInfo = helper.getView(R.id.tv_info);
            tvInfo.setText(s);
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
