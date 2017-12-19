package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.util.SparseIntArray;
import android.view.View;

import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragTreeCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author cuizhen
 * @date 2017/11/23
 */
public abstract class BaseSwipeDragTreeAdapter extends BaseSwipeDragAdapter {
    private boolean mMemoryExpandState = false;

    private OnExpandChangeListener mOnExpandChangeListener = null;

    public BaseSwipeDragTreeAdapter() {
        super();
    }

    /**
     * 初始化 {@link #mStates} 数据
     */
    @Override
    protected void initStates() {
        ArrayList<TreeState> states = new ArrayList<>();
        for (int i = 0; i < getDatas().size(); i++) {
            TreeState state = new TreeState();
            TreeData data = (TreeData) getDatas().get(i);
            state.setType(data.getType());
            state.setPositions(new int[]{i});
            state.setExpand(false);
            states.add(state);
        }
        setStates(states);
    }

    @Override
    protected TreeData getData(int position) {
        int[] positions = getState(position).getPositions();
        TreeData data = null;
        ArrayList datas = getDatas();
        for (int pos : positions) {
            if (datas.size() > pos) {
                data = (TreeData) datas.get(pos);
                if (!data.isLeaf()) {
                    datas = data.getList();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return data;
    }

    @Override
    protected TreeState getState(int position) {
        return (TreeState) super.getState(position);
    }

    /**
     * 删除当前位置的state并更新同分组下的state的positions
     *
     * @param position 删除位置
     */
    @Override
    protected void swipedState(int position) {
        TreeState state = getState(position);
        int[] positions = state.getPositions();
        if (state.isExpand()) {
            closeGroup(position, position);
        }
        int lastIndex = positions.length - 1;
        int count = 0;
        for (int i = position + 1; i < getStates().size(); i++) {
            int[] tempPositions = getState(i).getPositions();
            if (tempPositions.length >= positions.length) {
                tempPositions[lastIndex]--;
            } else {
                count = i - position;
                break;
            }
        }
        this.notifyItemRangeChanged(position + 1, count);
        getStates().remove(position);
    }

    @Override
    protected void swipedData(int position) {
        int[] positions = getState(position).getPositions();
        TreeData.deleteByPositions(getDatas(), positions);
    }

    /**
     * 拖拽更新data数据
     * 暂时只实现同分组，同级别的操作逻辑
     *
     * @param currentPosition 当前位置
     * @param targetPosition  目标位置
     */
    @Override
    protected void dragData(int currentPosition, int targetPosition) {
        int[] currentPositions = getState(currentPosition).getPositions();
        int[] targetPositions = getState(targetPosition).getPositions();
        TreeData.swapByPositions(getDatas(), currentPositions, targetPositions);
    }

    /**
     * 拖拽更新state数据
     * 暂时只实现同分组，同级别的操作逻辑
     *
     * @param currentPosition 当前位置
     * @param targetPosition  目标位置
     */
    @Override
    protected void dragState(int currentPosition, int targetPosition) {
        TreeState fromState = getState(currentPosition);
        int[] currentPositions = fromState.getPositions();
        TreeState toState = getState(targetPosition);
        int[] targetPositions = toState.getPositions();
        fromState.setPositions(targetPositions);
        toState.setPositions(currentPositions);
        Collections.swap(getStates(), currentPosition, targetPosition);
    }

    /**
     * 用于Drag操作，返回两个item是否是同级别的
     *
     * @param currentPosition currentPosition
     * @param targetPosition  targetPosition
     * @return boolean
     */
    private boolean isSameLevel(int currentPosition, int targetPosition) {
        int[] currentPositions = getState(currentPosition).getPositions();
        int[] targetPositions = getState(targetPosition).getPositions();
        return currentPositions.length == targetPositions.length;
    }

    /**
     * 用于Drag操作，判断2个item的最近上级是否相同
     *
     * @param currentPosition currentPosition
     * @param targetPosition  targetPosition
     * @return boolean
     */
    private boolean isSameGroup(int currentPosition, int targetPosition) {
        int[] currentPositions = getState(currentPosition).getPositions();
        int[] targetPositions = getState(targetPosition).getPositions();
        int limitSize = currentPositions.length < targetPositions.length ?
                currentPositions.length : targetPositions.length;
        boolean isSameGroup = true;
        if (limitSize == 1) {
            if (currentPositions.length == targetPositions.length) {
                return true;
            }
            if (currentPositions[0] != targetPositions[0]) {
                isSameGroup = false;
            }
        }
        for (int i = 0; i < limitSize - 1; i++) {
            if (currentPositions[i] != targetPositions[i]) {
                isSameGroup = false;
            }
        }
        return isSameGroup;
    }

    /**
     * 判断拖拽时移动之间是否有打开的分组
     *
     * @param currentPosition currentPosition
     * @param targetPosition  targetPosition
     * @return boolean
     */
    private boolean betweenHasExpand(int currentPosition, int targetPosition) {
        boolean betweenHasExpand = false;
        int small;
        int big;
        if (currentPosition < targetPosition) {
            small = currentPosition;
            big = targetPosition;
        } else {
            big = currentPosition;
            small = targetPosition;
        }
        for (int i = small; i <= big; i++) {
            if (getState(i).isExpand()) {
                betweenHasExpand = true;
                break;
            }
        }
        return betweenHasExpand;
    }

    @Override
    protected SwipeDragTreeViewHolder creatHolder(View itemView, int viewType) {
        return new SwipeDragTreeViewHolder(itemView, getViewIds(viewType), getStartDragViewIds(viewType));
    }

    /**
     * 是否记忆子分组展开状态 {@link #mMemoryExpandState}
     *
     * @return mMemoryExpandState
     */
    public final boolean isMemoryExpandState() {
        return mMemoryExpandState;
    }

    /**
     * 设置关闭分组后，是否记忆子分组展开状态 {@link #mMemoryExpandState}
     *
     * @param memoryExpandState memoryExpandState
     */
    public final void setMemoryExpandState(boolean memoryExpandState) {
        mMemoryExpandState = memoryExpandState;
    }


    /**
     * 判断分组是否全部打开
     *
     * @return boolean
     */
    public final boolean isAllExpand() {
        boolean isAllExpand = true;
        for (int i = 0; i < getStates().size(); i++) {
            if (!getData(i).isLeaf() && !getState(i).isExpand()) {
                isAllExpand = false;
                break;
            }
        }
        return isAllExpand;
    }

    /**
     * 展开所有分组
     */
    public final void expandAll() {
        for (int i = 0; i < getStates().size(); i++) {
            if (!getData(i).isLeaf() && !getState(i).isExpand()) {
                openGroup(i, i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 关闭所有分组
     */
    public final void unExpandAll() {
        for (int i = 0; i < getStates().size(); i++) {
            if (!getData(i).isLeaf() && getState(i).isExpand()) {
                closeGroup(i, i);
            }
        }
    }

    public final int[] getPositions(int position) {
        return getState(position).getPositions();
    }

    /**
     * 打开分组时调用
     *
     * @param clickPosition 点击位置（在方法内部使用，外部传入时和 position 相同即可）
     * @param position      打开的位置
     */
    private void openGroup(int clickPosition, int position) {
        synchronized (this) {
            TreeState state = getState(position);
            int[] positions = state.getPositions();
            TreeData treeData = getData(position);
            ArrayList<TreeData> dataTrees = treeData.getList();
            int count = dataTrees.size();
            for (int i = 0; i < count; i++) {
                TreeData childTreeData = dataTrees.get(i);
                TreeState newState = new TreeState();
                newState.setType(childTreeData.getType());
                int[] newPositions = Arrays.copyOf(positions, positions.length + 1);
                newPositions[newPositions.length - 1] = i;
                newState.setPositions(newPositions);
                newState.setExpand(childTreeData.isExpand());
                getStates().add(position + i + 1, newState);
            }
            if (position == clickPosition) {
                treeData.setExpand(true);
                state.setExpand(true);
            }
            notifyItemRangeInserted(position + 1, count);
            for (int i = count - 1; i >= 0; i--) {
                if (getState(position + i + 1).isExpand()) {
                    openGroup(clickPosition, position + i + 1);
                }
            }
            //所有循环结束后，调用分组展开状态变化接口
            if (position == clickPosition) {
                if (mOnExpandChangeListener != null) {
                    mOnExpandChangeListener.onExpand(getItemCount());
                }
            }
        }
    }

    /**
     * 关闭分组时调用
     *
     * @param clickPosition 点击的位置（在方法内部使用，外部传入时和 position 相同即可）
     * @param position      关闭的位置
     */
    private void closeGroup(int clickPosition, int position) {
        synchronized (this) {
            TreeData treeData = getData(position);
            ArrayList<TreeData> treeDatas = treeData.getList();
            int count = treeDatas.size();
            for (int i = 0; i < count; i++) {
                if (getState(position + i + 1).isExpand()) {
                    closeGroup(clickPosition, position + i + 1);
                }
            }
            for (int i = 0; i < count; i++) {
                getStates().remove(position + 1);
            }
            if (isMemoryExpandState()) {
                if (position == clickPosition) {
                    treeData.setExpand(false);
                }
            } else {
                treeData.setExpand(false);
            }
            TreeState state = getState(position);
            state.setExpand(false);
            notifyItemRangeRemoved(position + 1, count);
            //所有循环结束后，调用分组展开状态变化接口
            if (position == clickPosition) {
                if (mOnExpandChangeListener != null) {
                    mOnExpandChangeListener.onUnExpand(getItemCount());
                }
            }
        }
    }

    @Override
    protected SwipeDragTreeCallback getNewCallback() {
        SwipeDragTreeCallback callback = new SwipeDragTreeCallback();
        callback.setOnSelectedChangedCallbackListener(new SwipeDragTreeCallback.OnSelectedChangedCallbackListener() {
            @Override
            public void onSwipe(int position) {
                TreeState positionState = getState(position);
                if (positionState.isExpand()) {
                    closeGroup(position, position);
                }
            }

            @Override
            public void onDrag(int position) {
                TreeState positionState = getState(position);
                if (positionState.isExpand()) {
                    closeGroup(position, position);
                }
            }
        });
        callback.setOnCanDropOverCallbackListener(new SwipeDragTreeCallback.OnCanDropOverCallbackListener() {
            @Override
            public boolean canDropOver(int currentPosition, int targetPosition) {
                if (isSameLevel(currentPosition, targetPosition) &&
                        isSameGroup(currentPosition, targetPosition) &&
                        !betweenHasExpand(currentPosition, targetPosition)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return callback;
    }

    /**
     * 分组展开状态变化回调接口
     */
    public interface OnExpandChangeListener {
        /**
         * 分组展开
         * @param itemCount 当前adapter中item的数量
         */
        void onExpand(int itemCount);

        /**
         * 关闭展开
         * @param itemCount 当前adapter中item的数量
         */
        void onUnExpand(int itemCount);
    }

    /**
     * 设置分组展开状态变化回调接口
     * @param onExpandChangeListener 分组展开状态变化回调接口
     */
    public final void setOnExpandChangeListener(OnExpandChangeListener onExpandChangeListener) {
        if (mOnExpandChangeListener == null) {
            mOnExpandChangeListener = onExpandChangeListener;
        }
    }

    /**
     * SwipeDragTreeViewHolder
     */
    protected class SwipeDragTreeViewHolder extends SwipeDragViewHolder {

        protected SwipeDragTreeViewHolder(View itemView, SparseIntArray viewIds, SparseIntArray startDragViewIds) {
            super(itemView, viewIds, startDragViewIds);
        }

        @Override
        protected void itemViewOnClick(View v, int position) {
            try {
                TreeData treeData = getData(position);
                if (!treeData.isLeaf()) {
                    TreeState state = getState(position);
                    if (state.isExpand()) {
                        closeGroup(position, position);
                    } else {
                        openGroup(position, position);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mOnItemViewClickListener != null) {
                mOnItemViewClickListener.onItemViewClick(v, position);
            }
        }
    }
}
