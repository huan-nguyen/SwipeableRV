package io.huannguyen.swipeablerv.demo;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

/**
 * Created by huannguyen
 */

public class SampleViewHolder extends ViewHolder {

    public SampleViewHolder(View itemView) {
        super(itemView);
    }

    public void initData(String version) {
        ((TextView)itemView.findViewById(R.id.reminder)).setText(version);
    }
}
