package com.tftechsz.im.uikit;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.netease.nim.uikit.business.ait.AitBlock;
import com.netease.nim.uikit.business.ait.AitContactType;
import com.netease.nim.uikit.business.ait.AitContactsModel;
import com.netease.nim.uikit.business.ait.AitTextChangeListener;
import com.netease.nim.uikit.business.ait.selector.AitContactSelectorActivity;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.List;

/**
 * Created by hzchenkang on 2017/7/10.
 */

public class AitManager implements TextWatcher {

//    private Context context;

    private String tid;

    private int teamType = 0;

    private boolean robot;

    private AitContactsModel aitContactsModel;

    private int curPos;

    private boolean ignoreTextChange = false;

    public interface FriendInfoClickListener {

        void isonClick(int teamType,String tid);
    }


    private FriendInfoClickListener mFriendInfoClickListener;


    private AitTextChangeListener listener;

    public AitManager(FriendInfoClickListener friendInfoClickListener, String tid, boolean robot) {
        mFriendInfoClickListener = friendInfoClickListener;
        this.tid = tid;
        this.robot = robot;
        aitContactsModel = new AitContactsModel();
    }

    public void setTextChangeListener(AitTextChangeListener listener) {
        this.listener = listener;
    }

    public List<String> getAitTeamMember() {
        return aitContactsModel.getAitTeamMember();
    }

    public String getAitRobot() {
        return aitContactsModel.getFirstAitRobot();
    }

    public String removeRobotAitString(String text, String robotAccount) {
        AitBlock block = aitContactsModel.getAitBlock(robotAccount);
        if (block != null) {
            return text.replaceAll(block.text, "");
        } else {
            return text;
        }
    }

    public void reset() {
        aitContactsModel.reset();
        ignoreTextChange = false;
        curPos = 0;
    }

    public void setTeamType(int teamType) {
        this.teamType  = teamType;
    }

    /**
     * ------------------------------ 增加@成员 --------------------------------------
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtil.e("========================","====================" + requestCode + "===" + resultCode);
        if (requestCode == AitContactSelectorActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int userId = data.getIntExtra("userId", -1);
            String name = data.getStringExtra("nickName");
            String account = userId + "";
            LogUtil.e("========================",name);
            insertAitMemberInner(account, name,  AitContactType.TEAM_MEMBER, curPos, false);
        }
    }

    // 群昵称 > 用户昵称 > 账号
    private static String getAitTeamMemberName(TeamMember member) {
        if (member == null) {
            return "";
        }
        String memberNick = member.getTeamNick();
        if (!TextUtils.isEmpty(memberNick)) {
            return memberNick;
        }
        return UserInfoHelper.getUserName(member.getAccount());
    }

    public void insertAitRobot(String account, String name, int start) {
        insertAitMemberInner(account, name, AitContactType.ROBOT, start, true);
    }

    public void insertAitMemberInner(String account, String name, int type, int start, boolean needInsertAitInText) {
        name = name + " ";
        String content = needInsertAitInText ? "@" + name : name;
        if (listener != null) {
            // 关闭监听
            ignoreTextChange = true;
            // insert 文本到editText
            listener.onTextAdd(content, start, content.length());
            // 开启监听
            ignoreTextChange = false;
        }

        // update 已有的 aitBlock
        aitContactsModel.onInsertText(start, content);

        int index = needInsertAitInText ? start : start - 1;
        // 添加当前到 aitBlock
        aitContactsModel.addAitMember(account, name, type, index);
    }

    public String insertAitMember(String account, String name, int type, int start, boolean needInsertAitInText) {
        name = name + " ";
        String content = needInsertAitInText ? "@" + name : name;
        // update 已有的 aitBlock
        aitContactsModel.onInsertText(start, content);
        int index = needInsertAitInText ? start : start - 1;
        // 添加当前到 aitBlock
        aitContactsModel.addAitMember(account, name, type, index);
        return content;
    }


    /**
     * ------------------------------ editText 监听 --------------------------------------
     */

    // 当删除尾部空格时，删除一整个segment,包含界面上也删除
    private boolean deleteSegment(int start, int count) {
        if (count != 1) {
            return false;
        }
        boolean result = false;
        AitBlock.AitSegment segment = aitContactsModel.findAitSegmentByEndPos(start);
        if (segment != null) {
            int length = start - segment.start;
            if (listener != null) {
                ignoreTextChange = true;
                listener.onTextDelete(segment.start, length);
                ignoreTextChange = false;
            }
            aitContactsModel.onDeleteText(start, length);
            result = true;
        }
        return result;
    }

    /**
     * @param editable 变化后的Editable
     * @param start    text 变化区块的起始index
     * @param count    text 变化区块的大小
     * @param delete   是否是删除
     */
    private void afterTextChanged(Editable editable, int start, int count, boolean delete) {
        curPos = delete ? start : count + start;
        if (ignoreTextChange) {
            return;
        }
        if (delete) {
            int before = start + count;
            if (deleteSegment(before, count)) {
                return;
            }
            aitContactsModel.onDeleteText(before, count);

        } else {
            if (count <= 0 || editable.length() < start + count) {
                return;
            }
            CharSequence s = editable.subSequence(start, start + count);
            if (s == null) {
                return;
            }
            if (s.toString().equals("@")) {
                // 启动@联系人界面
                if (!TextUtils.isEmpty(tid) || robot) {
//                    AitContactSelectorActivity.start(context, tid, robot);
                    if(mFriendInfoClickListener != null){
                        mFriendInfoClickListener.isonClick(teamType,tid);
                    }

                }
            }
            aitContactsModel.onInsertText(start, s.toString());
        }
    }

    private int editTextStart;
    private int editTextCount;
    private int editTextBefore;
    private boolean delete;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        delete = count > after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.editTextStart = start;
        this.editTextCount = count;
        this.editTextBefore = before;
    }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextChanged(s, editTextStart, delete ? editTextBefore : editTextCount, delete);
    }
}
