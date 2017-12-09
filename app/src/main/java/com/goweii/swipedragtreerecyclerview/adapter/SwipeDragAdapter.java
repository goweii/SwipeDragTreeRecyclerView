package com.goweii.swipedragtreerecyclerview.adapter;

import android.content.Context;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerview.activity.R;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BaseData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BasePositionState;

/**
 * @author cuizhen
 * @date 2017/12/9
 */
public class SwipeDragAdapter extends BaseSwipeDragAdapter {
    public SwipeDragAdapter(Context context) {
        super(context);
    }

    @Override
    public void initLayoutAndViewIds() {
        putLayoutAndViewIds(BasePositionState.TYPE_LEAF, R.layout.item_drag_swiped_recycler_view,
                new int[]{R.id.item_tv}, null);
    }

    @Override
    public void bindData(BaseViewHolder holder, int viewType, BaseData data) {
        switch (viewType) {
            case BasePositionState.TYPE_LEAF:
                TextView textView = (TextView) holder.getView(R.id.item_tv);
                textView.setText((String) data.getData());
                break;
            default:
                break;
        }
    }
}
