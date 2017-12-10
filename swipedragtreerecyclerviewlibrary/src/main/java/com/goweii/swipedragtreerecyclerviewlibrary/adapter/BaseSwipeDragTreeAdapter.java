package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragTreeCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.DataTree;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.PositionState;
import com.goweii.swipedragtreerecyclerviewlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author cuizhen
 * @date 2017/11/23
 */

public abstract class BaseSwipeDragTreeAdapter extends RecyclerView.Adapter<BaseSwipeDragTreeAdapter.BaseViewHolder> {

    private final Context mContext;
    /**
     * 关闭时保存子分组的打开状态，下次打开是恢复
     */
    private boolean mMemoryExpandState = false;
    private boolean mItemViewLongClickEnabled = false;

    private SparseIntArray mLayoutIds = null;
    private SparseArray<SparseIntArray> mViewIds = null;
    private SparseArray<SparseIntArray> mStartDragViewIds = null;

    private ArrayList<DataTree> mDataTrees = new ArrayList<>();
    private static ArrayList<PositionState> mPositionStates = new ArrayList<>();

    private ItemTouchHelper mItemTouchHelper = null;
    private SwipeDragTreeCallback mSwipeDragTreeCallback = null;

    private OnItemViewClickListener mOnItemViewClickListener = null;
    private OnCustomViewClickListener mOnCustomViewClickListener = null;

    private final static long LONG_PRESS_DRAG_ENABLE_LATER = 500;

    /**
     * customView 是否可点击和长按
     */
    public static final class ClickFlag {
        public static final int CANNOT = 0;
        public static final int CLICK = 1;
        public static final int LONGCLICK = 1 << 1;
    }
    /**
     * customView 拖拽
     */
    public static final class StartDragFlag {
        public static final int CANNOT = 0;
        public static final int TOUCH = 1;
        public static final int LONGTOUCH = 1 << 1;
    }

    /**
     * 4 种滑动方向
     */
    public static final class TouchFlag {
        public static final int UP = ItemTouchHelper.UP;
        public static final int DOWN = ItemTouchHelper.DOWN;
        public static final int LEFT = ItemTouchHelper.LEFT;
        public static final int RIGHT = ItemTouchHelper.RIGHT;
    }

    public BaseSwipeDragTreeAdapter(Context context) {
        initLayoutAndViewIds();
        mContext = context;
    }

    /**
     * 用于初始化数据，在适配器实例化后调用
     * 继承该适配器后，应该在 {@link #initLayoutAndViewIds()} 方法中完成初始化
     * 详见该方法说明
     *
     * @param dataTrees 数据源
     */
    public void initDataTrees(ArrayList<DataTree> dataTrees) {
        mDataTrees = dataTrees;
        initPositionStates();
        this.notifyDataSetChanged();
    }

    /**
     * 滑动删除，在实现的 onSwiped() 方法中调用
     * {@link SwipeDragTreeCallback.OnItemTouchCallbackListener#onSwiped(int)}
     *
     * @param position 滑动位置
     */
    public final void notifyTreeItemDelete(int position) {
        setItemViewSwipeEnabled(false);
        deletePositionState(position);
        this.notifyItemRemoved(position);
        setItemViewSwipeEnabled(true);
    }

    private void deletePositionState(int position) {
        synchronized (mContext) {
            PositionState positionState = mPositionStates.get(position);
            int[] positions = positionState.getPositions();
            if (positionState.isExpand()) {
                closeGroup(position, position);
            }
            DataTree.deleteByPositions(mDataTrees, positions);
            int lastIndex = positions.length - 1;
            int count = 0;
            for (int i = position + 1; i < mPositionStates.size(); i++) {
                int[] tempPositions = mPositionStates.get(i).getPositions();
                if (tempPositions.length >= positions.length) {
                    tempPositions[lastIndex]--;
                } else {
                    count = i - position;
                    break;
                }
            }
            this.notifyItemRangeChanged(position + 1, count);
            mPositionStates.remove(position);
        }
    }

