package com.tftechsz.family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

public class GroupCoupleAdapter extends RecyclerView.Adapter<GroupCoupleAdapter.ViewHolder> {
    private final String mAllName = "*#*#*#*#";
    private View mHeaderView;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private final LayoutInflater mInflater;
    private List<FamilyMemberDto> mData;
    private final Context mContext;
    private int checkPosition = 0;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public GroupCoupleAdapter(Context context, List<FamilyMemberDto> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public GroupCoupleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        }
        View view = mInflater.inflate(R.layout.item_group_couple, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvSex = view.findViewById(R.id.tv_sex);
        viewHolder.tvTitle = view.findViewById(R.id.tv_title);
        viewHolder.tvType = view.findViewById(R.id.tv_type);
        viewHolder.tvName = view.findViewById(R.id.tv_name);
        viewHolder.ivAvatar = view.findViewById(R.id.iv_avatar);
        viewHolder.view = view.findViewById(R.id.view);
        viewHolder.clLeader = view.findViewById(R.id.cl_leader);
        viewHolder.ivCheck = view.findViewById(R.id.iv_check);
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    public void setCheckPositions(int p) {
        checkPosition = p;
        notifyDataSetChanged();
    }

    public int getCheckPositions() {
        return checkPosition;
    }

    @Override
    public void onBindViewHolder(final GroupCoupleAdapter.ViewHolder holder, final int position) {
        if (mHeaderView != null && position == 0) return;
        int section = getSectionForPosition(position);
        FamilyMemberDto data = mData.get(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(mData.get(position).getLetters());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        if (mOnItemClickListener != null) {
            holder.clLeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.clLeader, position);
                }
            });
        }
        CommonUtil.setSexAndAge(mContext, data.sex, data.age, holder.tvSex);
        holder.tvSex.setVisibility(View.VISIBLE);
        holder.tvName.setText(data.nickname);
        GlideUtils.loadRoundImageRadius(mContext, holder.ivAvatar, mData.get(position).icon);
        UIUtils.setFamilyTitle(holder.tvType, data.role_id);
        if (checkPosition == position) {
            holder.ivCheck.setImageResource(R.mipmap.chat_ic_group_couple_check);
        } else {
            holder.ivCheck.setImageResource(R.mipmap.chat_ic_group_couple_normal);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvName, tvType, tvSex;
        ImageView ivAvatar, ivCheck;
        View view;
        ConstraintLayout clLeader;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    /**
     * 提供给Activity刷新数据
     */
    public void updateList(List<FamilyMemberDto> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        if (mData != null && mData.size() > 0 && !TextUtils.isEmpty(mData.get(position).getLetters()))
            return mData.get(position).getLetters().charAt(0);
        return 0;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        if (mData != null)
            for (int i = 0; i < getItemCount(); i++) {
                String sortStr = mData.get(i).getLetters();
                if (!TextUtils.isEmpty(sortStr)) {
                    char firstChar = sortStr.toUpperCase().charAt(0);
                    if (firstChar == section) {
                        return i;
                    }
                }
            }
        return -1;
    }
}
