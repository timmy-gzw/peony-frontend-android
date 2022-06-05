package com.tftechsz.moment.other;

/**
 * 包 名 : com.tftechsz.moment.other
 * 描 述 : 拖拽监听事件
 */
public interface DragListener {
    /**
     * 是否将 item拖动到删除处，根据状态改变颜色
     *
     * @param isDelete
     */
    void deleteState(boolean isDelete);

    /**
     * 是否于拖拽状态
     *
     * @param isStart
     */
    void dragState(boolean isStart);
}