    /**
     * 拖拽移动位置，在实现的 onMove() 方法中调用
     * {@link SwipeDragTreeCallback.OnItemTouchCallbackListener#onMove(int, int)}
     * <p>
     * 暂时仅实现同组同级别的移动，跨组移动未实现。。。
     *
     * @param fromPosition 拖拽位置
     * @param toPosition   目标位置
     */
    public final void notifyTreeItemMoved(int fromPosition, int toPosition) {
        synchronized (mContext) {
            PositionState fromPositionState = mPositionStates.get(fromPosition);
            int[] fromPositions = fromPositionState.getPositions();
            PositionState toPositionState = mPositionStates.get(toPosition);
            int[] toPositions = toPositionState.getPositions();

            DataTree.swapByPositions(mDataTrees, fromPositions, toPositions);
            fromPositionState.setPositions(toPositions);
            toPositionState.setPositions(fromPositions);
            Collections.swap(mPositionStates, fromPosition, toPosition);
            this.notifyItemMoved(fromPosition, toPosition);
        }
    }

    /**
     * 用于 Drag 操作，返回两个 item 是否是同级别的
     *
     * @param currentPosition currentPosition
     * @param targetPosition  targetPosition
     * @return boolean
     */
    public static boolean isSameLevel(int currentPosition, int targetPosition) {
        PositionState currentPositionState = mPositionStates.get(currentPosition);
        int[] currentPositions = currentPositionState.getPositions();
        PositionState targetPositionState = mPositionStates.get(targetPosition);
        int[] targetPositions = targetPositionState.getPositions();
        return currentPositions.length == targetPositions.length;
    }

    /**
     * 用于 Drag 操作，判断2个 item 的最近上级是否相同
     *
     * @param currentPosition currentPosition
     * @param targetPosition  targetPosition
     * @return sameGroup
     */
    public static boolean isSameGroup(int currentPosition, int targetPosition) {
        PositionState currentPositionState = mPositionStates.get(currentPosition);
        int[] currentPositions = currentPositionState.getPositions();
        PositionState targetPositionState = mPositionStates.get(targetPosition);
        int[] targetPositions = targetPositionState.getPositions();
        int limitSize = currentPositions.length < targetPositions.length ? currentPositions.length : targetPositions.length;
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
     * @return betweenHasExpand
     */
    public static boolean betweenHasExpand(int currentPosition, int targetPosition) {
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
            if (mPositionStates.get(i).isExpand()) {
                betweenHasExpand = true;
                break;
            }
        }
        return betweenHasExpand;
    }


    /**
     * 初始化 {@link #mPositionStates} 数据
     */
    private void initPositionStates() {
        mPositionStates.clear();
        for (int i = 0; i < mDataTrees.size(); i++) {
            PositionState positionState = new PositionState();
            positionState.setViewType(mDataTrees.get(i).getViewType());
            positionState.setPositions(new int[]{i});
            positionState.setExpand(false);
            mPositionStates.add(positionState);
        }
    }

    /**
     * 实现时调用
     * 按从根到叶的顺序添加 viewType
     * 调用 {@link #putLayoutAndViewIds(int, int, int[], int[])} 方法添加
     */
    public abstract void initLayoutAndViewIds();

    /**
     * 需要在实现 {@link #initLayoutAndViewIds()} 中调用
     *
     * @param viewType   类型 多种布局时需要继承 {@link PositionState} 并添加其他级的布局
     * @param layoutId   布局id
     * @param viewIds    布局中需要用到的 view 的id
     * @param clickFlags 设置view是否需要点击事件,设置为null时默认全部设置
     *                   {@link ClickFlag}
     */
    protected void putLayoutAndViewIds(int viewType, int layoutId, int[] viewIds, int[] clickFlags) {
        putLayoutId(viewType, layoutId);
        putViewIds(viewType, viewIds, clickFlags);
    }

    /**
     * 存在多种布局时，添加布局类型对应的布局 xml 文件
     *
     * @param viewType 布局类型
     * @param layoutId 布局 xml 文件的资源 id
     */
    private void putLayoutId(int viewType, @LayoutRes int layoutId) {
        if (mLayoutIds == null) {
            mLayoutIds = new SparseIntArray();
        }
        mLayoutIds.put(viewType, layoutId);
    }

