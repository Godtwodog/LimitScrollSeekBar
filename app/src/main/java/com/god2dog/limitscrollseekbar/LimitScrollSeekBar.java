package com.god2dog.limitscrollseekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 * 自定义seekbar
 * 1、底部带有指示器文字
 * 2、限制大小滑动
 * 3、带有顶部弹窗
 *
 * @author Administrator
 */
public class LimitScrollSeekBar extends View {
    private String TAG = "LimitScrollSeekBar";
    private Context mContext;
    private Paint mBarPaint;
    private Paint mProgressBackgroundPaint;
    private Paint mFinishedPaint;
    private Paint mTextPaint;

    private int mMaxProgress = 100;
    private int mMinProgress = 0;

    private int mCurrentProgress;
//    private float mCurrentPercent;

    private int viewWidth;
    private int mLineLeft, mLineRight, mLineTop, mLineBottom;
    private int mLineWidth;

    private int left, right, top, bottom;
    private int mWidth;
    private int mHeight;

    private RectF line = new RectF();
    private RectF mFinishedLine = new RectF();
    private int mLineCorners;

    private Bitmap bitmap;
    float material = 0;
    ValueAnimator anim;


    public LimitScrollSeekBar(Context context) {
        super(context);
    }

    public LimitScrollSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mProgressBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBackgroundPaint.setStyle(Paint.Style.FILL);
        mProgressBackgroundPaint.setColor(0xFFD7D7D7);

        mFinishedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFinishedPaint.setStyle(Paint.Style.FILL);
        mFinishedPaint.setColor(0xFFFD444A);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);
    }

    public LimitScrollSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offest = (mLineWidth * mCurrentProgress / 100);

        mFinishedLine.set(mLineLeft,mLineTop,offest+mWidth,mLineBottom);
        Log.i(TAG, "onDraw: mLineLeft"+mLineLeft);
        Log.i(TAG, "onDraw: mLineTop"+mLineTop);
        Log.i(TAG, "onDraw: mLineBottom"+mLineBottom);
        Log.i(TAG, "onDraw: offest"+offest);
        canvas.drawRoundRect(line, mLineCorners, mLineCorners, mProgressBackgroundPaint);

        canvas.drawRoundRect(mFinishedLine,mLineCorners,mLineCorners,mFinishedPaint);

        canvas.save();
        canvas.translate(offest, 0);
        drawDefault(canvas);
        canvas.restore();

    }

    private void drawDefault(Canvas canvas) {
        int centerX = mWidth;
        int centerY = mHeight / 2;
        int radius = (int) (mWidth * 0.5f);
        // 绘制Shadow
        mBarPaint.setStyle(Paint.Style.FILL);
        int barShadowRadius = (int) (radius * 0.95f);
        canvas.save();
        canvas.translate(0, radius * 0.25f);
        canvas.scale(1 + (0.1f * material), 1 + (0.1f * material), centerX, centerY);
        RadialGradient shadowGradient = new RadialGradient(centerX, centerY, barShadowRadius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mBarPaint.setShader(shadowGradient);
        canvas.drawCircle(centerX, centerY, barShadowRadius, mBarPaint);
        mBarPaint.setShader(null);
        canvas.restore();
        // 绘制Body
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(0xFFFA584A);
        canvas.drawCircle(centerX, centerY, radius, mBarPaint);
        // 绘制Border
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setColor(0xFFD7D7D7);
        canvas.drawCircle(centerX, centerY, radius, mBarPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightSize * 2 > widthSize) {
            setMeasuredDimension(widthSize, (int) (widthSize / 2));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        int seekBarRadius = heightSize / 2;
        mLineLeft = seekBarRadius;
        mLineRight = widthSize - seekBarRadius;
        mLineTop = seekBarRadius - seekBarRadius / 4;
        mLineBottom = seekBarRadius + seekBarRadius / 4;
        mLineWidth = mLineRight - mLineLeft;
        Log.i(TAG, "onSizeChanged: " + mLineWidth);
        line.set(mLineLeft, mLineTop, mLineRight, mLineBottom);
        mLineCorners = (int) ((mLineBottom - mLineTop) * 0.5f);

        mWidth = (int) (heightSize * 0.5);
        mHeight = heightSize;
        Log.i(TAG, "onMeasure: " + mWidth);
        left = seekBarRadius - widthSize / 2;
        right = seekBarRadius + widthSize / 2;
        top = seekBarRadius - heightSize / 2;
        bottom = seekBarRadius + heightSize / 2;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x;
        float y;
        switch (event.getAction()) {
            default:
                break;
            case MotionEvent.ACTION_DOWN:
                boolean isTouchInside = false;
                x = event.getX();
                y = event.getY();
                int offest = mLineWidth * mCurrentProgress / 100;
                if (x > left + offest && x < right + offest && y > top && y < bottom) {
                    isTouchInside = true;
//                    Toast.makeText(mContext, "here", Toast.LENGTH_SHORT).show();
                }
                return isTouchInside;
            case MotionEvent.ACTION_MOVE:
                material = material >=1 ? 1:material +0.1f;
                x = event.getX();
                if (x <= mLineLeft) {
                    mCurrentProgress = 0;
                } else if (x >= mLineRight) {
                    mCurrentProgress = 100;
                } else {
                    mCurrentProgress = (int) ((x - mLineLeft) / mLineWidth * 100);
                }
                Log.i(TAG, "onTouchEvent: " + mCurrentProgress);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                materialRestore();
                break;
        }

        return super.onTouchEvent(event);
    }

    private void materialRestore() {
        if (anim != null) {
            anim.cancel();
        }
        anim = ValueAnimator.ofFloat(material, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                material = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                material = 0;
                invalidate();
            }
        });
        anim.start();
    }

}
