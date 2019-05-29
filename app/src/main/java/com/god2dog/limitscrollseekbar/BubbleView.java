package com.god2dog.limitscrollseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import static com.god2dog.limitscrollseekbar.SizeUtils.dp2px;
import static com.god2dog.limitscrollseekbar.SizeUtils.sp2px;

class BubbleView extends View {
    private Context mContext;
    private Paint mBubblePaint;
    private Path mBubblePath;
    private RectF mBubbleRectF;
    private Rect mRect;
    private String mProgressText = "";
    private int mBubbleRadius;
    private int mBubbleColor;
    private int mBubbleTextSize;
    private int mBubbleTextColor;

    BubbleView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setTextAlign(Paint.Align.CENTER);

        mBubblePath = new Path();
        mBubbleRectF = new RectF();
        mRect = new Rect();

        TypedArray ta =context.obtainStyledAttributes(attrs,R.styleable.BubbleView,defStyleAttr,0);
        mBubbleColor = ta.getColor(R.styleable.BubbleView_bubbleColor,0xFFFA584A);
        mBubbleTextSize = ta.getDimensionPixelSize(R.styleable.BubbleView_bubbleTextSize,sp2px(12));

        ta.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius);

        mBubbleRectF.set(getMeasuredWidth() / 2f - mBubbleRadius, 0,
                getMeasuredWidth() / 2f + mBubbleRadius, 2 * mBubbleRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBubblePath.reset();
        float x0 = getMeasuredWidth() / 2f;
        float y0 = getMeasuredHeight() - mBubbleRadius / 3f;
        mBubblePath.moveTo(x0, y0);
        float x1 = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * mBubbleRadius);
        float y1 = 3 / 2f * mBubbleRadius;
        mBubblePath.quadTo(
                x1 - dp2px(2), y1 - dp2px(2),
                x1, y1
        );
        mBubblePath.arcTo(mBubbleRectF, 150, 240);

        float x2 = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * mBubbleRadius);
        mBubblePath.quadTo(
                x2 + dp2px(2), y1 - dp2px(2),
                x0, y0
        );
        mBubblePath.close();

        mBubblePaint.setColor(mBubbleColor);
        canvas.drawPath(mBubblePath, mBubblePaint);

        mBubblePaint.setTextSize(mBubbleTextSize);
        mBubblePaint.setColor(mBubbleTextColor);
        mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length(), mRect);
        Paint.FontMetrics fm = mBubblePaint.getFontMetrics();
        float baseline = mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent;
        canvas.drawText(mProgressText, getMeasuredWidth() / 2f, baseline, mBubblePaint);
    }

    void setProgressText(String progressText) {
        if (progressText != null && !mProgressText.equals(progressText)) {
            mProgressText = progressText;
            invalidate();
        }
    }
}