    @LayoutRes
    protected int getLayoutId(int viewType) {
        return mLayoutIds.get(viewType);
    }

    /**
     * 添加布局中需要用到的 view 的id ，在创建 {@link #onCreateViewHolder(ViewGroup, int)} 中传入
     * 只有添加的 view 才能用于数据绑定
     *
     * @param viewType 类型
     * @param viewIds  view 的数组
     */
    private void putViewIds(int viewType, @IdRes int[] viewIds, int[] clickFlags) {
        if (mViewIds == null) {
            mViewIds = new SparseArray<>();
        }
        if (mViewIds.get(viewType) == null) {
            mViewIds.put(viewType, new SparseIntArray());
        }
        for (int i = 0; i < viewIds.length; i++) {
            int viewId = viewIds[i];
            int clickFlag = clickFlags == null ? (ClickFlag.CLICK | ClickFlag.LONGCLICK) : clickFlags[i];
            mViewIds.get(viewType).put(viewId, clickFlag);
        }
    }

    /**
     * 首先调用该方法，得到 item 布局类型
     *
     * @param position 条目显示位置，对应 {@link #mPositionStates} 的位置
     * @return item 布局类型，在{@link PositionState} 的子类中定义
     */
    @Override
    public int getItemViewType(int position) {
        return mPositionStates.get(position).getViewType();
    }

