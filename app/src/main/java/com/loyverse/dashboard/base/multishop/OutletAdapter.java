package com.loyverse.dashboard.base.multishop;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.core.api.Outlet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.ViewHolder> {
    List<Outlet> list = new ArrayList<>();
    List<Outlet> selectedOutlets;
    private OnOutletSelectedListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outlet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Outlet outlet = list.get(position);
        holder.outletTitle.setText(outlet.name);
        if (selectedOutlets.contains(outlet)) //compare by reference
            holder.outletIcon.setVisibility(View.VISIBLE);
        else
            holder.outletIcon.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.OnOutletSelected(outlet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<Outlet> list, List<Outlet> selectedOutlet) {
        this.list = list;
        this.selectedOutlets = selectedOutlet;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list = new ArrayList<>();
        this.selectedOutlets = null;
        notifyDataSetChanged();
    }

    public void setListener(OnOutletSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnOutletSelectedListener {
        void OnOutletSelected(Outlet outlet);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.outlet_title)
        TextView outletTitle;
        @BindView(R.id.outlet_select_icon)
        ImageView outletIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
