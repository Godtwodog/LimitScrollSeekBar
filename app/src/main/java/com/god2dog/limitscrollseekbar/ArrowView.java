package com.god2dog.limitscrollseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import static com.god2dog.limitscrollseekbar.SizeUtils.dp2px;

public class ArrowView extends View {
    private final int mWidth;
    private final int mHeight;
    private final Path mPath;
    private final Paint mPaint;

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth = dp2px( 12);
        mHeight = dp2px( 7);
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mWidth, 0);
        mPath.lineTo(mWidth / 2f, mHeight);
        mPath.close();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
    }

    void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}
