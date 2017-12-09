package com.goweii.swipedragtreerecyclerviewlibrary.entity;

/**
 * 树形数据结构
 *
 * @author cuizhen
 * @date 2017/11/22
 */

public class BaseData {

    /**
     * 数据，可为任意类型
     */
    private Object mData;
    /**
     * 用于显示 mData 数据的 layout 的类型，在 {@link PositionState} 的子类中定义
     */
    private int mViewType;

    /**
     * 构造方法，用于创建根数据或者枝数据
     *
     * @param data      实体数据类
     * @param viewType  用于显示数据的 layout 类型，在 {@link PositionState} 的子类中定义
     */
    public BaseData(Object data, int viewType) {
        mData = data;
        mViewType = viewType;
    }
    public Object getData() {
        return mData;
    }

    public int getViewType() {
        return mViewType;
    }
}