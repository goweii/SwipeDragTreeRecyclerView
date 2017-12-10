package com.goweii.swipedragtreerecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerview.activity.MainActivity;
import com.goweii.swipedragtreerecyclerview.activity.R;
import com.goweii.swipedragtreerecyclerview.entity.TreePositionState;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.DataTree;

/**
 * @author cuizhen
 * @date 2017/11/23
 */

public class SwipeDragTreeAdapter extends BaseSwipeDragTreeAdapter {
    private final int mOrientationType;

    public SwipeDragTreeAdapter(Context context, int orientationType) {
        super(context);
        mOrientationType = orientationType;
    }

    @Override
    public void initLayoutAndViewIds() {
        putLayoutAndViewIds(TreePositionState.TYPE_ONE, R.layout.item1_swipe_drag_tree_recycler_view,
                new int[]{R.id.item1_sdtrv_tv}, null);
        putLayoutAndViewIds(TreePositionState.TYPE_TEO, R.layout.item2_swipe_drag_tree_recycler_view,
                new int[]{R.id.item2_sdtrv_tv}, null);
        putLayoutAndViewIds(TreePositionState.TYPE_THREE, R.layout.item3_swipe_drag_tree_recycler_view,
                new int[]{R.id.item3_sdtrv_tv}, null);
        putLayoutAndViewIds(TreePositionState.TYPE_FOUR, R.layout.item4_swipe_drag_tree_recycler_view,
                new int[]{R.id.item4_sdtrv_tv}, null);
        putLayoutAndViewIds(TreePositionState.TYPE_LEAF, R.layout.item5_swipe_drag_tree_recycler_view,
                new int[]{R.id.item5_sdtrv_tv}, null);
        setStartDragViewIds(TreePositionState.TYPE_ONE,
                new int[]{R.id.item1_sdtrv_tv}, null);
        setStartDragViewIds(TreePositionState.TYPE_TEO,
                new int[]{R.id.item2_sdtrv_tv}, null);
        setStartDragViewIds(TreePositionState.TYPE_THREE,
                new int[]{R.id.item3_sdtrv_tv}, null);
        setStartDragViewIds(TreePositionState.TYPE_FOUR,
                new int[]{R.id.item4_sdtrv_tv}, null);
        setStartDragViewIds(TreePositionState.TYPE_LEAF,
                new int[]{R.id.item5_sdtrv_tv}, null);
    }

    @Override
    public SwipeDragTreeAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        if (mOrientationType == MainActivity.OrientationType.HORIZONTAL){
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }
        return new BaseViewHolder(itemView, viewType);
    }

    @Override
    public void bindData(BaseViewHolder holder, int viewType, DataTree dataTree) {
        switch (viewType) {
            case TreePositionState.TYPE_ONE:
                TextView textView0 = (TextView) holder.getView(R.id.item1_sdtrv_tv);
                textView0.setText((String) dataTree.getData());
                break;
            case TreePositionState.TYPE_TEO:
                TextView textView1 = (TextView) holder.getView(R.id.item2_sdtrv_tv);
                textView1.setText((String) dataTree.getData());
                break;
            case TreePositionState.TYPE_THREE:
                TextView textView2 = (TextView) holder.getView(R.id.item3_sdtrv_tv);
                textView2.setText((String) dataTree.getData());
                break;
            case TreePositionState.TYPE_FOUR:
                TextView textView3 = (TextView) holder.getView(R.id.item4_sdtrv_tv);
                textView3.setText((String) dataTree.getData());
                break;
            case TreePositionState.TYPE_LEAF:
                TextView textView4 = (TextView) holder.getView(R.id.item5_sdtrv_tv);
                textView4.setText((String) dataTree.getData());
                break;
            default:
                break;
        }
    }

}
