package com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces;

import java.util.ArrayList;

/**
 * @author cuizhen
 * @date 2017/12/11
 */
public interface ITree<T> extends IExpand{
    /**
     * 获取子数据列表
     * @return 子数据列表
     */
    ArrayList<T> getList();

    /**
     * 设置子数据列表
     * @param list 子数据列表
     */
    void setList(ArrayList<T> list);

    /**
     * 判断是否为叶子数据
     * @return boolean
     */
    boolean isLeaf();
}
