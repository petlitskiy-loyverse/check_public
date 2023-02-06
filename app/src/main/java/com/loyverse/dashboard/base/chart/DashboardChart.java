package com.loyverse.dashboard.base.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.annotation.Keep;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.loyverse.dashboard.R;

public class DashboardChart extends View {
    private final static float LEFT_START_ANGLE = 120;
    private final static float RIGHT_END_ANGLE = 420;
    private final static float CIRCLE_ANGLE = 270;
    private final static float DELIMITER_SPACE_ANGLE = 10;
    private final static float CANVAS_PADDING = 10;
    private final static float INDICATOR_TEXT_PADDING = 10;
    private final static float MAGIC_MULTIPLIER = 0.9F; // Don't touch this, it's pure magic

    //    DEFAULT VALUES
    private final static float DEFAULT_VALUE = 50;
    private final static int DEFAULT_COLOR_LEFT = Color.YELLOW;
    private final static int DEFAULT_COLOR_RIGHT = Color.BLUE;
    private final static int DEFAULT_COLOR_POINT = Color.BLUE;
    private final static int DEFAULT_COLOR_ARC_BACKGROUND = Color.GRAY;
    private final static int DEFAULT_INDICATOR_TEXT_SIZE = 22;
    private final static boolean DEFAULT_INDICATOR_TEXT_AUTOSIZE = false;
    private final static int DEFAULT_INDICATOR_TEXT_COLOR = Color.BLACK;
    private final static int DEFAULT_THICKNESS = 8;
    private final static int DEFAULT_POINTER_RADIUS = 8;
    private final static TextUtils.TruncateAt DEFAULT_TRUNCATE_AT = TextUtils.TruncateAt.END;

    private final static int DEFAULT_HEIGHT = 100;
    private final static int DEFAULT_WIDTH = 100;


    private float value;
    private int colorLeft;
    private int colorRight;
    private int colorArcBackground;
    private int colorPoint;
    private String indicatorText;
    private String ellipsizedIndicatorText;
    private boolean indicatorTextSizeAutoSize;
    private float indicatorTextSize;
    private float optimizedIndicatorTextSize;
    private int indicatorTextColor;
    private int thickness;
    private int pointerRadius;

    private boolean isInitialized = false;

    private RenderDimensions renderDimensions = new RenderDimensions();


    public DashboardChart(Context context) {
        super(context);
    }

