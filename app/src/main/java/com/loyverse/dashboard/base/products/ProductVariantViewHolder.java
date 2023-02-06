package com.loyverse.dashboard.base.products;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loyverse.dashboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductVariantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_options)
    TextView variantName1;
    @BindView(R.id.item_variant_amount)
    TextView variantAmount;
    @BindView(R.id.item_variant_count)
    TextView variantCount;

    ProductVariantViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

     public TextView getVariantCount() {
        return variantCount;
    }
}
