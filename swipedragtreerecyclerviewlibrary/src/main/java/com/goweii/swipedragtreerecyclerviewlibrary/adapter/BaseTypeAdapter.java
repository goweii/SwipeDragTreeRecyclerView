package com.goweii.swipedragtreerecyclerviewlibrary.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeState;

import java.util.ArrayList;


/**
 * @author cuizhen
 * @date 2017/12/11
 */
public abstract class BaseTypeAdapter extends RecyclerView.Adapter<BaseTypeAdapter.BaseViewHolder> {
    private ArrayList<TypeData> mDatas = null;
    private ArrayList<TypeState> mStates = null;
    private SparseIntArray mTypeLayoutIds = null;
    private SparseArray<SparseIntArray> mTypeViewIds = null;

    protected OnItemViewClickListener mOnItemViewClickListener = null;
    protected OnItemViewLongClickListener mOnItemViewLongClickListener = null;
    protected OnCustomViewClickListener mOnCustomViewClickListener = null;
    protected OnCustomViewLongClickListener mOnCustomViewLongClickListener = null;

    /**
     * customView 是否可点击和长按
     */
    protected static final class ClickFlag {
        public static final int CANNOT = 0;
        public static final int CLICK = 1;
        public static final int LONGCLICK = 1 << 1;
        public static final int BOTH = CLICK | LONGCLICK;
    }

    public BaseTypeAdapter() {
        initIds();
    }

    /**
     * 初始化完adapter后调用，用于绑定数据
     *
     * @param datas 数据，ArrayList类型
     */
    public final void init(ArrayList datas) {
        initDatas(datas);
        initStates();
        notifyDataSetChanged();
    }

    /**
     * 若{@link #mDatas}的泛型类型不是{@link TypeData}类型
     * 需要在子类中重写该方法，并在adapter中添加自己的ArrayList数据
     *
     * @param datas ArrayList
     */
    protected void initDatas(ArrayList datas) {
        mDatas = datas;
    }

    /**
     * 根据{@link #mDatas}的泛型类型，新建{@link #mStates}泛型类型并添加
     * 需要在子类中重写该方法
     */
    protected void initStates() {
        if (mStates == null) {
            mStates = new ArrayList<>();
        }
        for (int i = 0; i < getDatasSize(); i++) {
            TypeState state = new TypeState();
            state.setType(getData(i).getType());
            mStates.add(state);
        }
    }

    protected ArrayList getDatas() {
        return mDatas;
    }

    protected ArrayList getStates() {
        return mStates;
    }

    protected int getDatasSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    protected int getStatesSize() {
        return mStates == null ? 0 : mStates.size();
    }

    protected TypeData getData(int position) {
        return mDatas.get(position);
    }

    protected TypeState getState(int position) {
        return mStates.get(position);
    }

    /**
     * 用于初始化布局类型，布局资源id，控件id，控件是否可点击和长按等
     * 实现后调用 {@link #putTypeLayoutViewIds(int, int, int[], int[])} 方法添加数据
     */
    public abstract void initIds();

    /**
     * 需要在实现 {@link #initIds()} 中调用
     *
     * @param viewType   类型 多种布局时需要继承 {@link TypeState} 并添加其他级的布局
     * @param layoutId   布局id
     * @param viewIds    布局中需要用到的 view 的id
     * @param clickFlags 设置view是否需要点击事件,设置为null时默认不开启长按和单击
     *                   {@link ClickFlag}
     */
    protected final void putTypeLayoutViewIds(int viewType, int layoutId, int[] viewIds, int[] clickFlags) {
        putTypeLayoutId(viewType, layoutId);
        putTypeViewIds(viewType, viewIds, clickFlags);
    }

