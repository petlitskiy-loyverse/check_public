package com.loyverse.dashboard.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loyverse.dashboard.R;

public class NinePCardView extends FrameLayout {
    private Drawable cardDrawable;
    private Rect cardDrawablePadding = new Rect();

    public NinePCardView(@NonNull Context context) {
        super(context);
        setUp(context);
    }

    public NinePCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    public NinePCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context);
    }

    public NinePCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(context);
    }

    private void setUp(Context context){
        cardDrawable = context.getResources().getDrawable(R.drawable.cardview);
        cardDrawable.getPadding(cardDrawablePadding);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        cardDrawable.setBounds(
                child.getLeft()- cardDrawablePadding.left,
                child.getTop()-cardDrawablePadding.top,
                child.getRight()+cardDrawablePadding.right,
                child.getBottom()+cardDrawablePadding.bottom
        );
        cardDrawable.draw(canvas);

        return super.drawChild(canvas, child, drawingTime);
    }
}
