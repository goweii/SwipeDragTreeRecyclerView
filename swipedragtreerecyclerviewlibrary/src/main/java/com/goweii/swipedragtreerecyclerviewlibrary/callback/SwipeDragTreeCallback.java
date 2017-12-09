package com.goweii.swipedragtreerecyclerviewlibrary.callback;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goweii.swipedragtreerecyclerviewlibrary.R;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseDragSwipedTreeAdapter;

/**
 *
 * @author cuizhen
 * @date 2017/11/21
 */
public class SwipeDragTreeCallback extends ItemTouchHelper.Callback {
    private Context mContext;

    private boolean mLongPressDragEnabled = true;
    private boolean mItemViewSwipeEnabled = true;

    private ImageView mSwipedBackgroundColorImage;
    private boolean mSwipeBackgroundColorEnabled = true;
    private int mSwipedBackgroundColor = R.color.colorSwipedBackground;

    private int mCustomSwipedFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    private int mCustomDragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    public SwipeDragTreeCallback(Context context) {
        mContext = context;
    }

    public void setCustomSwipedFlag(int customSwipedFlag) {
        mCustomSwipedFlag = customSwipedFlag;
    }

    public void setCustomDragFlag(int customDragFlag) {
        mCustomDragFlag = customDragFlag;
    }

    /**
     * 设置开启关闭拖拽
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public void setLongPressDragEnabled(boolean longPressDragEnabled) {
        this.mLongPressDragEnabled = longPressDragEnabled;
    }

    /**
     * 设置开启关闭滑动
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        this.mItemViewSwipeEnabled = itemViewSwipeEnabled;
    }

    /**
     * 设置开启关闭，滑动删除时是否背景变色，默认开启，为红色选中色
     *
     * @param swipeBackgroundColorEnabled swipeBackgroundColorEnabled
     */
    public void setSwipeBackgroundColorEnabled(boolean swipeBackgroundColorEnabled) {
        mSwipeBackgroundColorEnabled = swipeBackgroundColorEnabled;
    }

    /**
     * 设置滑动删除时背景色
     *
     * @param swipedBackgroundColor 颜色资源id
     */
    public void setSwipedBackgroundColor(@ColorRes int swipedBackgroundColor) {
        mSwipedBackgroundColor = swipedBackgroundColor;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mLongPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mItemViewSwipeEnabled;
    }

    public boolean isSwipeBackgroundColorEnabled() {
        return mSwipeBackgroundColorEnabled;
    }

