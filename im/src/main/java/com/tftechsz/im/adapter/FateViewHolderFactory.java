package com.tftechsz.im.adapter;


import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.tftechsz.im.model.FateInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FateViewHolderFactory {

    private static HashMap<Class<? extends MsgAttachment>, Class<? extends FateViewHolderBase>> viewHolders = new HashMap<>();

    private static Class<? extends FateViewHolderBase> tipMsgViewHolder;

    private static void register(Class<? extends MsgAttachment> attach, Class<? extends FateViewHolderBase> viewHolder) {
        viewHolders.put(attach, viewHolder);
    }

    private static void registerTipMsgViewHolder(Class<? extends FateViewHolderBase> viewHolder) {
        tipMsgViewHolder = viewHolder;
    }

    /**
     * 消息holder
     *
     * @param message
     * @return
     */
    public static Class<? extends FateViewHolderBase> getViewHolderByType(FateInfo message) {
        if (message.getMsg_type() == 1) {
            return FateViewHolderText.class;
        } else if (message.getMsg_type() == 2) {
            return FateViewHolderPicture.class;
        }else if (message.getMsg_type() == 3) {
            return FateViewHolderAudio.class;
        }else if (message.getMsg_type() == 4) {
            return FateViewHolderReplyAccost.class;
        }else {
            return FateViewHolderGift.class;
        }
    }

    private static Class<? extends MsgAttachment> getSuperClass(Class<? extends MsgAttachment> derived) {
        Class sup = derived.getSuperclass();
        if (sup != null && MsgAttachment.class.isAssignableFrom(sup)) {
            return sup;
        } else {
            for (Class itf : derived.getInterfaces()) {
                if (MsgAttachment.class.isAssignableFrom(itf)) {
                    return itf;
                }
            }
        }
        return null;
    }

    public static List<Class<? extends FateViewHolderBase>> getAllViewHolders() {
        List<Class<? extends FateViewHolderBase>> list = new ArrayList<>();
        list.addAll(viewHolders.values());
        if (tipMsgViewHolder != null) {
            list.add(tipMsgViewHolder);
        }

        list.add(FateViewHolderText.class);
        list.add(FateViewHolderAudio.class);
        list.add(FateViewHolderPicture.class);
        list.add(FateViewHolderReplyAccost.class);
        list.add(FateViewHolderGift.class);
        return list;
    }
}
