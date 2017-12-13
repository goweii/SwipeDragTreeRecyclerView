package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
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
    private ItemTouchHelper mItemTouchHelper = null;
    private SwipeDragCallback mCallback = null;
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
     * 滑动删除，在实现的 onSwiped() 方法中调用
     * {@link SwipeDragCallback.OnItemTouchCallbackListener#onSwiped(int)}
     *
     * @param position 滑动位置
     */
    public final void notifyItemSwiped(int position) {
        setItemViewSwipeEnabled(false);
        itemSwiped(position);
        this.notifyItemRemoved(position);
        setItemViewSwipeEnabled(true);
    }

    /**
     * 对Data和State数据进行删除操作
     *
     * @param position 删除位置
     */
    private void itemSwiped(int position) {
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
     * {@link SwipeDragCallback.OnItemTouchCallbackListener#onMove(int, int)}
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

    /**
     * 为了方便使用，可直接调用 adapter 的 setOnItemTouchCallbackListener() 方法获取 ItemTouchHelper
     * 然后调用 ItemTouchHelper 的 attachToRecyclerView() 方法绑定 RecyclerView
     * 绑定后默认开启拖拽和滑动，若要关闭调用 adapter 的设置方法即可
     *
     * @param onItemTouchCallbackListener 调用时实现该接口
     * @return mItemTouchHelper
     */
    public final ItemTouchHelper setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        if (mItemTouchHelper == null && mCallback == null) {
            mCallback = getNewCallback();
            mItemTouchHelper = new ItemTouchHelper(mCallback);
        }
        mCallback.setOnItemTouchCallbackListener(onItemTouchCallbackListener);
        return mItemTouchHelper;
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
     * 设置开启关闭拖拽
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     * 默认开启
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public final void setLongPressDragEnabled(boolean longPressDragEnabled) {
        if (mCallback != null) {
            mCallback.setLongPressDragEnabled(longPressDragEnabled);
        }
    }

    public final boolean isLongPressDragEnabled() {
        return mCallback != null && mCallback.isLongPressDragEnabled();
    }

    /**
     * 设置开启关闭滑动
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public final void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        if (mCallback != null) {
            mCallback.setItemViewSwipeEnabled(itemViewSwipeEnabled);
        }
    }

    public final boolean isItemViewSwipeEnabled() {
        return mCallback != null && mCallback.isItemViewSwipeEnabled();
    }

    /**
     * 设置开启关闭滑动删除时是否背景变色
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param swipeBackgroundColorEnabled swipeBackgroundColorEnabled
     */
    public final void setSwipeBackgroundColorEnabled(boolean swipeBackgroundColorEnabled) {
        if (mCallback != null) {
            mCallback.setSwipeBackgroundColorEnabled(swipeBackgroundColorEnabled);
        }
    }

    public final boolean isSwipeBackgroundColorEnabled() {
        return mCallback != null && mCallback.isSwipeBackgroundColorEnabled();
    }

    /**
     * 设置滑动删除时背景色
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param swipeBackgroundColor 颜色资源id
     */
    public final void setSwipeBackgroundColor(@ColorInt int swipeBackgroundColor) {
        if (mCallback != null) {
            mCallback.setSwipeBackgroundColor(swipeBackgroundColor);
        }
    }

    /**
     * 设置可以滑动删除的方向
     * 需要在调用绑定监听器方法后调用设置，默认2个方向，垂直于列表滚动
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param customSwipeFlag customSwipeFlag
     */
    public final void setCustomSwipeFlag(int customSwipeFlag) {
        if (mCallback != null) {
            mCallback.setCustomSwipeFlag(customSwipeFlag);
        }
    }

    /**
     * 设置可以拖拽的方向
     * 需要在调用绑定监听器方法后调用设置，默认4 个方向全部开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param customDragFlag customDragFlag
     */
    public final void setCustomDragFlag(int customDragFlag) {
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
