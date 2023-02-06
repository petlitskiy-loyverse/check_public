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

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public DividerItemDecoration(Context context) {
        int edgeDividerPadding = context.getResources().getDimensionPixelSize(R.dimen.item_edge_margin);
        divider = new InsetDrawable(
                new ColorDrawable(context.getResources().getColor(R.color.light_grey)),
                edgeDividerPadding,
                0,
                edgeDividerPadding,
                0
        );
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        int adapterItemCount = parent.getAdapter().getItemCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            if (parent.getChildAdapterPosition(child) == adapterItemCount - 1) {
                break;
            }

            int bottom = child.getBottom() + Math.round(child.getTranslationY());
            int top = bottom - Utils.dpToPx(1);
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
        c.restore();
    }
}
