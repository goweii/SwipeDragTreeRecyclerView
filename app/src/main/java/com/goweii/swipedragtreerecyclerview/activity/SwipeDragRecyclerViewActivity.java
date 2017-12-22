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

import com.goweii.swipedragtreerecyclerview.adapter.TestBaseSwipeDragAdapter;
import com.goweii.swipedragtreerecyclerview.util.ToastUtil;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseTypeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeData;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TypeState;

import java.util.ArrayList;

/**
 * @author cuizhen
 */
public class SwipeDragRecyclerViewActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private Button mBtnOpenCloseSwipe;
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

    private ArrayList<TypeData> mDatas = null;
    private RecyclerView mDragSwipedRecyclerView;
    private TestBaseSwipeDragAdapter mTestBaseSwipeDragAdapter;
    private int mLayoutManagerType;
    private int mOrientationType;
    private int mSpanCount;
    private int mDataCount;
    public static boolean mItemLongClickEnable;
    public static boolean mCustomViewDragEnable;
    public static boolean mCustomLongClickEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_drag_recycler_view);

        Intent intent = getIntent();
        mLayoutManagerType = intent.getIntExtra(MainActivity.LayoutManagerType.NAME, MainActivity.LayoutManagerType.LINEAR);
        mOrientationType = intent.getIntExtra(MainActivity.OrientationType.NAME, MainActivity.OrientationType.VERTICAL);
        mSpanCount = intent.getIntExtra(MainActivity.SpanCount.NAME, MainActivity.SpanCount.DEFAULT);
        mDataCount = intent.getIntExtra(MainActivity.DataCount.NAME, MainActivity.DataCount.DEFAULT);
        mItemLongClickEnable = intent.getBooleanExtra(MainActivity.Enable.ItemLongClickEnable, false);
        mCustomLongClickEnable = intent.getBooleanExtra(MainActivity.Enable.CustomLongClickEnable, false);
        mCustomViewDragEnable = intent.getBooleanExtra(MainActivity.Enable.CustomViewDragEnable, false);
        initData();
        initView();
        initSwipeDragFlag();
        initRecyclerView();
        initCheckBox();
    }

    private void initView() {
        mDragSwipedRecyclerView = findViewById(R.id.swipe_drag_recyclerView);
        mBtnOpenCloseSwipe = findViewById(R.id.btn_open_close_swipe);
        mBtnOpenCloseSwipe.setTextColor(getResources().getColor(R.color.colorAccent));
        mBtnOpenCloseSwipe.setOnClickListener(this);
        mBtnOpenCloseDrag = findViewById(R.id.btn_open_close_drag);
        mBtnOpenCloseDrag.setTextColor(getResources().getColor(R.color.colorAccent));
        mBtnOpenCloseDrag.setOnClickListener(this);
        mBtnOpenCloseSwipeBackgroundColor = findViewById(R.id.btn_open_close_swipe_background_color);
        mBtnOpenCloseSwipeBackgroundColor.setTextColor(getResources().getColor(R.color.colorAccent));
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
                        dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.GRID:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN | TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN | TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.STAGGERED:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN | TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN | TestBaseSwipeDragAdapter.TouchFlag.LEFT | TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragAdapter.TouchFlag.UP | TestBaseSwipeDragAdapter.TouchFlag.DOWN);
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
        if ((swipeFlag & TestBaseSwipeDragAdapter.TouchFlag.LEFT) != 0) {
            mCheckBoxSwipeFlagLeft.setEnabled(true);
            mCheckBoxSwipeFlagLeft.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragAdapter.TouchFlag.RIGHT) != 0) {
            mCheckBoxSwipeFlagRight.setEnabled(true);
            mCheckBoxSwipeFlagRight.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragAdapter.TouchFlag.UP) != 0) {
            mCheckBoxSwipeFlagUp.setEnabled(true);
            mCheckBoxSwipeFlagUp.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragAdapter.TouchFlag.DOWN) != 0) {
            mCheckBoxSwipeFlagDown.setEnabled(true);
            mCheckBoxSwipeFlagDown.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragAdapter.TouchFlag.LEFT) != 0) {
            mCheckBoxDragFlagLeft.setEnabled(true);
            mCheckBoxDragFlagLeft.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragAdapter.TouchFlag.RIGHT) != 0) {
            mCheckBoxDragFlagRight.setEnabled(true);
            mCheckBoxDragFlagRight.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragAdapter.TouchFlag.UP) != 0) {
            mCheckBoxDragFlagUp.setEnabled(true);
            mCheckBoxDragFlagUp.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragAdapter.TouchFlag.DOWN) != 0) {
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
        mTestBaseSwipeDragAdapter = new TestBaseSwipeDragAdapter(mOrientationType);
        mDragSwipedRecyclerView.setAdapter(mTestBaseSwipeDragAdapter);
        mTestBaseSwipeDragAdapter.init(mDatas);
        mTestBaseSwipeDragAdapter.setOnItemSwipeListener(new SwipeDragCallback.OnItemSwipeListener() {
            @Override
            public void onSwipe(int position) {
                mTestBaseSwipeDragAdapter.notifyItemSwipe(position);
            }
        });
        mTestBaseSwipeDragAdapter.setOnItemDragListener(new SwipeDragCallback.OnItemDragListener() {
            @Override
            public boolean onMove(int fromPosition, int toPosition) {
                mTestBaseSwipeDragAdapter.notifyItemDrag(fromPosition, toPosition);
                return true;
            }
        });
        mTestBaseSwipeDragAdapter.setSwipeBackgroundColorEnabled(true);
        mTestBaseSwipeDragAdapter.setLongPressDragEnabled(true);
        mTestBaseSwipeDragAdapter.setOnItemViewClickListener(new BaseTypeAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                ToastUtil.show(getApplicationContext(), position, "onItemViewClick");
            }
        });
        if (mItemLongClickEnable) {
            mTestBaseSwipeDragAdapter.setOnItemViewLongClickListener(new BaseTypeAdapter.OnItemViewLongClickListener() {
                @Override
                public boolean onItemViewLongClick(View view, int position) {
                    ToastUtil.show(getApplicationContext(), position, "onItemViewLongClick");
                    return true;
                }
            });
        }
        if (mCustomLongClickEnable) {
            mTestBaseSwipeDragAdapter.setOnCustomViewClickListener(new BaseTypeAdapter.OnCustomViewClickListener() {
                @Override
                public void onCustomViewClick(View view, int position) {
                    ToastUtil.show(getApplicationContext(), position, "onCustomViewClick");
                }
            });
            mTestBaseSwipeDragAdapter.setOnCustomViewLongClickListener(new BaseTypeAdapter.OnCustomViewLongClickListener() {
                @Override
                public boolean onCustomViewLongClick(View view, int position) {
                    ToastUtil.show(getApplicationContext(), position, "onCustomViewLongClick");
                    return true;
                }
            });
        }
    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < mDataCount; i++) {
            mDatas.add(new TypeData("测试数据" + i, TypeState.TYPE_LEAF));
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
        boolean isEnabled = !mTestBaseSwipeDragAdapter.isItemViewSwipeEnabled();
        mTestBaseSwipeDragAdapter.setItemViewSwipeEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwipe.setText(R.string.btn_close_swipe);
            mBtnOpenCloseSwipe.setTextColor(getResources().getColor(R.color.colorAccent));
            enableRadioGroup(mRadioGroupCheckSwipeFlag);
        } else {
            mBtnOpenCloseSwipe.setText(R.string.btn_open_swipe);
            mBtnOpenCloseSwipe.setTextColor(getResources().getColor(R.color.colorBlack));
            disableRadioGroup(mRadioGroupCheckSwipeFlag);
        }
    }


    private void openCloseDrag() {
        boolean isEnabled = !mTestBaseSwipeDragAdapter.isLongPressDragEnabled();
        mTestBaseSwipeDragAdapter.setLongPressDragEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseDrag.setText(R.string.btn_close_drag);
            mBtnOpenCloseDrag.setTextColor(getResources().getColor(R.color.colorAccent));
            enableRadioGroup(mRadioGroupCheckDragFlag);
        } else {
            mBtnOpenCloseDrag.setText(R.string.btn_open_drag);
            mBtnOpenCloseDrag.setTextColor(getResources().getColor(R.color.colorBlack));
            disableRadioGroup(mRadioGroupCheckDragFlag);
        }
    }

    private void openCloseSwipeBackgroundColor() {
        boolean isEnabled = !mTestBaseSwipeDragAdapter.isSwipeBackgroundColorEnabled();
        mTestBaseSwipeDragAdapter.setSwipeBackgroundColorEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_close_swipe_background_color);
            mBtnOpenCloseSwipeBackgroundColor.setTextColor(getResources().getColor(R.color.colorAccent));
            enableRadioGroup(mRadioGroupChooseSwipeColor);
        } else {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_open_swipe_background_color);
            mBtnOpenCloseSwipeBackgroundColor.setTextColor(getResources().getColor(R.color.colorBlack));
            disableRadioGroup(mRadioGroupChooseSwipeColor);
        }
    }

    private void disableRadioGroup(RadioGroup radioGroup) {
        radioGroup.setVisibility(View.GONE);
    }

    private void enableRadioGroup(RadioGroup radioGroup) {
        radioGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioBtn_swipe_color_red:
                mTestBaseSwipeDragAdapter.setSwipeBackgroundColor(0xFFFF4081);
                break;
            case R.id.radioBtn_swipe_color_green:
                mTestBaseSwipeDragAdapter.setSwipeBackgroundColor(0xff669900);
                break;
            case R.id.radioBtn_swipe_color_blue:
                mTestBaseSwipeDragAdapter.setSwipeBackgroundColor(0xFF303F9F);
                break;
            case R.id.radioBtn_swipe_color_yellow:
                mTestBaseSwipeDragAdapter.setSwipeBackgroundColor(0xffff8800);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox_drag_flag_left:
                changeDragFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_drag_flag_right:
                changeDragFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_drag_flag_up:
                changeDragFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_drag_flag_down:
                changeDragFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.DOWN);
                break;
            case R.id.checkBox_swipe_flag_left:
                changeSwipeFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_swipe_flag_right:
                changeSwipeFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_swipe_flag_up:
                changeSwipeFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_swipe_flag_down:
                changeSwipeFlag(isChecked, TestBaseSwipeDragAdapter.TouchFlag.DOWN);
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
        mTestBaseSwipeDragAdapter.setCustomDragFlag(dragFlag);
    }

    private void changeSwipeFlag(boolean change, int touchFlag) {
        if (change) {
            swipeFlag = swipeFlag | touchFlag;
        } else {
            swipeFlag = swipeFlag & (~touchFlag);
        }
        mTestBaseSwipeDragAdapter.setCustomSwipeFlag(swipeFlag);
    }
}
