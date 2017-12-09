package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BaseData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BasePositionState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author cuizhen
 * @date 2017/11/23
 */

public abstract class BaseSwipeDragAdapter extends RecyclerView.Adapter<BaseSwipeDragAdapter.BaseViewHolder> {

    private final Context mContext;

    private boolean mItemViewLongClickEnabled = false;

    private SparseIntArray mLayoutIds = null;
    private SparseArray<SparseIntArray> mViewIds = null;
    private SparseBooleanArray mStartDragView = null;

    private ArrayList<BaseData> mBaseData = new ArrayList<>();
    private ArrayList<BasePositionState> mBasePositionStates = new ArrayList<>();

    private ItemTouchHelper mItemTouchHelper = null;
    private SwipeDragCallback mSwipeDragCallback = null;

    private OnItemViewClickListener mOnItemViewClickListener = null;
    private OnCustomViewClickListener mOnCustomViewClickListener = null;

    private final static long LONG_PRESS_DRAG_ENABLED_LATER = 500;

    /**
     * customView 是否可点击和长按
     */
    public static final class ClickFlag {
        public static final int CANNOT = 0;
        public static final int CLICK = 1;
        public static final int LONG = 1 << 1;
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

    public BaseSwipeDragAdapter(Context context) {
        initLayoutAndViewIds();
        mContext = context;
    }

    /**
     * 滑动删除，在实现的 onSwiped() 方法中调用
     * {@link SwipeDragCallback.OnItemTouchCallbackListener#onSwiped(int)}
     *
     * @param position 滑动位置
     */
    public final void notifyItemSwiped(int position) {
        setItemViewSwipeEnabled(false);
        mBasePositionStates.remove(position);
        mBaseData.remove(position);
        this.notifyItemRemoved(position);
        setItemViewSwipeEnabled(true);
    }


    /**
     * 拖拽移动位置，在实现的 onMove() 方法中调用
     * {@link SwipeDragCallback.OnItemTouchCallbackListener#onMove(int, int)}
     * <p>
     * 暂时仅实现同组同级别的移动，跨组移动未实现。。。
     *
     * @param fromPosition 拖拽位置
     * @param toPosition   目标位置
     */
    public final void notifyItemDrag(int fromPosition, int toPosition) {
        synchronized (mContext) {
            Collections.swap(mBaseData, fromPosition, toPosition);
            Collections.swap(mBasePositionStates, fromPosition, toPosition);
            this.notifyItemMoved(fromPosition, toPosition);
        }
    }

    /**
     * 用于初始化数据，在适配器实例化后调用
     * 继承该适配器后，应该在 {@link #initLayoutAndViewIds()} 方法中完成初始化
     * 详见该方法说明
     *
     * @param datas 数据源
     */
    public void initDatas(ArrayList<BaseData> datas) {
        mBaseData = datas;
        initBasePositionStates();
        this.notifyDataSetChanged();
    }

    /**
     * 初始化 {@link #mBasePositionStates} 数据
     */
    private void initBasePositionStates() {
        mBasePositionStates.clear();
        for (int i = 0; i < mBaseData.size(); i++) {
            BasePositionState positionState = new BasePositionState();
            positionState.setViewType(mBaseData.get(i).getViewType());
            mBasePositionStates.add(positionState);
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
     * @param viewType   类型 多种布局时需要继承 {@link BasePositionState} 并添加其他级的布局
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
    private void putLayoutId(int viewType, @IdRes int layoutId) {
        if (mLayoutIds == null) {
            mLayoutIds = new SparseIntArray();
        }
        mLayoutIds.put(viewType, layoutId);
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
            int clickFlag = clickFlags == null ? (ClickFlag.CLICK | ClickFlag.LONG) : clickFlags[i];
            mViewIds.get(viewType).put(viewId, clickFlag);
        }
    }

    /**
     * 首先调用该方法，得到 item 布局类型
     *
     * @param position 条目显示位置，对应 {@link #mBasePositionStates} 的位置
     * @return item 布局类型，在{@link BasePositionState} 的子类中定义
     */
    @Override
    public int getItemViewType(int position) {
        return mBasePositionStates.get(position).getViewType();
    }

    /**
     * 依据布局类型创建 BaseTreeAdapter.BaseViewHolder
     *
     * @param parent   父布局
     * @param viewType item 布局类型
     * @return BaseTreeAdapter.BaseViewHolder
     */
    @Override
    public BaseSwipeDragAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutIds.get(viewType), parent, false);
        return new BaseViewHolder(itemView, viewType);
    }

    /**
     * 依据传入的 holder 绑定数据
     * 调用 {@link #bindData(BaseSwipeDragAdapter.BaseViewHolder, int, BaseData)} 方法
     *
     * @param holder   BaseTreeAdapter.BaseViewHolder
     * @param position 显示的位置，对应 {@link #mBasePositionStates} 的位置
     */
    @Override
    public void onBindViewHolder(BaseSwipeDragAdapter.BaseViewHolder holder, final int position) {
        final BasePositionState positionState = mBasePositionStates.get(holder.getAdapterPosition());
        final int viewType = positionState.getViewType();
        bindData(holder, viewType, getData(position));
    }

    /**
     * 实现该方法，用于绑定数据
     * 需要依据 viewType 做出 switch 判断
     * 然后调用 {@link BaseSwipeDragAdapter.BaseViewHolder#getView(int)} 获取 itemView 中的子 View （需要强制类型转换）
     * 给子 View  绑定数据
     *
     * @param holder   BaseTreeAdapter.BaseViewHolder
     * @param viewType 依据 viewType 做出 switch 判断
     * @param data     数据为 {@link BaseData#getData()}
     */
    public abstract void bindData(BaseSwipeDragAdapter.BaseViewHolder holder, int viewType, BaseData data);

