package io.huannguyen.swipeablerv.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.huannguyen.swipeablerv.adapter.StandardSWAdapter;

import java.util.List;

/**
 * Created by huannguyen
 */

public class SampleAdapter extends StandardSWAdapter<String, SampleViewHolder> {

    public SampleAdapter(List<String> reminderList) {
        super(reminderList);
    }

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new io.huannguyen.swipeablerv.demo.SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {
        holder.initData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0: mItems.size();
    }
}
