package com.goweii.swipedragtreerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author cuizhen
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnStartDragSwipedRecyclerView;
    private Button mBtnStartTreeRecyclerView;
    private Button mBtnStartDragSwipedTreeRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mBtnStartDragSwipedRecyclerView = findViewById(R.id.btn_start_DragSwipedRecyclerView);
        mBtnStartDragSwipedRecyclerView.setOnClickListener(this);
        mBtnStartTreeRecyclerView = findViewById(R.id.btn_start_TreeRecyclerView);
        mBtnStartTreeRecyclerView.setOnClickListener(this);
        mBtnStartDragSwipedTreeRecyclerView = findViewById(R.id.btn_start_DragSwipedTreeRecyclerView);
        mBtnStartDragSwipedTreeRecyclerView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_DragSwipedRecyclerView:
                startActivity(SwipeDragRecyclerViewActivity.class);
                break;
            case R.id.btn_start_TreeRecyclerView:
                break;
            case R.id.btn_start_DragSwipedTreeRecyclerView:
                break;
            default:
                break;
        }
    }

    private void startActivity(Class activityClass){
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
