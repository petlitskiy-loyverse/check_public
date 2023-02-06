package com.loyverse.dashboard.base.sales;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

class FooterViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.total_value)
    TextView totalValue;

    FooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(BaseSalesClassWrapper.Total value) {
        String amount = Utils.formatSalesNumber(
                Utils.formatCoinValue((long) value.getTotal()));
        Utils.setRTLText(totalValue, amount);
    }
}
