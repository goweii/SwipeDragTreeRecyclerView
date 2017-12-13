package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

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
    private int mSubCount = LevelCount.DEFAULT;
    private NumberPicker mNumberPickerSpanCount;
    private LinearLayout mLlTreeSet;
    private NumberPicker mNumberPickerLevelCount;
    private NumberPicker mNumberPickerSubCount;

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
        mNumberPickerSpanCount.setEnabled(false);
        mNumberPickerSpanCount.setMinValue(1);
        mNumberPickerSpanCount.setMaxValue(20);
        mNumberPickerSpanCount.setValue(SpanCount.DEFAULT);
        mNumberPickerSpanCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerSpanCount.setOnValueChangedListener(this);
        NumberPicker numberPickerDataCount = findViewById(R.id.numberPicker_dataCount);
        numberPickerDataCount.setMinValue(0);
        numberPickerDataCount.setMaxValue(100);
        numberPickerDataCount.setValue(DataCount.DEFAULT);
        numberPickerDataCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerDataCount.setOnValueChangedListener(this);
        mNumberPickerLevelCount = findViewById(R.id.numberPicker_levelCount);
        mNumberPickerLevelCount.setMinValue(1);
        mNumberPickerLevelCount.setMaxValue(5);
        mNumberPickerLevelCount.setValue(LevelCount.DEFAULT);
        mNumberPickerLevelCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerSubCount = findViewById(R.id.numberPicker_subCount);
        mNumberPickerSubCount.setMinValue(1);
        mNumberPickerSubCount.setMaxValue(10);
        mNumberPickerSubCount.setValue(5);
        mNumberPickerSubCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mLlTreeSet = findViewById(R.id.ll_tree_set);
        mLlTreeSet.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numberPicker_dataCount:
                mDataCount = newVal;
                break;
            case R.id.numberPicker_spanCount:
                mSpanCount = newVal;
                break;
            case R.id.numberPicker_levelCount:
                mLevelCount = newVal;
                break;
            case R.id.numberPicker_subCount:
                mSubCount = newVal;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startActivity();
                break;
            default:
                break;
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
                startActivity(intent);
                break;
            case RecyclerViewType.SWIP_DARG_TREE:
                intent = new Intent(this, SwipeDragTreeRecyclerViewActivity.class);
                intent.putExtra(LayoutManagerType.NAME, mLayoutManagerType);
                intent.putExtra(OrientationType.NAME, mOrientationType);
                intent.putExtra(SpanCount.NAME, mSpanCount);
                intent.putExtra(DataCount.NAME, mDataCount);
                intent.putExtra(LevelCount.NAME, mLevelCount);
                intent.putExtra(SpanCount.NAME, mSubCount);
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
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
