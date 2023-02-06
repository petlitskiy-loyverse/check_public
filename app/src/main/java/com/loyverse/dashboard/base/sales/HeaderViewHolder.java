package com.loyverse.dashboard.base.sales;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loyverse.dashboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class HeaderViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.header_text)
    TextView headerText;

    HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
