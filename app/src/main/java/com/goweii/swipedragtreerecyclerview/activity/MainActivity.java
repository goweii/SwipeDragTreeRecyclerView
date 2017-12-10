package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

/**
 * @author cuizhen
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    private int mLayoutManagerType = LayoutManagerType.LINEAR;
    private int mOrientationType = OrientationType.VERTICAL;
    private int mSpanCount = SpanCount.DEFAULT;
    private int mDataCount = DataCount.DEFAULT;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        Button btnStartDragSwipedRecyclerView = findViewById(R.id.btn_start_DragSwipedRecyclerView);
        btnStartDragSwipedRecyclerView.setOnClickListener(this);
        Button btnStartTreeRecyclerView = findViewById(R.id.btn_start_TreeRecyclerView);
        btnStartTreeRecyclerView.setOnClickListener(this);
        Button btnStartDragSwipedTreeRecyclerView = findViewById(R.id.btn_start_DragSwipedTreeRecyclerView);
        btnStartDragSwipedTreeRecyclerView.setOnClickListener(this);

        RadioGroup radioGroupLayoutManager = findViewById(R.id.radioGroup_layoutManager);
        radioGroupLayoutManager.setOnCheckedChangeListener(this);
        RadioGroup radioGroupOrientation = findViewById(R.id.radioGroup_orientation);
        radioGroupOrientation.setOnCheckedChangeListener(this);

        NumberPicker numberPickerSpanCount = findViewById(R.id.numberPicker_spanCount);
        numberPickerSpanCount.setMinValue(1);
        numberPickerSpanCount.setMaxValue(20);
        numberPickerSpanCount.setValue(SpanCount.DEFAULT);
        numberPickerSpanCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerSpanCount.setOnValueChangedListener(this);
        NumberPicker numberPickerDataCount = findViewById(R.id.numberPicker_dataCount);
        numberPickerDataCount.setMinValue(0);
        numberPickerDataCount.setMaxValue(100);
        numberPickerDataCount.setValue(DataCount.DEFAULT);
        numberPickerDataCount.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerDataCount.setOnValueChangedListener(this);
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
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_DragSwipedRecyclerView:
                startActivity(SwipeDragRecyclerViewActivity.class);
                break;
            case R.id.btn_start_TreeRecyclerView:
                startActivity(TreeRecyclerViewActivity.class);
                break;
            case R.id.btn_start_DragSwipedTreeRecyclerView:
                startActivity(SwipeDragTreeRecyclerViewActivity.class);
                break;
            default:
                break;
        }
    }

    private void startActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(LayoutManagerType.NAME, mLayoutManagerType);
        intent.putExtra(OrientationType.NAME, mOrientationType);
        intent.putExtra(SpanCount.NAME, mSpanCount);
        intent.putExtra(DataCount.NAME, mDataCount);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radioGroup_layoutManager:
                switch (checkedId) {
                    case R.id.radioBtn_linear:
                        mLayoutManagerType = LayoutManagerType.LINEAR;
                        break;
                    case R.id.radioBtn_grid:
                        mLayoutManagerType = LayoutManagerType.GRID;
                        break;
                    case R.id.radioBtn_staggered:
                        mLayoutManagerType = LayoutManagerType.STAGGERED;
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
            default:
                break;
        }
    }
}
