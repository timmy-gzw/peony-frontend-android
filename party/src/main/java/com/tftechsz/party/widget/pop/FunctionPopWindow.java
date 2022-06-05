package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.PartyInfoDto;
import com.tftechsz.common.entity.PartyPermission;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.FunctionPopAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.JoinPartyDto;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 功能弹窗
 */
public class FunctionPopWindow extends BaseBottomPop implements View.OnClickListener {

    RecyclerView recyclerView;
    TextView mTvPlayText;
    TextView mIvClickPk;
    GridLayoutManager gridLayoutManager;
    IFunctionListener iFunctionListener;
    FunctionPopAdapter mAdapter;
    List<PartyPermission> mListPartyManager;
    private int mTag;
    //是否是房管
    private final boolean mFlagIsAni;
    //是否静音
    private boolean mIsSilence;
    protected int mFightPattern = 1;  //当前模式  1:普通模式  2：pk 模式
    private TextView mTvFunText;
    protected CompositeDisposable mCompositeDisposable;
    public PartyApiService service;
    public int roomId;
    public String userId;
    public TextView mTvClickCancel;
    private JoinPartyDto mData;

    //权限菜单列表
    public List<PartyPermission> mPermissions;

    public FunctionPopWindow(Context context, boolean mIsSilence, IFunctionListener iFunctionListener, int fightPattern, boolean isAni, int roomId, int tag, String userId, JoinPartyDto data) {
        super(context);
        this.mData = data;
        this.mIsSilence = mIsSilence;
        mFightPattern = fightPattern;
        mFlagIsAni = isAni;
        mContext = context;
        this.roomId = roomId;
        this.mTag = tag;
        this.iFunctionListener = iFunctionListener;
        mCompositeDisposable = new CompositeDisposable();
        this.userId = userId;
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        initUI();
        if (mTag == 1) {
            net();
        } else {
            netPermissions();
        }

    }

    public FunctionPopWindow(Context context, boolean mIsSilence, IFunctionListener iFunctionListener, int fightPattern, boolean isAni, int roomId, int tag, JoinPartyDto data) {
        super(context);
        this.mData = data;
        this.mIsSilence = mIsSilence;
        mFightPattern = fightPattern;
        mFlagIsAni = isAni;
        mContext = context;
        this.roomId = roomId;
        this.mTag = tag;
        this.iFunctionListener = iFunctionListener;
        mCompositeDisposable = new CompositeDisposable();
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        initUI();
        net();
    }


