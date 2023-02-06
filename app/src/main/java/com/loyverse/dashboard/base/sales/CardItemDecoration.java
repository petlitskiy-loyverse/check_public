package com.loyverse.dashboard.base.sales;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyverse.dashboard.R;

public class CardItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable header;
    private Drawable body;
    private Drawable footer;
    private Rect drawablePadding = new Rect();

    public CardItemDecoration(Context context) {
        header = context.getResources().getDrawable(R.drawable.cardview_up);
        footer = context.getResources().getDrawable(R.drawable.cardview_down);
        body = context.getResources().getDrawable(R.drawable.cardview_middle);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        int adapterItemCount = adapter.getItemCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int top = child.getTop();
            int bottom = child.getBottom();

            final Drawable drawable;
            if (parent.getChildAdapterPosition(child) == 0) {
                drawable = header;
            } else if (parent.getChildAdapterPosition(child) == adapterItemCount - 1) {
                drawable = footer;
            } else {
                drawable = body;
            }
            drawable.getPadding(drawablePadding);
            drawable.setBounds(
                    left - drawablePadding.left,
                    top - drawablePadding.top,
                    right + drawablePadding.right,
                    bottom + drawablePadding.bottom
            );
            drawable.draw(c);
        }
        super.onDraw(c, parent, state);
    }
}
