package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyTimeChooseAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.PkTimeDto;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 选择PK时间弹窗
 */
public class ChoosePkTeamTimePopWindow extends BaseBottomPop implements View.OnClickListener {
    private final CompositeDisposable mCompositeDisposable;
    private final PartyApiService partyApiService;
    private final RecyclerView mRvTime;

    public ChoosePkTeamTimePopWindow(Context context) {
        super(context);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        mCompositeDisposable = new CompositeDisposable();
        partyApiService = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mRvTime = findViewById(R.id.rv_time);
        mRvTime.setLayoutManager(new LinearLayoutManager(mContext));
        getPkTime();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_choose_pk_team_time);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel)
            dismiss();
    }


    /**
     * 获取派对信息
     */
    public void getPkTime() {
        mCompositeDisposable.add(partyApiService.getPkTime()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<PkTimeDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PkTimeDto> response) {
                        if (response.getData() != null) {
                            PartyTimeChooseAdapter adapter = new PartyTimeChooseAdapter();
                            mRvTime.setAdapter(adapter);
                            adapter.setList(response.getData().getPk_duration());
                            adapter.setOnItemClickListener((adapter1, view, position) -> {
                                if (listener != null)
                                    listener.onSelectTime(adapter.getData().get(position));
                                dismiss();
                            });
                        }
                    }
                }));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    public interface OnSelectListener {
        void onSelectTime(PkTimeDto.PkTimeInfo data);

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