    /**
     * 获取显示条目的总数，由 {@link #mBasePositionStates} 控制
     *
     * @return 条目的总数
     */
    @Override
    public int getItemCount() {
        return mBasePositionStates == null ? 0 : mBasePositionStates.size();
    }

    /**
     * 获取数据
     *
     * @param position position
     * @return BaseData
     */
    private BaseData getData(int position) {
        return mBaseData.get(position);
    }


    /**
     * 设置开启关闭拖拽
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     * 默认开启
     *
     * @param longPressDragEnabled longPressDragEnabled
     */
    public void setLongPressDragEnabled(boolean longPressDragEnabled) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setLongPressDragEnabled(longPressDragEnabled);
        }
    }
    public boolean isLongPressDragEnabled() {
        if (mSwipeDragCallback != null) {
            return mSwipeDragCallback.isLongPressDragEnabled();
        }
        return false;
    }

    /**
     * 设置开启关闭滑动
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param itemViewSwipeEnabled itemViewSwipeEnabled
     */
    public void setItemViewSwipeEnabled(boolean itemViewSwipeEnabled) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setItemViewSwipeEnabled(itemViewSwipeEnabled);
        }
    }
    public boolean isItemViewSwipeEnabled() {
        if (mSwipeDragCallback != null) {
            return mSwipeDragCallback.isItemViewSwipeEnabled();
        }
        return false;
    }

    /**
     * 设置开启关闭滑动删除时是否背景变色
     * 需要在调用绑定监听器方法后调用设置，默认开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param swipedBackgroundColorEnabled swipedBackgroundColorEnabled
     */
    public void setSwipedBackgroundColorEnabled(boolean swipedBackgroundColorEnabled) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setSwipeBackgroundColorEnabled(swipedBackgroundColorEnabled);
        }
    }

    /**
     * 设置滑动删除时背景色
     *
     * @param swipedBackgroundColor 颜色资源id
     */
    public void setSwipedBackgroundColor(@ColorRes int swipedBackgroundColor) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setSwipeBackgroundColor(swipedBackgroundColor);
        }
    }

    /**
     * 设置可以滑动删除的方向
     * 需要在调用绑定监听器方法后调用设置，默认2个方向，垂直于列表滚动
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param customSwipedFlag customSwipedFlag
     */
    public void setCustomSwipedFlag(int customSwipedFlag) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setCustomSwipedFlag(customSwipedFlag);
        }
    }

    /**
     * 设置可以拖拽的方向
     * 需要在调用绑定监听器方法后调用设置，默认4 个方向全部开启
     * {@link #setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener)}
     *
     * @param customDragFlag customDragFlag
     */
    public void setCustomDragFlag(int customDragFlag) {
        if (mSwipeDragCallback != null) {
            mSwipeDragCallback.setCustomDragFlag(customDragFlag);
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
    public ItemTouchHelper setOnItemTouchCallbackListener(SwipeDragCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        if (mItemTouchHelper == null && mSwipeDragCallback == null) {
            mSwipeDragCallback = new SwipeDragCallback(mContext);
            mItemTouchHelper = new ItemTouchHelper(mSwipeDragCallback);
        }
        mSwipeDragCallback.setOnItemTouchCallbackListener(onItemTouchCallbackListener);
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
        private static final long IS_NOT_SWIPED_LATER = 100;

        private BaseViewHolder(View itemView, int viewType) {
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
                        int position = getAdapterPosition();
                        if (mOnItemViewClickListener != null) {
                            mOnItemViewClickListener.onItemViewClick(v, position);
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
                            int position = getAdapterPosition();
                            if (mOnItemViewClickListener != null) {
                                final boolean dragEnable = mSwipeDragCallback.isLongPressDragEnabled();
                                setLongPressDragEnabled(false);
                                longClick = mOnItemViewClickListener.onItemViewLongClick(v, position);
                                setLongPressDragEnabledLater(dragEnable);
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
                                        mOnCustomViewClickListener.onCustomViewClick(v, viewId, position);
                                    }
                                }
                            });
                        }
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  10     10      10      &
                        //  00     10      10
                        if ((clickFlag & ClickFlag.LONG) == ClickFlag.LONG) {
                            view.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    boolean longClick = true;
                                    if (mOnCustomViewClickListener != null) {
                                        final int position = getAdapterPosition();
                                        final boolean dragEnable = mSwipeDragCallback.isLongPressDragEnabled();
                                        setLongPressDragEnabled(false);
                                        longClick = mOnCustomViewClickListener.onCustomViewLongClick(v, viewId, position);
                                        setLongPressDragEnabledLater(dragEnable);
                                    }
                                    return longClick;
                                }
                            });
                        }
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

    /**
     * itemView 的点击事件的接口
     */
    public interface OnItemViewClickListener {
        /**
         * item的单击事件回调
         *
         * @param view     view
         * @param position position
         */
        void onItemViewClick(View view, int position);

        /**
         * item的长按事件回调
         *
         * @param view     view
         * @param position position
         * @return boolean
         */
        boolean onItemViewLongClick(View view, int position);
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
         * @param view     view
         * @param viewId   viewId
         * @param position position
         */
        void onCustomViewClick(View view, int viewId, int position);

        /**
         * item中子view的长按监听回调
         *
         * @param view     view
         * @param viewId   viewId
         * @param position position
         * @return boolean
         */
        boolean onCustomViewLongClick(View view, int viewId, int position);
    }

    public void setOnCustomViewClickListener(OnCustomViewClickListener onCustomViewClickListener) {
        if (mOnCustomViewClickListener == null) {
            mOnCustomViewClickListener = onCustomViewClickListener;
        }
    }
}
