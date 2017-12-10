package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.goweii.swipedragtreerecyclerview.adapter.SwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BaseData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.BasePositionState;
import com.goweii.swipedragtreerecyclerviewlibrary.util.LogUtil;

import java.util.ArrayList;

/**
 * @author cuizhen
 */
public class SwipeDragRecyclerViewActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private RecyclerView mDragSwipedRecyclerView;
    private SwipeDragAdapter mSwipeDragAdapter;
    private ArrayList<BaseData> mBaseDatas = null;
    private Button mBtnOpenCloseSwiped;
    private Button mBtnOpenCloseDrag;
    private Button mBtnOpenCloseSwipeBackgroundColor;
    private RadioGroup mRadioGroupCheckDragFlag;
    private RadioGroup mRadioGroupCheckSwipeFlag;
    private RadioGroup mRadioGroupChooseSwipeColor;
    private CheckBox mCheckBoxDragFlagLeft;
    private CheckBox mCheckBoxDragFlagRight;
    private CheckBox mCheckBoxDragFlagUp;
    private CheckBox mCheckBoxDragFlagDown;
    private CheckBox mCheckBoxSwipeFlagLeft;
    private CheckBox mCheckBoxSwipeFlagRight;
    private CheckBox mCheckBoxSwipeFlagUp;
    private CheckBox mCheckBoxSwipeFlagDown;

    private int dragFlag = 0;
    private int swipeFlag = 0;
    private int mLayoutManagerType;
    private int mOrientationType;
    private int mSpanCount;
    private int mDataCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_drag_recycler_view);

        Intent intent = getIntent();
        mLayoutManagerType = intent.getIntExtra(MainActivity.LayoutManagerType.NAME, MainActivity.LayoutManagerType.LINEAR);
        mOrientationType = intent.getIntExtra(MainActivity.OrientationType.NAME, MainActivity.OrientationType.VERTICAL);
        mSpanCount = intent.getIntExtra(MainActivity.SpanCount.NAME, MainActivity.SpanCount.DEFAULT);
        mDataCount = intent.getIntExtra(MainActivity.DataCount.NAME, MainActivity.DataCount.DEFAULT);

        initView();
        initSwipeDragFlag();
        initRecyclerView();
        initCheckBox();
    }

    private void initView() {
        mDragSwipedRecyclerView = findViewById(R.id.swipe_drag_recyclerView);
        mBtnOpenCloseSwiped = findViewById(R.id.btn_open_close_swipe);
        mBtnOpenCloseSwiped.setOnClickListener(this);
        mBtnOpenCloseDrag = findViewById(R.id.btn_open_close_drag);
        mBtnOpenCloseDrag.setOnClickListener(this);
        mBtnOpenCloseSwipeBackgroundColor = findViewById(R.id.btn_open_close_swipe_background_color);
        mBtnOpenCloseSwipeBackgroundColor.setOnClickListener(this);

        mRadioGroupCheckDragFlag = findViewById(R.id.radioGroup_check_dragFlag);
        mCheckBoxDragFlagLeft = findViewById(R.id.checkBox_drag_flag_left);
        mCheckBoxDragFlagLeft.setOnCheckedChangeListener(this);
        mCheckBoxDragFlagRight = findViewById(R.id.checkBox_drag_flag_right);
        mCheckBoxDragFlagRight.setOnCheckedChangeListener(this);
        mCheckBoxDragFlagUp = findViewById(R.id.checkBox_drag_flag_up);
        mCheckBoxDragFlagUp.setOnCheckedChangeListener(this);
        mCheckBoxDragFlagDown = findViewById(R.id.checkBox_drag_flag_down);
        mCheckBoxDragFlagDown.setOnCheckedChangeListener(this);

        mRadioGroupCheckSwipeFlag = findViewById(R.id.radioGroup_check_swipeFlag);
        mCheckBoxSwipeFlagLeft = findViewById(R.id.checkBox_swipe_flag_left);
        mCheckBoxSwipeFlagLeft.setOnCheckedChangeListener(this);
        mCheckBoxSwipeFlagRight = findViewById(R.id.checkBox_swipe_flag_right);
        mCheckBoxSwipeFlagRight.setOnCheckedChangeListener(this);
        mCheckBoxSwipeFlagUp = findViewById(R.id.checkBox_swipe_flag_up);
        mCheckBoxSwipeFlagUp.setOnCheckedChangeListener(this);
        mCheckBoxSwipeFlagDown = findViewById(R.id.checkBox_swipe_flag_down);
        mCheckBoxSwipeFlagDown.setOnCheckedChangeListener(this);

        mRadioGroupChooseSwipeColor = findViewById(R.id.radioGroup_choose_swipe_color);
        mRadioGroupChooseSwipeColor.setOnCheckedChangeListener(this);
    }

    private void initSwipeDragFlag() {
        switch (mLayoutManagerType) {
            case MainActivity.LayoutManagerType.LINEAR:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        swipeFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        dragFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        swipeFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.GRID:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN | SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN | SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.STAGGERED:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN | SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN | SwipeDragAdapter.TouchFlag.LEFT | SwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (SwipeDragAdapter.TouchFlag.UP | SwipeDragAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void initCheckBox() {
        for (int i = 0; i < mRadioGroupCheckSwipeFlag.getChildCount(); i++) {
            mRadioGroupCheckSwipeFlag.getChildAt(i).setEnabled(false);
        }
        for (int i = 0; i < mRadioGroupCheckDragFlag.getChildCount(); i++) {
            mRadioGroupCheckDragFlag.getChildAt(i).setEnabled(false);
        }
        if ((swipeFlag & SwipeDragAdapter.TouchFlag.LEFT) != 0){
            mCheckBoxSwipeFlagLeft.setEnabled(true);
            mCheckBoxSwipeFlagLeft.setChecked(true);
        }
        if ((swipeFlag & SwipeDragAdapter.TouchFlag.RIGHT) != 0){
            mCheckBoxSwipeFlagRight.setEnabled(true);
            mCheckBoxSwipeFlagRight.setChecked(true);
        }
        if ((swipeFlag & SwipeDragAdapter.TouchFlag.UP) != 0){
            mCheckBoxSwipeFlagUp.setEnabled(true);
            mCheckBoxSwipeFlagUp.setChecked(true);
        }
        if ((swipeFlag & SwipeDragAdapter.TouchFlag.DOWN) != 0){
            mCheckBoxSwipeFlagDown.setEnabled(true);
            mCheckBoxSwipeFlagDown.setChecked(true);
        }
        if ((dragFlag & SwipeDragAdapter.TouchFlag.LEFT) != 0){
            mCheckBoxDragFlagLeft.setEnabled(true);
            mCheckBoxDragFlagLeft.setChecked(true);
        }
        if ((dragFlag & SwipeDragAdapter.TouchFlag.RIGHT) != 0){
            mCheckBoxDragFlagRight.setEnabled(true);
            mCheckBoxDragFlagRight.setChecked(true);
        }
        if ((dragFlag & SwipeDragAdapter.TouchFlag.UP) != 0){
            mCheckBoxDragFlagUp.setEnabled(true);
            mCheckBoxDragFlagUp.setChecked(true);
        }
        if ((dragFlag & SwipeDragAdapter.TouchFlag.DOWN) != 0){
            mCheckBoxDragFlagDown.setEnabled(true);
            mCheckBoxDragFlagDown.setChecked(true);
        }
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

    private void initRecyclerView() {
        mDragSwipedRecyclerView.setLayoutManager(getLayoutManager());
        mSwipeDragAdapter = new SwipeDragAdapter(this, mOrientationType);
        mDragSwipedRecyclerView.setAdapter(mSwipeDragAdapter);
        initData();
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
        mSwipeDragAdapter.setSwipeBackgroundColorEnabled(true);
    }

    private void initData() {
        mBaseDatas = new ArrayList<>();
        for (int i = 0; i < mDataCount; i++) {
            mBaseDatas.add(new BaseData("测试数据" + i, BasePositionState.TYPE_LEAF));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_close_swipe:
                openCloseSwipe();
                break;
            case R.id.btn_open_close_drag:
                openCloseDrag();
                break;
            case R.id.btn_open_close_swipe_background_color:
                openCloseSwipeBackgroundColor();
                break;
            default:
                break;
        }
    }

    private void openCloseSwipe() {
        boolean isEnabled = !mSwipeDragAdapter.isItemViewSwipeEnabled();
        mSwipeDragAdapter.setItemViewSwipeEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwiped.setText(R.string.btn_close_swipe);
            enableRadioGroup(mRadioGroupCheckSwipeFlag);
        } else {
            mBtnOpenCloseSwiped.setText(R.string.btn_open_swipe);
            disableRadioGroup(mRadioGroupCheckSwipeFlag);
        }
    }


    private void openCloseDrag() {
        boolean isEnabled = !mSwipeDragAdapter.isLongPressDragEnabled();
        mSwipeDragAdapter.setLongPressDragEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseDrag.setText(R.string.btn_close_drag);
            enableRadioGroup(mRadioGroupCheckDragFlag);
        } else {
            mBtnOpenCloseDrag.setText(R.string.btn_open_drag);
            disableRadioGroup(mRadioGroupCheckDragFlag);
        }
    }

    private void openCloseSwipeBackgroundColor() {
        boolean isEnabled = !mSwipeDragAdapter.isSwipedBackgroundColorEnabled();
        mSwipeDragAdapter.setSwipeBackgroundColorEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_close_swipe_background_color);
            enableRadioGroup(mRadioGroupChooseSwipeColor);
        } else {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_open_swipe_background_color);
            disableRadioGroup(mRadioGroupChooseSwipeColor);
        }
    }

    private void disableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void enableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioBtn_swipe_color_red:
                mSwipeDragAdapter.setSwipedBackgroundColor(0xFFFF4081);
                break;
            case R.id.radioBtn_swipe_color_green:
                mSwipeDragAdapter.setSwipedBackgroundColor(0xff669900);
                break;
            case R.id.radioBtn_swipe_color_blue:
                mSwipeDragAdapter.setSwipedBackgroundColor(0xFF303F9F);
                break;
            case R.id.radioBtn_swipe_color_yellow:
                mSwipeDragAdapter.setSwipedBackgroundColor(0xffff8800);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox_drag_flag_left:
                changeDragFlag(isChecked, SwipeDragAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_drag_flag_right:
                changeDragFlag(isChecked, SwipeDragAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_drag_flag_up:
                changeDragFlag(isChecked, SwipeDragAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_drag_flag_down:
                changeDragFlag(isChecked, SwipeDragAdapter.TouchFlag.DOWN);
                break;
            case R.id.checkBox_swipe_flag_left:
                changeSwipeFlag(isChecked, SwipeDragAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_swipe_flag_right:
                changeSwipeFlag(isChecked, SwipeDragAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_swipe_flag_up:
                changeSwipeFlag(isChecked, SwipeDragAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_swipe_flag_down:
                changeSwipeFlag(isChecked, SwipeDragAdapter.TouchFlag.DOWN);
                break;
            default:
                break;
        }
    }


    private void changeDragFlag(boolean change, int touchFlag) {
        if (change) {
            dragFlag = dragFlag | touchFlag;
        } else {
            dragFlag = dragFlag & (~touchFlag);
        }
        mSwipeDragAdapter.setCustomDragFlag(dragFlag);
        LogUtil.d("---dragFlag, swipeFlag-->", dragFlag + ", " + swipeFlag);
    }

    private void changeSwipeFlag(boolean change, int touchFlag) {
        if (change) {
            swipeFlag = swipeFlag | touchFlag;
        } else {
            swipeFlag = swipeFlag & (~touchFlag);
        }
        mSwipeDragAdapter.setCustomSwipeFlag(swipeFlag);
        LogUtil.d("---dragFlag, swipeFlag-->", dragFlag + ", " + swipeFlag);
    }
}
