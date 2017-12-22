package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.goweii.swipedragtreerecyclerviewlibrary.util.LogUtil;

/**
 * @author cuizhen
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    private int mRecyclerViewType = RecyclerViewType.SWIP_DRAG;
    private int mLayoutManagerType = LayoutManagerType.LINEAR;
    private int mOrientationType = OrientationType.VERTICAL;
    private int mSpanCount = SpanCount.DEFAULT;
    private int mDataCount = DataCount.DEFAULT;
    private int mLevelCount = LevelCount.DEFAULT;
    private int[] mSubCount = new int[]{SubCount.DEFAULT, SubCount.DEFAULT, SubCount.DEFAULT, SubCount.DEFAULT};
    private NumberPicker mNumberPickerSpanCount;
    private LinearLayout mLlTreeSet;
    private NumberPicker mNumberPickerLevelCount;
    private NumberPicker mNumberPickerSubCount1;
    private NumberPicker mNumberPickerSubCount2;
    private NumberPicker mNumberPickerSubCount3;
    private NumberPicker mNumberPickerSubCount4;
    private Button mBtnOpenCloseItemLongClick;
    private Button mBtnOpenCloseCustomLongClick;
    private Button mBtnOpenCloseCustomViewDrag;
    private boolean mItemLongClickEnable = false;
    private boolean mCustomLongClickEnable = false;
    private boolean mCustomViewDragEnable = false;


    static class Enable {
        static final String ItemLongClickEnable = "ItemLongClickEnable";
        static final String CustomLongClickEnable = "CustomLongClickEnable";
        static final String CustomViewDragEnable = "CustomViewDragEnable";
    }

    static class RecyclerViewType {
        static final String NAME = "RecyclerViewType";
        static final int SWIP_DRAG = 0;
        static final int SWIP_DARG_TREE = 1;
    }

    static class LayoutManagerType {
        static final String NAME = "LayoutManagerType";
        static final int LINEAR = 0;
        static final int GRID = 1;
        static final int STAGGERED = 2;
    }

    public static class OrientationType {
        static final String NAME = "OrientationType";
        public static final int VERTICAL = 0;
        public static final int HORIZONTAL = 1;
    }

    static class SpanCount {
        static final String NAME = "SpanCount";
        static final int DEFAULT = 3;
    }

    static class DataCount {
        static final String NAME = "DataCount";
        static final int DEFAULT = 5;
    }

    static class LevelCount {
        static final String NAME = "LevelCount";
        static final int DEFAULT = 3;
    }

    static class SubCount {
        static final String NAME = "SubCount";
        static final int DEFAULT = 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        RadioGroup radioGroupLayoutManager = findViewById(R.id.radioGroup_layoutManager);
        radioGroupLayoutManager.setOnCheckedChangeListener(this);
        RadioGroup radioGroupOrientation = findViewById(R.id.radioGroup_orientation);
        radioGroupOrientation.setOnCheckedChangeListener(this);
        RadioGroup radioGroupRecyclerView = findViewById(R.id.radioGroup_recyclerView);
        radioGroupRecyclerView.setOnCheckedChangeListener(this);

        mNumberPickerSpanCount = findViewById(R.id.numberPicker_spanCount);
        mNumberPickerSpanCount.setOnValueChangedListener(this);
        mNumberPickerSpanCount.setEnabled(false);
        mNumberPickerSpanCount.setMinValue(1);
        mNumberPickerSpanCount.setMaxValue(20);
        mNumberPickerSpanCount.setValue(SpanCount.DEFAULT);
        mNumberPickerSpanCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        NumberPicker numberPickerDataCount = findViewById(R.id.numberPicker_dataCount);
        numberPickerDataCount.setOnValueChangedListener(this);
        numberPickerDataCount.setMinValue(0);
        numberPickerDataCount.setMaxValue(100);
        numberPickerDataCount.setValue(DataCount.DEFAULT);
        numberPickerDataCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerLevelCount = findViewById(R.id.numberPicker_levelCount);
        mNumberPickerLevelCount.setOnValueChangedListener(this);
        mNumberPickerLevelCount.setMinValue(1);
        mNumberPickerLevelCount.setMaxValue(5);
        mNumberPickerLevelCount.setValue(LevelCount.DEFAULT);
        mNumberPickerLevelCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mLlTreeSet = findViewById(R.id.ll_tree_set);
        mLlTreeSet.setVisibility(View.INVISIBLE);
        mNumberPickerSubCount1 = findViewById(R.id.numberPicker_subCount1);
        mNumberPickerSubCount1.setOnValueChangedListener(this);
        mNumberPickerSubCount1.setMinValue(1);
        mNumberPickerSubCount1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerSubCount2 = findViewById(R.id.numberPicker_subCount2);
        mNumberPickerSubCount2.setOnValueChangedListener(this);
        mNumberPickerSubCount2.setMinValue(1);
        mNumberPickerSubCount2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerSubCount3 = findViewById(R.id.numberPicker_subCount3);
        mNumberPickerSubCount3.setOnValueChangedListener(this);
        mNumberPickerSubCount3.setMinValue(1);
        mNumberPickerSubCount3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerSubCount4 = findViewById(R.id.numberPicker_subCount4);
        mNumberPickerSubCount4.setOnValueChangedListener(this);
        mNumberPickerSubCount4.setMinValue(1);
        mNumberPickerSubCount4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mBtnOpenCloseItemLongClick = findViewById(R.id.btn_open_close_item_long_click);
        mBtnOpenCloseItemLongClick.setOnClickListener(this);
        mBtnOpenCloseCustomViewDrag = findViewById(R.id.btn_open_close_custom_view_drag);
        mBtnOpenCloseCustomViewDrag.setOnClickListener(this);
        mBtnOpenCloseCustomLongClick = findViewById(R.id.btn_open_close_custom_long_click);
        mBtnOpenCloseCustomLongClick.setOnClickListener(this);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numberPicker_dataCount:
                mDataCount = newVal;
                setSubNumberPickerCount();
                break;
            case R.id.numberPicker_spanCount:
                mSpanCount = newVal;
                break;
            case R.id.numberPicker_levelCount:
                mLevelCount = newVal;
                setSubNumberPickerEnabled();
                setSubNumberPickerCount();
                break;
            case R.id.numberPicker_subCount1:
                mSubCount[0] = newVal;
                break;
            case R.id.numberPicker_subCount2:
                mSubCount[1] = newVal;
                break;
            case R.id.numberPicker_subCount3:
                mSubCount[2] = newVal;
                break;
            case R.id.numberPicker_subCount4:
                mSubCount[3] = newVal;
                break;
            default:
                break;
        }
    }

    private void setSubNumberPickerCount() {
        int max = getSubCount(mDataCount, mLevelCount);
        int value = (int) Math.floor((max + 1) / 2);
        if (mLevelCount >= 2) {
            mNumberPickerSubCount1.setMaxValue(max);
            mNumberPickerSubCount1.setValue(value);
            mSubCount[0] = value;
        }
        if (mLevelCount >= 3) {
            mNumberPickerSubCount2.setMaxValue(max);
            mNumberPickerSubCount2.setValue(value);
            mSubCount[1] = value;
        }
        if (mLevelCount >= 4) {
            mNumberPickerSubCount3.setMaxValue(max);
            mNumberPickerSubCount3.setValue(value);
            mSubCount[2] = value;
        }
        if (mLevelCount >= 5) {
            mNumberPickerSubCount4.setMaxValue(max);
            mNumberPickerSubCount4.setValue(value);
            mSubCount[3] = value;
        }
    }

    private void setSubNumberPickerEnabled() {
        mNumberPickerSubCount1.setEnabled(false);
        mNumberPickerSubCount2.setEnabled(false);
        mNumberPickerSubCount3.setEnabled(false);
        mNumberPickerSubCount4.setEnabled(false);
        if (mLevelCount >= 2) mNumberPickerSubCount1.setEnabled(true);
        if (mLevelCount >= 3) mNumberPickerSubCount2.setEnabled(true);
        if (mLevelCount >= 4) mNumberPickerSubCount3.setEnabled(true);
        if (mLevelCount >= 5) mNumberPickerSubCount4.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startActivity();
                break;
            case R.id.btn_open_close_item_long_click:
                changeItemLongClickEnable();
                break;
            case R.id.btn_open_close_custom_view_drag:
                changeCustomViewDragEnable();
                break;
            case R.id.btn_open_close_custom_long_click:
                changeCustomLongClickEnable();
                break;
            default:
                break;
        }
    }

    private void changeCustomLongClickEnable() {
        mCustomLongClickEnable = !mCustomLongClickEnable;
        if (mCustomLongClickEnable) {
            mBtnOpenCloseCustomLongClick.setText(R.string.btn_close_customView_long_click);
            mBtnOpenCloseCustomLongClick.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            mBtnOpenCloseCustomLongClick.setText(R.string.btn_open_customView_long_click);
            mBtnOpenCloseCustomLongClick.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    private void changeCustomViewDragEnable() {
        mCustomViewDragEnable = !mCustomViewDragEnable;
        if (mCustomViewDragEnable) {
            mBtnOpenCloseCustomViewDrag.setText(R.string.btn_close_customView_drag);
            mBtnOpenCloseCustomViewDrag.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            mBtnOpenCloseCustomViewDrag.setText(R.string.btn_open_customView_drag);
            mBtnOpenCloseCustomViewDrag.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    private void changeItemLongClickEnable() {
        mItemLongClickEnable = !mItemLongClickEnable;
        if (mItemLongClickEnable) {
            mBtnOpenCloseItemLongClick.setText(R.string.btn_close_itemView_long_click);
            mBtnOpenCloseItemLongClick.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            mBtnOpenCloseItemLongClick.setText(R.string.btn_open_itemView_long_click);
            mBtnOpenCloseItemLongClick.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    private void startActivity() {
        Intent intent;
        switch (mRecyclerViewType) {
            case RecyclerViewType.SWIP_DRAG:
                intent = new Intent(this, SwipeDragRecyclerViewActivity.class);
                intent.putExtra(LayoutManagerType.NAME, mLayoutManagerType);
                intent.putExtra(OrientationType.NAME, mOrientationType);
                intent.putExtra(SpanCount.NAME, mSpanCount);
                intent.putExtra(DataCount.NAME, mDataCount);
                intent.putExtra(Enable.ItemLongClickEnable, mItemLongClickEnable);
                intent.putExtra(Enable.CustomLongClickEnable, mCustomLongClickEnable);
                intent.putExtra(Enable.CustomViewDragEnable, mCustomViewDragEnable);
                startActivity(intent);
                break;
            case RecyclerViewType.SWIP_DARG_TREE:
                intent = new Intent(this, SwipeDragTreeRecyclerViewActivity.class);
                intent.putExtra(LayoutManagerType.NAME, mLayoutManagerType);
                intent.putExtra(OrientationType.NAME, mOrientationType);
                intent.putExtra(SpanCount.NAME, mSpanCount);
                intent.putExtra(DataCount.NAME, mDataCount);
                for (int i = mLevelCount - 1; i < mSubCount.length; i++) {
                    mSubCount[i] = 0;
                }
                intent.putExtra(SubCount.NAME, mSubCount);
                intent.putExtra(Enable.ItemLongClickEnable, mItemLongClickEnable);
                intent.putExtra(Enable.CustomLongClickEnable, mCustomLongClickEnable);
                intent.putExtra(Enable.CustomViewDragEnable, mCustomViewDragEnable);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radioGroup_layoutManager:
                switch (checkedId) {
                    case R.id.radioBtn_linear:
                        mLayoutManagerType = LayoutManagerType.LINEAR;
                        mNumberPickerSpanCount.setEnabled(false);
                        break;
                    case R.id.radioBtn_grid:
                        mLayoutManagerType = LayoutManagerType.GRID;
                        mNumberPickerSpanCount.setEnabled(true);
                        break;
                    case R.id.radioBtn_staggered:
                        mLayoutManagerType = LayoutManagerType.STAGGERED;
                        mNumberPickerSpanCount.setEnabled(true);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.radioGroup_orientation:
                switch (checkedId) {
                    case R.id.radioBtn_vertical:
                        mOrientationType = OrientationType.VERTICAL;
                        break;
                    case R.id.radioBtn_horizontal:
                        mOrientationType = OrientationType.HORIZONTAL;
                        break;
                    default:
                        break;
                }
                break;
            case R.id.radioGroup_recyclerView:
                switch (checkedId) {
                    case R.id.radioBtn_swipeDragRecyclerView:
                        mRecyclerViewType = RecyclerViewType.SWIP_DRAG;
                        mLlTreeSet.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioBtn_swipeDragTreeRecyclerView:
                        mRecyclerViewType = RecyclerViewType.SWIP_DARG_TREE;
                        mLlTreeSet.setVisibility(View.VISIBLE);
                        setSubNumberPickerEnabled();
                        setSubNumberPickerCount();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private final int count = 10000;

    private int getSubCount(int data, int level) {
        if (level == 1) {
            return 0;
        }
        int[] sub = new int[4];
        for (int i = 0; i < sub.length; i++) {
            if (i < level - 1) {
                sub[i] = 1;
            } else {
                sub[i] = 0;
            }
        }
        while (true) {
            int num = data * (1 + sub[0] * (1 + sub[1] * (1 + sub[2] * (1 + sub[3]))));
            if (sub[0] > 100) {
                break;
            }
            if (count < num) {
                break;
            }
            for (int i = 0; i < level - 1; i++) {
                sub[i]++;
            }
        }
        LogUtil.d("---count-->", data * (1 + sub[0] * (1 + sub[1] * (1 + sub[2] * (1 + sub[3])))));
        return sub[0];
    }
}