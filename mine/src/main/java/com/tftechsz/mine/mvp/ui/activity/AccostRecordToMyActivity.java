package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.widget.pop.CustomEditPopWindow;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AccostVoiceAdapter;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.req.DelAccostSettingBean;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 我的语音
 */
@Route(path = ARouterApi.ACTIVITY_ACCOST_SETTING_AUDIO)
public class AccostRecordToMyActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView, View.OnClickListener {
    private final int RESULT_TO_ADD = 10000;
    private TextView mTopHint;
    private RecyclerView mRecy;
    private TextView mAddRecord;
    private TextView mEmpty;
    private AccostVoiceAdapter mAdapter;
    private int mAccostSize = 0;
    private String mChooseTitle;
    private MediaPlayer mMediaPlayer;  // 播放录音

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_record_tomy;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle(Interfaces.ACCOST_RECORD_TOMY).build();
        mTopHint = findViewById(R.id.ar_top_hint);
        mRecy = findViewById(R.id.recy);
        mEmpty = findViewById(R.id.accost_setting_empty);
        mAddRecord = findViewById(R.id.add_record);
    }

    @Override
    protected void initData() {
        mTopHint.setText("点击气泡可播放语音，长按语音可删除");
        mEmpty.setText("暂无语音，快去录制吧~");
        mTopHint.setVisibility(View.GONE);
        mRecy.setLayoutManager(new LinearLayoutManager(mContext));
        mAddRecord.setOnClickListener(this);
        p.getAccostSettingList(AccostType.VOICE);
        mAdapter = new AccostVoiceAdapter(null);
        mAdapter.addChildClickViewIds(R.id.iv_editor, R.id.ll_voice);
        mRecy.setAdapter(mAdapter);
        mAdapter.onAttachedToRecyclerView(mRecy);
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (mAdapter.getData().get(position) != null) {
                if (mAdapter.getData().get(position).is_show == 0) {
                    toastTip("审核未通过不允许删除");
                    return false;
                }
                CustomPopWindow customPopWindow = new CustomPopWindow(AccostRecordToMyActivity.this);
                customPopWindow.setContent("确定要删除“" + mAdapter.getData().get(position).title + "”这段语音么？");
                customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        List<Integer> id = new ArrayList<>();
                        id.add(mAdapter.getData().get(position).id);
                        p.delAccostSetting(position, new DelAccostSettingBean(id));
                    }
                });
                customPopWindow.showPopupWindow();
            }
            return false;
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            AccostSettingListBean listBean = mAdapter.getItem(position);
            if (listBean == null) return;
            if (view.getId() == R.id.iv_editor) {
                CustomEditPopWindow popWindow = new CustomEditPopWindow(AccostRecordToMyActivity.this);
                popWindow.setHintContent(listBean.title);
                popWindow.setEtLength(60);
                popWindow.setTitle("语音备注");
                popWindow.addOnClickListener(new CustomEditPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSure() {
                        mChooseTitle = popWindow.getContent();
                        if (TextUtils.isEmpty(mChooseTitle)) {
                            mChooseTitle = listBean.title;
                            return;
                        }
                        listBean.title = popWindow.getContent();
                        p.updateAccostSetting(listBean.id, position, AccostType.VOICE, listBean);
                    }
                });
                popWindow.showPopupWindow();
            } else if (view.getId() == R.id.ll_voice) {
                if (!TextUtils.isEmpty(listBean.url))
                    initMediaPlayer(listBean.url, position);
            }
        });
    }


    /**
     * 播放录音
     */
    private void initMediaPlayer(String url, int position) {
        try {
            if (mMediaPlayer == null)
                mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mAdapter.startCurrentPosition(position);
                mediaPlayer.start();
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                mMediaPlayer.pause();
                mAdapter.stopCurrentPosition();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_record) {
            if (mAccostSize >= 9) {
                toastTip("最多可添加9条搭讪语音");
                return;
            }
            Intent intent = new Intent(AccostRecordToMyActivity.this, AccostRecordToAddActivity.class);
            intent.putExtra("accostSize", mAccostSize);
            intent.putExtra("status", 0);
            startActivityForResult(intent, RESULT_TO_ADD);
        }
    }


    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {
        if (data != null && data.size() > 0) {
            mAccostSize = data.size();
            mAdapter.setList(data);
            mTopHint.setVisibility(View.VISIBLE);
            mRecy.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        } else {
            mTopHint.setVisibility(View.GONE);
            mRecy.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addAccostSettingSuccess() {

    }


    @Override
    public void addAccostSettingError() {

    }

    @Override
    public void updateAccostSettingSuccess(int position) {
        mAdapter.getData().get(position).title = mChooseTitle;
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void delAccostSettingSuccess(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.getData();
        mAccostSize = mAdapter.getData().size();

        if (mAdapter.getItemCount() > 0) {
            mTopHint.setVisibility(View.VISIBLE);
            mRecy.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        } else {
            mTopHint.setVisibility(View.GONE);
            mRecy.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_TO_ADD) {
            int status = data.getIntExtra("status", 0);
            if (status == 0) {  //添加
                p.getAccostSettingList(AccostType.VOICE);
            } else {  //更新

            }

        }
    }
}