    public void net() {
        mCompositeDisposable.add(service.partyListFunc(roomId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<PartyPermission>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<PartyPermission>> response) {
                        if (response.getData() == null) return;
                        FunctionPopWindow.this.mListPartyManager = response.getData();
                        initFunctionMenuManager(mFightPattern, mTag, userId, mData, false, null);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }


    public void netPermissions() {
        mCompositeDisposable.add(new RetrofitManager()
                .createPartyApi(PublicService.class)
                .getPartyUserInfo(userId, String.valueOf(roomId))
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<PartyInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyInfoDto> response) {
                        if (null != response.getData() && response.getData().permissions != null) {
                            setPermissionView(response.getData().permissions);
                        }
                    }
                }));

    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_function);
    }

    private void initUI() {
        mTvFunText = findViewById(R.id.tv_funtext);
        recyclerView = findViewById(R.id.rv_party);
        mTvClickCancel = findViewById(R.id.tv_click_cancel);
        mTvClickCancel.setOnClickListener(this);
        mTvPlayText = findViewById(R.id.tv_play_method);
        mIvClickPk = findViewById(R.id.img_play_click);
        mAdapter = new FunctionPopAdapter(mTag, mIsSilence, mData != null && mData.getRoom() != null ? mData.getRoom().getLove_switch() : 0, (mData != null && mData.getRoom() != null && mData.getRoom().getDress_switch() == 1 && mData.getRoom().getGift_switch() == 1));
        mAdapter.onAttachedToRecyclerView(recyclerView);
        findViewById(R.id.img_play_click).setOnClickListener(v -> iFunctionListener.pk());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            onItemClicks(position);

        });

    }

    /**
     * 修改爱意值属性
     */
    public void updateLoveData(int value) {
        mAdapter.updateLoveValue(value);
    }

    /**
     * 修改特效管理属性
     */
    public void updateSpecialData(boolean value) {
        mAdapter.updateSpecial(value);
    }

    private void onItemClicks(int position) {
        if (mTag == 1) {
            switch (mListPartyManager.get(position).id) {
                case 15:
                    iFunctionListener.aiYiZhi(mData != null && mData.getRoom() != null ? mData.getRoom().getLove_switch() : 0);
                    return;
                case 16:
                    iFunctionListener.set();
                    break;
                case 17:
                    iFunctionListener.manager();
                    break;
                case 18:
                    iFunctionListener.music();
                    break;
                case 19:
                    iFunctionListener.share();
                    break;
                case 21:
                    mIsSilence = !mIsSilence;
                    mAdapter.updateSilence(mIsSilence);
                    iFunctionListener.silence();
                    return;
                case 20:
                    iFunctionListener.dressUp();
                    break;
                case 25:
                    iFunctionListener.specialEffects();
                    break;
                case 26:  //耳机监听
                    iFunctionListener.earphoneSetting();
                    break;
            }
        } else {
            if (mAdapter == null || mAdapter.getData() == null) {
                return;
            }
            switch (mAdapter.getData().get(position).key) {
                case "is_room_manager":
                    iFunctionListener.setHoursManager(mAdapter.getData().get(position).value == 2);
                    break;
                case "on_microphone":
                    iFunctionListener.onMai(mAdapter.getData().get(position).value == 2);
                    break;
                case "is_kicked":
                    iFunctionListener.outRoom(mAdapter.getData().get(position).value == 2);
                    break;
                case "is_black":
                    iFunctionListener.blacklist(mAdapter.getData().get(position).value == 2);
                    break;
                case "is_muted":
                    iFunctionListener.roomShut(mAdapter.getData().get(position).value == 2);
                    break;
            }
        }

        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    /**
     * 0  管理权限
     * 1  功能
     *
     * @param tag
     */
    public void initFunctionMenuManager(int fightPattern, int tag, String userId, JoinPartyDto mData, boolean isNet, List<PartyPermission> permissionList) {
        this.mFightPattern = fightPattern;
        this.mTag = tag;
        this.userId = userId;
        this.mData = mData;
        if (mTag == 0) {
            setPermissionView(permissionList);
            mTvFunText.setText("权限管理");
            mTvClickCancel.setVisibility(View.VISIBLE);
            netPermissions();
        } else if (mTag == 1) {
            if (isNet) {
                net();
            }
            setFuncView();
            mTvFunText.setText("功能");
            mTvClickCancel.setVisibility(View.GONE);

        }
        mAdapter.setTag(tag);

    }


    private void setPermissionView(List<PartyPermission> permissionList) {
        mTvPlayText.setVisibility(View.GONE);
        mIvClickPk.setVisibility(View.GONE);
        gridLayoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
//        mAdapter.setEmptyView(View.inflate(mContext, R.layout.base_empty_view, null));
        this.mPermissions = permissionList;
        mAdapter.setList(permissionList);
    }


    private void setFuncView() {
        if (mFlagIsAni) {
            mTvPlayText.setVisibility(View.VISIBLE);
            mIvClickPk.setVisibility(View.VISIBLE);
            if (mFightPattern == 1) {
                mIvClickPk.setText("PK模式");
            } else {
                mIvClickPk.setText("关闭PK");
            }
        } else {
            mTvPlayText.setVisibility(View.GONE);
            mIvClickPk.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(mContext, 5);
        recyclerView.setLayoutManager(gridLayoutManager);
        mAdapter.setList(mListPartyManager);
//        mAdapter.setEmptyView(View.inflate(mContext, R.layout.base_empty_view, null));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_click_cancel) {
            dismiss();
        }

    }

    public interface IFunctionListener {
        void music();

        void set();

        void setHoursManager(boolean isManager);

        void onMai(boolean isUp);

        void outRoom(boolean isKi);

        void roomShut(boolean isMute);

        void blacklist(boolean isBlack);

        // love_switch;   //爱意值 开关  0 : 开  1：关
        void aiYiZhi(int open);

        void manager();

        void share();

        void dressUp();

        void silence();

        void pk();

        void outSuccess(int type);

        void specialEffects();

        void earphoneSetting();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();

    }

}
