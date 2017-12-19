package com.goweii.swipedragtreerecyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerview.activity.MainActivity;
import com.goweii.swipedragtreerecyclerview.activity.R;
import com.goweii.swipedragtreerecyclerview.activity.SwipeDragTreeRecyclerViewActivity;
import com.goweii.swipedragtreerecyclerview.entity.TestTreeState;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeData;

/**
 * @author cuizhen
 * @date 2017/11/23
 */

public class TestBaseSwipeDragTreeAdapter extends BaseSwipeDragTreeAdapter {
    private final int mOrientationType;

    public TestBaseSwipeDragTreeAdapter(int orientationType) {
        super();
        mOrientationType = orientationType;
    }

    @Override
    public void initIds() {
        int[] clickFlags = new int[1];
        if (SwipeDragTreeRecyclerViewActivity.mCustomLongClickEnable) {
            clickFlags[0] = ClickFlag.BOTH;
        } else {
            clickFlags[0] = ClickFlag.CANNOT;
        }
        putTypeLayoutViewIds(TestTreeState.TYPE_ONE, R.layout.item1_swipe_drag_tree_recycler_view,
                new int[]{R.id.item1_sdtrv_tv}, clickFlags);
        putTypeLayoutViewIds(TestTreeState.TYPE_TEO, R.layout.item2_swipe_drag_tree_recycler_view,
                new int[]{R.id.item2_sdtrv_tv}, clickFlags);
        putTypeLayoutViewIds(TestTreeState.TYPE_THREE, R.layout.item3_swipe_drag_tree_recycler_view,
                new int[]{R.id.item3_sdtrv_tv}, clickFlags);
        putTypeLayoutViewIds(TestTreeState.TYPE_FOUR, R.layout.item4_swipe_drag_tree_recycler_view,
                new int[]{R.id.item4_sdtrv_tv}, clickFlags);
        putTypeLayoutViewIds(TestTreeState.TYPE_LEAF, R.layout.item5_swipe_drag_tree_recycler_view,
                new int[]{R.id.item5_sdtrv_tv}, clickFlags);
        if (SwipeDragTreeRecyclerViewActivity.mCustomViewDragEnable) {
            putTypeStartDragViewIds(TestTreeState.TYPE_ONE,
                    new int[]{R.id.item1_sdtrv_tv}, null);
            putTypeStartDragViewIds(TestTreeState.TYPE_TEO,
                    new int[]{R.id.item2_sdtrv_tv}, null);
            putTypeStartDragViewIds(TestTreeState.TYPE_THREE,
                    new int[]{R.id.item3_sdtrv_tv}, null);
            putTypeStartDragViewIds(TestTreeState.TYPE_FOUR,
                    new int[]{R.id.item4_sdtrv_tv}, null);
            putTypeStartDragViewIds(TestTreeState.TYPE_LEAF,
                    new int[]{R.id.item5_sdtrv_tv}, null);
        }
    }

    @Override
    protected SwipeDragTreeViewHolder creatHolder(View itemView, int viewType) {
        if (mOrientationType == MainActivity.OrientationType.HORIZONTAL) {
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }
        return super.creatHolder(itemView, viewType);
    }

    @Override
    protected void bindData(BaseViewHolder holder, TypeData data) {
        SwipeDragTreeViewHolder viewHolder = (SwipeDragTreeViewHolder) holder;
        switch (holder.getItemViewType()) {
            case TestTreeState.TYPE_ONE:
                TextView textView0 = (TextView) viewHolder.getView(R.id.item1_sdtrv_tv);
                textView0.setText((String) data.getData());
                break;
            case TestTreeState.TYPE_TEO:
                TextView textView1 = (TextView) viewHolder.getView(R.id.item2_sdtrv_tv);
                textView1.setText((String) data.getData());
                break;
            case TestTreeState.TYPE_THREE:
                TextView textView2 = (TextView) viewHolder.getView(R.id.item3_sdtrv_tv);
                textView2.setText((String) data.getData());
                break;
            case TestTreeState.TYPE_FOUR:
                TextView textView3 = (TextView) viewHolder.getView(R.id.item4_sdtrv_tv);
                textView3.setText((String) data.getData());
                break;
            case TestTreeState.TYPE_LEAF:
                TextView textView4 = (TextView) viewHolder.getView(R.id.item5_sdtrv_tv);
                textView4.setText((String) data.getData());
                break;
            default:
                break;
        }
    }
}
