package com.god2dog.limitscrollseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * 自定义seekbar
 * 1、底部带有指示器文字
 * 2、限制大小滑动
 * 3、带有顶部弹窗
 * @author Administrator
 */
public class LimitScrollSeekBar extends View {
    private Context mContext ;

    private int mMaxProgress = 100;
    private int mMinProgress = 0;
    private int mLimitMaxProgress = 80;
    private int mLimitMinProgress = 1;
    private float mProgress;
    private float mLastProgress;

    private int mHeight;
    private int mWidth;

    private Paint mBarPaint;
    private Paint mTextPaint;

    private int mLineTop, mLineRight,mLineLeft,mLineBottom;
    private int mLineCorners;
    private int mLineWidth;
    private int mLineHeight;
    private RectF line = new RectF();




    public LimitScrollSeekBar(Context context) {
       this(context,null);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LimitScrollSeekBar);
        mLimitMaxProgress = ta.getInt(R.styleable.LimitScrollSeekBar_limit_max_progress,100);
        mLimitMinProgress = ta.getInt(R.styleable.LimitScrollSeekBar_limit_min_progress,0);



    }

    public LimitScrollSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttrs(context,attrs);
    }

    public LimitScrollSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    public void plusProgress(){

    }

    public void minusProgress(){

    }

    private void initProgressRangeValue() {
        if (mLimitMaxProgress < mLimitMinProgress) {
            throw new IllegalArgumentException("the Argument: MAX's value must be larger than MIN's.");
        }
        if (mProgress < mLimitMinProgress) {
            mProgress = mLimitMinProgress;
        }
        if (mProgress > mLimitMaxProgress) {
            mProgress = mLimitMaxProgress;
        }
    }

    private void initPaint(){
        if (mBarPaint ==null){
            mBarPaint = new Paint();
        }
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(0xFFD7D7D7);
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int seekBarRadius = heightSize / 2 ;
        mLineLeft = seekBarRadius;
        mLineRight = widthSize -seekBarRadius;
        mLineTop = seekBarRadius - seekBarRadius /4;
        mLineBottom = seekBarRadius + seekBarRadius / 4;
        mLineWidth = mLineRight - mLineLeft;
        line.set(mLineLeft,mLineTop,mLineRight,mLineBottom);
        mLineCorners = (int) ((mLineBottom - mLineTop)*0.45f);

        if (heightSize * 2 > widthSize){
            setMeasuredDimension(widthSize,widthSize / 2 );
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initPaint();
        canvas.drawRoundRect(line,mLineCorners,mLineCorners,mBarPaint);
    }
}
