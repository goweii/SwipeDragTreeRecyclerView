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

import com.goweii.swipedragtreerecyclerview.adapter.TestBaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerview.entity.TestTreeState;
import com.goweii.swipedragtreerecyclerview.util.ToastUtil;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseSwipeDragTreeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.adapter.BaseTypeAdapter;
import com.goweii.swipedragtreerecyclerviewlibrary.callback.SwipeDragTreeCallback;
import com.goweii.swipedragtreerecyclerviewlibrary.entity.TreeData;

import java.util.ArrayList;

/**
 * @author cuizhen
 * @date 2017/12/10
 */
public class SwipeDragTreeRecyclerViewActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private Button mBtnOpenCloseSwipe;
    private Button mBtnOpenCloseDrag;
    private Button mBtnOpenCloseSwipeBackgroundColor;
    private Button mBtnOpenCloseMemoryExpand;
    private Button mBtnExpandUnexpandAll;
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
    private int[] mSubCount;
    private ArrayList<TreeData> mDatas = null;
    private RecyclerView mSwipeDragTreeRecyclerView;
    private TestBaseSwipeDragTreeAdapter mTestBaseSwipeDragTreeAdapter;
    public static boolean mItemLongClickEnable;
    public static  boolean mCustomViewDragEnable;
    public static  boolean mCustomLongClickEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_drag_tree_recycler_view);

        Intent intent = getIntent();
        mLayoutManagerType = intent.getIntExtra(MainActivity.LayoutManagerType.NAME, MainActivity.LayoutManagerType.LINEAR);
        mOrientationType = intent.getIntExtra(MainActivity.OrientationType.NAME, MainActivity.OrientationType.VERTICAL);
        mSpanCount = intent.getIntExtra(MainActivity.SpanCount.NAME, MainActivity.SpanCount.DEFAULT);
        mDataCount = intent.getIntExtra(MainActivity.DataCount.NAME, MainActivity.DataCount.DEFAULT);
        mSubCount = intent.getIntArrayExtra(MainActivity.SubCount.NAME);
        mItemLongClickEnable = intent.getBooleanExtra(MainActivity.Enable.ItemLongClickEnable, false);
        mCustomLongClickEnable = intent.getBooleanExtra(MainActivity.Enable.CustomLongClickEnable, false);
        mCustomViewDragEnable = intent.getBooleanExtra(MainActivity.Enable.CustomViewDragEnable, false);

        initData();
        initView();
        initSwipeDragFlag();
        initRecyclerView();
        initExpandAllBtnText(mTestBaseSwipeDragTreeAdapter.getItemCount());
        initCheckBox();
    }

    private void initView() {
        mSwipeDragTreeRecyclerView = findViewById(R.id.swipe_drag_tree_recyclerView);

        mBtnOpenCloseSwipe = findViewById(R.id.btn_open_close_swipe);
        mBtnOpenCloseSwipe.setOnClickListener(this);
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
        mBtnOpenCloseMemoryExpand = findViewById(R.id.btn_open_close_memory_expand);
        mBtnOpenCloseMemoryExpand.setOnClickListener(this);
        mBtnExpandUnexpandAll = findViewById(R.id.btn_expand_unexpand_all);
        mBtnExpandUnexpandAll.setOnClickListener(this);
    }

    private void initSwipeDragFlag() {
        switch (mLayoutManagerType) {
            case MainActivity.LayoutManagerType.LINEAR:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.GRID:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN | TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN | TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LayoutManagerType.STAGGERED:
                switch (mOrientationType) {
                    case MainActivity.OrientationType.VERTICAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                        } else {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN | TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        break;
                    case MainActivity.OrientationType.HORIZONTAL:
                        if (mSpanCount == 1) {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        } else {
                            dragFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN | TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT | TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                        }
                        swipeFlag = (TestBaseSwipeDragTreeAdapter.TouchFlag.UP | TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
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
        if ((swipeFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT) != 0) {
            mCheckBoxSwipeFlagLeft.setEnabled(true);
            mCheckBoxSwipeFlagLeft.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT) != 0) {
            mCheckBoxSwipeFlagRight.setEnabled(true);
            mCheckBoxSwipeFlagRight.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.UP) != 0) {
            mCheckBoxSwipeFlagUp.setEnabled(true);
            mCheckBoxSwipeFlagUp.setChecked(true);
        }
        if ((swipeFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN) != 0) {
            mCheckBoxSwipeFlagDown.setEnabled(true);
            mCheckBoxSwipeFlagDown.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT) != 0) {
            mCheckBoxDragFlagLeft.setEnabled(true);
            mCheckBoxDragFlagLeft.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT) != 0) {
            mCheckBoxDragFlagRight.setEnabled(true);
            mCheckBoxDragFlagRight.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.UP) != 0) {
            mCheckBoxDragFlagUp.setEnabled(true);
            mCheckBoxDragFlagUp.setChecked(true);
        }
        if ((dragFlag & TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN) != 0) {
            mCheckBoxDragFlagDown.setEnabled(true);
            mCheckBoxDragFlagDown.setChecked(true);
        }
    }

    private void initRecyclerView() {
        mSwipeDragTreeRecyclerView.setLayoutManager(getLayoutManager());
        mTestBaseSwipeDragTreeAdapter = new TestBaseSwipeDragTreeAdapter(mOrientationType);
        mSwipeDragTreeRecyclerView.setAdapter(mTestBaseSwipeDragTreeAdapter);
        mTestBaseSwipeDragTreeAdapter.init(mDatas);
        mTestBaseSwipeDragTreeAdapter.setOnItemSwipeListener(new SwipeDragTreeCallback.OnItemSwipeListener() {
            @Override
            public void onSwipe(int position) {
                mTestBaseSwipeDragTreeAdapter.notifyItemSwipe(position);
            }
        });
        mTestBaseSwipeDragTreeAdapter.setOnItemDragListener(new SwipeDragTreeCallback.OnItemDragListener() {
            @Override
            public boolean onMove(int fromPosition, int toPosition) {
                mTestBaseSwipeDragTreeAdapter.notifyItemDrag(fromPosition, toPosition);
                return true;
            }
        });
        mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColorEnabled(true);
        mTestBaseSwipeDragTreeAdapter.setLongPressDragEnabled(true);
        mTestBaseSwipeDragTreeAdapter.setMemoryExpandState(true);

        mTestBaseSwipeDragTreeAdapter.setOnItemViewClickListener(new BaseSwipeDragAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                int[] positions = mTestBaseSwipeDragTreeAdapter.getPositions(position);
                ToastUtil.show(getApplicationContext(), positions, "onItemViewClick");
            }
        });
        if (mItemLongClickEnable) {
            mTestBaseSwipeDragTreeAdapter.setOnItemViewLongClickListener(new BaseTypeAdapter.OnItemViewLongClickListener() {
                @Override
                public boolean onItemViewLongClick(View view, int position) {
                    int[] positions = mTestBaseSwipeDragTreeAdapter.getPositions(position);
                    ToastUtil.show(getApplicationContext(), positions, "onItemViewLongClick");
                    return true;
                }
            });
        }
        if (mCustomLongClickEnable) {
            mTestBaseSwipeDragTreeAdapter.setOnCustomViewClickListener(new BaseSwipeDragAdapter.OnCustomViewClickListener() {
                @Override
                public void onCustomViewClick(View view, int position) {
                    int[] positions = mTestBaseSwipeDragTreeAdapter.getPositions(position);
                    ToastUtil.show(getApplicationContext(), positions, "onCustomViewClick");
                }
            });
            mTestBaseSwipeDragTreeAdapter.setOnCustomViewLongClickListener(new BaseTypeAdapter.OnCustomViewLongClickListener() {
                @Override
                public boolean onCustomViewLongClick(View view, int position) {
                    int[] positions = mTestBaseSwipeDragTreeAdapter.getPositions(position);
                    ToastUtil.show(getApplicationContext(), positions, "onCustomViewLongClick");
                    return true;
                }
            });
        }
        mTestBaseSwipeDragTreeAdapter.setOnExpandChangeListener(new BaseSwipeDragTreeAdapter.OnExpandChangeListener() {
            @Override
            public void onExpand(int itemCount) {
                initExpandAllBtnText(itemCount);
            }

            @Override
            public void onUnExpand(int itemCount) {
                initExpandAllBtnText(itemCount);
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
        mDatas = new ArrayList<>();
        for (int i = 0; i < mDataCount; i++) {
            ArrayList<TreeData> dataTrees1 = null;
            for (int j = 0; j < mSubCount[0]; j++) {
                ArrayList<TreeData> dataTrees2 = null;
                for (int k = 0; k < mSubCount[1]; k++) {
                    ArrayList<TreeData> dataTrees3 = null;
                    for (int l = 0; l < mSubCount[2]; l++) {
                        ArrayList<TreeData> dataTrees4 = null;
                        for (int m = 0; m < mSubCount[3]; m++) {
                            if (dataTrees4 == null) {
                                dataTrees4 = new ArrayList<>();
                            }
                            dataTrees4.add(new TreeData("五级分组" + m, TestTreeState.TYPE_LEAF));
                        }
                        if (dataTrees3 == null) {
                            dataTrees3 = new ArrayList<>();
                        }
                        dataTrees3.add(new TreeData("四级分组" + l, TestTreeState.TYPE_FOUR, dataTrees4));
                    }
                    if (dataTrees2 == null) {
                        dataTrees2 = new ArrayList<>();
                    }
                    dataTrees2.add(new TreeData("三级分组" + k, TestTreeState.TYPE_THREE, dataTrees3));
                }
                if (dataTrees1 == null) {
                    dataTrees1 = new ArrayList<>();
                }
                dataTrees1.add(new TreeData("二级分组" + j, TestTreeState.TYPE_TEO, dataTrees2));
            }
            mDatas.add(new TreeData("一级分组" + i, TestTreeState.TYPE_ONE, dataTrees1));
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
            case R.id.btn_open_close_memory_expand:
                openCloseMemoryExpand();
                break;
            case R.id.btn_expand_unexpand_all:
                expandUnexpandAll();
                break;
            default:
                break;
        }
    }

    private void openCloseSwipe() {
        boolean isEnabled = !mTestBaseSwipeDragTreeAdapter.isItemViewSwipeEnabled();
        mTestBaseSwipeDragTreeAdapter.setItemViewSwipeEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwipe.setText(R.string.btn_close_swipe);
            enableRadioGroup(mRadioGroupCheckSwipeFlag);
        } else {
            mBtnOpenCloseSwipe.setText(R.string.btn_open_swipe);
            disableRadioGroup(mRadioGroupCheckSwipeFlag);
        }
    }


    private void openCloseDrag() {
        boolean isEnabled = !mTestBaseSwipeDragTreeAdapter.isLongPressDragEnabled();
        mTestBaseSwipeDragTreeAdapter.setLongPressDragEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseDrag.setText(R.string.btn_close_drag);
            enableRadioGroup(mRadioGroupCheckDragFlag);
        } else {
            mBtnOpenCloseDrag.setText(R.string.btn_open_drag);
            disableRadioGroup(mRadioGroupCheckDragFlag);
        }
    }

    private void openCloseSwipeBackgroundColor() {
        boolean isEnabled = !mTestBaseSwipeDragTreeAdapter.isSwipeBackgroundColorEnabled();
        mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColorEnabled(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_close_swipe_background_color);
            enableRadioGroup(mRadioGroupChooseSwipeColor);
        } else {
            mBtnOpenCloseSwipeBackgroundColor.setText(R.string.btn_open_swipe_background_color);
            disableRadioGroup(mRadioGroupChooseSwipeColor);
        }
    }

    private void openCloseMemoryExpand() {
        boolean isEnabled = !mTestBaseSwipeDragTreeAdapter.isMemoryExpandState();
        mTestBaseSwipeDragTreeAdapter.setMemoryExpandState(isEnabled);
        if (isEnabled) {
            mBtnOpenCloseMemoryExpand.setText(R.string.btn_close_memory_expand);
        } else {
            mBtnOpenCloseMemoryExpand.setText(R.string.btn_open_memory_expand);
        }
    }

    private void expandUnexpandAll() {
        boolean isAllExpand = mTestBaseSwipeDragTreeAdapter.isAllExpand();
        if (isAllExpand) {
            mTestBaseSwipeDragTreeAdapter.unExpandAll();
            String all = getResources().getString(R.string.btn_expand_all);
            int count = mTestBaseSwipeDragTreeAdapter.getItemCount();
            mBtnExpandUnexpandAll.setText((all + "(" + count + ")"));
        } else {
            mTestBaseSwipeDragTreeAdapter.expandAll();
            String all = getResources().getString(R.string.btn_unexpand_all);
            int count = mTestBaseSwipeDragTreeAdapter.getItemCount();
            mBtnExpandUnexpandAll.setText((all + "(" + count + ")"));
        }
    }

    private void initExpandAllBtnText(int itemCount) {
        boolean isAllExpand = mTestBaseSwipeDragTreeAdapter.isAllExpand();
        if (isAllExpand) {
            String str = getResources().getString(R.string.btn_unexpand_all);
            mBtnExpandUnexpandAll.setText((str + "(" + itemCount + ")"));
        } else {
            String str = getResources().getString(R.string.btn_expand_all);
            mBtnExpandUnexpandAll.setText((str + "(" + itemCount + ")"));
        }
    }


    private void disableRadioGroup(RadioGroup radioGroup) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            radioGroup.getChildAt(i).setEnabled(false);
