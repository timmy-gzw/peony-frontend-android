package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.GradeIntroduceAdapter;
import com.tftechsz.mine.entity.dto.GradeIntroduceInfo;
import com.tftechsz.mine.entity.dto.GradeLevelDto;
import com.tftechsz.mine.mvp.IView.IGradeIntroduceView;
import com.tftechsz.mine.mvp.presenter.GradeIntroducePresenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 魅力土豪介绍
 */
public class GradeIntroduceActivity extends BaseMvpActivity<IGradeIntroduceView, GradeIntroducePresenter> implements IGradeIntroduceView {

    private final static String EXTRA_LEVEL_VALUE = "extra_level_value";
    private final static String EXTRA_TYPE = "type";

    private HeadImageView mIvAvatar;

    private UserInfo.LevelValue mLevelValue;
    private int mType;
    private ProgressBar mProgress;
    private TextView mTvStartLevel, mTvTip, mTvEndLevel, mTvContent;
    private TextView mTvType, mTvTotal, mTvLevel;
    private ImageView mIvType;
    private RecyclerView mRvPic;

    @Autowired
    UserProviderService service;

    public static void startActivity(Context context, int type, UserInfo.LevelValue levelValue) {
        Intent intent = new Intent(context, GradeIntroduceActivity.class);
        intent.putExtra(EXTRA_LEVEL_VALUE, levelValue);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public GradeIntroducePresenter initPresenter() {
        return new GradeIntroducePresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvStartLevel = findViewById(R.id.tv_start_level);
        mIvType = findViewById(R.id.iv_type);
        mTvTip = findViewById(R.id.tv_tip);
        mTvEndLevel = findViewById(R.id.tv_end_level);
        mTvContent = findViewById(R.id.tv_content);
        mTvLevel = findViewById(R.id.tv_level);
        mTvTotal = findViewById(R.id.tv_total);
        mTvType = findViewById(R.id.tv_type);
        mRvPic = findViewById(R.id.rv_pic);
        mProgress = findViewById(R.id.progress_bar);
        mProgress.setMax(100);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRvPic.setLayoutManager(layoutManager);
    }

    @Override
    protected void initData() {
        super.initData();
        mLevelValue = (UserInfo.LevelValue) getIntent().getSerializableExtra(EXTRA_LEVEL_VALUE);
        mType = getIntent().getIntExtra(EXTRA_TYPE, 0);
        GlideUtils.loadImage(this, mIvAvatar, service.getUserInfo().getIcon(), service.getUserInfo().getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);   //头像

        List<GradeIntroduceInfo> list = new ArrayList<>();
        list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_gift, mType == 1 ? "发礼物" : "收礼物"));
        list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_call, "语音视频呼叫"));
        list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_match_call, "语音速配"));
        list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_message, "私信聊天"));
        if (mType == 1) {
            new ToolBarBuilder().showBack(true)
                    .setTitle("我的财富等级")
                    .build();
            mTvType.setText("财富值");
            mIvType.setBackgroundResource(R.mipmap.mine_ic_rich_love);
            mIvAvatar.setBorderColor(Color.parseColor("#FDEFAB"));
            p.getGradeLevel("rich");
            mTvContent.setBackgroundResource(R.mipmap.mine_bg_rich_content);
        } else {
            new ToolBarBuilder().showBack(true)
                    .setTitle("我的魅力等级")
                    .build();
            mTvType.setText("魅力值");
            mIvType.setBackgroundResource(R.mipmap.mine_ic_charm_love);
            mTvContent.setBackgroundResource(R.mipmap.mine_bg_charm_content);
            list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_avatar_like, "头像被赞"));
            list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_comment_like, "动态被赞或评论"));
            list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_video_like, "语音签名被赞"));
            list.add(new GradeIntroduceInfo(R.mipmap.mine_ic_charm_pic_like, "照片被赞"));
            p.getGradeLevel("charm");
            mIvAvatar.setBorderColor(Color.parseColor("#FDE5FF"));
        }
        GradeIntroduceAdapter adapter = new GradeIntroduceAdapter(list);
        mRvPic.setAdapter(adapter);


    }

    @Override
    protected int getLayout() {
        return R.layout.activity_grade_introduce;
    }

    @Override
    public void getGradeIntroduceSuccess(GradeLevelDto data) {
        mTvStartLevel.setText("LV" + data.level);
        mTvTip.setText("距离升级还差：" + data.diff);
        mTvEndLevel.setText("LV" + (data.level + 1));
        mTvContent.setText(data.title);
        mTvLevel.setText(String.valueOf(data.level));
        mTvTotal.setText(String.valueOf(data.total));
        BigDecimal myl = new BigDecimal(data.total);
        BigDecimal next = new BigDecimal(data.total + "").add(new BigDecimal(data.diff + ""));
        BigDecimal progress = myl.divide(next, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
        mProgress.setProgress(progress.intValue());

    }
}
