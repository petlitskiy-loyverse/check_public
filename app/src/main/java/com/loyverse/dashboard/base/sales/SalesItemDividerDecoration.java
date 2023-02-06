package com.loyverse.dashboard.base.sales;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;

public class SalesItemDividerDecoration extends RecyclerView.ItemDecoration {

    private final Drawable sortingDivider;
    private final Drawable divider;

    public SalesItemDividerDecoration(Context context) {
        int edgeDividerPadding = context.getResources().getDimensionPixelSize(R.dimen.item_edge_margin);
        ColorDrawable line = new ColorDrawable(context.getResources().getColor(R.color.light_grey));
        Drawable itemLine = new ColorDrawable(context.getResources().getColor(R.color.light_grey));
        sortingDivider = line;

        itemLine = new InsetDrawable(
                itemLine,
                edgeDividerPadding,
                0,
                edgeDividerPadding,
                0
        );
        divider = itemLine;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        int adapterItemCount = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(child);
            boolean isSortingHeader = holder instanceof SortingViewHolder;

            if (!isSortingHeader && (holder.getAdapterPosition() == adapterItemCount - 1)) {
                continue;
            }

            int top = child.getBottom();
            int bottom = top + Utils.dpToPx(1);

            if (isSortingHeader) {
                sortingDivider.setBounds(left, top, right, bottom);
                sortingDivider.draw(c);
                continue;
            }

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
        c.restore();
    }
}
