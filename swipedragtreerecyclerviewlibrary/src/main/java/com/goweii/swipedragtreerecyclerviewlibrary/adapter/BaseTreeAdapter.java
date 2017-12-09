package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.DataTree;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.PositionState;
import com.goweii.swipedragtreerecyclerviewlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author cuizhen
 * @date 2017/11/23
 */

public abstract class BaseTreeAdapter extends RecyclerView.Adapter<BaseTreeAdapter.BaseViewHolder> {

    private final Context mContext;
    /**
     * 关闭时保存子分组的打开状态，下次打开是恢复
     */
    private boolean mMemoryExpandState = false;

    private SparseIntArray mLayoutIds = null;
    private SparseArray<SparseIntArray> mViewIds = null;

    private ArrayList<DataTree> mDataTrees = new ArrayList<>();
    private static ArrayList<PositionState> mPositionStates = new ArrayList<>();

    private OnItemViewClickListener mOnItemViewClickListener = null;
    private OnCustomViewClickListener mOnCustomViewClickListener = null;

    /**
     * customView 是否可点击和长按
     */
    public static final class ClickFlag {
        public static final int CANNOT = 0;
        public static final int CLICK = 1;
        public static final int LONG = 1 << 1;
    }

    public BaseTreeAdapter(Context context) {
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
    public BaseTreeAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(BaseTreeAdapter.BaseViewHolder holder, final int position) {
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
     * 然后调用 {@link BaseTreeAdapter.BaseViewHolder#getView(int)} 获取 itemView 中的子 View （需要强制类型转换）
     * 给子 View  绑定数据
     *
     * @param holder   BaseTreeAdapter.BaseViewHolder
     * @param viewType 依据 viewType 做出 switch 判断
     * @param dataTree 数据为 {@link DataTree#getData()}
     */
    public abstract void bindData(BaseTreeAdapter.BaseViewHolder holder, int viewType, DataTree dataTree);

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
     * 打开分组时调用 {@link #onBindViewHolder(BaseTreeAdapter.BaseViewHolder, int)}
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
     * 关闭分组时调用 {@link #onBindViewHolder(BaseTreeAdapter.BaseViewHolder, int)}
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
     * BaseTreeAdapter.BaseViewHolder
     */
    public class BaseViewHolder extends RecyclerView.ViewHolder {
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
                                    longClick = mOnItemViewClickListener.onLeafItemViewLongClick(v, position, positions);
                                }
                            } else {
                                if (mOnItemViewClickListener != null) {
                                    longClick = mOnItemViewClickListener.onRootItemViewLongClick(v, position, positions);
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
