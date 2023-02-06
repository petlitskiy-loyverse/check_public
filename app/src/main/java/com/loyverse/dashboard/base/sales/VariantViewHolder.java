package com.loyverse.dashboard.base.sales;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandr on 2/1/2018.
 */

class VariantViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_options)
    TextView variantName1;
    @BindView(R.id.item_variant_amount)
    TextView variantAmount;
    @BindView(R.id.item_variant_count)
    TextView variantCount;

    VariantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(WaresPeriodReportResponse.Variant variant) {
        variantCount.setText(String.format("x %s", Utils.formatNumber(variant.getQuantity())));
        final String value = Utils.formatSalesNumber(variant.getEarningSum());
        Utils.setRTLText(variantAmount, value);
        variantName1.setText(joinList(variant.getOptions(), " / "));
    }

    private <T> String joinList(List<T> list, String separator) {
        StringBuilder builder = new StringBuilder();
        for (T item : list) {
            builder.append(item);
            builder.append(separator);
        }

        String outputString = builder.toString();
        return outputString.substring(0, outputString.length() - separator.length());
    }
}
