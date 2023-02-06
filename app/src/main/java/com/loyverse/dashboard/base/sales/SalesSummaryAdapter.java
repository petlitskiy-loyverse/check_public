package com.loyverse.dashboard.base.sales;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.api.EarningsReportResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesSummaryAdapter extends RecyclerView.Adapter<SalesSummaryAdapter.ViewHolder> {

    private final WeakReference<Context> contextRef;


    private final DiffUtil.ItemCallback<ItemView> diffCallback = new DiffUtil.ItemCallback<ItemView>() {
        @Override
        public boolean areItemsTheSame(ItemView oldItem, ItemView newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(ItemView oldItem, ItemView newItem) {
            return oldItem.name.equals(newItem.name) &&
                    oldItem.hasBottomDivider == newItem.hasBottomDivider &&
                    oldItem.hasTopDivider == newItem.hasTopDivider &&
                    oldItem.isBold == newItem.isBold &&
                    oldItem.value == newItem.value;
        }
    };
    private final AsyncListDiffer<ItemView> items = new AsyncListDiffer<>(this, diffCallback);
    private EarningsReportResponse.HideFields hideFields;

    public SalesSummaryAdapter(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    public void updateData(EarningsReportResponse.TotalValues values) {
        List<ItemView> items = new ArrayList<>();
        items.add(new ItemView(R.string.sales_summary_gross_sales, values.totalPeriodSum).isBold(true));
        items.add(new ItemView(R.string.sales_summary_refunds, values.totalPeriodReturnSum));
        items.add(new ItemView(R.string.sales_summary_discounts, values.totalPeriodDiscountSum));
        items.add(new ItemView(R.string.sales_summary_net_sales, values.totalPeriodEarningsSum)
                .hasTopDivider(true)
                .isBold(true));
        if (hideFields.taxes)
            items.add(new ItemView(R.string.sales_summary_taxes, values.totalPeriodAllTaxesSum));
        if (hideFields.tips)
            items.add(new ItemView(R.string.sales_summary_tips, values.totalPeriodTipsSum));
        final ItemView costOfGoods = new ItemView(R.string.sales_summary_cost_of_goods, values.totalPeriodCostOfGoodsSum);
        final ItemView grossProfit = new ItemView(R.string.sales_summary_gross_profit, values.totalPeriodGrossProfit).isBold(true);

        if (hideFields.taxes || hideFields.tips) {
            items.add(new ItemView(R.string.sales_summary_total_tendered, values.totalTendered)
                    .hasTopDivider(true)
                    .isBold(true));
            if (!hideFields.cost) {
                items.add(costOfGoods.hasTopDivider(true));
            }
            if (!hideFields.cost) {
                items.add(grossProfit);
            }
        } else {
            if (!hideFields.cost) {
                items.add(costOfGoods);
            }
            if (!hideFields.cost) {
                items.add(grossProfit.hasTopDivider(true));
            }
        }

        this.items.submitList(items);
    }

    public void clearData() {
        items.submitList(new ArrayList<>());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_summary, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemView itemView = items.getCurrentList().get(position);
        holder.name.setText(itemView.name);
        String value = Utils.formatSalesNumber(Utils.formatCoinValue(itemView.value));
        Utils.setRTLText(holder.value, value);

        if (itemView.isBold) {
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.value.setTypeface(null, Typeface.BOLD);
        } else {
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.value.setTypeface(null, Typeface.NORMAL);
        }

        if (itemView.hasBottomDivider)
            holder.bottomDivider.setVisibility(View.VISIBLE);
        else
            holder.bottomDivider.setVisibility(View.GONE);

        if (itemView.hasTopDivider)
            holder.topDivider.setVisibility(View.VISIBLE);
        else
            holder.topDivider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.getCurrentList().size();
    }

    public void setHideFields(EarningsReportResponse.HideFields hideFields) {
        this.hideFields = hideFields;
    }

    private class ItemView {
        String name;
        long value;
        boolean isBold = false;
        boolean hasTopDivider = false;
        boolean hasBottomDivider = false;

        ItemView(int stringId, long value) {
            this.value = value;
            Context context = contextRef.get();
            if (context != null)
                this.name = context.getResources().getString(stringId);
            else
                this.name = "";
        }

        ItemView isBold(boolean bold) {
            isBold = bold;
            return this;
        }

        ItemView hasTopDivider(boolean hasTopDivider) {
            this.hasTopDivider = hasTopDivider;
            return this;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_sales_summary_name)
        TextView name;

        @BindView(R.id.item_sales_summary_value)
        TextView value;

        @BindView(R.id.item_top_divider)
        View topDivider;

        @BindView(R.id.item_bottom_divider)
        View bottomDivider;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
