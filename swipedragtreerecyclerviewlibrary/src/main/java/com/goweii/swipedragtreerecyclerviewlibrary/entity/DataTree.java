package com.goweii.swipedragtreerecyclerviewlibrary.entity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 树形数据结构
 *
 * @author cuizhen
 * @date 2017/11/22
 */

public class DataTree extends BaseData{
    /**
     * 用于保存分组的打开状态
     */
    private boolean mExpand = false;
    /**
     * 子数据
     */
    private ArrayList<DataTree> mDataTrees = null;

    /**
     * 构造方法，用于创建根数据或者枝数据
     *
     * @param data      实体数据类
     * @param viewType  用于显示数据的 layout 类型，在 {@link PositionState} 的子类中定义
     * @param dataTrees 子数据， DataTree 集合
     */
    public DataTree(Object data, int viewType, ArrayList<DataTree> dataTrees) {
        super(data, viewType);
        mDataTrees = dataTrees;
    }

    /**
     * 构造方法，用于创建叶子数据
     *
     * @param data     实体数据类
     * @param viewType 用于显示数据的 layout 类型，在 {@link PositionState} 的子类中定义
     */
    public DataTree(Object data, int viewType) {
        super(data, viewType);
        mDataTrees = null;
    }

    /**
     * 判断该数据是否为叶子数据，即最末尾一级
     *
     * @return
     */
    public boolean isLeaf() {
        return mDataTrees == null;
    }

    public boolean isExpand() {
        return mExpand;
    }

    public void setExpand(boolean expand) {
        mExpand = expand;
    }

    public ArrayList<DataTree> getDataTrees() {
        return mDataTrees;
    }

    /**
     * 删除数据
     *
     * @param dataTrees 数据源 ArrayList<DataTree>
     * @param positions 位置数组 int[]
     */
    public static void deleteByPositions(ArrayList<DataTree> dataTrees, int[] positions) {
        ArrayList<DataTree> tempDataTrees = dataTrees;
        for (int i = 0; i < positions.length - 1; i++) {
            if (tempDataTrees.size() > positions[i]) {
                tempDataTrees = tempDataTrees.get(positions[i]).getDataTrees();
            }
        }
        if (tempDataTrees != null && tempDataTrees.size() > positions[positions.length - 1]) {
            tempDataTrees.remove(positions[positions.length - 1]);
        }
    }

    /**
     * 插入数据
     *
     * @param dataTrees 数据源 ArrayList<DataTree>
     * @param positions 位置数组 int[]
     * @param dataTree  插入的数据 DataTree dataTree
     */
    public static void insertByPositions(ArrayList<DataTree> dataTrees, int[] positions, DataTree dataTree) {
        ArrayList<DataTree> tempDataTrees = dataTrees;
        for (int i = 0; i < positions.length - 1; i++) {
            if (tempDataTrees.size() > positions[i]) {
                tempDataTrees = tempDataTrees.get(positions[i]).getDataTrees();
            }
        }
        if (tempDataTrees != null && tempDataTrees.size() > positions[positions.length - 1]) {
            tempDataTrees.add(positions[positions.length - 1], dataTree);
        }
    }

    /**
     * 移动数据
     * 1.同级同组数据
     *
     * @param dataTrees     数据源 ArrayList<DataTree>
     * @param fromPositions 原位置数组 int[]
     * @param toPositions   目标位置数组 int[]
     */
    public static void swapByPositions(ArrayList<DataTree> dataTrees, int[] fromPositions, int[] toPositions) {
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
            ArrayList<DataTree> tempDataTrees = dataTrees;
            for (int i = 0; i < lastPosIndex; i++) {
                if (tempDataTrees.size() > fromPositions[i]) {
                    tempDataTrees = tempDataTrees.get(fromPositions[i]).getDataTrees();
                }
            }
            Collections.swap(tempDataTrees, fromPositions[lastPosIndex], toPositions[lastPosIndex]);
        }
    }

}