package com.tftechsz.im.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.holder.RecyclerViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.TeamG2Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAdapter extends BaseMultiItemFetchLoadAdapter<TeamG2Item, BaseViewHolder> {

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int VIEW_TYPE_HOLDER = 3;

    private RemoteUserMuteChangeListener onItemMuteChangeListener;

    public TeamAdapter(RecyclerView recyclerView, List<TeamG2Item> data) {
        super(recyclerView, data);

        Map<Class<? extends RecyclerViewHolder>, Integer> holder2ViewType = new HashMap<>();
        addItemType(VIEW_TYPE_DATA, R.layout.team_g2_item, TeamG2ItemViewHolder.class);
        addItemType(VIEW_TYPE_HOLDER, R.layout.team_avchat_holder, TeamG2EmptyViewHolder.class);
        holder2ViewType.put(TeamG2ItemViewHolder.class, VIEW_TYPE_DATA);
        holder2ViewType.put(TeamG2EmptyViewHolder.class, VIEW_TYPE_HOLDER);
    }

    @Override
    protected int getViewType(TeamG2Item item) {
        if (item.type == TeamG2Item.TYPE.TYPE_DATA) {
            return VIEW_TYPE_DATA;
        } else if (item.type == TeamG2Item.TYPE.TYPE_HOLDER) {
            return VIEW_TYPE_HOLDER;
        } else {
            return VIEW_TYPE_ADD;
        }
    }

    @Override
    protected void convert(BaseViewHolder baseHolder, TeamG2Item item, int position, boolean isScrolling) {
        super.convert(baseHolder, item, position, isScrolling);
        baseHolder.itemView.setOnClickListener(v -> {
            TeamG2Item itemStep = item;
            item.isMute = !item.isMute;
            if (onItemMuteChangeListener != null) {
                onItemMuteChangeListener.onMuteChange(item.uid, item.isMute);
            }
            notifyItemChanged(position, itemStep);
        });
    }

    public void setOnItemMuteChangeListener(RemoteUserMuteChangeListener onItemMuteChangeListener) {
        this.onItemMuteChangeListener = onItemMuteChangeListener;
    }

    @Override
    protected String getItemKey(TeamG2Item item) {
        return item.type + "_" + item.teamId + "_" + item.account;
    }

    public NERtcVideoView getViewHolderSurfaceView(TeamG2Item item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
        if (holder instanceof TeamG2ItemViewHolder) {
            return ((TeamG2ItemViewHolder) holder).getSurfaceView();
        }

        return null;
    }

    public void updateVolumeBar(TeamG2Item item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
        if (holder instanceof TeamG2ItemViewHolder) {
            ((TeamG2ItemViewHolder) holder).updateVolume(item.volume);
        }
    }

    public interface RemoteUserMuteChangeListener {
        void onMuteChange(long uid, boolean isMute);
    }
}
