package com.goweii.swipedragtreerecyclerviewlibrary.callback;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author cuizhen
 * @date 2017/11/21
 */
public class SwipeDragCallback extends ItemTouchHelper.Callback {
    private Paint mPaint = null;
    private boolean mLongPressDragEnabled = true;
    private boolean mItemViewSwipeEnabled = true;
    private boolean mSwipeBackgroundColorEnabled = false;
    private @ColorInt
    int mSwipeBackgroundColor = 0xFFFF4081;
    private int mCustomSwipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    private int mCustomDragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    private OnItemDragListener mOnItemDragListener = null;
    private OnItemSwipeListener mOnItemSwipeListener = null;

    public SwipeDragCallback() {
    }

    public final void setCustomSwipeFlag(int customSwipeFlag) {
        mCustomSwipeFlag = customSwipeFlag;
    }

    public final void setCustomDragFlag(int customDragFlag) {
        mCustomDragFlag = customDragFlag;
    }

    /**
     * 设置开启关闭拖拽
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public final void setLongPressDragEnabled(boolean longPressDragEnabled) {
        this.mLongPressDragEnabled = longPressDragEnabled;
    }

    /**
     * 设置开启关闭滑动
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public final void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        this.mItemViewSwipeEnabled = itemViewSwipeEnabled;
    }

    /**
     * 设置开启关闭，滑动删除时是否背景变色，默认开启，为红色选中色
     *
     * @param swipeBackgroundColorEnabled swipeBackgroundColorEnabled
     */
    public final void setSwipeBackgroundColorEnabled(boolean swipeBackgroundColorEnabled) {
        mSwipeBackgroundColorEnabled = swipeBackgroundColorEnabled;
        if (mSwipeBackgroundColorEnabled) {
            mPaint = new Paint();
            mPaint.setColor(mSwipeBackgroundColor);
        } else {
            mPaint = null;
        }
    }

    /**
     * 设置滑动删除时背景色
     *
     * @param swipeBackgroundColor 颜色资源id
     */
    public final void setSwipeBackgroundColor(@ColorInt int swipeBackgroundColor) {
        mSwipeBackgroundColor = swipeBackgroundColor;
        if (mPaint != null) {
            mPaint.setColor(mSwipeBackgroundColor);
        }
    }

    @Override
    public final boolean isLongPressDragEnabled() {
        return mLongPressDragEnabled;
    }

    @Override
    public final boolean isItemViewSwipeEnabled() {
        return mItemViewSwipeEnabled;
    }

    public final boolean isSwipeBackgroundColorEnabled() {
        return mSwipeBackgroundColorEnabled;
    }

    public final int getSwipeBackgroundColor() {
        return mSwipeBackgroundColor;
    }

