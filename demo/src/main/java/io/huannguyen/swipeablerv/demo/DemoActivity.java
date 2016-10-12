package io.huannguyen.swipeablerv.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import io.huannguyen.swipeablerv.view.SWRecyclerView;
import io.huannguyen.swipeablerv.view.SWRecyclerView.SwipeMessageBuilder;

public class DemoActivity extends AppCompatActivity {

    SWRecyclerView mRecyclerView;
    io.huannguyen.swipeablerv.demo.SampleAdapter mAdapter;
    List<String> reminders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initViews();
    }

    private void initViews() {
        mRecyclerView = (SWRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.getSwipeMessageBuilder()
                     .withFontPath(getString(R.string.droidSerif))
                     .withSwipeDirection(SwipeMessageBuilder.BOTH)
                     .build();
        LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        reminders.add("Reminder 1");
        reminders.add("Reminder 2");
        reminders.add("Reminder 3");
        reminders.add("Reminder 4");
        reminders.add("Reminder 5");
        reminders.add("Reminder 6");
        reminders.add("Reminder 7");
        reminders.add("Reminder 8");
        reminders.add("Reminder 9");
        reminders.add("Reminder 10");

        mAdapter = new io.huannguyen.swipeablerv.demo.SampleAdapter(reminders);
        // allow swiping with both directions (left-to-right and right-to-left)
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setupSwipeToDismiss(mAdapter, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
    }
}
