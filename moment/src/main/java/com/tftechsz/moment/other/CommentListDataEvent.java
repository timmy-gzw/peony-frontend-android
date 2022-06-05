package com.tftechsz.moment.other;

import com.tftechsz.moment.entity.CommentListItem;
import java.util.ArrayList;

public class CommentListDataEvent {
    private String tag;
    private ArrayList<CommentListItem> arrayList;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArrayList<CommentListItem> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<CommentListItem> arrayList) {
        this.arrayList = arrayList;
    }
}
