package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.goweii.swipedragtreerecyclerview.adapter.SwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerview.entity.TreePositionState;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragTreeCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.DataTree;
import com.goweii.swipedragtreerecyclerviewlibrary.util.LogUtil;
import com.goweii.swipedragtreerecyclerviewlibrary.util.ToastUtil;

import java.util.ArrayList;

/**
 * @author cuizhen
 * @date 2017/12/10
 */
public class SwipeDragTreeRecyclerViewActivity extends AppCompatActivity {

    private int mLayoutManagerType;
    private int mOrientationType;
    private int mSpanCount;
    private int mDataCount;
    private ArrayList<DataTree> mDataTrees;
    private RecyclerView mSwipeDragTreeRecyclerView;
    private SwipeDragTreeAdapter mSwipeDragTreeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_drag_tree_recycler_view);

        Intent intent = getIntent();
        mLayoutManagerType = intent.getIntExtra(MainActivity.LayoutManagerType.NAME, MainActivity.LayoutManagerType.LINEAR);
        mOrientationType = intent.getIntExtra(MainActivity.OrientationType.NAME, MainActivity.OrientationType.VERTICAL);
        mSpanCount = intent.getIntExtra(MainActivity.SpanCount.NAME, MainActivity.SpanCount.DEFAULT);
        mDataCount = intent.getIntExtra(MainActivity.DataCount.NAME, MainActivity.DataCount.DEFAULT);

        LogUtil.d("----->", "start init data");
        initData();
        LogUtil.d("----->", "end init data");
        initView();
        initRecyclerView();
    }

    private void initView() {
        mSwipeDragTreeRecyclerView = findViewById(R.id.swipe_drag_tree_recyclerView);
    }

    private void initRecyclerView() {
        mSwipeDragTreeRecyclerView.setLayoutManager(getLayoutManager());
        mSwipeDragTreeAdapter = new SwipeDragTreeAdapter(this, mOrientationType);
        mSwipeDragTreeRecyclerView.setAdapter(mSwipeDragTreeAdapter);
        mSwipeDragTreeAdapter.initDataTrees(mDataTrees);
        mSwipeDragTreeAdapter.setOnItemTouchCallbackListener(new SwipeDragTreeCallback.OnItemTouchCallbackListener() {
            @Override
            public boolean onMove(int fromPosition, int toPosition) {
                mSwipeDragTreeAdapter.notifyTreeItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(int position) {
                mSwipeDragTreeAdapter.notifyTreeItemDelete(position);
            }
        }).attachToRecyclerView(mSwipeDragTreeRecyclerView);

        mSwipeDragTreeAdapter.setSwipeBackgroundColorEnabled(true);
        mSwipeDragTreeAdapter.setItemViewLongClickEnabled(false);
        mSwipeDragTreeAdapter.setLongPressDragEnabled(false);

        mSwipeDragTreeAdapter.setOnItemViewClickListener(new BaseSwipeDragTreeAdapter.OnItemViewClickListener() {
            @Override
            public void onLeafItemViewClick(View view, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onLeafItemViewClick");
            }

            @Override
            public boolean onLeafItemViewLongClick(View view, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onLeafItemViewLongClick");
                return true;
            }

            @Override
            public void onRootItemViewClick(View view, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onRootItemViewClick");
            }

            @Override
            public boolean onRootItemViewLongClick(View view, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onRootItemViewLongClick");
                return true;
            }
        });
        mSwipeDragTreeAdapter.setOnCustomViewClickListener(new BaseSwipeDragTreeAdapter.OnCustomViewClickListener() {
            @Override
            public void onCustomViewClick(View view, int viewId, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onCustomViewClick");
            }

            @Override
            public boolean onCustomViewLongClick(View view, int viewId, int position, int[] positions) {
                ToastUtil.show(getApplicationContext(), positions, "onCustomViewLongClick");
                return true;
            }
        });
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        RecyclerView.LayoutManager layoutManager = null;
        switch (mLayoutManagerType) {
            case MainActivity.LayoutManagerType.LINEAR:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.GRID:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        layoutManager = new GridLayoutManager(this, mSpanCount, GridLayoutManager.VERTICAL, false);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        layoutManager = new GridLayoutManager(this, mSpanCount, GridLayoutManager.HORIZONTAL, false);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.STAGGERED:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        layoutManager = new StaggeredGridLayoutManager(mSpanCount, StaggeredGridLayoutManager.VERTICAL);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        layoutManager = new StaggeredGridLayoutManager(mSpanCount, StaggeredGridLayoutManager.HORIZONTAL);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return layoutManager;
    }

    private void initData() {
        mDataTrees = new ArrayList<>();
        for (int i = 0; i < mDataCount; i++) {
            ArrayList<DataTree> dataTrees1 = new ArrayList<>();
            int subDataCount = 5;
            for (int j = 0; j < subDataCount; j++) {
                ArrayList<DataTree> dataTrees2 = new ArrayList<>();
                for (int k = 0; k < subDataCount; k++) {
                    ArrayList<DataTree> dataTrees3 = new ArrayList<>();
                    for (int l = 0; l < subDataCount; l++) {
                        ArrayList<DataTree> dataTrees4 = new ArrayList<>();
                        for (int m = 0; m < subDataCount; m++) {
                            dataTrees4.add(new DataTree("五级分组" + m, TreePositionState.TYPE_LEAF));
                        }
                        dataTrees3.add(new DataTree("四级分组" + l, TreePositionState.TYPE_FOUR, dataTrees4));
                    }
                    dataTrees2.add(new DataTree("三级分组" + k, TreePositionState.TYPE_THREE, dataTrees3));
                }
                dataTrees1.add(new DataTree("二级分组" + j, TreePositionState.TYPE_TEO, dataTrees2));
            }
            mDataTrees.add(new DataTree("一级分组" + i, TreePositionState.TYPE_ONE, dataTrees1));
        }
    }
}
