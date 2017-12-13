package com.goweii.swipedragtreerecyclerviewlibrary.entity;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.IExpand;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.IPositions;

/**
 * 用于记录 adapter 的 position 位置的状态
 * <p>
 * 在 TreeData 中的位置
 * 判断item 的 type
 * 打开状态
 * 选中状态
 * <p>
 *
 * @author cuizhen
 * @date 2017/11/23
 */

public class TreeState extends TypeState implements IPositions, IExpand {
    private int[] mPositions = new int[]{};
    private boolean mExpand = false;

    @Override
    public void setPositions(int[] positions) {
        this.mPositions = positions;
    }

    @Override
    public int[] getPositions() {
        return mPositions;
    }

    @Override
    public void setExpand(boolean expand) {
        this.mExpand = expand;
    }

    @Override
    public boolean isExpand() {
        return mExpand;
    }
}
