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

public class BasePositionState {
    /**
     * 默认 TYPE 叶子数据，即不存在子数据
     * 若为多级列表，则需要继承该类，并定义其他类别
     */
    public static final int TYPE_LEAF = 0;
    /**
     * item 的 type
     */
    private int mViewType = TYPE_LEAF;
    /**
     * 选中状态
     */
    private boolean mSelected = false;


    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public int getViewType() {
        return mViewType;
    }

    public boolean isSelected() {
        return mSelected;
    }
}
