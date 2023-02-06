package com.loyverse.dashboard.base.sales;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.sales.BaseSalesClassWrapper.Item;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse.Variant;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse.Wares;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseSalesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SORTBY = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_VARIANT = 2;
    private static final int TYPE_TOTAL = 3;
    private final List<BaseSalesClassWrapper> itemsList = new ArrayList<>();
    private SortingViewHolder.OnSortChangeListener listener;

    public BaseSalesAdapter(SortingViewHolder.OnSortChangeListener listener) {
        this.listener = listener;
    }

    public BaseSalesAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SORTBY) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_header, parent, false);
            itemView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.color_chart_bg));
            return new SortingViewHolder(itemView, listener != null ? listener : (sortBy, type) -> {});
        }
        if (viewType == TYPE_TOTAL) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
            itemView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.color_chart_bg));
            return new FooterViewHolder(itemView);
        }
        if (viewType == TYPE_VARIANT) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_variant, parent, false);
            itemView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.color_chart_bg));
            return new VariantViewHolder(itemView);
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales, parent, false);
        itemView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.color_chart_bg));
        return new BaseSalesViewHolder(itemView);
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof BaseSalesViewHolder) {
            BaseSalesViewHolder holder = (BaseSalesViewHolder) viewHolder;
            holder.bind(((Item) itemsList.get(position)).get());
        } else if (viewHolder instanceof VariantViewHolder) {
            VariantViewHolder holder = (VariantViewHolder) viewHolder;
            holder.bind(((BaseSalesClassWrapper.Variant) itemsList.get(position)).get());
        } else if (viewHolder instanceof SortingViewHolder) {
            SortingViewHolder holder = (SortingViewHolder) viewHolder;
            holder.bind((BaseSalesClassWrapper.Sorting) itemsList.get(position));
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder holder = (FooterViewHolder) viewHolder;
            holder.bind((BaseSalesClassWrapper.Total) itemsList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseSalesClassWrapper item = itemsList.get(position);
        if (item instanceof BaseSalesClassWrapper.Sorting) {
            return TYPE_SORTBY;
        } else if (item instanceof BaseSalesClassWrapper.Total) {
            return TYPE_TOTAL;
        } else if (item instanceof BaseSalesClassWrapper.Variant) {
            return TYPE_VARIANT;
        } else return TYPE_ITEM;
    }

    public void updateData(List<BaseSalesItem> newItems) {
        final ArrayList<BaseSalesClassWrapper> items = new ArrayList<>(itemsList);
        BaseSalesClassWrapper.Sorting sorting = null;
        BaseSalesClassWrapper.Total total = null;

        if (!itemsList.isEmpty()) {
            boolean isFirstItemSorting = itemsList.get(0) instanceof BaseSalesClassWrapper.Sorting;
            sorting = isFirstItemSorting ? (BaseSalesClassWrapper.Sorting) itemsList.get(0) : null;
            boolean isLastItemTotal = itemsList.get(itemsList.size() - 1) instanceof BaseSalesClassWrapper.Total;
            total = isLastItemTotal ? (BaseSalesClassWrapper.Total) itemsList.get(itemsList.size() - 1) : null;
        }

        itemsList.clear();
        if (sorting != null) {
            itemsList.add(sorting);
        }
        for (BaseSalesItem item : newItems) {
            itemsList.addAll(mapToItems(item));
        }
        if (total != null) {
            itemsList.add(total);
        }

        notifyChanges(items, itemsList);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Collection<? extends BaseSalesClassWrapper> mapToItems(BaseSalesItem item) {
        ArrayList<BaseSalesClassWrapper> items = new ArrayList<>();
        items.add(new Item(item));
        if (item instanceof Wares) {
            Collection<Variant> variants = ((Wares) item).getVariations();
            if (variants != null && !variants.isEmpty()) {
                for (Variant variant : variants) {
                    items.add(new BaseSalesClassWrapper.Variant(variant));
                }
            }
        }
        return items;
    }

    public void updateTotal(Double total) {
        final ArrayList<BaseSalesClassWrapper> items = new ArrayList<>(itemsList);
        if (itemsList.isEmpty() && total == null) {
            return;
        }

        int indexOfElement = -1;
        if (!itemsList.isEmpty()) {
            boolean isLastItemTotal = itemsList.get(itemsList.size() - 1) instanceof BaseSalesClassWrapper.Total;
            indexOfElement = isLastItemTotal ? itemsList.size() - 1 : -1;
        }

        if (total == null) {
            if (indexOfElement >= 0) {
                itemsList.remove(indexOfElement);
            }
            return;
        }

        if (indexOfElement != -1) {
            itemsList.remove(indexOfElement);
            itemsList.add(new BaseSalesClassWrapper.Total(total));
        } else itemsList.add(new BaseSalesClassWrapper.Total(total));

        notifyChanges(items, itemsList);
    }

    public void updateSorting(BaseSalesClassWrapper.Sorting sorting) {
        final ArrayList<BaseSalesClassWrapper> items = new ArrayList<>(itemsList);
        if (itemsList.isEmpty() && sorting == null) {
            return;
        }

        boolean isFirstItemSorting = false;
        if (!itemsList.isEmpty()) {
            isFirstItemSorting = itemsList.get(0) instanceof BaseSalesClassWrapper.Sorting;
        }
        if (sorting == null) {
            if (isFirstItemSorting) {
                itemsList.remove(0);
            }
            return;
        }

        if (isFirstItemSorting) {
            itemsList.remove(0);
        }

        itemsList.add(0, sorting);

        notifyChanges(items, itemsList);
    }

    private void notifyChanges(List<BaseSalesClassWrapper> oldItems, List<BaseSalesClassWrapper> newItems) {
        DiffUtil.calculateDiff(new ItemDiffUtilCallback(oldItems, newItems), false)
                .dispatchUpdatesTo(this);
    }

    public void addData(List<BaseSalesItem> newItems) {
        final ArrayList<BaseSalesClassWrapper> items = new ArrayList<>(itemsList);
        BaseSalesClassWrapper.Total total = null;
        if (!itemsList.isEmpty()) {
            boolean isLastItemTotal = itemsList.get(itemsList.size() - 1) instanceof BaseSalesClassWrapper.Total;
            total = isLastItemTotal ? (BaseSalesClassWrapper.Total) itemsList.get(itemsList.size() - 1) : null;
        }
        if (total != null) {
            itemsList.remove(itemsList.size() - 1);
        }

        for (BaseSalesItem item : newItems) {
            itemsList.addAll(mapToItems(item));
        }
        if (total != null) {
            itemsList.add(total);
        }

        notifyChanges(items, itemsList);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void clearData() {
        final ArrayList<BaseSalesClassWrapper> items = new ArrayList<>(itemsList);
        if (items.size() == 0)
            return;

        boolean isFirstItemSorting = itemsList.get(0) instanceof BaseSalesClassWrapper.Sorting;
        BaseSalesClassWrapper.Sorting sorting = isFirstItemSorting ? (BaseSalesClassWrapper.Sorting) itemsList.get(0) : null;
        boolean isLastItemTotal = itemsList.get(itemsList.size() - 1) instanceof BaseSalesClassWrapper.Total;
        itemsList.clear();
        if (isFirstItemSorting) {
            itemsList.add(sorting);
        }
        if (isLastItemTotal) {
            itemsList.add(new BaseSalesClassWrapper.Total(0));
        }
        notifyChanges(items, itemsList);
    }

    private class ItemDiffUtilCallback extends DiffUtil.Callback {

        private final List<BaseSalesClassWrapper> oldList;
        private final List<BaseSalesClassWrapper> newList;

        public ItemDiffUtilCallback(List<BaseSalesClassWrapper> oldItems, List<BaseSalesClassWrapper> newItems) {
            this.oldList = oldItems;
            this.newList = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            BaseSalesClassWrapper oldItem = oldList.get(oldItemPosition);
            BaseSalesClassWrapper newItem = newList.get(newItemPosition);
            if (oldItem.getClass() != newItem.getClass()) {
                return false;
            }
            if (oldItem instanceof BaseSalesClassWrapper.Sorting || oldItem instanceof BaseSalesClassWrapper.Total) {
                return true;
            }
            if (oldItem instanceof Item) {
                BaseSalesItem oldBaseSalesItem = ((Item) oldItem).get();
                BaseSalesItem newBaseSalesItem = ((Item) newItem).get();
                if (oldBaseSalesItem.getClass() != newBaseSalesItem.getClass()) {
                    return false;
                } else if (oldBaseSalesItem instanceof Wares) {
                    return ((Wares) oldBaseSalesItem).getId() == ((Wares) newBaseSalesItem).getId();
                } else return oldBaseSalesItem.getName().equals(newBaseSalesItem.getName());
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            BaseSalesClassWrapper oldItem = oldList.get(oldItemPosition);
            BaseSalesClassWrapper newItem = newList.get(newItemPosition);
            return oldItem.equals(newItem);
        }
    }
}