    public DashboardChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DashboardChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private static float calculateTextSizeByWidth(TextPaint paint, float textSize, float desiredWidth,
                                                  String text) {
        TextPaint newPaint = new TextPaint(paint);
        newPaint.setTextSize(textSize);
        return MAGIC_MULTIPLIER * newPaint.getTextSize() * (desiredWidth / newPaint.measureText(text));
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.DashboardChart, 0, 0);
        try {
            setValueWithChecking(a.getFloat(R.styleable.DashboardChart_chartValue, DEFAULT_VALUE));
            colorLeft = a.getColor(R.styleable.DashboardChart_colorLeft, DEFAULT_COLOR_LEFT);
            colorRight = a.getColor(R.styleable.DashboardChart_colorRight, DEFAULT_COLOR_RIGHT);
            colorPoint = a.getColor(R.styleable.DashboardChart_colorPoint, DEFAULT_COLOR_POINT);
            colorArcBackground = a.getColor(R.styleable.DashboardChart_colorArcBackground, DEFAULT_COLOR_ARC_BACKGROUND);
            indicatorTextColor = a.getColor(R.styleable.DashboardChart_indicator_textColor,
                    DEFAULT_INDICATOR_TEXT_COLOR);
            indicatorTextSize = a.getDimensionPixelSize(
                    R.styleable.DashboardChart_indicator_textSize, DEFAULT_INDICATOR_TEXT_SIZE);
            indicatorTextSizeAutoSize = a.getBoolean(R.styleable.DashboardChart_indicator_textAutoSize,
                    DEFAULT_INDICATOR_TEXT_AUTOSIZE);
            thickness = a.getDimensionPixelSize(R.styleable.DashboardChart_thickness, DEFAULT_THICKNESS);
            pointerRadius = a.getDimensionPixelSize(R.styleable.DashboardChart_pointer_radius,
                    DEFAULT_POINTER_RADIUS);
            CharSequence indicatorTextTemp = a.getText(R.styleable.DashboardChart_indicator_text);
            if (indicatorTextTemp != null) {
                indicatorText = indicatorTextTemp.toString();
            }
        } finally {
            a.recycle();
        }
    }

    private void initRenderDimensions(float width, float height) {
        float horizontalPadding;
        float verticalPadding;

        if (width > height) {
            horizontalPadding = (width - height) / 2;
            verticalPadding = 0;
        } else {
            verticalPadding = (height - width) / 2;
            horizontalPadding = 0;
        }

        RectF rectF = new RectF();
        rectF.set(
                horizontalPadding + CANVAS_PADDING + pointerRadius / 2F,
                verticalPadding + CANVAS_PADDING + pointerRadius / 2F,
                width - CANVAS_PADDING - horizontalPadding - pointerRadius / 2F,
                height - CANVAS_PADDING - verticalPadding - pointerRadius / 2F
        );
        renderDimensions.ellipseBox = rectF;

        final float circleAngleInRadian = (float) convertToRadian(CIRCLE_ANGLE);
        float ellipseRadius = calculateEllipseRadius(
                renderDimensions.ellipseBox.width(),
                renderDimensions.ellipseBox.height(),
                circleAngleInRadian);

        PointF ellipseCenter = new PointF(width / 2, height / 2);
        renderDimensions.circleCenter = calculateCircleCenter(
                ellipseCenter,
                ellipseRadius,
                circleAngleInRadian);

        updateGraphicData(renderDimensions, width, height);
        updateIndicatorTextData(renderDimensions, width, height);

        isInitialized = true;
    }

    private void updateGraphicData(RenderDimensions renderDimensions, float width, float height) {
        final float circleLeftAngle = CIRCLE_ANGLE-DELIMITER_SPACE_ANGLE;
        final float circleRightAngle = CIRCLE_ANGLE+DELIMITER_SPACE_ANGLE;
        final float valueAngle = calculateValuePointAngle();

        renderDimensions.leftPartStartAngel = LEFT_START_ANGLE;
        renderDimensions.rightPartStartAngle = circleRightAngle;
        if (valueAngle < CIRCLE_ANGLE){
            renderDimensions.leftPartEndAngle = calculateArcEndAngle(renderDimensions.leftPartStartAngel, Math.min(circleLeftAngle, valueAngle));
            renderDimensions.leftPartBackgroundStartAngle = renderDimensions.leftPartStartAngel+renderDimensions.leftPartEndAngle;
            renderDimensions.leftPartBackgroundEndAngle = calculateArcEndAngle(renderDimensions.leftPartBackgroundStartAngle, circleLeftAngle);

            renderDimensions.rightPartEndAngle = 0;
            renderDimensions.rightPartBackgroundStartAngle = renderDimensions.rightPartStartAngle;
            renderDimensions.rightPartBackgroundEndAngle = calculateArcEndAngle(renderDimensions.rightPartBackgroundStartAngle, RIGHT_END_ANGLE);
        } else {
            renderDimensions.leftPartEndAngle = calculateArcEndAngle(renderDimensions.leftPartStartAngel,circleLeftAngle);
            renderDimensions.leftPartBackgroundStartAngle = circleLeftAngle;
            renderDimensions.leftPartBackgroundEndAngle = 0;

            renderDimensions.rightPartEndAngle = calculateArcEndAngle(renderDimensions.rightPartStartAngle, valueAngle);
            renderDimensions.rightPartBackgroundStartAngle = Math.max(valueAngle,renderDimensions.rightPartStartAngle); //DO NOT TOUCH IT! IT IS RUNTIME FIX WHEN valueAngle == CIRCLE_ANGLE
            renderDimensions.rightPartBackgroundEndAngle = calculateArcEndAngle(renderDimensions.rightPartBackgroundStartAngle, RIGHT_END_ANGLE);
        }
    }

    private void updateIndicatorTextData(RenderDimensions renderDimensions, float width, float height) {
        double angle = convertToRadian(calculateValuePointAngle());
        float ellipseRadius = calculateEllipseRadius(
                renderDimensions.ellipseBox.width(),
                renderDimensions.ellipseBox.height(),
                angle);

        optimizedIndicatorTextSize = indicatorTextSize;
        RectF box = calculateIndicatorBoundingBox(indicatorText, ellipseRadius, width,
                height, getTextPaint());
        renderDimensions.indicatorBoundingBox = box;
        if (indicatorTextSizeAutoSize) {
            optimizedIndicatorTextSize = calculateTextSizeByWidth(getTextPaint(), indicatorTextSize,
                    box.width(), indicatorText);
        }
        ellipsizedIndicatorText = (String) TextUtils.ellipsize(indicatorText, getTextPaint(),
                renderDimensions.indicatorBoundingBox.width(), DEFAULT_TRUNCATE_AT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            height = heightSize;
        } else if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        } else if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            int size = Math.max(widthSize, heightSize);
            width = size;
            height = size;
        } else {
            int size = Math.min(widthSize, heightSize);
            width = Math.min(size, widthSize);
            height = Math.min(size, heightSize);
        }

        setMeasuredDimension(width, height);
        //init RenderDimension object
        initRenderDimensions(width, height);
    }

    private float calculateArcEndAngle(float startAngle, float endAngle){
        return Math.max(endAngle-startAngle, 0);
    }

    private float calculateValuePointAngle() {
        //TODO: Fix returning wrong angle, when width != height
        float percentageValue = (float) (value / 100.0);
        return (RIGHT_END_ANGLE - LEFT_START_ANGLE) * percentageValue + LEFT_START_ANGLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(renderDimensions.ellipseBox, renderDimensions.leftPartStartAngel,
                renderDimensions.leftPartEndAngle, false, getLeftPaint());
        canvas.drawArc(renderDimensions.ellipseBox, renderDimensions.leftPartBackgroundStartAngle,
                renderDimensions.leftPartBackgroundEndAngle, false, getBackgroundPaint());
        canvas.drawArc(renderDimensions.ellipseBox, renderDimensions.rightPartStartAngle,
                renderDimensions.rightPartEndAngle, false, getRightPaint());
        canvas.drawArc(renderDimensions.ellipseBox, renderDimensions.rightPartBackgroundStartAngle,
                renderDimensions.rightPartBackgroundEndAngle, false, getBackgroundPaint());

        canvas.drawCircle(renderDimensions.circleCenter.x, renderDimensions.circleCenter.y,
                pointerRadius, getCirclePaint());

        if (indicatorText != null) {
            canvas.drawText(ellipsizedIndicatorText, renderDimensions.indicatorBoundingBox.centerX(),
                    renderDimensions.indicatorBoundingBox.top, getTextPaint());
        }
    }

    /**
     * @param center center of main circle
     * @param radius radius of main circle
     * @param angle An angle in radian. Be sure, that your value in radian.
     * @return
     */
    private PointF calculateCircleCenter(PointF center, float radius, double angle) {
        float x = center.x + (float) (radius * Math.cos(angle));
        float y = center.y + (float) (radius * Math.sin(angle));
        return new PointF(x, y);
    }

    private double convertToRadian(float degree) {
        return degree * Math.PI / 180F;
    }

    private float calculateEllipseRadius(float width, float height, double angle) {
        final float a = width / 2;
        final float b = height / 2;
        double part1 = (Math.pow(a, 2) * Math.pow(Math.sin(angle), 2));
        double part2 = (Math.pow(b, 2) * Math.pow(Math.cos(angle), 2));
        double result = (a * b / Math.sqrt(part1 + part2));
        return (float) result;
    }

    private PointF calculateIndicatorTextPoint(String text, float width, float height, Paint paint) {
        PointF point = new PointF();
        point.x = (width - paint.measureText(text)) / 2;
        point.y = (height - paint.descent() - paint.ascent()) / 2;
        return point;
    }

    private double calculateDistanceBetweenPoints(PointF a, PointF b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    private RectF calculateIndicatorBoundingBox(String text, float radius, float width, float height, Paint paint) {
        PointF startPoint = calculateIndicatorTextPoint(text, width, height, paint);
        PointF center = new PointF(width / 2, height / 2);

        RectF box = new RectF();
        box.top = startPoint.y;
        box.bottom = startPoint.y + paint.descent() + paint.ascent();

        double distance = calculateDistanceBetweenPoints(center, startPoint);
        if (distance >= radius - INDICATOR_TEXT_PADDING) {
            float difference = 2 * (radius - INDICATOR_TEXT_PADDING);
            box.left = (width - difference) / 2;
            box.right = box.left + difference;
        } else {
            box.left = startPoint.x;
            box.right = startPoint.x + paint.measureText(indicatorText);
        }

        return box;
    }

    private Paint getBasicPaint() {
        Paint paint = new Paint();
        paint.setStrokeWidth(thickness);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getLeftPaint() {
        Paint paint = getBasicPaint();
        paint.setColor(colorLeft);
        return paint;
    }

    private Paint getRightPaint() {
        Paint paint = getBasicPaint();
        paint.setColor(colorRight);
        return paint;
    }

    private Paint getBackgroundPaint() {
        Paint paint = getBasicPaint();
        paint.setColor(colorArcBackground);
        return paint;
    }

    private Paint getCirclePaint() {
        Paint paint = getRightPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorPoint);
        return paint;
    }

    private TextPaint getTextPaint() {
        TextPaint paint = new TextPaint(getBasicPaint());
        paint.setTextSize(optimizedIndicatorTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(indicatorTextColor);
        return paint;
    }

    private void invalidateData() {
        if (!isInitialized)
            return;

        invalidate();
        requestLayout();
    }

    private void invalidateGraphicData() {
        if (!isInitialized)
            return;

        updateGraphicData(renderDimensions, getWidth(), getHeight());
        invalidate();
    }

    private void invalidateIndicatorTextData() {
        if (!isInitialized)
            return;

        updateIndicatorTextData(renderDimensions, getWidth(), getHeight());
        invalidate();
    }

    public float getValue() {
        return value;
    }

    @Keep
    public void setValue(float value) {
        setValueWithChecking(value);
        invalidateGraphicData();
    }

    private void setValueWithChecking(float value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("The value mast be in the range from 0 to 100");
        }
        this.value = value;
    }

    public int getColorLeft() {
        return colorLeft;
    }

    public void setColorLeft(int colorLeft) {
        this.colorLeft = colorLeft;
        invalidate();
    }


    public int getColorArcBackground() {
        return colorArcBackground;
    }

    public void setColorArcBackground(int colorArcBackground) {
        this.colorArcBackground = colorArcBackground;
        invalidate();
    }


    public int getColorRight() {
        return colorRight;
    }

    public void setColorRight(int colorRight) {
        this.colorRight = colorRight;
        invalidate();
    }

    public int getColorPoint() {
        return colorPoint;
    }

    public void setColorPoint(int colorPoint) {
        this.colorPoint = colorPoint;
        invalidate();
    }

    public String getIndicatorText() {
        return indicatorText;
    }

    public void setIndicatorText(String indicatorText) {
        this.indicatorText = indicatorText;
        invalidateIndicatorTextData();
    }

    public float getIndicatorTextSize() {
        return indicatorTextSize;
    }

    public void setIndicatorTextSize(float indicatorTextSize) {
        this.indicatorTextSize = indicatorTextSize;
        invalidateIndicatorTextData();
    }

    public int getIndicatorTextColor() {
        return indicatorTextColor;
    }

    public void setIndicatorTextColor(int indicatorTextColor) {
        this.indicatorTextColor = indicatorTextColor;
        invalidate();
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        invalidate();
    }

    public int getPointerRadius() {
        return pointerRadius;
    }

    public void setPointerRadius(int pointerRadius) {
        this.pointerRadius = pointerRadius;
        invalidateData();
    }

    private static class RenderDimensions {
        RectF ellipseBox;
        float leftPartStartAngel;
        float leftPartEndAngle;
        float leftPartBackgroundStartAngle;
        float leftPartBackgroundEndAngle;

        float rightPartStartAngle;
        float rightPartEndAngle;
        float rightPartBackgroundStartAngle;
        float rightPartBackgroundEndAngle;
        PointF circleCenter;
        RectF indicatorBoundingBox;
    }
}
