package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author cuizhen
 */
public class TreeRecyclerViewActivity extends AppCompatActivity {

    private int mLayoutManagerType;
    private int mOrientationType;
    private int mSpanCount;
    private int mDataCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_recycler_view);

        Intent intent = getIntent();
        mLayoutManagerType = intent.getIntExtra(MainActivity.LayoutManagerType.NAME, MainActivity.LayoutManagerType.LINEAR);
        mOrientationType = intent.getIntExtra(MainActivity.OrientationType.NAME, MainActivity.OrientationType.VERTICAL);
        mSpanCount = intent.getIntExtra(MainActivity.SpanCount.NAME, MainActivity.SpanCount.DEFAULT);
        mDataCount = intent.getIntExtra(MainActivity.DataCount.NAME, MainActivity.DataCount.DEFAULT);
    }
}