    /**
     * 添加布局中需要用到的 view 的id ，在创建 {@link #onCreateViewHolder(ViewGroup, int)} 中传入
     * 只有添加的 view 才能用于数据绑定
     *
     * @param viewType 类型
     * @param viewIds  view 的数组
     */
    private void putTypeViewIds(int viewType, @IdRes int[] viewIds, int[] clickFlags) {
        if (mTypeViewIds == null) {
            mTypeViewIds = new SparseArray<>();
        }
        if (mTypeViewIds.get(viewType) == null) {
            mTypeViewIds.put(viewType, new SparseIntArray());
        }
        for (int i = 0; i < viewIds.length; i++) {
            int viewId = viewIds[i];
            int clickFlag = clickFlags == null ? ClickFlag.CANNOT : clickFlags[i];
            mTypeViewIds.get(viewType).put(viewId, clickFlag);
        }
    }

    /**
     * 存在多种布局时，添加布局类型对应的布局 xml 文件
     *
     * @param viewType 布局类型
     * @param layoutId 布局 xml 文件的资源 id
     */
    private void putTypeLayoutId(int viewType, @LayoutRes int layoutId) {
        if (mTypeLayoutIds == null) {
            mTypeLayoutIds = new SparseIntArray();
        }
        mTypeLayoutIds.put(viewType, layoutId);
    }

    /**
     * 首先调用该方法，得到 item 布局类型
     *
     * @param position 条目显示位置，对应 {@link #mStates} 的位置
     * @return item 布局类型，在{@link TypeState} 的子类中定义
     */
    @Override
    public final int getItemViewType(int position) {
        return getState(position).getType();
    }

