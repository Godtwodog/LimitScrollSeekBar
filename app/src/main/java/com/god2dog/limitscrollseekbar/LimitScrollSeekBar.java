package com.god2dog.limitscrollseekbar;

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
    private int mLineCorners;

    private Bitmap bitmap;


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

        canvas.drawRoundRect(line, mLineCorners, mLineCorners, mProgressBackgroundPaint);

        int offest =  (mLineWidth * mCurrentProgress / 100);
        Log.i(TAG, "onDraw: "+mCurrentProgress);
        canvas.save();
        canvas.translate(offest,0);
        canvas.drawBitmap(bitmap, left, top, null);
        canvas.restore();

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int seekBarRadius = h / 2;
        mLineLeft = seekBarRadius;
        mLineRight = w - seekBarRadius;
        mLineTop = seekBarRadius - seekBarRadius / 4;
        mLineBottom = seekBarRadius + seekBarRadius / 4;
        mLineWidth = mLineRight - mLineLeft;
        Log.i(TAG, "onSizeChanged: "+mLineWidth);
        line.set(mLineLeft, mLineTop, mLineRight, mLineBottom);
        mLineCorners = (int) ((mLineBottom - mLineTop) * 0.5f);

        mWidth = (int) (h * 0.5);
        mHeight = h;
        left = seekBarRadius - mWidth / 2;
        top = seekBarRadius - mHeight / 2;
        right = seekBarRadius + mWidth / 2;
        bottom = seekBarRadius + mHeight / 2;
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        int bmpCenterX = bitmap.getWidth() / 2;
        int bmpCenterY = bitmap.getHeight() / 2;
        int bmpRadius = (int) (mWidth * 0.5f);
        Canvas defaultCanvas = new Canvas(bitmap);
        // 绘制Shadow
        mBarPaint.setStyle(Paint.Style.FILL);
        int barShadowRadius = (int) (bmpRadius * 0.95f);
        defaultCanvas.save();
        defaultCanvas.translate(0, bmpRadius * 0.25f);
        RadialGradient shadowGradient = new RadialGradient(bmpCenterX, bmpCenterY, barShadowRadius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mBarPaint.setShader(shadowGradient);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, barShadowRadius, mBarPaint);
        mBarPaint.setShader(null);
        defaultCanvas.restore();
        // 绘制Body
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(0xFFFA584A);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, bmpRadius, mBarPaint);
        // 绘制Border
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setColor(0xFFD7D7D7);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, bmpRadius, mBarPaint);

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
                int offest =  mLineWidth * mCurrentProgress / 100;
                if (x > left + offest && x < right + offest && y > top && y < bottom){
                    isTouchInside = true;
                }
                return  isTouchInside;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (x <= mLineLeft){
                    mCurrentProgress = 0;
                }else if (x >= mLineRight){
                    mCurrentProgress = 100;
                }else {
                    mCurrentProgress = (int) ((x - mLineLeft ) / mLineWidth *100);
                }
                Log.i(TAG, "onTouchEvent: "+mCurrentProgress);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }
}