//        }
        radioGroup.setVisibility(View.GONE);
    }

    private void enableRadioGroup(RadioGroup radioGroup) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            radioGroup.getChildAt(i).setEnabled(true);
//        }
        radioGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioBtn_swipe_color_red:
                mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColor(0xFFFF4081);
                break;
            case R.id.radioBtn_swipe_color_green:
                mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColor(0xff669900);
                break;
            case R.id.radioBtn_swipe_color_blue:
                mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColor(0xFF303F9F);
                break;
            case R.id.radioBtn_swipe_color_yellow:
                mTestBaseSwipeDragTreeAdapter.setSwipeBackgroundColor(0xffff8800);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox_drag_flag_left:
                changeDragFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_drag_flag_right:
                changeDragFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_drag_flag_up:
                changeDragFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_drag_flag_down:
                changeDragFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
                break;
            case R.id.checkBox_swipe_flag_left:
                changeSwipeFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.LEFT);
                break;
            case R.id.checkBox_swipe_flag_right:
                changeSwipeFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.RIGHT);
                break;
            case R.id.checkBox_swipe_flag_up:
                changeSwipeFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.UP);
                break;
            case R.id.checkBox_swipe_flag_down:
                changeSwipeFlag(isChecked, TestBaseSwipeDragTreeAdapter.TouchFlag.DOWN);
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
        mTestBaseSwipeDragTreeAdapter.setCustomDragFlag(dragFlag);
    }

    private void changeSwipeFlag(boolean change, int touchFlag) {
        if (change) {
            swipeFlag = swipeFlag | touchFlag;
        } else {
            swipeFlag = swipeFlag & (~touchFlag);
        }
        mTestBaseSwipeDragTreeAdapter.setCustomSwipeFlag(swipeFlag);
    }
}