    public int getSwipedBackgroundColor() {
        return mSwipedBackgroundColor;
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
        int swipedFlag = 0;
        if (mLongPressDragEnabled) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int orientation = linearLayoutManager.getOrientation();
                switch (orientation) {
                    case LinearLayoutManager.VERTICAL:
                        dragFlag = mCustomDragFlag & (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                        swipedFlag = mCustomSwipedFlag & (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                        break;
                    case LinearLayoutManager.HORIZONTAL:
                        dragFlag = mCustomDragFlag & (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                        swipedFlag = mCustomSwipedFlag & (ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                        break;
                    default:
                        break;
                }
            } else if (layoutManager instanceof GridLayoutManager) {
                dragFlag = mCustomDragFlag & (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }
        }
        return makeMovementFlags(dragFlag, swipedFlag);
    }

    /**
     * 从静止状态变为拖拽或者滑动的时候会回调该方法，参数actionState表示当前的状态。
     *
     * @param viewHolder viewHolder
     * @param actionState actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (mSwipeBackgroundColorEnabled) {
                ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
                ViewGroup.LayoutParams viewGroupParams = viewGroup.getLayoutParams();
                mSwipedBackgroundColorImage = new ImageView(mContext);
                mSwipedBackgroundColorImage.setLayoutParams(viewGroupParams);
                viewGroup.addView(mSwipedBackgroundColorImage, -1);
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
            viewGroup.animate()
                    .translationZBy(1)
                    .setDuration(100)
                    .start();
        }
    }


    /**
     * 在RecyclerView的onDraw回调中由ItemTouchHelper调用。
     * 我们可以在这个方法内实现我们自定义的交互规则或者自定义的动画效果。
     * ---------------------------------------------------------------------
     * ---注意:为确保滑动时出现红色背景动画, item 的根布局应当采用 FrameLayout ---
     * ---------------------------------------------------------------------
     *
     * @param c                 Canvas
     * @param recyclerView      recyclerView
     * @param viewHolder        viewHolder
     * @param dX                滑动距离，右为正
     * @param dY                滑动距离，下为正
     * @param actionState       滑动方向
     * @param isCurrentlyActive isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (mSwipeBackgroundColorEnabled) {
                //仅对侧滑状态下的效果做出改变
                viewGroup.scrollTo(-(int) dX, 0);
                mSwipedBackgroundColorImage.setImageResource(mSwipedBackgroundColor);
                float threshold = getThreshold(viewHolder);
                mSwipedBackgroundColorImage.setAlpha(Math.abs(dX) / threshold);
                if (dX > 0) {
                    mSwipedBackgroundColorImage.setTranslationX(-viewHolder.itemView.getWidth());
                } else {
                    mSwipedBackgroundColorImage.setTranslationX(viewHolder.itemView.getWidth());
                }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
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
     * 如果当前ViewHolder可以放在目标ViewHolder上，则返回true。
     * 官方文档如下：返回true 当前ViewHolder可以被拖动到目标位置后，直接”落“在target上，其他的上面的ViewHolder跟着“落”，
     * 所以要重写这个方法，不然只是拖动的ViewHolder在动，target ViewHolder不动，静止的
     *
     * @param recyclerView recyclerView
     * @param current      current
     * @param target       target
     * @return boolean
     */
    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
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
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        if (mSwipeBackgroundColorEnabled) {
            viewGroup.scrollTo(0, 0);
            if (viewGroup.getChildAt(viewGroup.getChildCount() - 1) == mSwipedBackgroundColorImage) {
                viewGroup.removeView(mSwipedBackgroundColorImage);
            }
        }
        viewGroup.animate()
                .translationZ(0)
                .setDuration(0)
                .scaleX(1f).scaleY(1f)
                .start();
    }

    /**
     * 当item 被拖拽时调用
     *
     * @param recyclerView     recyclerView
     * @param srcViewHolder    拖拽的 ViewHolder
     * @param targetViewHolder 目的地 ViewHolder
     * @return {@link OnItemTouchCallbackListener#onMove(int, int)}
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        if (mOnItemTouchCallbackListener != null) {
            int fromPosition = srcViewHolder.getAdapterPosition();
            int toPosition = targetViewHolder.getAdapterPosition();
            if (BaseDragSwipedTreeAdapter.betweenHasExpand(fromPosition, toPosition)) {
                return false;
            } else {
                if (BaseDragSwipedTreeAdapter.isSameLevel(fromPosition, toPosition) &&
                        BaseDragSwipedTreeAdapter.isSameGroup(fromPosition, toPosition)) {
                    return mOnItemTouchCallbackListener.onMove(fromPosition, toPosition);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 当滑动删除调用
     *
     * @param viewHolder 滑动的 ViewHolder
     * @param direction  滑动的方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (mOnItemTouchCallbackListener != null) {
            mOnItemTouchCallbackListener.onSwiped(position);
        }
    }

    /**
     * 设置监听器，用于更新数据和UI
     *
     * @param onItemTouchCallbackListener OnItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        if (mOnItemTouchCallbackListener == null) {
            mOnItemTouchCallbackListener = onItemTouchCallbackListener;
        }
    }


    private OnItemTouchCallbackListener mOnItemTouchCallbackListener = null;

    public interface OnItemTouchCallbackListener {

        /**
         * 移动数据
         *
         * @param fromPosition fromPosition
         * @param toPosition   toPosition
         * @return boolean
         */
        boolean onMove(int fromPosition, int toPosition);

        /**
         * 滑动删除数据
         * 实现时应该这样使用：
         * 调用 BaseDragSwipedTreeAdapter.notifyTreeItemDelete 方法更新显示，方法链接如下：
         * {@link BaseDragSwipedTreeAdapter#notifyTreeItemDelete(int)}
         * 该方法内部会自动更新数据，不需要在其之前手动中更新删除数据，否则会重复删除，使程序崩溃
         *
         * @param position 滑动的位置
         */
        void onSwiped(int position);
    }
}
