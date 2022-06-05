package com.tftechsz.moment.other;

public interface OnPraiseOrCommentClickListener {
    void onPraiseClick(int position);

    void onCommentClick(int position);

    void onClickFrendCircleTopBg();

    void onDeleteItem(String id, int position);
}
