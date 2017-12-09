package com.goweii.swipedragtreerecyclerviewlibrary.entity;

/**
 * 用于记录 adapter 的 position 位置的状态
 * <p>
 * 在 DataTree 中的位置
 * 判断item 的 type
 * 打开状态
 * 选中状态
 * <p>
 *
 * @author cuizhen
 * @date 2017/11/23
 */

public class PositionState extends BasePositionState{
    /**
     * 在 DataTree 中的位置
     */
    private int[] mPositions = new int[]{};
    /**
     * 打开状态
     */
    private boolean mExpand = false;

    public void setPositions(int[] positions) {
        this.mPositions = positions;
    }

    public void setExpand(boolean expand) {
        this.mExpand = expand;
    }

    public int[] getPositions() {
        return mPositions;
    }

    public boolean isExpand() {
        return mExpand;
    }
}
