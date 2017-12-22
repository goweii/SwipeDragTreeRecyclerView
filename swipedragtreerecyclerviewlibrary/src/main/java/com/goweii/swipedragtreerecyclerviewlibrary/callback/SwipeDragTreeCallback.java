package com.goweii.swipedragtreerecyclerviewlibrary.callback;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

/**
 * @author cuizhen
 * @date 2017/11/21
 */
public class SwipeDragTreeCallback extends SwipeDragCallback {

    private OnSelectedChangedCallbackListener mOnSelectedChangedCallbackListener = null;
    private OnCanDropOverCallbackListener mOnCanDropOverCallbackListener = null;

    public SwipeDragTreeCallback() {
    }

    /**
     * 从静止状态变为拖拽或者滑动的时候会回调该方法，参数actionState表示当前的状态。
     *
     * @param viewHolder  viewHolder
     * @param actionState actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (mOnSelectedChangedCallbackListener != null) {
                mOnSelectedChangedCallbackListener.onSwipe(viewHolder.getAdapterPosition());
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (mOnSelectedChangedCallbackListener != null) {
                mOnSelectedChangedCallbackListener.onDrag(viewHolder.getAdapterPosition());
            }
        }
    }

    /**
     * 如果当前ViewHolder可以放在目标ViewHolder上，则返回true。
     * 官方文档如下：返回true 当前ViewHolder可以被拖动到目标位置后，直接”落“在target上，其他的上面的ViewHolder跟着“落”，
     * 所以要重写这个方法，不然只是拖动的ViewHolder在动，target ViewHolder不动，静止的
     *
     * @param recyclerView      recyclerView
     * @param currentViewHolder currentViewHolder
     * @param targetViewHolder  targetViewHolder
     * @return boolean
     */
    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder currentViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        if (mOnCanDropOverCallbackListener != null) {
            int currentPosition = currentViewHolder.getAdapterPosition();
            int targetPosition = targetViewHolder.getAdapterPosition();
            return mOnCanDropOverCallbackListener.canDropOver(currentPosition, targetPosition);
        } else {
            return super.canDropOver(recyclerView, currentViewHolder, targetViewHolder);
        }
    }

    /**
     * 绑定viewHolder状态改变回调接口
     *
     * @param onSelectedChangedCallbackListener viewHolder状态改变回调接口
     */
    public final void setOnSelectedChangedCallbackListener(OnSelectedChangedCallbackListener onSelectedChangedCallbackListener) {
        if (mOnSelectedChangedCallbackListener == null) {
            mOnSelectedChangedCallbackListener = onSelectedChangedCallbackListener;
        }
    }

    /**
     * viewHolder状态改变回调接口
     */
    public interface OnSelectedChangedCallbackListener {
        /**
         * 开始滑动
         *
         * @param position position
         */
        void onSwipe(int position);

        /**
         * 开始拖拽
         *
         * @param position position
         */
        void onDrag(int position);
    }

    /**
     * 绑定viewHolder是否可以拖拽到目标位置的回调接口
     *
     * @param onCanDropOverCallbackListener viewHolder是否可以拖拽到目标位置的回调接口
     */
    public final void setOnCanDropOverCallbackListener(OnCanDropOverCallbackListener onCanDropOverCallbackListener) {
        if (mOnCanDropOverCallbackListener == null) {
            mOnCanDropOverCallbackListener = onCanDropOverCallbackListener;
        }
    }

    /**
     * viewHolder是否可以拖拽到目标位置的回调接口
     */
    public interface OnCanDropOverCallbackListener {
        /**
         * 是否可以到当前位置，可以返回 true
         *
         * @param currentPosition position
         * @param targetPosition  position
         * @return boolean
         */
        boolean canDropOver(int currentPosition, int targetPosition);
    }
}
