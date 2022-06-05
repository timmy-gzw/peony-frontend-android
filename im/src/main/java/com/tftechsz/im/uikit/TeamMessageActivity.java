package com.tftechsz.im.uikit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.tftechsz.im.R;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.SoftHideKeyBoardUtil;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.ToastUtil;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 群聊界面
 * <p/>
 * Created by huangjun on 2015/3/5.
 */
@Route(path = ARouterApi.ACTIVITY_TEAM_MESSAGE)
public class TeamMessageActivity extends BaseMessageActivity {

    // model
    private Team team;

    private TeamMessageFragment fragment;

    private Class<? extends Activity> backToClass;

    private int mTeamType;  //1:聊天室  0群聊
    protected CompositeDisposable mCompositeDisposable;

    public static void start(Context context, String tid, SessionCustomization customization,
                             Class<? extends Activity> backToClass, IMMessage anchor) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.putExtra(Extras.EXTRA_BACK_TO_CLASS, backToClass);
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor);
        }
        intent.setClass(context, TeamMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }


    protected void findViews() {
        View invalidTeamTipView = findView(R.id.invalid_team_tip);
        TextView invalidTeamTipText = findView(R.id.invalid_team_text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        StatusBarUtil.fullScreen(this);
        StatusBarUtil.setLightStatusBar(this, true, true);
        SoftHideKeyBoardUtil.assistActivity(this);
        backToClass = (Class<? extends Activity>) getIntent().getSerializableExtra(Extras.EXTRA_BACK_TO_CLASS);
        mTeamType = getIntent().getIntExtra("teamType", 0);
        findViews();
        registerTeamUpdateObserver(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerTeamUpdateObserver(false);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        registerTeamUpdateObserver1(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestTeamInfo();
        registerTeamUpdateObserver1(true);
    }

    /**
     * 请求群基本信息
     */
    private void requestTeamInfo() {
        // 请求群基本信息
        Team t = NimUIKit.getTeamProvider().getTeamById(sessionId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(sessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    } else {
                        onRequestTeamInfoFailed();
                    }
                }
            });
        }
    }

    private void onRequestTeamInfoFailed() {
        ToastHelper.showToast(TeamMessageActivity.this, "获取群组信息失败!");
        finish();
    }

    /**
     * R
     * 更新群信息
     *
     * @param d
     */
    private void updateTeamInfo(final Team d) {
        if (d == null) {
            return;
        }

        team = d;
        fragment.setTeam(team);
//        invalidTeamTipText.setText(team.getType() == TeamTypeEnum.Normal ? R.string.normal_team_invalid_tip : R.string.team_invalid_tip);
//        invalidTeamTipView.setVisibility(team.isMyTeam() ? View.GONE : View.VISIBLE);
        String content;
        if (mTeamType == 0) {
            content = team == null ? sessionId : team.getName() + "(" + team.getMemberCount() + ")";
        } else {
            content = team == null ? sessionId : team.getName();
        }
//            RxBus.getDefault().post(new TeamEvent(content));
    }

    /**
     * 注册群信息更新监听
     *
     * @param register
     */
    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver, register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
    }

    /**
     * 注册群信息更新监听
     *
     * @param register
     */
    private void registerTeamUpdateObserver1(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
    }

    /**
     * 群资料变动通知和移除群的通知（包括自己退群和群被解散）
     */
    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            if (team == null) {
                return;
            }
            for (Team t : teams) {
                if (t.getId().equals(team.getId())) {
                    updateTeamInfo(t);
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            if (team == null || TeamMessageActivity.this.team == null) {
                return;
            }
            if (team.getId().equals(TeamMessageActivity.this.team.getId())) {
                ToastUtil.showToast(TeamMessageActivity.this, "您已被移出家族");
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_EXIT_FAMILY));
//                updateTeamInfo(team);
//                finish();
            }
        }
    };


    /**
     * 群成员资料变动通知和移除群成员通知
     */
    TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            fragment.refreshMessageList();
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> member) {
        }
    };

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            fragment.refreshMessageList();
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            fragment.refreshMessageList();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            fragment.refreshMessageList();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            fragment.refreshMessageList();
        }
    };

    @Override
    protected MessageFragment fragment() {
        // 添加fragment
        Bundle arguments = getIntent().getExtras();
        if (arguments == null) {
            arguments = new Bundle();
        }
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.Team);
        fragment = new TeamMessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_team_fragment_container);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.nim_team_message_activity;
    }

    @Override
    protected void initToolBar() {
//        ToolBarOptions options = new NimToolBarOptions();
//        options.titleString = "群聊";
//        setToolBar(R.id.toolbar, options);
    }

    @Override
    protected boolean enableSensor() {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backToClass != null) {
            Intent intent = new Intent();
            intent.setClass(this, backToClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            messageFragment.onKeyBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnHideKeyboardListener {
        boolean hideKeyboard(MotionEvent event);
    }

    public void setOnHideKeyboardListener(OnHideKeyboardListener onHideKeyboardListener) {
        this.onHideKeyboardListener = onHideKeyboardListener;
    }

    private OnHideKeyboardListener onHideKeyboardListener;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (onHideKeyboardListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                if (onHideKeyboardListener.hideKeyboard(ev)) {
                    return false;  //不在分发触控给子控件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