    /**
     * 依据布局类型创建 BaseTreeAdapter.BaseViewHolder
     *
     * @param parent   父布局
     * @param viewType item 布局类型
     * @return BaseTreeAdapter.BaseViewHolder
     */
    @Override
    public BaseSwipeDragTreeAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutIds.get(viewType), parent, false);
        return new BaseViewHolder(itemView, viewType);
    }

    /**
     * 依据传入的 holder 绑定数据
     * 调用 {@link #bindData(BaseViewHolder, int, DataTree)} 方法
     *
     * @param holder   BaseTreeAdapter.BaseViewHolder
     * @param position 显示的位置，对应 {@link #mPositionStates} 的位置
     */
    @Override
    public void onBindViewHolder(BaseSwipeDragTreeAdapter.BaseViewHolder holder, final int position) {
        final PositionState positionState = mPositionStates.get(holder.getAdapterPosition());
        final int viewType = positionState.getViewType();
        final int[] positions = positionState.getPositions();
        bindData(holder, viewType, getDataTree(positions));
    }

    /**
     * 获取显示条目的总数，由 {@link #mPositionStates} 控制
     *
     * @return 条目的总数
     */
    @Override
    public int getItemCount() {
        return mPositionStates == null ? 0 : mPositionStates.size();
    }

    /**
     * 实现该方法，用于绑定数据
     * 需要依据 viewType 做出 switch 判断
     * 然后调用 {@link BaseSwipeDragTreeAdapter.BaseViewHolder#getView(int)} 获取 itemView 中的子 View （需要强制类型转换）
     * 给子 View  绑定数据
     *
     * @param holder   BaseTreeAdapter.BaseViewHolder
     * @param viewType 依据 viewType 做出 switch 判断
     * @param dataTree 数据为 {@link DataTree#getData()}
     */
    public abstract void bindData(BaseSwipeDragTreeAdapter.BaseViewHolder holder, int viewType, DataTree dataTree);

    /**
     * 获取数据
     *
     * @param positions 数据的 positions 集合，即对应不同的分级
     * @return DataTree
     */
    private DataTree getDataTree(int[] positions) {
        ArrayList<DataTree> dataTrees = mDataTrees;
        DataTree dataTree = null;
        for (int position : positions) {
            if (dataTrees.size() > position) {
                dataTree = dataTrees.get(position);
            } else {
                break;
            }
            if (dataTree != null) {
                if (!dataTree.isLeaf()) {
                    dataTrees = dataTree.getDataTrees();
                }
            }
        }
        return dataTree;
    }


    /**
     * 是否记忆子分组展开状态 {@link #mMemoryExpandState}
     *
     * @return mMemoryExpandState
     */
    public boolean isMemoryExpandState() {
        return mMemoryExpandState;
    }

    /**
     * 设置关闭分组后，是否记忆子分组展开状态 {@link #mMemoryExpandState}
     *
     * @param memoryExpandState memoryExpandState
     */
    public void setMemoryExpandState(boolean memoryExpandState) {
        mMemoryExpandState = memoryExpandState;
    }

    /**
     * 打开分组时调用 {@link #onBindViewHolder(BaseSwipeDragTreeAdapter.BaseViewHolder, int)}
     *
     * @param clickPosition 点击位置（在方法内部使用，外部传入时和 position 相同即可）
     * @param position      打开的位置
     */
    private void openGroup(int clickPosition, int position) {
        synchronized (mContext) {
            PositionState positionState = mPositionStates.get(position);
            int[] positions = positionState.getPositions();
            DataTree dataTree = getDataTree(positions);
            ArrayList<DataTree> dataTrees = dataTree.getDataTrees();
            int count = dataTrees.size();
            for (int i = 0; i < count; i++) {
                DataTree childDataTree = dataTrees.get(i);
                PositionState newPositionState = new PositionState();
                newPositionState.setViewType(childDataTree.getViewType());
                int[] newPositions = Arrays.copyOf(positions, positions.length + 1);
                newPositions[newPositions.length - 1] = i;
                newPositionState.setPositions(newPositions);
                newPositionState.setExpand(childDataTree.isExpand());
                mPositionStates.add(position + i + 1, newPositionState);
            }
            if (position == clickPosition) {
                dataTree.setExpand(true);
                positionState.setExpand(true);
            }
            notifyItemRangeInserted(position + 1, count);
            for (int i = count - 1; i >= 0; i--) {
                if (mPositionStates.get(position + i + 1).isExpand()) {
                    openGroup(clickPosition, position + i + 1);
                }
            }
        }
    }

    /**
     * 关闭分组时调用 {@link #onBindViewHolder(BaseSwipeDragTreeAdapter.BaseViewHolder, int)}
     *
     * @param clickPosition 点击的位置（在方法内部使用，外部传入时和 position 相同即可）
     * @param position      关闭的位置
     */
    private void closeGroup(int clickPosition, int position) {
        synchronized (mContext) {
            final PositionState positionState = mPositionStates.get(position);
            int[] positions = positionState.getPositions();
            DataTree dataTree = getDataTree(positions);
            ArrayList<DataTree> dataTrees = dataTree.getDataTrees();
            int count = dataTrees.size();
            for (int i = 0; i < count; i++) {
                if (mPositionStates.get(position + i + 1).isExpand()) {
                    closeGroup(clickPosition, position + i + 1);
                }
            }
            for (int i = 0; i < count; i++) {
                mPositionStates.remove(position + 1);
            }
            if (mMemoryExpandState) {
                if (position == clickPosition) {
                    dataTree.setExpand(false);
                }
            } else {
                dataTree.setExpand(false);
            }
            positionState.setExpand(false);
            notifyItemRangeRemoved(position + 1, count);
        }
    }

    /**
     * 设置开启关闭拖拽
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener)}
     * 默认开启
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public void setLongPressDragEnabled(boolean longPressDragEnabled) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setLongPressDragEnabled(longPressDragEnabled);
        }
    }

    public boolean isLongPressDragEnabled() {
        return mSwipeDragTreeCallback != null && mSwipeDragTreeCallback.isLongPressDragEnabled();
    }

    /**
     * 设置开启关闭滑动
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener)}
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setItemViewSwipeEnabled(itemViewSwipeEnabled);
        }
    }

    public boolean isItemViewSwipeEnabled() {
        return mSwipeDragTreeCallback != null && mSwipeDragTreeCallback.isItemViewSwipeEnabled();
    }

    /**
     * 设置开启关闭滑动删除时是否背景变色
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener)}
     *
     * @param swipeBackgroundColorEnabled swipeBackgroundColorEnabled
     */
    public void setSwipeBackgroundColorEnabled(boolean swipeBackgroundColorEnabled) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setSwipeBackgroundColorEnabled(swipeBackgroundColorEnabled);
        }
    }

    public boolean isSwipeBackgroundColorEnabled() {
        return mSwipeDragTreeCallback != null && mSwipeDragTreeCallback.isSwipeBackgroundColorEnabled();
    }

    /**
     * 设置滑动删除时背景色
     *
     * @param swipeBackgroundColor 颜色资源id
     */
    public void setSwipeBackgroundColor(@ColorInt int swipeBackgroundColor) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setSwipeBackgroundColor(swipeBackgroundColor);
        }
    }

    /**
     * 设置可以滑动删除的方向
     * 需要在调用绑定监听器方法后调用设置，默认2个方向，垂直于列表滚动
     * {@link #setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener)}
     *
     * @param customSwipeFlag customSwipeFlag
     */
    public void setCustomSwipeFlag(int customSwipeFlag) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setCustomSwipeFlag(customSwipeFlag);
        }
    }

    /**
     * 设置可以拖拽的方向
     * 需要在调用绑定监听器方法后调用设置，默认4 个方向全部开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener)}
     *
     * @param customDragFlag customDragFlag
     */
    public void setCustomDragFlag(int customDragFlag) {
        if (mSwipeDragTreeCallback != null) {
            mSwipeDragTreeCallback.setCustomDragFlag(customDragFlag);
        }
    }

    /**
     * 为了方便使用，可直接调用 adapter 的 setOnItemTouchCallbackListener() 方法获取 ItemTouchHelper
     * 然后调用 ItemTouchHelper 的 attachToRecyclerView() 方法绑定 RecyclerView
     * 绑定后默认开启拖拽和滑动，若要关闭调用 adapter 的设置方法即可
     *
     * @param onItemTouchCallbackListener 调用时实现该接口
     * @return mItemTouchHelper
     */
    public ItemTouchHelper setOnItemTouchCallbackListener(SwipeDragTreeCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        if (mItemTouchHelper == null && mSwipeDragTreeCallback == null) {
            mSwipeDragTreeCallback = new SwipeDragTreeCallback(mContext);
            mItemTouchHelper = new ItemTouchHelper(mSwipeDragTreeCallback);
        }
        mSwipeDragTreeCallback.setOnItemTouchCallbackListener(onItemTouchCallbackListener);
        mSwipeDragTreeCallback.setOnSelectedChangedCallbackListener(new SwipeDragTreeCallback.OnSelectedChangedCallbackListener() {
            @Override
            public void onSwipe(int position) {
                PositionState positionState = mPositionStates.get(position);
                if (positionState.isExpand()) {
                    closeGroup(position, position);
                }
            }

            @Override
            public void onDrag(int position) {
                PositionState positionState = mPositionStates.get(position);
                if (positionState.isExpand()) {
                    closeGroup(position, position);
                }
            }
        });
        return mItemTouchHelper;
    }

    public boolean isItemViewLongClickEnabled() {
        return mItemViewLongClickEnabled;
    }

    public void setItemViewLongClickEnabled(boolean itemViewLongClickEnabled) {
        mItemViewLongClickEnabled = itemViewLongClickEnabled;
    }


    /**
     * BaseTreeAdapter.BaseViewHolder
     */
    public final class BaseViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews = null;
        private int mViewType;
        private boolean mIsSwiped = false;
        private boolean mIsStartDrag;
        private static final long IS_NOT_SWIPED_LATER = 100;

        public BaseViewHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            if (mViewIds != null) {
                for (int i = 0; i < mViewIds.get(viewType).size(); i++) {
                    addView(mViewIds.get(viewType).keyAt(i));
                }
            }
            setItemViewOnClickListener();
            setCustomViewOnClickListener();
        }

        private void addView(@IdRes int viewId) {
            if (mViews == null) {
                mViews = new SparseArray<>();
            }
            if (mViews.indexOfKey(viewId) <= -1) {
                mViews.put(viewId, itemView.findViewById(viewId));
            }
        }

        public View getView(@IdRes int viewId) {
            return mViews.get(viewId);
        }

        private void setItemViewOnClickListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsSwiped) {
                        try {
                            int position = getAdapterPosition();
                            PositionState positionState = mPositionStates.get(position);
                            int[] positions = positionState.getPositions();
                            DataTree dataTree = getDataTree(positions);
                            if (dataTree.isLeaf()) {
                                if (mOnItemViewClickListener != null) {
                                    mOnItemViewClickListener.onLeafItemViewClick(v, position, positions);
                                }
                            } else {
                                if (positionState.isExpand()) {
                                    closeGroup(position, position);
                                } else {
                                    openGroup(position, position);
                                }
                                LogUtil.d("---setOnClickListener--onClick-->", "onClick");
                                if (mOnItemViewClickListener != null) {
                                    mOnItemViewClickListener.onRootItemViewClick(v, position, positions);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            if (mItemViewLongClickEnabled) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!mIsSwiped) {
                            boolean longClick = true;
                            try {
                                int position = getAdapterPosition();
                                PositionState positionState = mPositionStates.get(position);
                                int[] positions = positionState.getPositions();
                                DataTree dataTree = getDataTree(positions);
                                if (dataTree.isLeaf()) {
                                    if (mOnItemViewClickListener != null) {
                                        final boolean dragEnabled = mSwipeDragTreeCallback.isLongPressDragEnabled();
                                        setLongPressDragEnabled(false);
                                        longClick = mOnItemViewClickListener.onLeafItemViewLongClick(v, position, positions);
                                        setLongPressDragEnabledLater(dragEnabled);
                                    }
                                } else {
                                    if (mOnItemViewClickListener != null) {
                                        final boolean dragEnabled = mSwipeDragTreeCallback.isLongPressDragEnabled();
                                        setLongPressDragEnabled(false);
                                        longClick = mOnItemViewClickListener.onRootItemViewLongClick(v, position, positions);
                                        setLongPressDragEnabledLater(dragEnabled);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return longClick;
                        }
                        return false;
                    }
                });
            }
        }

        /**
         * 根据clickFlag设置用户打开的子view的点击和长按事件
         */
        private void setCustomViewOnClickListener() {
            if (mViewIds != null && mViewIds.get(mViewType) != null) {
                for (int i = 0; i < mViewIds.get(mViewType).size(); i++) {
                    int clickFlag = mViewIds.get(mViewType).valueAt(i);
                    if (clickFlag != ClickFlag.CANNOT) {
                        final int viewId = mViewIds.get(mViewType).keyAt(i);
                        final View view = mViews.get(viewId);
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  01     01      01      &
                        //  01     00      01
                        if ((clickFlag & ClickFlag.CLICK) == ClickFlag.CLICK) {
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mOnCustomViewClickListener != null) {
                                        final int position = getAdapterPosition();
                                        final int[] positions = mPositionStates.get(position).getPositions();
                                        mOnCustomViewClickListener.onCustomViewClick(v, viewId, position, positions);
                                    }
                                }
                            });
                        }
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  10     10      10      &
                        //  00     10      10
                        if ((mStartDragViewIds != null) && (mStartDragViewIds.get(mViewType) != null)) {
                            int startDragFlag = mStartDragViewIds.get(mViewType).valueAt(i);
                            if ((startDragFlag & StartDragFlag.LONGTOUCH) == StartDragFlag.LONGTOUCH) {
                                mIsStartDrag = true;
                            }
                        }
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  10     10      10      &
                        //  00     10      10
                        if ((clickFlag & ClickFlag.LONGCLICK) == ClickFlag.LONGCLICK) {
                            view.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    boolean longClick = true;
                                    if (mOnCustomViewClickListener != null) {
                                        final int position = getAdapterPosition();
                                        final int[] positions = mPositionStates.get(position).getPositions();
                                        final boolean dragEnabled = mSwipeDragTreeCallback.isLongPressDragEnabled();
                                        if (mIsStartDrag) {
                                            setLongPressDragEnabled(false);
                                            mItemTouchHelper.startDrag(BaseViewHolder.this);
                                            setLongPressDragEnabled(dragEnabled);
                                        } else {
                                            setLongPressDragEnabled(false);
                                            longClick = mOnCustomViewClickListener.onCustomViewLongClick(v, viewId, position, positions);
                                            setLongPressDragEnabledLater(dragEnabled);
                                        }
                                    }
                                    return longClick;
                                }
                            });
                        }
                    }
                }
            }
            if ((mStartDragViewIds != null) && (mStartDragViewIds.get(mViewType) != null)) {
                for (int i = 0; i < mStartDragViewIds.get(mViewType).size(); i++) {
                    int startDragFlag = mStartDragViewIds.get(mViewType).valueAt(i);
                    //  单击   长按  单击&长按
                    //  01     10      11      flag
                    //  01     01      01      &
                    //  01     00      01
                    if ((startDragFlag & StartDragFlag.TOUCH) == StartDragFlag.TOUCH) {
                        int viewId = mViewIds.get(mViewType).keyAt(i);
                        View view = mViews.get(viewId);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        final boolean dragEnabled = mSwipeDragTreeCallback.isLongPressDragEnabled();
                                        setLongPressDragEnabled(false);
                                        mItemTouchHelper.startDrag(BaseViewHolder.this);
                                        setLongPressDragEnabled(dragEnabled);
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                }
            }
        }
    }

    protected void setStartDragViewIds(int viewType, @IdRes int[] viewIds, int[] startDragFlags) {
        if (mStartDragViewIds == null) {
            mStartDragViewIds = new SparseArray<>();
        }
        if (mStartDragViewIds.get(viewType) == null) {
            mStartDragViewIds.put(viewType, new SparseIntArray());
        }
        for (int i = 0; i < viewIds.length; i++) {
            int viewId = viewIds[i];
            int startDragFlag = startDragFlags == null ? (StartDragFlag.TOUCH | StartDragFlag.LONGTOUCH) : startDragFlags[i];
            mStartDragViewIds.get(viewType).put(viewId, startDragFlag);
        }
    }

    /**
     * 一段时间后恢复默认的 dragEnabled
     * 当item或者custom view 设置长按时间时，应该屏蔽长按拖拽事件，
     * 所以在长按回调方法前关闭拖拽，
     * 然后再回调方法后调用该方法间隔一段时间后再恢复默认打开或关闭状态
     *
     * @param dragEnabled 默认值
     */
    private void setLongPressDragEnabledLater(final boolean dragEnabled) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setLongPressDragEnabled(dragEnabled);
            }
        }, LONG_PRESS_DRAG_ENABLE_LATER);
    }

    /**
     * itemView 的点击事件的接口
     */
    public interface OnItemViewClickListener {
        /**
         * item的单击事件回调
         *
         * @param view      view
         * @param position  position
         * @param positions positions
         */
        void onLeafItemViewClick(View view, int position, int[] positions);

        /**
         * item的长按事件回调
         *
         * @param view      view
         * @param position  position
         * @param positions positions
         * @return boolean
         */
        boolean onLeafItemViewLongClick(View view, int position, int[] positions);

        /**
         * item的单击事件回调
         *
         * @param view      view
         * @param position  position
         * @param positions positions
         */
        void onRootItemViewClick(View view, int position, int[] positions);

        /**
         * item的长按事件回调
         *
         * @param view      view
         * @param position  position
         * @param positions positions
         * @return boolean
         */
        boolean onRootItemViewLongClick(View view, int position, int[] positions);
    }

    /**
     * 设置itemView 的点击事件监听器
     *
     * @param onItemViewClickListener 最末一级itemView 的点击事件的回调
     */
    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        if (mOnItemViewClickListener == null) {
            mOnItemViewClickListener = onItemViewClickListener;
        }
    }


    public interface OnCustomViewClickListener {
        /**
         * item中子view的单击监听回调
         *
         * @param view      view
         * @param viewId    viewId
         * @param position  position
         * @param positions positions
         */
        void onCustomViewClick(View view, int viewId, int position, int[] positions);

        /**
         * item中子view的长按监听回调
         *
         * @param view      view
         * @param viewId    viewId
         * @param position  position
         * @param positions positions
         * @return boolean
         */
        boolean onCustomViewLongClick(View view, int viewId, int position, int[] positions);
    }

    public void setOnCustomViewClickListener(OnCustomViewClickListener onCustomViewClickListener) {
        if (mOnCustomViewClickListener == null) {
            mOnCustomViewClickListener = onCustomViewClickListener;
        }
    }
}
