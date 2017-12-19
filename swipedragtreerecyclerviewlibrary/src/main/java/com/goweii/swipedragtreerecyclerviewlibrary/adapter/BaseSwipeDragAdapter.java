package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragCallback;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author cuizhen
 * @date 2017/11/23
 */
public abstract class BaseSwipeDragAdapter extends BaseTypeAdapter {
    /**
     * mTypeStartDragViewIds不能声明是赋值为null，否则在初始化完成后又变为null了
     * 貌似是因为初始化的方法{@link #putTypeStartDragViewIds(int, int[], int[])}
     * 是在{@link BaseTypeAdapter#initIds()}方法中调用，而该方法又是在其构造方法中调用
     * 各种super来this去的就变成null了
     * ( ╯□╰ )
     * 反正这里别赋值null就对了
     */
    private SparseArray<SparseIntArray> mTypeStartDragViewIds;

    private RecyclerView mRecyclerView = null;
    private SwipeDragCallback mCallback = null;
    private ItemTouchHelper mItemTouchHelper = null;

    private boolean mLongPressDragEnabled = false;
    private boolean mItemViewSwipeEnabled = false;
    private boolean mSwipeBackgroundColorEnabled = false;
    private int mSwipeBackgroundColor = 0xFFFF4081;
    private int mCustomSwipeFlag = TouchFlag.UP | TouchFlag.DOWN | TouchFlag.LEFT | TouchFlag.RIGHT;
    private int mCustomDragFlag = TouchFlag.UP | TouchFlag.DOWN | TouchFlag.LEFT | TouchFlag.RIGHT;

    private final static long LONG_PRESS_DRAG_ENABLED_LATER = 500;

    protected static final class StartDragFlag {
        public static final int TOUCH = 1;
        public static final int LONGTOUCH = 1 << 1;
    }

    public static final class TouchFlag {
        public static final int UP = ItemTouchHelper.UP;
        public static final int DOWN = ItemTouchHelper.DOWN;
        public static final int LEFT = ItemTouchHelper.LEFT;
        public static final int RIGHT = ItemTouchHelper.RIGHT;
    }

    public BaseSwipeDragAdapter() {
        super();
    }

    /**
     * 滑动删除，在实现的 onSwipe() 方法中调用
     * {@link SwipeDragCallback.OnItemSwipeListener#onSwipe(int)}
     *
     * @param position 滑动位置
     */
    public final void notifyItemSwipe(int position) {
        setItemViewSwipeEnabled(false);
        itemSwipe(position);
        this.notifyItemRemoved(position);
        setItemViewSwipeEnabled(true);
    }

    /**
     * 对Data和State数据进行删除操作
     *
     * @param position 删除位置
     */
    private void itemSwipe(int position) {
        synchronized (this) {
            swipedData(position);
            swipedState(position);
        }
    }

    /**
     * 可以在子类中重写删除操作
     *
     * @param position 删除位置
     */
    protected void swipedState(int position) {
        getStates().remove(position);
    }

    /**
     * 可以在子类中重写删除操作
     *
     * @param position 删除位置
     */
    protected void swipedData(int position) {
        getDatas().remove(position);
    }

    /**
     * 拖拽移动位置，在实现的 onMove() 方法中调用
     * {@link SwipeDragCallback.OnItemDragListener#onMove(int, int)}
     *
     * @param currentPosition 拖拽位置
     * @param targetPosition  目标位置
     */
    public final void notifyItemDrag(int currentPosition, int targetPosition) {
        itemDrag(currentPosition, targetPosition);
        this.notifyItemMoved(currentPosition, targetPosition);
    }

    /**
     * 对Data和State数据进行拖拽操作
     *
     * @param currentPosition 当前位置
     * @param targetPosition  目标位置
     */
    private void itemDrag(int currentPosition, int targetPosition) {
        synchronized (this) {
            dragData(currentPosition, targetPosition);
            dragState(currentPosition, targetPosition);
        }
    }

    /**
     * 可以在子类中重写对State的拖拽逻辑
     *
     * @param currentPosition 当前位置
     * @param targetPosition  目标位置
     */
    protected void dragState(int currentPosition, int targetPosition) {
        Collections.swap(getDatas(), currentPosition, targetPosition);
    }

    /**
     * 可以在子类中重写对Data数据的拖拽逻辑
     *
     * @param currentPosition 当前位置
     * @param targetPosition  目标位置
     */
    protected void dragData(int currentPosition, int targetPosition) {
        Collections.swap(getStates(), currentPosition, targetPosition);
    }

