package com.goweii.swipedragtreerecyclerviewlibrary.entity;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.ISelected;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.IType;

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

public class TypeState implements ISelected, IType {
    public static final int TYPE_LEAF = 0;

    private int mType = TYPE_LEAF;
    private boolean mSelected = false;

    @Override
    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void setType(int type) {
        this.mType = type;
    }

    @Override
    public int getType() {
        return mType;
    }
}
