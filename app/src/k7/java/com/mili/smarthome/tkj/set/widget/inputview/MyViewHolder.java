package com.mili.smarthome.tkj.set.widget.inputview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mili.smarthome.tkj.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public final InputItemView customNum;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        customNum = (InputItemView)itemView.findViewById(R.id.input_item_view);
    }

}
