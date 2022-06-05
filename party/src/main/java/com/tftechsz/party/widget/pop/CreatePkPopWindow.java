package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyTeamAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.PartyPkSaveDto;
import com.tftechsz.party.entity.dto.PkTimeDto;
import com.tftechsz.party.entity.req.SavePkReq;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 创建PK弹窗
 */
public class CreatePkPopWindow extends BaseBottomPop implements View.OnClickListener {
    private TextView mTvCreatePk;
    private CommonItemView mItemPkTime;  //pk 时间
    private ChoosePkTeamPopWindow mPkTeamPopWindow;
    private ChoosePkTeamTimePopWindow mTimePopWindow;  //时间选择弹窗
    private PartyTeamAdapter mRedAdapter, mBlueAdapter;
    private List<VoiceRoomSeat> mRoomSeatList;  //房间内用户
    private VoiceRoomSeat mHostSeat;  //房间内主持
    protected CompositeDisposable mCompositeDisposable;
    private final PartyApiService partyApiService;
    private int mDuration = 15;
    private int mPartyId;  //partyID

    public CreatePkPopWindow(Context context, int partyId) {
        super(context);
        mPartyId = partyId;
        mCompositeDisposable = new CompositeDisposable();
        partyApiService = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        initView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mPkTeamPopWindow != null) {
                    if (mRoomSeatList != null && mRoomSeatList.size() > 0) {
                        int size = mRoomSeatList.size();
                        for (int i = 0; i < size; i++) {
                            mRoomSeatList.get(i).setSelect(false);
                            mRoomSeatList.get(i).setBlueSelect(false);
                        }
                    }
                    if (mHostSeat != null) {
                        mHostSeat.setBlueSelect(false);
                        mHostSeat.setSelect(false);
                    }
                    mPkTeamPopWindow.onDestroy();
                    mPkTeamPopWindow = null;
                }
            }
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_create_pk);
    }


    private void initView() {
        findViewById(R.id.iv_close).setOnClickListener(this);  //关闭弹窗
        mTvCreatePk = findViewById(R.id.tv_create_pk);
        mTvCreatePk.setOnClickListener(this);  //创建
        mItemPkTime = findViewById(R.id.item_pk_time);
        mItemPkTime.setOnClickListener(this);
        ImageView mIvRedAdd = findViewById(R.id.iv_red_add);
        mIvRedAdd.setOnClickListener(this);
        //红队添加，蓝队添加
        ImageView mIvBlueAdd = findViewById(R.id.iv_blue_add);
        mIvBlueAdd.setOnClickListener(this);
        RecyclerView mRvRed = findViewById(R.id.rv_red_team);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mRvRed.setLayoutManager(layoutManager);
        mRedAdapter = new PartyTeamAdapter();
        mRvRed.setAdapter(mRedAdapter);
        RecyclerView mRvBlue = findViewById(R.id.rv_blue_team);
        GridLayoutManager layoutManager1 = new GridLayoutManager(mContext, 2);
        mRvBlue.setLayoutManager(layoutManager1);
        mBlueAdapter = new PartyTeamAdapter();
        mRvBlue.setAdapter(mBlueAdapter);
        List<VoiceRoomSeat> list1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list1.add(new VoiceRoomSeat(i));
        }
        mRedAdapter.setList(list1);
        mBlueAdapter.setList(list1);
        mRedAdapter.addChildClickViewIds(R.id.iv_del);
        mRedAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_del) {
                if (mRedAdapter.getData().get(position) != null) {
                    mRedAdapter.getData().get(position).setSelect(false);
                    mRedAdapter.remove(mRedAdapter.getData().get(position));
                    if (mRedAdapter.getData().size() < 4) {
                        mRedAdapter.addData(new VoiceRoomSeat());
                    }
                }
            }
        });
        mBlueAdapter.addChildClickViewIds(R.id.iv_del);
        mBlueAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_del) {
                if (mBlueAdapter.getData().get(position) != null) {
                    mBlueAdapter.getData().get(position).setBlueSelect(false);
                    mBlueAdapter.remove(mBlueAdapter.getData().get(position));
                    if (mBlueAdapter.getData().size() < 4) {
                        mBlueAdapter.addData(new VoiceRoomSeat());
                    }
                }
            }
        });
        mBlueAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (mPkTeamPopWindow == null)
                mPkTeamPopWindow = new ChoosePkTeamPopWindow(mContext, 2, mRoomSeatList, mHostSeat);
            mPkTeamPopWindow.setTeamType(2);
            mPkTeamPopWindow.showPopupWindow();
            addOnClick();
        });
        mRedAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (mPkTeamPopWindow == null)
                mPkTeamPopWindow = new ChoosePkTeamPopWindow(mContext, 1, mRoomSeatList, mHostSeat);
            mPkTeamPopWindow.setTeamType(1);
            mPkTeamPopWindow.showPopupWindow();
            addOnClick();
        });
        getPkTime();
    }


    private void addOnClick() {
        mPkTeamPopWindow.addOnClickListener(new ChoosePkTeamPopWindow.OnSelectListener() {
            @Override
            public void onSelectMember(int teamType, List<VoiceRoomSeat> list) {
                if (teamType == 1) {   //红队
                    int size = 4 - list.size();
                    for (int i = 0; i < size; i++) {
                        list.add(new VoiceRoomSeat());
                    }
                    mRedAdapter.setList(list);
                } else {
                    int size = 4 - list.size();
                    for (int i = 0; i < size; i++) {
                        list.add(new VoiceRoomSeat());
                    }
                    mBlueAdapter.setList(list);
                }
                if (mRedAdapter.getData().size() <= 0 || mBlueAdapter.getData().size() <= 0) {
                    mTvCreatePk.setBackgroundResource(R.drawable.bg_gray_radius25);
                } else {
                    mTvCreatePk.setBackgroundResource(R.drawable.bg_orange_radius25);
                }
            }
        });
    }

    /**
     * 设置房间的用户
     */
    public void setRoomSeat(List<VoiceRoomSeat> list, VoiceRoomSeat seat) {
        mRoomSeatList = list;
        mHostSeat = seat;
    }


    /**
     * 获取派对信息
     */
    public void startSave() {
        List<Integer> red = new ArrayList<>();
        List<Integer> blue = new ArrayList<>();
        if ((mRedAdapter == null || mRedAdapter.getData().size() <= 0) || (mBlueAdapter == null || mBlueAdapter.getData().size() <= 0)) {
            return;
        }
        for (int i = 0; i < mRedAdapter.getData().size(); i++) {
            if (mRedAdapter.getData().get(i).getUser_id() != 0)
                red.add(mRedAdapter.getData().get(i).getUser_id());
        }
        for (int i = 0; i < mBlueAdapter.getData().size(); i++) {
            if (mBlueAdapter.getData().get(i).getUser_id() != 0)
                blue.add(mBlueAdapter.getData().get(i).getUser_id());
        }
        SavePkReq startPkReq = new SavePkReq();
        startPkReq.blue = blue;
        startPkReq.red = red;
        startPkReq.duration = mDuration;
        startPkReq.party_id = mPartyId;
        mCompositeDisposable.add(partyApiService.startSave(startPkReq)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<PartyPkSaveDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyPkSaveDto> response) {
                        if (response.getData() != null) {
                            if (listener != null)
                                listener.savePkSuccess(response.getData().pk_info_id, startPkReq);
                            dismiss();
                        }
                    }
                }));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {  //关闭
            dismiss();
        } else if (id == R.id.tv_create_pk) {  //创建PK
            int redSize = 0;
            int blueSize = 0;
            for (int i = 0; i < mRedAdapter.getData().size(); i++) {
                if (mRedAdapter.getData().get(i).getUser_id() != 0)
                    redSize += 1;
            }
            for (int i = 0; i < mBlueAdapter.getData().size(); i++) {
                if (mBlueAdapter.getData().get(i).getUser_id() != 0)
                    blueSize += 1;
            }
            if (redSize <= 0 || blueSize <= 0) {
                Utils.toast("请选择PK成员");
                return;
            }
            startSave();
        } else if (id == R.id.item_pk_time) {  //pk 时间
            if (null == mTimePopWindow)
                mTimePopWindow = new ChoosePkTeamTimePopWindow(mContext);
            mTimePopWindow.addOnClickListener(new ChoosePkTeamTimePopWindow.OnSelectListener() {
                @Override
                public void onSelectTime(PkTimeDto.PkTimeInfo data) {
                    mDuration = data.getMinute();
                    mItemPkTime.setRightText(data.getMinute() + data.getText());
                }
            });
            mTimePopWindow.showPopupWindow();
        } else if (id == R.id.item_pk_rule) {  //pk 规则

        } else if (id == R.id.iv_red_add) {  //红队添加
            showPkTeamPop(1);
        } else if (id == R.id.iv_blue_add) {  //蓝队添加
            showPkTeamPop(2);
        }
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
                            if (response.getData().getPk_duration() != null && response.getData().getPk_duration().size() > 0) {
                                mDuration = response.getData().getPk_duration().get(0).getMinute();
                                mItemPkTime.setRightText(response.getData().getPk_duration().get(0).getMinute() + response.getData().getPk_duration().get(0).getText());
                            }
                        }
                    }
                }));
    }


    /**
     * 显示弹窗
     */
    private void showPkTeamPop(int teamType) {
        if (mPkTeamPopWindow == null)
            mPkTeamPopWindow = new ChoosePkTeamPopWindow(mContext, teamType, mRoomSeatList, mHostSeat);
        if (mRedAdapter != null && teamType == 1) {
            mRedAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (mPkTeamPopWindow == null)
                    mPkTeamPopWindow = new ChoosePkTeamPopWindow(mContext, 1, mRoomSeatList, mHostSeat);
                mPkTeamPopWindow.setTeamType(1);
                mPkTeamPopWindow.showPopupWindow();
                addOnClick();
            });
        }
        if (mBlueAdapter != null && teamType == 2) {
            mPkTeamPopWindow.setTeamType(teamType);
            mPkTeamPopWindow.showPopupWindow();
        }

    }


    public interface OnSelectListener {
        void savePkSuccess(int pk_info_id, SavePkReq savePkReq);

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
