package com.loyverse.dashboard.base.products;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.loyverse.dashboard.R;

public class ProductFilterAdapter extends ArrayAdapter<String> {
    private int selectedItem = -1;

    public ProductFilterAdapter(Context context) {
        super(context, R.layout.item_product_filter, R.id.item_product_spinner_name);
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ImageView selected = (ImageView) view.findViewById(R.id.item_product_spinner_selected);
        if (position == selectedItem) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ImageView selected = (ImageView) view.findViewById(R.id.item_product_spinner_selected);
        if (position == selectedItem) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }
        return view;
    }
}
