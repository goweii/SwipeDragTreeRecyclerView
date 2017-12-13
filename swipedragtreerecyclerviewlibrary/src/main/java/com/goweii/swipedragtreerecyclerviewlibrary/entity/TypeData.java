package com.goweii.swipedragtreerecyclerviewlibrary.entity;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.IData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.ISelected;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.IType;

/**
 * 多布局数据结构
 *
 * @author cuizhen
 * @date 2017/11/22
 */
public class TypeData implements IData, IType, ISelected {
    private Object mData;
    private int mType;
    private boolean mSelected;

    /**
     * 构造方法，用于创建根数据或者枝数据
     *
     * @param data      实体数据类
     * @param type  用于显示数据的 layout 类型，在 {@link TreeState} 的子类中定义
     */
    public TypeData(Object data, int type) {
        mData = data;
        mType = type;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public void setData(Object data) {
        mData = data;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}