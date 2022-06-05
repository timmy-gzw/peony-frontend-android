package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.VoiceChatApplyAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.RoomApplyDto;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.widget.pop.BaseBottomPop;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 语音房申请
 */
public class VoiceChatApplyPopWindow extends BaseBottomPop implements View.OnClickListener {
    private TextView mTvTitle;
    private VoiceChatApplyAdapter mAdapter;
    private final UserProviderService userService;
    private final ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;
    private View mNotDataView;

    public VoiceChatApplyPopWindow(Context context) {
        super(context);
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }

    private void initUI() {
        mTvTitle = findViewById(R.id.tv_title);
        RecyclerView mRvApply = findViewById(R.id.rv_apply);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvApply.setLayoutManager(layoutManager);
        mNotDataView = getContext().getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mRvApply.getParent(), false);
        TextView tvEmpty = mNotDataView.findViewById(R.id.tv_empty);
        tvEmpty.setText("还没有排麦哦～");
        mAdapter = new VoiceChatApplyAdapter(userService.getUserId());
        mRvApply.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.tv_agree, R.id.tv_reject);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            RoomApplyDto data = mAdapter.getData().get(position);
            if (data != null) {
                if (data.user_id == userService.getUserId()) {   //自己
                    if (view.getId() == R.id.tv_reject) {   //取消申请
                        apply(data.id, data.user_id, 3);
                    }
                } else {   //管理员操作
                    if (view.getId() == R.id.tv_reject) {   // 拒绝申请
                        apply(data.id, data.user_id, 2);
                    } else if (view.getId() == R.id.tv_agree) {  //同意申请
                        apply(data.id, data.user_id, 1);
                    }
                }
            }
        });
        getApplyList();
    }


    private void getApplyList() {
        mCompositeDisposable.add(chatApiService
                .getApplyList()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<RoomApplyDto>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<RoomApplyDto>> response) {
                        if (response.getData() != null && response.getData().size() > 0) {
                            mAdapter.setList(response.getData());
                            mTvTitle.setText("申请列表（" + response.getData().size() + "）");
                        } else {
                            mAdapter.setEmptyView(mNotDataView);
                        }
                    }
                }));
    }


    /**
     * @param applyId 申请ID
     * @param userId  用户ID
     * @param type    1-通过 2-拒绝 3-个人取消
     */
    private void apply(int applyId, int userId, int type) {
        mCompositeDisposable.add(chatApiService
                .apply(applyId, userId, type)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null && response.getData())
                            dismiss();
                    }
                }));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_voice_chat_apply);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    public interface OnSelectListener {
        void deleteContact();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
