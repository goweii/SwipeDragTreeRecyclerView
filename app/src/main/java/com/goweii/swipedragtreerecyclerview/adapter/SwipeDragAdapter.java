package com.goweii.swipedragtreerecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goweii.swipedragtreerecyclerview.activity.MainActivity;
import com.goweii.swipedragtreerecyclerview.activity.R;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BaseData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BasePositionState;

/**
 * @author cuizhen
 * @date 2017/12/9
 */
public class SwipeDragAdapter extends BaseSwipeDragAdapter {
    private int mOrientationType;

    public SwipeDragAdapter(Context context, int orientationType) {
        super(context);
        mOrientationType = orientationType;
    }

    @Override
    public void initLayoutAndViewIds() {
        putLayoutAndViewIds(BasePositionState.TYPE_LEAF, R.layout.item_swipe_drag_recycler_view,
                new int[]{R.id.item_sdrv_tv}, null);
    }

    @Override
    public SwipeDragAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void bindData(BaseViewHolder holder, int viewType, BaseData data) {
        switch (viewType) {
            case BasePositionState.TYPE_LEAF:
                TextView textView = (TextView) holder.getView(R.id.item_sdrv_tv);
                textView.setText((String) data.getData());
                break;
            default:
                break;
        }
    }
}
