package com.loyverse.dashboard.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.loyverse.dashboard.R;

public class BoundedLinearLayout extends LinearLayout {
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;

    public BoundedLinearLayout(Context context) {
        super(context);
    }

    public BoundedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BoundedView);
            maxWidth = a.getDimensionPixelSize(R.styleable.BoundedView_boundedWidth, Integer.MAX_VALUE);
            maxHeight = a.getDimensionPixelSize(R.styleable.BoundedView_boundedHeight, Integer.MAX_VALUE);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                Math.min(
                        MeasureSpec.getSize(widthMeasureSpec),
                        maxWidth
                ),
                MeasureSpec.getMode(widthMeasureSpec)
        );
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                Math.min(
                        MeasureSpec.getSize(heightMeasureSpec),
                        maxHeight
                ),
                MeasureSpec.getMode(heightMeasureSpec)
        );
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }
}