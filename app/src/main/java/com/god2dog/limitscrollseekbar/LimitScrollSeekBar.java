package com.god2dog.limitscrollseekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static com.god2dog.limitscrollseekbar.SizeUtils.dp2px;
import static com.god2dog.limitscrollseekbar.SizeUtils.sp2px;


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
    private Paint mBarPaint;
    private Paint mProgressBackgroundPaint;
    private Paint mFinishedPaint;


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


    float material = 0;
    ValueAnimator anim;
    private boolean isAlwaysShowBubble = false;

    private Paint mTextPaint;
    //背景图片
    private int img;
    private Bitmap bitmap;
    private float imgWidth, imgHeight;

    private int arrowImg;
    private Bitmap arrowBitmap;
    private float arrowWidth,arrowHeight;

    private Paint mBottomTextPaint;
    //文本字体颜色
    private int mTitleTextColor;
    //文本字体大小
    private float mTitleTextSize;
    //文本内容
    private String mTitleText;

    private float textCenterX;

    private float textBaselineY;

    private Paint.FontMetrics fm;
    private Rect seekRect;
    private int mLineBackgroundHeight;

    private Rect mRectText;
    private int mTextSpace;

    private int mCircleCenterX;
    private int mCircleCenterY;

    private int mBubbleMarginBottom;

    private OnProgressChangedListener listener;

    public void setListener(OnProgressChangedListener listener) {
        this.listener = listener;
    }

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

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LimitScrollSeekBar);
        isAlwaysShowBubble = ta.getBoolean(R.styleable.LimitScrollSeekBar_isShowBubble, false);
        img = ta.getResourceId(R.styleable.LimitScrollSeekBar_bubbleImg, R.mipmap.ic_launcher);
        arrowImg = ta.getResourceId(R.styleable.LimitScrollSeekBar_arrowImageSrc, R.mipmap.ic_launcher);
        mTitleTextSize = ta.getDimensionPixelSize(R.styleable.LimitScrollSeekBar_titleTextSize, sp2px(12));
        mTitleTextColor = ta.getColor(R.styleable.LimitScrollSeekBar_titleTextColor, 0xFFFFFFFF);
        mLineBackgroundHeight = ta.getDimensionPixelSize(R.styleable.LimitScrollSeekBar_lineHeight, dp2px(12));
        mBubbleMarginBottom = ta.getDimensionPixelSize(R.styleable.LimitScrollSeekBar_bubbleMarginBottom, dp2px(15));
        ta.recycle();

        getImgWH();


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTitleTextSize);
        mTextPaint.setColor(mTitleTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBottomTextPaint = new Paint();
        mBottomTextPaint.setAntiAlias(true);
        mBottomTextPaint.setTextSize(sp2px(9));
        mBottomTextPaint.setColor(0xFF959595);
//        //设置控件的padding 给提示文字留出位置
//        setPadding((int) Math.ceil(imgWidth) / 2, 0, (int) Math.ceil(imgHeight) / 2, (int) Math.ceil(imgHeight) + 10);

        mRectText = new Rect();
        mTextSpace = dp2px(2);

    }

    public LimitScrollSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);


    }

    private void getImgWH() {
        bitmap = BitmapFactory.decodeResource(getResources(), img);
        arrowBitmap = BitmapFactory.decodeResource(getResources(),arrowImg);
        imgWidth = bitmap.getWidth();
        imgHeight = bitmap.getHeight();

        arrowHeight = arrowBitmap.getHeight();
        arrowWidth = arrowBitmap.getWidth();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBottomText(canvas);
        //定位文本位置
        setTextLocation();
        //定位背景图片的位置
        int offest = (mLineWidth * mCurrentProgress / 100);


        float arrow_x = mCircleCenterX+offest - arrowWidth /  2;
        float arrow_y = mCircleCenterY - mHeight / 2 - mBubbleMarginBottom / 2- arrowHeight;


        float bm_x = arrow_x  + arrowWidth / 2 - imgWidth / 2;
        float bm_y = arrow_y - imgHeight;

        //计算文字的中心位置在bitmap
        float text_x = bm_x + imgWidth / 2;

        canvas.drawBitmap(arrowBitmap,arrow_x,arrow_y,mTextPaint);

        if (bm_x >= 0 && bm_x <= viewWidth - imgWidth) {
            //画背景图
            canvas.drawBitmap(bitmap, bm_x, bm_y, mTextPaint);
            //画文字
            canvas.drawText(mTitleText, text_x, textBaselineY, mTextPaint);
        }else if (bm_x < 0){
            canvas.drawBitmap(bitmap,0,bm_y,mTextPaint);
            canvas.drawText(mTitleText,imgWidth / 2,textBaselineY,mTextPaint);
        }else if (bm_x > viewWidth - imgWidth){
            canvas.drawBitmap(bitmap,viewWidth - imgWidth,bm_y,mTextPaint);
            canvas.drawText(mTitleText,viewWidth-imgWidth / 2,textBaselineY,mTextPaint);
        }


        mFinishedLine.set(mLineLeft, mLineTop, offest + mWidth, mLineBottom);
        canvas.drawRoundRect(line, mLineCorners, mLineCorners, mProgressBackgroundPaint);

        canvas.drawRoundRect(mFinishedLine, mLineCorners, mLineCorners, mFinishedPaint);

        canvas.save();
        canvas.translate(offest, 0);
        drawDefault(canvas);
        canvas.restore();

    }

    private void drawBottomText(Canvas canvas) {
        float x;
        for (int i = 0; i < 6; i++) {
            if (i == 5) {
                x = mLineWidth;
            } else {
                x = mCircleCenterX + mLineWidth / 5 * i;
            }
            canvas.drawText(20 * i + "", x, getMeasuredHeight() - mRectText.height(), mBottomTextPaint);
        }
    }


    private void setTextLocation() {
        fm = mTextPaint.getFontMetrics();
        mTitleText = "拿出" + mCurrentProgress + "元做活动";
        //计算baseline
        textCenterX = imgWidth / 2;
        textBaselineY = bitmap.getHeight() / 2  +mBubbleMarginBottom / 2;
    }

    private void drawDefault(Canvas canvas) {
        int radius = (int) (mWidth * 0.5f);
        // 绘制Shadow
        mBarPaint.setStyle(Paint.Style.FILL);
        int barShadowRadius = (int) (radius * 0.95f);
        canvas.save();
        canvas.translate(0, radius * 0.25f);
        canvas.scale(1 + (0.1f * material), 1 + (0.1f * material), mCircleCenterX, mCircleCenterY);
        RadialGradient shadowGradient = new RadialGradient(mCircleCenterX, mCircleCenterY, barShadowRadius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mBarPaint.setShader(shadowGradient);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, barShadowRadius, mBarPaint);
        mBarPaint.setShader(null);
        canvas.restore();
        // 绘制Body
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(0xFFFA584A);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, radius, mBarPaint);
        // 绘制Border
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setColor(0xFFD7D7D7);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, radius, mBarPaint);

    }

    public void setProgress(int progress) throws Exception {
        if (progress < 0 ||progress >100){
           throw new Exception("Please set the progress from 0 to 100");
        }
        mCurrentProgress = progress;

        if (listener != null){
            listener.onProgressChanged(this,mCurrentProgress,mCurrentProgress/100f,false);
        }
    }

    public void plusProgress(){
        mCurrentProgress ++;
        if (mCurrentProgress >=100){
            mCurrentProgress = 100;
        }
        if (listener != null){
            listener.onProgressChanged(this,mCurrentProgress,mCurrentProgress/100f,false);
        }
        invalidate();
    }

    public void minusProgress(){
        mCurrentProgress--;
        if (mCurrentProgress <= 0){
            mCurrentProgress = 0;
        }
        if (listener != null){
            listener.onProgressChanged(this,mCurrentProgress,mCurrentProgress/100f,false);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTextPaint.setTextSize(mTitleTextSize);
        mTextPaint.getTextBounds("j", 0, 1, mRectText);
        viewWidth = measureWidth(getSuggestedMinimumWidth(), widthMeasureSpec);
        int viewHeight = (int) (mLineBackgroundHeight * 2 + imgHeight + mRectText.height() + mTextSpace * 2 + mBubbleMarginBottom);

        setMeasuredDimension(viewWidth, viewHeight);
        int lineCenterY = viewHeight - mRectText.height() - mTextSpace * 2 - mLineBackgroundHeight;
        mLineTop = lineCenterY - mLineBackgroundHeight / 2;
        Log.i(TAG, "onMeasure: mLineTop" + mLineTop);
        mLineBottom = lineCenterY + mLineBackgroundHeight / 2;
        Log.i(TAG, "onMeasure: mLineBottom" + mLineBottom);
        mLineLeft = (int) (imgHeight / 2 + arrowWidth /2);
        mLineRight = (int) (viewWidth - (imgHeight / 2 + arrowWidth /2));
        mLineWidth = mLineRight - mLineLeft;
        Log.i(TAG, "onSizeChanged: " + mLineWidth);
        line.set(mLineLeft, mLineTop, mLineRight, mLineBottom);
        mLineCorners = (int) ((mLineBottom - mLineTop) * 0.5f);

        mHeight = mWidth = (int) (mLineBackgroundHeight * 1.5);
        mCircleCenterY = lineCenterY;
        mCircleCenterX = (int) (imgHeight / 2 + arrowWidth /2);
        left = mCircleCenterX - mWidth / 2;
        right = mCircleCenterX + mWidth / 2;
        top = mCircleCenterY - mHeight / 2;
        bottom = mCircleCenterY + mHeight / 2;
    }

    private int measureWidth(int defaultWidth, int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            default:
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.AT_MOST:
                defaultWidth = 2 * specSize / 3;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
                break;

        }
        return defaultWidth;
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
                material = material >= 1 ? 1 : material + 0.1f;
                x = event.getX();
                if (x <= mLineLeft) {
                    mCurrentProgress = 0;
                } else if (x >= mLineRight) {
                    mCurrentProgress = 100;
                } else {
                    mCurrentProgress = (int) ((x - mLineLeft) / mLineWidth * 100);
                }
                if (listener != null){
                    listener.onProgressChanged(this,mCurrentProgress,mCurrentProgress/100f,true);
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

    /**
     * Listen to progress onChanged,  onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(LimitScrollSeekBar seekBar, int progress, float progressPercent, boolean fromUser);
    }

}
