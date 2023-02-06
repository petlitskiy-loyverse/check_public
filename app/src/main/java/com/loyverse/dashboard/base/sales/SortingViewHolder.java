package com.loyverse.dashboard.base.sales;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;

import butterknife.BindView;
import butterknife.ButterKnife;

class SortingViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name_sort)
    TextView nameSort;

    @BindView(R.id.net_sort)
    TextView netSort;

    OnSortChangeListener listener;

    BaseSalesClassWrapper.Sorting data;

    public SortingViewHolder(@NonNull View itemView, OnSortChangeListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = listener;

        nameSort.setOnClickListener(view -> onSortingClick(DataModel.SortBy.NAME));
        netSort.setOnClickListener(view -> onSortingClick(DataModel.SortBy.NET));
    }

    private void onSortingClick(DataModel.SortBy sortBy){
        if(data.getSortBy() == sortBy){
            data = data.changeSortType(data.getSortType().invert());
        } else {
            DataModel.SortBy newSortBy = data.getSortBy().invert();
            data = data.changeSortByAndType(newSortBy, Utils.getDefaultSortTypeFor(newSortBy));
        }
        Utils.makeActiveSortField(
                itemView.getContext(),
                data.getSortBy() == DataModel.SortBy.NAME ? nameSort : netSort,
                data.getSortType()
        );
        Utils.makeInactiveSortField(
                itemView.getContext(),
                data.getSortBy().invert() == DataModel.SortBy.NAME ? nameSort : netSort
        );

        if(listener != null){
            listener.onSortChange(data.getSortBy(), data.getSortType());
        }
    }

    public void bind(BaseSalesClassWrapper.Sorting data){
        this.data = data;

        nameSort.setText(data.getNameSort());
        if(data.getSortBy().equals(DataModel.SortBy.NAME)){
            Utils.makeActiveSortField(itemView.getContext(), nameSort, data.getSortType());
            Utils.makeInactiveSortField(itemView.getContext(), netSort);
        } else {
            Utils.makeActiveSortField(itemView.getContext(), netSort, data.getSortType());
            Utils.makeInactiveSortField(itemView.getContext(), nameSort);
        }
    }

    public interface OnSortChangeListener {
        void onSortChange(DataModel.SortBy sortBy, DataModel.SortType type);
    }
}
