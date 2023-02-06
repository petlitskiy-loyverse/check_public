package com.loyverse.dashboard.base.sales;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSalesViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_name)
    TextView itemName;
    @BindView(R.id.item_sales_amount)
    TextView itemSalesAmount;
    @BindView(R.id.item_sales_count)
    TextView itemSaleCount;
    @BindView(R.id.item_image)
    ImageView itemImage;

    BaseSalesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(BaseSalesItem item) {
        itemName.setText(item.getName());
        if (item.getQuantity() == 0 && item.getReturns() > 0) {
            itemSaleCount.setVisibility(View.VISIBLE);
            itemSaleCount.setText(String.format("x %s", Utils.formatNumber(item.getQuantity())));
        } else if (item.getQuantity() > 0 || item.getQuantity() < 0) {
            itemSaleCount.setVisibility(View.VISIBLE);
            itemSaleCount.setText(String.format("x %s", Utils.formatNumber(item.getQuantity())));
        } else {
            itemSaleCount.setVisibility(View.GONE);
        }
        String value = Utils.formatSalesNumber(item.getAmount());
        Log.e("PRINT", "" + value);
        Utils.setRTLText(itemSalesAmount, value);

        if (item.getImageLink() != null)
            Utils.loadRoundedImage(itemView.getContext(), item.getImageLink(), itemImage);
        else {
            itemImage.setImageBitmap(Utils.generateCircle(
                    itemView.getContext(),
                    Utils.isDarkModeEnabled(itemView.getContext())
                            ? Utils.getDarkThemeColorFor(item.getColorName())
                            : item.getColor(),
                    item.getName()));
            Log.e("=============", "========color====" + item.getColor() + "   " + item.getName());
        }
    }
}
