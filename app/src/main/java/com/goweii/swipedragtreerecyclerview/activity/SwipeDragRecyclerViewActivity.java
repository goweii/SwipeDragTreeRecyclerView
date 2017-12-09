package com.goweii.swipedragtreerecyclerview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.goweii.swipedragtreerecyclerview.adapter.SwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BaseData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BasePositionState;

import java.util.ArrayList;

/**
 * @author cuizhen
 */
public class SwipeDragRecyclerViewActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mDragSwipedRecyclerView;
    private SwipeDragAdapter mSwipeDragAdapter;
    private ArrayList<BaseData> mBaseDatas = null;
    private Button mBtnOpenCloseSwiped;
    private Button mBtnOpenCloseDrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_swiped_recycler_view);
        initView();
        initRecyclerView();

    }

    private void initView() {
        mDragSwipedRecyclerView = findViewById(R.id.swipe_drag_recyclerView);
        mBtnOpenCloseSwiped = findViewById(R.id.btn_open_close_swiped);
        mBtnOpenCloseSwiped.setOnClickListener(this);
        mBtnOpenCloseDrag = findViewById(R.id.btn_open_close_drag);
        mBtnOpenCloseDrag.setOnClickListener(this);
    }

    private void initRecyclerView() {
        mDragSwipedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSwipeDragAdapter = new SwipeDragAdapter(this);
        mDragSwipedRecyclerView.setAdapter(mSwipeDragAdapter);
        initData(20);
        mSwipeDragAdapter.initDatas(mBaseDatas);
        mSwipeDragAdapter.setOnItemTouchCallbackListener(new SwipeDragCallback.OnItemTouchCallbackListener() {
            @Override
            public boolean onMove(int fromPosition, int toPosition) {
                mSwipeDragAdapter.notifyItemDrag(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(int position) {
                mSwipeDragAdapter.notifyItemSwiped(position);
            }
        }).attachToRecyclerView(mDragSwipedRecyclerView);
    }

    private void initData(int count) {
        mBaseDatas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mBaseDatas.add(new BaseData("测试数据" + i, BasePositionState.TYPE_LEAF));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_close_swiped:
                openCloseSwiped();
                break;
            case R.id.btn_open_close_drag:
                openCloseDrag();
                break;
            default:
                break;
        }
    }

    private void openCloseSwiped() {
        boolean isEnabled = !mSwipeDragAdapter.isItemViewSwipeEnabled();
        mSwipeDragAdapter.setItemViewSwipeEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwiped.setText(R.string.btn_close_swiped);
        } else {
            mBtnOpenCloseSwiped.setText(R.string.btn_open_swiped);
        }
    }
    private void openCloseDrag() {
        boolean isEnabled = !mSwipeDragAdapter.isLongPressDragEnabled();
        mSwipeDragAdapter.setLongPressDragEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseDrag.setText(R.string.btn_close_drag);
        } else {
            mBtnOpenCloseDrag.setText(R.string.btn_open_drag);
        }
    }
}