    /**
     * 获取显示条目的总数，由 {@link #mStates} 控制
     *
     * @return 条目的总数
     */
    @Override
    public final int getItemCount() {
        return getStatesSize();
    }


    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mTypeLayoutIds.get(viewType), parent, false);
        return creatHolder(itemView, viewType);
    }

    /**
     * 依据布局类型创建 BaseTreeAdapter
     *
     * @param itemView itemView
     * @param viewType item 布局类型
     * @return BaseTreeAdapter
     */
    protected BaseTypeAdapter.BaseViewHolder creatHolder(View itemView, int viewType) {
        return new BaseViewHolder(itemView, getViewIds(viewType));
    }

    protected final SparseIntArray getViewIds(int viewType) {
        return mTypeViewIds == null ? null : mTypeViewIds.get(viewType);
    }

    @Override
    public final void onBindViewHolder(BaseTypeAdapter.BaseViewHolder holder, int position) {
        bindData(holder, getData(position));
    }

    /**
     * 实现该方法，用于绑定数据
     * 需要依据 viewType 做出 switch 判断
     * 然后调用 {@link BaseViewHolder#getView(int)} 获取 itemView 中的子 View （需要强制类型转换）
     * 给子 View  绑定数据
     *
     * @param holder BaseTreeAdapter.SwipeDragViewHolder
     * @param data   数据为 {@link TypeData#getData()}
     */
    protected abstract void bindData(BaseViewHolder holder, TypeData data);


    /**
     * item的单击回调接口
     */
    public interface OnItemViewClickListener {
        /**
         * item的单击回调方法
         *
         * @param view     item的根控件
         * @param position 控件在adapter中的位置
         */
        void onItemViewClick(View view, int position);
    }

    /**
     * 用于绑定item的单击回调接口
     *
     * @param onItemViewClickListener item的单击监听接口
     */
    public final void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        if (mOnItemViewClickListener == null) {
            mOnItemViewClickListener = onItemViewClickListener;
        }
    }

    /**
     * item的长按回调接口
     */
    public interface OnItemViewLongClickListener {
        /**
         * item的长按回调方法
         *
         * @param view     item的根控件
         * @param position 控件在adapter中的位置
         * @return 如果已经实现接口应该返回true
         */
        boolean onItemViewLongClick(View view, int position);
    }

    /**
     * 用于绑定item的长按回调接口
     *
     * @param onItemViewLongClickListener item的长按监听接口
     */
    public final void setOnItemViewLongClickListener(OnItemViewLongClickListener onItemViewLongClickListener) {
        if (mOnItemViewLongClickListener == null) {
            mOnItemViewLongClickListener = onItemViewLongClickListener;
        }
    }

    /**
     * 子控件的单击回调接口
     */
    public interface OnCustomViewClickListener {
        /**
         * 子控件的单击回调方法
         *
         * @param view     当前控件
         * @param position 控件在adapter中的位置
         */
        void onCustomViewClick(View view, int position);
    }

    /**
     * 用于绑定子控件的单击回调接口
     *
     * @param onCustomViewClickListener 子控件的单击回调接口
     */
    public final void setOnCustomViewClickListener(OnCustomViewClickListener onCustomViewClickListener) {
        if (mOnCustomViewClickListener == null) {
            mOnCustomViewClickListener = onCustomViewClickListener;
        }
    }

    /**
     * 子控件的长按回调接口
     */
    public interface OnCustomViewLongClickListener {
        /**
         * 子控件的长按回调方法
         *
         * @param view     当前控件
         * @param position 控件在adapter中的位置
         * @return 如果已经实现接口应该返回true
         */
        boolean onCustomViewLongClick(View view, int position);
    }

    /**
     * 用于绑定子控件的长按回调接口
     *
     * @param onCustomViewLongClickListener 子控件的长按回调接口
     */
    public final void setOnCustomViewLongClickListener(OnCustomViewLongClickListener onCustomViewLongClickListener) {
        if (mOnCustomViewLongClickListener == null) {
            mOnCustomViewLongClickListener = onCustomViewLongClickListener;
        }
    }


    /**
     * BaseViewHolder
     */
    protected class BaseViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews = null;
        protected SparseIntArray mViewIds = null;

        protected BaseViewHolder(View itemView, SparseIntArray viewIds) {
            super(itemView);
            mViewIds = viewIds;
            if (mViewIds != null) {
                for (int i = 0; i < mViewIds.size(); i++) {
                    addView(mViewIds.keyAt(i));
                }
            }
            setItemViewClickListener();
            setItemViewLongClickListener();
            setCustomViewListeners();
        }

        /**
         * 在构造方法中调用，依据viewId找到控件并添加到mViews中
         *
         * @param viewId 控件id
         */
        private void addView(@IdRes int viewId) {
            if (mViews == null) {
                mViews = new SparseArray<>();
            }
            if (mViews.indexOfKey(viewId) <= -1) {
                mViews.put(viewId, itemView.findViewById(viewId));
            }
        }

        /**
         * 依据控件id获取对应的view对象
         * 在{@link BaseTypeAdapter#bindData(BaseViewHolder, TypeData)}
         * 方法中调用获取view实例绑定数据
         *
         * @param viewId 控件id
         * @return view对象
         */
        public final View getView(@IdRes int viewId) {
            return mViews.get(viewId);
        }

        /**
         * 如果外部调用方法绑定了itemView的单击回调接口
         * {@link BaseTypeAdapter#setOnItemViewClickListener(OnItemViewClickListener)}
         * 则对itemView绑定单击事件
         */
        private void setItemViewClickListener() {
            if (mOnItemViewClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemViewOnClick(v, getAdapterPosition());
                    }
                });
            }
        }

        /**
         * 如果外部调用方法绑定了itemView的长按回调接口
         * {@link BaseTypeAdapter#setOnItemViewLongClickListener(OnItemViewLongClickListener)}
         * 则对itemView绑定长按事件
         */
        private void setItemViewLongClickListener() {
            if (mOnItemViewLongClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return itemViewOnLongClick(v, getAdapterPosition());
                    }
                });
            }
        }

        /**
         * 在对itemView绑定单机事件的onClick(View v)方法中调用
         * 你可以在子类中重写，并在回调 {@link BaseTypeAdapter#mOnItemViewClickListener}
         * 前后做一些其他操作
         *
         * @param v        item的根控件
         * @param position 控件在adapter中的位置
         */
        protected void itemViewOnClick(View v, int position) {
            mOnItemViewClickListener.onItemViewClick(v, position);
        }

        /**
         * 在对itemView绑定长按事件的onLongClick(View v)方法中调用
         * 你可以在子类中重写，并在回调 {@link BaseTypeAdapter#mOnItemViewLongClickListener}
         * 前后做一些其他操作
         *
         * @param v        item的根控件
         * @param position 控件在adapter中的位置
         */
        protected boolean itemViewOnLongClick(View v, int position) {
            return mOnItemViewLongClickListener.onItemViewLongClick(v, position);
        }

        /**
         * 对子控件绑定各种监听器，默认仅有单击和长按监听长按
         * 如果需要绑定其他监听器，可以重写{@link #setCustomViewListener(View)}方法
         */
        private void setCustomViewListeners() {
            if (mViewIds != null) {
                for (int i = 0; i < mViewIds.size(); i++) {
                    final int viewId = mViewIds.keyAt(i);
                    final View view = getView(viewId);
                    int clickFlag = mViewIds.valueAt(i);
                    if (clickFlag != ClickFlag.CANNOT) {
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  01     01      01      &
                        //  01     00      01
                        if ((clickFlag & ClickFlag.CLICK) == ClickFlag.CLICK) {
                            setCustomViewClickListener(view);
                        }
                        //  单击   长按  单击&长按
                        //  01     10      11      flag
                        //  10     10      10      &
                        //  00     10      10
                        if ((clickFlag & ClickFlag.LONGCLICK) == ClickFlag.LONGCLICK) {
                            setCustomViewLongClickListener(view);
                        }
                    }
                    setCustomViewListener(view);
                }
            }
        }

        /**
         * 如果外部调用方法绑定了customView的单击回调接口
         * {@link BaseTypeAdapter#setOnCustomViewClickListener(OnCustomViewClickListener)}
         * 则对customView绑定单击事件
         *
         * @param view 子控件
         */
        private void setCustomViewClickListener(View view) {
            if (mOnCustomViewClickListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        customViewOnClick(v, position);
                    }
                });
            }
        }

        /**
         * 在对customView绑定单击事件的onClick(View v)方法中调用
         * 你可以在子类中重写，并在回调 {@link BaseTypeAdapter#mOnCustomViewClickListener}
         * 前后做一些其他操作
         *
         * @param v        customView
         * @param position 控件在adapter中的位置
         */
        protected void customViewOnClick(View v, int position) {
            mOnCustomViewClickListener.onCustomViewClick(v, position);
        }

        /**
         * 如果外部调用方法绑定了customView的长按回调接口
         * {@link BaseTypeAdapter#setOnCustomViewLongClickListener(OnCustomViewLongClickListener)}
         * 则对customView绑定长按事件
         *
         * @param view 子控件
         */
        private void setCustomViewLongClickListener(View view) {
            if (mOnCustomViewClickListener != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        return customViewOnLongClick(v, position);
                    }
                });
            }
        }

        /**
         * 在对customView绑定长按事件的onLongClick(View v)方法中调用
         * 你可以在子类中重写，并在回调 {@link BaseTypeAdapter#mOnCustomViewLongClickListener}
         * 前后做一些其他操作
         *
         * @param v        customView
         * @param position 控件在adapter中的位置
         */
        protected boolean customViewOnLongClick(View v, int position) {
            return mOnCustomViewLongClickListener.onCustomViewLongClick(v, position);
        }

        /**
         * 你可以重写该方法对一些特殊控件绑定监听方法
         * 例如checkBox, radioButton 等
         * 只需要在重写时依据view 的id 判断是否为对应view，然后强转类型绑定监听方法
         * 当然，你需要在adapter设置对应的回调接口
         *
         * @param view 需要调用view.getId()进行判断
         */
        protected void setCustomViewListener(View view) {
        }
    }
}
