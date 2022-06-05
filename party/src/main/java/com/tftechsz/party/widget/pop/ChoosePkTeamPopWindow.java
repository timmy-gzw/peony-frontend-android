package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.ChoosePkTeamAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择队伍弹窗
 */
public class ChoosePkTeamPopWindow extends BaseCenterPop implements View.OnClickListener {
    private int mTeamType;  // 1:红队 2:蓝队
    private final ImageView mIvAvatar, mIvChoose;
    private final TextView mTvTeamTitle, mTvAdd;  //标题,添加
    private final ChoosePkTeamAdapter mAdapter;
    private final List<VoiceRoomSeat> mChooseRedMember = new ArrayList<>();
    private final List<VoiceRoomSeat> mChooseBlueMember = new ArrayList<>();
    private final List<VoiceRoomSeat> mRoomSeatList;  //房间内用户
    private final VoiceRoomSeat mHostSeat;

    public ChoosePkTeamPopWindow(Context context, int teamType, List<VoiceRoomSeat> roomSeatList, VoiceRoomSeat hostSeat) {
        super(context);
        mTeamType = teamType;
        mRoomSeatList = roomSeatList;
        mHostSeat = hostSeat;
        mTvTeamTitle = findViewById(R.id.tv_team_title);
        mTvAdd = findViewById(R.id.tv_add);
        mTvAdd.setOnClickListener(this);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvAvatar.setOnClickListener(this);
        mIvChoose = findViewById(R.id.iv_choose);
        mIvChoose.setOnClickListener(this);
        RecyclerView mRvTeam = findViewById(R.id.rv_team);
        findViewById(R.id.iv_close).setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        mRvTeam.setLayoutManager(gridLayoutManager);
        mAdapter = new ChoosePkTeamAdapter();
        mRvTeam.setAdapter(mAdapter);
        mAdapter.setTeamType(mTeamType);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_choose_pk_team);
    }


    /**
     * 设置队类型
     */
    public void setTeamType(int teamType) {
        this.mTeamType = teamType;
        mTvTeamTitle.setText(mTeamType == 1 ? "选择红队成员" : "选择蓝队成员");
        mTvAdd.setBackgroundResource(mTeamType == 1 ? R.drawable.bg_pk_red_team_add : R.drawable.bg_pk_blue_team_add);
        if (mHostSeat != null) {
            if (mHostSeat.getUser_id() != 0) {
                if (mHostSeat.getIcon().startsWith("http"))
                    GlideUtils.loadCircleImage(mContext, mIvAvatar, mHostSeat.getIcon(), R.drawable.party_ic_member_empty);
                else {
                    GlideUtils.loadCircleImage(mContext, mIvAvatar, CommonUtil.getHttpUrlHead() + mHostSeat.getIcon(), R.drawable.party_ic_member_empty);
                }
            } else {
                mHostSeat.setSelect(false);
                mHostSeat.setBlueSelect(false);
                mIvAvatar.setImageResource(R.drawable.party_ic_member_empty);
                mIvChoose.setVisibility(View.INVISIBLE);
            }
        } else {
            mIvChoose.setVisibility(View.INVISIBLE);
        }
        if (mAdapter != null) {
            mAdapter.setTeamType(mTeamType);
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                VoiceRoomSeat item = mAdapter.getData().get(position);
                if (item == null || item.getUser_id() == 0)
                    return;
                int num = getSelectNum();
                //红队
                if (mTeamType == 1) {
                    //蓝队选中后不可点击
                    if (item.isBlueSelect()) {
                        Utils.toast("该成员已被选中");
                        return;
                    }
                    //是否选中
                    if (item.isSelect()) {
                        item.setSelect(!item.isSelect());
                        mAdapter.setData(position, item);
                        num -= 1;
                    } else {
                        if (num >= 4) {
                            Utils.toast("最多只能选择4个哦");
                            return;
                        } else {
                            num += 1;
                            item.setSelect(!item.isSelect());
                            mAdapter.setData(position, item);
                        }
                    }
                } else { //蓝队
                    //是否选中
                    if (item.isSelect()) {
                        Utils.toast("该成员已被选中");
                        return;
                    }
                    if (item.isBlueSelect()) {
                        item.setBlueSelect(!item.isBlueSelect());
                        mAdapter.setData(position, item);
                        num -= 1;
                    } else {
                        if (num >= 4) {
                            Utils.toast("最多只能选择4个哦");
                            return;
                        } else {
                            num += 1;
                            item.setBlueSelect(!item.isBlueSelect());
                            mAdapter.setData(position, item);
                        }
                    }
                }
                if (num == 0) {
                    mTvAdd.setText("添加");
                    mTvAdd.setEnabled(false);
                } else {
                    mTvAdd.setEnabled(true);
                    mTvAdd.setText("添加(" + num + ")");
                }
            });
            if (mRoomSeatList != null && mRoomSeatList.size() > 0) {
                mAdapter.setList(mRoomSeatList);
            }
            mIvAvatar.setAlpha(1f);
            mIvChoose.setVisibility(View.INVISIBLE);
            if (mTeamType == 1 && mHostSeat != null && mHostSeat.isBlueSelect()) {
                mIvAvatar.setAlpha(0.5f);
            }
            if (mTeamType == 2 && mHostSeat != null && mHostSeat.isSelect()) {
                mIvAvatar.setAlpha(0.5f);
            }
            int num = getSelectNum();
            if (num == 0) {
                mTvAdd.setText("添加");
                mTvAdd.setEnabled(false);
            } else {
                mTvAdd.setText("添加(" + num + ")");
                mTvAdd.setEnabled(true);
            }
        }
    }


    /**
     * 获取选中数量
     */
    private int getSelectNum() {
        int num = 0;
        if (mTeamType == 1 && mHostSeat.isSelect()) {
            num += 1;
            mIvChoose.setImageResource(R.drawable.party_ic_red_choose);
            mIvChoose.setVisibility(View.VISIBLE);
        }
        if (mTeamType == 2 && mHostSeat.isBlueSelect()) {
            num += 1;
            mIvChoose.setImageResource(R.drawable.party_ic_blue_choose);
            mIvChoose.setVisibility(View.VISIBLE);
        }
        int size = mAdapter.getData().size();
        for (int i = 0; i < size; i++) {
            if (mAdapter.getData().get(i).isSelect() && mTeamType == 1) {
                num += 1;
            }
            if (mAdapter.getData().get(i).isBlueSelect() && mTeamType == 2) {
                num += 1;
            }
        }
        return num;
    }

    /**
     * 设置选中的用户
     */
    public void setChooseMember(List<VoiceRoomSeat> chooseMember, List<VoiceRoomSeat> chooseBlueMember) {
        mChooseRedMember.clear();
        mChooseBlueMember.clear();
        if (chooseMember != null && chooseMember.size() > 0) {
            mChooseRedMember.addAll(chooseMember);
            for (int i = 0; i < chooseMember.size(); i++) {
                for (int j = 0; j < mRoomSeatList.size(); j++) {
                    if (mRoomSeatList.get(j).getUser_id() != 0 && (chooseMember.get(i).getUser_id() == mRoomSeatList.get(j).getUser_id()) && chooseMember.get(i).isSelect()) {
                        mRoomSeatList.get(j).setSelect(true);
                    } else {
                        mRoomSeatList.get(j).setSelect(false);
                    }
                }
            }
        }
        if (chooseBlueMember != null && chooseBlueMember.size() > 0) {
            mChooseBlueMember.addAll(chooseBlueMember);
            for (int i = 0; i < chooseBlueMember.size(); i++) {
                for (int j = 0; j < mRoomSeatList.size(); j++) {
                    if (mRoomSeatList.get(j).getUser_id() != 0 && (chooseBlueMember.get(i).getUser_id() == mRoomSeatList.get(j).getUser_id()) && chooseBlueMember.get(i).isBlueSelect()) {
                        mRoomSeatList.get(j).setBlueSelect(true);
                    } else {
                        mRoomSeatList.get(j).setSelect(false);
                    }
                }
            }
        }
    }

    /**
     * 设置选中的用户
     */
    public void setChooseMember(List<VoiceRoomSeat> chooseMember) {
        mChooseRedMember.clear();
        if (chooseMember != null && chooseMember.size() > 0) {
            mChooseRedMember.addAll(chooseMember);
            for (int i = 0; i < chooseMember.size(); i++) {
                for (int j = 0; j < mRoomSeatList.size(); j++) {
                    if (mRoomSeatList.get(j).getUser_id() != 0 && (chooseMember.get(i).getUser_id() == mRoomSeatList.get(j).getUser_id()) && chooseMember.get(i).isSelect()) {
                        mRoomSeatList.get(j).setSelect(true);
                    } else {
                        mRoomSeatList.get(j).setSelect(false);
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_add) {
            mChooseRedMember.clear();
            mChooseBlueMember.clear();
            if (listener != null) {
                //红队确定
                if (mTeamType == 1) {
                    if (mHostSeat.isSelect())
                        mChooseRedMember.add(mHostSeat);
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (mAdapter.getData().get(i).isSelect()) {
                            mChooseRedMember.add(mAdapter.getData().get(i));
                        }
                    }
                    listener.onSelectMember(mTeamType, mChooseRedMember);
                } else {
                    if (mHostSeat.isBlueSelect())
                        mChooseBlueMember.add(mHostSeat);
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (mAdapter.getData().get(i).isBlueSelect()) {
                            mChooseBlueMember.add(mAdapter.getData().get(i));
                        }
                    }
                    listener.onSelectMember(mTeamType, mChooseBlueMember);
                }
                mChooseRedMember.clear();
                mChooseBlueMember.clear();
                dismiss();
            }
        } else if (id == R.id.iv_avatar) {  //选主持
            if (mHostSeat == null || mHostSeat.getUser_id() == 0)
                return;
            int num = getSelectNum();
            if (mTeamType == 1) {
                if (mHostSeat.isBlueSelect()) {
                    Utils.toast("该成员已被选中");
                    return;
                }
                //是否选中
                if (mHostSeat.isSelect()) {
                    mHostSeat.setSelect(!mHostSeat.isSelect());
                    num -= 1;
                } else {
                    if (num >= 4) {
                        Utils.toast("最多只能选择4个哦");
                        return;
                    } else {
                        num += 1;
                        mHostSeat.setSelect(!mHostSeat.isSelect());
                    }
                }
            } else {
                if (mHostSeat.isSelect()) {
                    Utils.toast("该成员已被选中");
                    return;
                }
                //是否选中
                if (mHostSeat.isBlueSelect()) {
                    mHostSeat.setBlueSelect(!mHostSeat.isBlueSelect());
                    num -= 1;
                } else {
                    if (num >= 4) {
                        Utils.toast("最多只能选择4个哦");
                        return;
                    } else {
                        num += 1;
                        mHostSeat.setBlueSelect(!mHostSeat.isBlueSelect());
                    }
                }
            }
            if (num == 0) {
                mTvAdd.setText("添加");
                mTvAdd.setEnabled(false);
            } else {
                mTvAdd.setText("添加(" + num + ")");
                mTvAdd.setEnabled(true);
            }
            mIvChoose.setVisibility(View.INVISIBLE);
            if (mHostSeat.isSelect()) {
                mIvChoose.setImageResource(R.drawable.party_ic_red_choose);
                mIvChoose.setVisibility(View.VISIBLE);
            }
            if (mHostSeat.isBlueSelect()) {
                mIvChoose.setImageResource(R.drawable.party_ic_blue_choose);
                mIvChoose.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.iv_close) {
            dismiss();
        }
    }


    public interface OnSelectListener {
        void onSelectMember(int teamType, List<VoiceRoomSeat> list);

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