    /**
     * 通知系统拖拽和滑动的方向
     * 共4个方向，0为关闭
     * ItemTouchHelper.UP
     * ItemTouchHelper.DOWN
     * ItemTouchHelper.LEFT
     * ItemTouchHelper.RIGHT
     * ItemTouchHelper.START    水平起始方向滑动，决定于左或右视RecyclerView的布局方向
     * ItemTouchHelper.END      水平末尾方向滑动，决定于左或右视RecyclerView的布局方向
     *
     * @param recyclerView 绑定的RecyclerView
     * @param viewHolder   绑定的RecyclerView.ViewHolder
     * @return makeMovementFlags(dragFlag, swapFlag)
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = 0;
        int swipeFlag = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();
            switch (orientation) {
                case LinearLayoutManager.VERTICAL:
                    dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    swipeFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    break;
                case LinearLayoutManager.HORIZONTAL:
                    dragFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    swipeFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    break;
                default:
                    break;
            }
        }
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int orientation = gridLayoutManager.getOrientation();
            int spanCount = gridLayoutManager.getSpanCount();
            switch (orientation) {
                case GridLayoutManager.VERTICAL:
                    if (spanCount == 1) {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    } else {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    }
                    swipeFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    break;
                case GridLayoutManager.HORIZONTAL:
                    if (spanCount == 1) {
                        dragFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    } else {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    }
                    swipeFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    break;
                default:
                    break;
            }
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int orientation = staggeredGridLayoutManager.getOrientation();
            int spanCount = staggeredGridLayoutManager.getSpanCount();
            switch (orientation) {
                case StaggeredGridLayoutManager.VERTICAL:
                    if (spanCount == 1) {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    } else {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    }
                    swipeFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    break;
                case StaggeredGridLayoutManager.HORIZONTAL:
                    if (spanCount == 1) {
                        dragFlag = (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    } else {
                        dragFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                    }
                    swipeFlag = (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                    break;
                default:
                    break;
            }
        }
        dragFlag = mCustomDragFlag & dragFlag;
        swipeFlag = mCustomSwipeFlag & swipeFlag;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    /**
     * 在RecyclerView的onDraw回调中由ItemTouchHelper调用。
     * 我们可以在这个方法内实现我们自定义的交互规则或者自定义的动画效果。
     *
     * @param c                 Canvas
     * @param recyclerView      recyclerView
     * @param viewHolder        viewHolder
     * @param dX                水平滑动时 dY 为0.0
     * @param dY                垂直滑动时 dX 为0.0
     * @param actionState       滑动方向
     * @param isCurrentlyActive isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (mSwipeBackgroundColorEnabled) {
                float threshold = getThreshold(viewHolder);
                float alpha = ((Math.abs(dX + dY) / threshold) > 1 ? 1 : (Math.abs(dX + dY) / threshold));
                mPaint.setAlpha((int) (alpha * 255));
                int left = viewHolder.itemView.getLeft();
                int top = viewHolder.itemView.getTop();
                int right = viewHolder.itemView.getRight();
                int bottom = viewHolder.itemView.getBottom();
                c.drawRect(left, top, right, bottom, mPaint);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    /**
     * 获取滑动删除的阈值
     *
     * @param viewHolder viewHolder
     * @return float
     */
    private float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getWidth() * getSwipeThreshold(viewHolder);
    }

    /**
     * 当用户操作完毕某个item并且其动画也结束后会调用该方法，
     * 一般我们在该方法内恢复ItemView的初始状态，防止由于复用而产生的显示错乱问题。
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    /**
     * 当item 被拖拽时调用
     *
     * @param recyclerView     recyclerView
     * @param srcViewHolder    拖拽的
     * @param targetViewHolder 目标的
     * @return boolean
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        if (mOnItemDragListener != null) {
            int srcPosition = srcViewHolder.getAdapterPosition();
            int targetPosition = targetViewHolder.getAdapterPosition();
            return mOnItemDragListener.onMove(srcPosition, targetPosition);
        }
        return false;
    }

    /**
     * 当item被滑动删除后调用
     *
     * @param viewHolder 拖拽的
     * @param direction  方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mOnItemSwipeListener != null) {
            mOnItemSwipeListener.onSwipe(viewHolder.getAdapterPosition());
        }
    }

    /**
     * 设置监听器，用于更新数据和UI
     *
     * @param onItemSwipeListener OnItemSwipeListener
     */
    public final void setOnItemSwipeListener(OnItemSwipeListener onItemSwipeListener) {
        if (mOnItemSwipeListener == null) {
            mOnItemSwipeListener = onItemSwipeListener;
        }
    }

    public interface OnItemSwipeListener {
        /**
         * 滑动删除数据
         *
         * @param position 滑动的位置
         */
        void onSwipe(int position);
    }

    /**
     * 设置监听器，用于更新数据和UI
     *
     * @param onItemSwipeListener OnItemSwipeListener
     */
    public final void setOnItemDragListener(OnItemDragListener onItemSwipeListener) {
        if (mOnItemDragListener == null) {
            mOnItemDragListener = onItemSwipeListener;
        }
    }

    public interface OnItemDragListener {
        /**
         * 移动数据
         *
         * @param fromPosition fromPosition
         * @param toPosition   toPosition
         * @return boolean
         */
        boolean onMove(int fromPosition, int toPosition);
    }
}
