package com.loyverse.dashboard.base.products;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_VARIANT = 1;
    private static final int ARROW_ROTATION_DURATION = 150;
    private List<ProductClassWrapper> itemList = new ArrayList<>();
    private SparseIntArray expand = new SparseIntArray();

    private static void openArrow(View view) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(180);
    }

    private static void closeArrow(View view) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(0);
    }

    private int getColorByStockType(Context context, @ProductItem.StockType int type) {
        int color = ContextCompat.getColor(context, R.color.light_main_font_color);
        if (type == ProductItem.OUT_OF_STOCK) {
            color = ContextCompat.getColor(context, R.color.product_item_warning_out_of_stock_stock_color);
        } else if (type == ProductItem.LOW_STOCK) {
            color = ContextCompat.getColor(context, R.color.product_item_warning_low_stock_color);
        }
        return color;
    }

    private int getColorByStockVariantType(Context context, @ProductItem.StockTypeVariant int type) {
        int color = ContextCompat.getColor(context, R.color.light_main_font_color);
        if (type == ProductItem.OUT_OF_STOCK) {
            color = ContextCompat.getColor(context, R.color.product_item_warning_out_of_stock_stock_color);
        } else if (type == ProductItem.LOW_STOCK) {
            color = ContextCompat.getColor(context, R.color.product_item_warning_low_stock_color);
        }
        return color;
    }

    private void setUpWarning(ProductViewHolder holder, ProductClassWrapper.Item item) {
        int stockType = getItemStockType(item.getProduct());
        Context context = holder.itemView.getContext();
        if (stockType == ProductItem.NORMAL_STOCK || stockType == ProductItem.UNCOUNTED_STOCK) {
            holder.inStock.setTextColor(getColorByStockVariantType(context, stockType));
            holder.hideWarning();
            return;
        }

        final int color = getColorByStockType(context, stockType);
        holder.inStock.setTextColor(color);
        holder.showWarning();
        holder.warningBadge.setColorFilter(color);
    }

    private int getItemStockType(ProductItem item) {
        if (item.getVariations() != null && !item.getVariations().isEmpty()) {
            if (isAnyVariationOutOfStock(item)) {
                return ProductItem.OUT_OF_STOCK;
            }

            if (isAnyVariationLowStock(item)) {
                return ProductItem.LOW_STOCK;
            }

            return ProductItem.NORMAL_STOCK;
        }

        return item.getStockType();
    }

    private boolean isAnyVariationLowStock(ProductItem item) {
        for (ProductItem.Variant variant : item.getVariations()) {
            if (variant.getStockType() == ProductItem.LOW_STOCK)
                return true;
        }

        return false;
    }

    private boolean isAnyVariationOutOfStock(ProductItem item) {
        for (ProductItem.Variant variant : item.getVariations()) {
            if (variant.getStockType() == ProductItem.OUT_OF_STOCK)
                return true;
        }

        return false;
    }

    private void setUpWarningForVariants(ProductVariantViewHolder holder, ProductClassWrapper.Variant item) {
        Context context = holder.itemView.getContext();
        if (item.getVariant().getStockType() == ProductItem.NORMAL_STOCK
                || item.getVariant().getStockType() == ProductItem.UNCOUNTED_STOCK
                || context == null) {


            holder.variantCount.setTextColor(getColorByStockVariantType(context, item.getVariant().getStockType()));
            return;
        }

        final int color = getColorByStockType(context, item.getVariant().getStockType());
        holder.variantCount.setTextColor(color);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_VARIANT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_old_product_variant, parent, false);
            return new ProductVariantViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Context context = viewHolder.itemView.getContext();
        if (viewHolder instanceof ProductViewHolder) {
            ProductClassWrapper.Item item = (ProductClassWrapper.Item) itemList.get(position);
            ProductViewHolder holder = (ProductViewHolder) viewHolder;
            bindItem(position, context, item, holder);
        } else if (viewHolder instanceof ProductVariantViewHolder) {
            ProductClassWrapper.Variant item = (ProductClassWrapper.Variant) itemList.get(position);
            ProductVariantViewHolder holder = (ProductVariantViewHolder) viewHolder;
            bindVariant(context, item, holder);
        }
    }

    private void bindVariant(Context context, ProductClassWrapper.Variant item, ProductVariantViewHolder holder) {
        holder.variantCount.setText(context.getResources().getString(R.string.product_item_stock, Utils.formatNumber(item.getVariant().getWareCount())));
        holder.variantName1.setText(joinList(item.getVariant().getOptions(), " / "));
        setUpWarningForVariants(holder, item);

        if (item.getVariant().getStockType() == ProductItem.UNCOUNTED_STOCK) {
            holder.variantCount.setText(context.getString(R.string.product_uncounted_stock_symbols));
        }
    }

    private void bindItem(int position, Context context, ProductClassWrapper.Item item, ProductViewHolder holder) {
        holder.bind(position);
        holder.name.setText(item.getProduct().getName());
        Log.v("ItemName", "" + item.getProduct().getName());
        if (item.getProduct().getVariations() != null && !(item.getProduct().getVariations().isEmpty())) {
            holder.arrow.setVisibility(View.VISIBLE);
            holder.verticalDelimiter.setVisibility(View.VISIBLE);
            int size = item.getProduct().getVariations().size();
            String formatVariantCount = String.format(Utils.EN_LOCALE, "%s", context.getResources().getQuantityString(R.plurals.variantCount, size, size));
            holder.variantProduct.setText(formatVariantCount);
            holder.variantProduct.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setVisibility(View.GONE);
            holder.variantProduct.setVisibility(View.GONE);
            holder.verticalDelimiter.setVisibility(View.GONE);
        }

        setUpWarning(holder, item);

        if (item.getProduct().getStockType() == ProductItem.UNCOUNTED_STOCK) {
            holder.inStock.setText(context.getString(R.string.product_uncounted_stock_symbols));

        } else {
            holder.inStock.setText(context.getResources().getString(R.string.product_item_stock, Utils.formatNumber(item.getProduct().getStock())));
        }

        if (item.getProduct().getStockType() == ProductItem.UNCOUNTED_STOCK && item.getProduct().getVariations() != null) {
            holder.inStock.setText(context.getString(R.string.variant_track_stock_off));
            holder.verticalDelimiter.setVisibility(View.GONE);
        }

        if (item.getProduct().getImgUrl() != null) {
            Utils.loadRoundedImage(
                    context,
                    item.getProduct().getImgUrl(),
                    holder.icon,
                    () -> holder.icon.setImageBitmap(Utils.generateCircle(
                            context,
                            Utils.isDarkModeEnabled(context) ? Utils.getDarkThemeColorFor(item.getProduct().getColorName()) : item.getProduct().getColor(),
                            item.getProduct().getName())));
        } else {
            holder.icon.setImageBitmap(Utils.generateCircle(
                    context,
                    Utils.isDarkModeEnabled(context) ? Utils.getDarkThemeColorFor(item.getProduct().getColorName()) : item.getProduct().getColor(),
                    item.getProduct().getName()));
        }
        if (position == itemList.size() - 1 && !Utils.isPhoneLayout(context)) {
            holder.itemDivider.setVisibility(View.GONE);
        } else holder.itemDivider.setVisibility(View.VISIBLE);
    }

    private boolean toggleExpandedItems(int position) {
        if (isExpanded(position)) {
            collapseItems(position);
            return false;
        } else {
            expandItems(position);
            return true;
        }
    }

    private void expandItems(int position) {
        if (expand.get(position) > 0) {
            return;
        }
        ProductClassWrapper productClassWrapper = itemList.get(position);
        if (!(productClassWrapper instanceof ProductClassWrapper.Item)) {
            return;
        }
        ProductClassWrapper.Item item = (ProductClassWrapper.Item) productClassWrapper;

        List<ProductClassWrapper.Variant> variants = getVariantForItem(item);
        if (variants.size() == 0) {
            return;
        }

        itemList.addAll(position + 1, variants);
        expand.put(position, variants.size());
        notifyItemRangeInserted(position + 1, variants.size());
    }

    private void collapseItems(int position) {
        int countCollapsedVariantsInProduct = 0;
        int variantNumber = expand.get(position);
        if (variantNumber > 0) {
            for (int i = position; i < position + variantNumber; i++) {
                itemList.remove(position + 1);
            }
        }
        expand.delete(position);
        notifyItemRangeRemoved(position + 1, variantNumber);
    }

    @NonNull
    private <T> String joinList(List<T> list, String separator) {
        StringBuilder builder = new StringBuilder();
        if (list.isEmpty())
            return list.toString();

        for (T item : list) {
            builder.append(item);
            builder.append(separator);
        }

        String outputString = builder.toString();
        return outputString.substring(0, outputString.length() - separator.length());
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof ProductClassWrapper.Item) {
            return TYPE_ITEM;
        }
        if (itemList.get(position) instanceof ProductClassWrapper.Variant) {
            return TYPE_VARIANT;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private List<ProductClassWrapper> mapToItemWrappers(List<ProductItem> newItems) {
        ArrayList<ProductClassWrapper> items = new ArrayList<>();
        for (ProductItem product : newItems) {
            items.add(new ProductClassWrapper.Item(product));
        }
        return items;
    }

    private List<ProductClassWrapper.Variant> getVariantForItem(ProductClassWrapper.Item item) {
        List<ProductClassWrapper.Variant> items = new ArrayList<>();
        ProductItem product = item.getProduct();
        if (product.getVariations() != null) {
            for (ProductItem.Variant variant : product.getVariations()) {
                items.add(new ProductClassWrapper.Variant(variant));
            }
        }
        return items;
    }

    public void addProductItemList(List<ProductItem> productItemList) {
        final int size = this.itemList.size();
        final List<ProductClassWrapper> items = mapToItemWrappers(productItemList);
        this.itemList.addAll(items);
        this.notifyItemRangeInserted(size, items.size());
    }

    public void clearProductItemList() {
        expand.clear();
        final int size = this.itemList.size();
        this.itemList = new ArrayList<>();
        this.notifyItemRangeRemoved(0, size);
    }

    private boolean isExpanded(int position) {
        return expand.get(position) > 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_icon)
        ImageView icon;

        @BindView(R.id.product_icon_warning_badge)
        ImageView warningBadge;

        @BindView(R.id.product_name)
        TextView name;

        @BindView(R.id.product_in_stock)
        TextView inStock;

        @BindView(R.id.arrow_more_variants)
        ImageView arrow;

        @BindView(R.id.vertical_divider)
        TextView verticalDelimiter;

        @BindView(R.id.product_variants)
        TextView variantProduct;

        @BindView(R.id.item_bottom_divider)
        View itemDivider;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            hideWarning();

            itemView.setOnClickListener(v -> handleClick());
            arrow.setOnClickListener(v -> handleClick());
        }

        private void handleClick() {
            final int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            if (toggleExpandedItems(position)) {
                openArrow(arrow);
            } else {
                closeArrow(arrow);
            }
        }

        public void bind(int position) {
            arrow.setRotation(isExpanded(position) ? 180 : 0);

        }

        void showWarning() {
            warningBadge.setVisibility(View.VISIBLE);
        }

        void hideWarning() {
            warningBadge.setVisibility(View.GONE);
        }
    }
}