    /**
     * 如果你想让某一个view在点击或者长按时实现拖拽，而不是在长按整个item时
     * 可以在实现{@link BaseTypeAdapter#initIds()}时调用添加
     *
     * @param viewType       布局类型
     * @param viewIds        拖拽操作的view的id
     * @param startDragFlags 拖拽标志，在{@link StartDragFlag}中定义了
     */
    protected final void putTypeStartDragViewIds(int viewType, @IdRes int[] viewIds, int[] startDragFlags) {
        if (mTypeStartDragViewIds == null) {
            mTypeStartDragViewIds = new SparseArray<>();
        }
        if (mTypeStartDragViewIds.get(viewType) == null) {
            mTypeStartDragViewIds.put(viewType, new SparseIntArray());
        }
        for (int i = 0; i < viewIds.length; i++) {
            int viewId = viewIds[i];
            int startDragFlag = (startDragFlags == null ? StartDragFlag.TOUCH : startDragFlags[i]);
            mTypeStartDragViewIds.get(viewType).put(viewId, startDragFlag);
        }
    }

    @Override
    protected SwipeDragViewHolder creatHolder(View itemView, int viewType) {
        return new SwipeDragViewHolder(itemView, getViewIds(viewType), getStartDragViewIds(viewType));
    }

    /**
     * 依据布局类型获取执行拖拽操作的view
     *
     * @param viewType 布局类型
     * @return view的SparseIntArray，键为id，值为标志
     */
    protected final SparseIntArray getStartDragViewIds(int viewType) {
        return mTypeStartDragViewIds == null ? null : mTypeStartDragViewIds.get(viewType);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    private void openItemTouchHelper() {
        mCallback = getNewCallback();
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mCallback.setItemViewSwipeEnabled(mItemViewSwipeEnabled);
        mCallback.setLongPressDragEnabled(mLongPressDragEnabled);
        mCallback.setCustomSwipeFlag(mCustomSwipeFlag);
        mCallback.setCustomDragFlag(mCustomDragFlag);
        mCallback.setSwipeBackgroundColorEnabled(mSwipeBackgroundColorEnabled);
        mCallback.setSwipeBackgroundColor(mSwipeBackgroundColor);
        if (mRecyclerView != null) {
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    private void closeItemTouchHelper() {
        mItemTouchHelper = null;
        mCallback = null;
    }

    /**
     * 为了方便使用，可直接调用 adapter 的 setOnItemSwipeListener() 方法获取 ItemTouchHelper
     * 然后调用 ItemTouchHelper 的 attachToRecyclerView() 方法绑定 RecyclerView
     * 绑定后默认开启拖拽和滑动，若要关闭调用 adapter 的设置方法即可
     *
     * @param onItemSwipeListener 调用时实现该接口
     */
    public final void setOnItemSwipeListener(SwipeDragCallback.OnItemSwipeListener onItemSwipeListener) {
        if (mCallback == null) {
            setItemViewSwipeEnabled(true);
        }
        mCallback.setOnItemSwipeListener(onItemSwipeListener);
    }

    public final void setOnItemDragListener(SwipeDragCallback.OnItemDragListener onItemDragListener) {
        if (mCallback == null) {
            setLongPressDragEnabled(true);
        }
        mCallback.setOnItemDragListener(onItemDragListener);
    }

    /**
     * 获取对应的callback实例，你可以重写该方法，并在其中绑定其他回调接口
     *
     * @return callback实例
     */
    protected SwipeDragCallback getNewCallback() {
        return new SwipeDragCallback();
    }

    /**
     * 设置开启关闭滑动
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public final void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        mItemViewSwipeEnabled = itemViewSwipeEnabled;
        if (!(mLongPressDragEnabled || mItemViewSwipeEnabled || mSwipeBackgroundColorEnabled)) {
            closeItemTouchHelper();
        } else {
            if (mCallback == null) {
                openItemTouchHelper();
            } else {
                mCallback.setItemViewSwipeEnabled(itemViewSwipeEnabled);
            }
        }
    }

    /**
     * 设置开启关闭拖拽
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public final void setLongPressDragEnabled(boolean longPressDragEnabled) {
        mLongPressDragEnabled = longPressDragEnabled;
        if (!(mLongPressDragEnabled || mItemViewSwipeEnabled || mSwipeBackgroundColorEnabled)) {
            closeItemTouchHelper();
        } else {
            if (mCallback == null) {
                openItemTouchHelper();
            } else {
                mCallback.setLongPressDragEnabled(mLongPressDragEnabled);
            }
        }
    }

    /**
     * 设置开启关闭滑动删除时是否背景变色
     *
     * @param swipeBackgroundColorEnabled swipeBackgroundColorEnabled
     */
    public final void setSwipeBackgroundColorEnabled(boolean swipeBackgroundColorEnabled) {
        mSwipeBackgroundColorEnabled = swipeBackgroundColorEnabled;
        if (!(mLongPressDragEnabled || mItemViewSwipeEnabled || mSwipeBackgroundColorEnabled)) {
            closeItemTouchHelper();
        } else {
            if (mCallback == null) {
                openItemTouchHelper();
            } else {
                mCallback.setSwipeBackgroundColorEnabled(swipeBackgroundColorEnabled);
            }
        }
    }

    public final boolean isLongPressDragEnabled() {
        return mLongPressDragEnabled;
    }

    public final boolean isItemViewSwipeEnabled() {
        return mItemViewSwipeEnabled;
    }

    public final boolean isSwipeBackgroundColorEnabled() {
        return mSwipeBackgroundColorEnabled;
    }

    /**
     * 设置滑动删除时背景色
     *
     * @param swipeBackgroundColor 颜色资源id
     */
    public final void setSwipeBackgroundColor(@ColorInt int swipeBackgroundColor) {
        mSwipeBackgroundColor = swipeBackgroundColor;
        if (mCallback != null) {
            mCallback.setSwipeBackgroundColor(swipeBackgroundColor);
        }
    }

    /**
     * 设置可以滑动删除的方向
     *
     * @param customSwipeFlag customSwipeFlag
     */
    public final void setCustomSwipeFlag(int customSwipeFlag) {
        mCustomSwipeFlag = customSwipeFlag;
        if (mCallback != null) {
            mCallback.setCustomSwipeFlag(customSwipeFlag);
        }
    }

    /**
     * 设置可以拖拽的方向
     *
     * @param customDragFlag customDragFlag
     */
    public final void setCustomDragFlag(int customDragFlag) {
        mCustomDragFlag = customDragFlag;
        if (mCallback != null) {
            mCallback.setCustomDragFlag(customDragFlag);
        }
    }

    /**
     * SwipeDragViewHolder
     */
    protected class SwipeDragViewHolder extends BaseTypeAdapter.BaseViewHolder {
        private SparseIntArray mStartDragClickFlags = null;

        protected SwipeDragViewHolder(View itemView, SparseIntArray viewIds, SparseIntArray startDragClickFlags) {
            super(itemView, viewIds);
            mStartDragClickFlags = startDragClickFlags;
            setCustomViewOnTouchListener();
        }

        @Override
        protected boolean itemViewOnLongClick(View v, int position) {
            final boolean dragEnable = mCallback.isLongPressDragEnabled();
            setLongPressDragEnabled(false);
            boolean longClick = mOnItemViewLongClickListener.onItemViewLongClick(v, position);
            setLongPressDragEnabledLater(dragEnable);
            return longClick;
        }

        @Override
        protected boolean customViewOnLongClick(View v, int position) {
            if (mStartDragClickFlags != null &&
                    ((mStartDragClickFlags.get(v.getId()) & StartDragFlag.LONGTOUCH) == StartDragFlag.LONGTOUCH)) {
                mItemTouchHelper.startDrag(SwipeDragViewHolder.this);
            } else {
                final boolean dragEnabled = mCallback.isLongPressDragEnabled();
                setLongPressDragEnabled(false);
                boolean longClick = mOnCustomViewLongClickListener.onCustomViewLongClick(v, position);
                setLongPressDragEnabledLater(dragEnabled);
                return longClick;
            }
            return false;
        }

        protected void setCustomViewOnTouchListener() {
            if (mStartDragClickFlags != null) {
                for (int i = 0; i < mStartDragClickFlags.size(); i++) {
                    int startDragFlag = mStartDragClickFlags.valueAt(i);
                    if ((startDragFlag & StartDragFlag.TOUCH) == StartDragFlag.TOUCH) {
                        int viewId = mViewIds.keyAt(i);
                        View view = getView(viewId);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        mItemTouchHelper.startDrag(SwipeDragViewHolder.this);
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
        }, LONG_PRESS_DRAG_ENABLED_LATER);
    }
}
