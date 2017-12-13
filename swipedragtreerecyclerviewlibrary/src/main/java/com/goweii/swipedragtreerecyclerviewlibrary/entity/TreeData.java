package com.goweii.swipedragtreerecyclerviewlibrary.entity;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.interfaces.ITree;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 树形多布局数据结构
 *
 * @author cuizhen
 * @date 2017/11/22
 */
public class TreeData extends TypeData implements ITree<TreeData> {

    private ArrayList<TreeData> mTreeDatas = null;
    private boolean mExpand = false;

    /**
     * 构造方法，用于创建根数据或者枝数据
     *
     * @param data     实体数据类
     * @param viewType 用于显示数据的 layout 类型，在 {@link TreeState} 的子类中定义
     * @param treeDatas 子数据， TreeData 集合
     */
    public TreeData(Object data, int viewType, ArrayList<TreeData> treeDatas) {
        super(data, viewType);
        mTreeDatas = treeDatas;
    }

    /**
     * 构造方法，用于创建叶子数据
     *
     * @param data     实体数据类
     * @param viewType 用于显示数据的 layout 类型，在 {@link TreeState} 的子类中定义
     */
    public TreeData(Object data, int viewType) {
        super(data, viewType);
        mTreeDatas = null;
    }

    @Override
    public ArrayList<TreeData> getList() {
        return mTreeDatas;
    }

    @Override
    public void setList(ArrayList<TreeData> list) {
        mTreeDatas = list;
    }

    /**
     * 判断该数据是否为叶子数据，即最末尾一级
     *
     * @return 是否为叶子数据
     */
    @Override
    public boolean isLeaf() {
        return mTreeDatas == null;
    }

    @Override
    public void setExpand(boolean expand) {
        mExpand = expand;
    }

    @Override
    public boolean isExpand() {
        return mExpand;
    }

    /**
     * 删除数据
     *
     * @param treeDatas 数据源 ArrayList<TreeData>
     * @param positions 位置数组 int[]
     */
    public static void deleteByPositions(ArrayList<TreeData> treeDatas, int[] positions) {
        ArrayList<TreeData> tempTreeDatas = treeDatas;
        for (int i = 0; i < positions.length - 1; i++) {
            if (tempTreeDatas.size() > positions[i]) {
                tempTreeDatas = tempTreeDatas.get(positions[i]).getList();
            }
        }
        if (tempTreeDatas != null && tempTreeDatas.size() > positions[positions.length - 1]) {
            tempTreeDatas.remove(positions[positions.length - 1]);
        }
    }

    /**
     * 插入数据
     *
     * @param treeDatas  数据源 ArrayList<TreeData>
     * @param positions 位置数组 int[]
     * @param treeData  插入的数据 TreeData treeData
     */
    public static void insertByPositions(ArrayList<TreeData> treeDatas, int[] positions, TreeData treeData) {
        ArrayList<TreeData> tempTreeDatas = treeDatas;
        for (int i = 0; i < positions.length - 1; i++) {
            if (tempTreeDatas.size() > positions[i]) {
                tempTreeDatas = tempTreeDatas.get(positions[i]).getList();
            }
        }
        if (tempTreeDatas != null && tempTreeDatas.size() > positions[positions.length - 1]) {
            tempTreeDatas.add(positions[positions.length - 1], treeData);
        }
    }

    /**
     * 移动数据
     * 1.同级同组数据
     *
     * @param treeDatas     数据源 ArrayList<TreeData>
     * @param fromPositions 原位置数组 int[]
     * @param toPositions   目标位置数组 int[]
     */
    public static void swapByPositions(ArrayList<TreeData> treeDatas, int[] fromPositions, int[] toPositions) {
        boolean sameLevelAndGroup = true;
        int lastPosIndex = -1;
        if (fromPositions.length == toPositions.length) {
            lastPosIndex = fromPositions.length - 1;
            for (int i = 0; i < lastPosIndex; i++) {
                if (fromPositions[i] != toPositions[i]) {
                    sameLevelAndGroup = false;
                    break;
                }
            }
        } else {
            sameLevelAndGroup = false;
        }
        if (sameLevelAndGroup) {
            ArrayList<TreeData> tempTreeData = treeDatas;
            for (int i = 0; i < lastPosIndex; i++) {
                if (tempTreeData.size() > fromPositions[i]) {
                    tempTreeData = tempTreeData.get(fromPositions[i]).getList();
                }
            }
            Collections.swap(tempTreeData, fromPositions[lastPosIndex], toPositions[lastPosIndex]);
        }
    }
}