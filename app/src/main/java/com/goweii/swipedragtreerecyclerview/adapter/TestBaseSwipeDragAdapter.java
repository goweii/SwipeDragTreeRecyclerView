package com.goweii.swipedragtreerecyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerview.activity.MainActivity;
import com.goweii.swipedragtreerecyclerview.activity.R;
import com.goweii.swipedragtreerecyclerview.activity.SwipeDragRecyclerViewActivity;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeState;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeState;

/**
 * @author cuizhen
 * @date 2017/12/9
 */
public class TestBaseSwipeDragAdapter extends BaseSwipeDragAdapter {
    private int mOrientationType;

    public TestBaseSwipeDragAdapter(int orientationType) {
        super();
        mOrientationType = orientationType;
    }

    @Override
    public void initIds() {
        int[] clickFlags = new int[1];
        if (SwipeDragRecyclerViewActivity.mCustomLongClickEnable) {
            clickFlags[0] = ClickFlag.BOTH;
        } else {
            clickFlags[0] = ClickFlag.CANNOT;
        }
        putTypeLayoutViewIds(TypeState.TYPE_LEAF, R.layout.item_swipe_drag_recycler_view,
                new int[]{R.id.item_sdrv_tv}, clickFlags);
        if (SwipeDragRecyclerViewActivity.mCustomViewDragEnable) {
            putTypeStartDragViewIds(TypeState.TYPE_LEAF, new int[]{R.id.item_sdrv_tv}, null);
        }
    }

    @Override
    protected SwipeDragViewHolder creatHolder(View itemView, int viewType) {
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
        switch (holder.getItemViewType()) {
            case TreeState.TYPE_LEAF:
                TextView textView = (TextView) holder.getView(R.id.item_sdrv_tv);
                textView.setText((String) data.getData());
                break;
            default:
                break;
        }

    }
}
