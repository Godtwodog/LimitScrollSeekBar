package com.god2dog.limitscrollseekbar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class SeekBar {
    int widthSize;
    int left,top,right,bottom;
    Bitmap bitmap;

    void onSizeChanged(int centerX,int centerY,int heightSize){
        /**
         * 属性 left right top bottom 描述了SeekBar按钮的位置<br>
         * widthSize = heightSize * 0.8f 可见按钮实际区域是个矩形而非正方形
         * 圆圈按钮为什么要占有矩形区域？因为按钮阴影效果。不要阴影不行吗？我就不
         * 那么 onMeasure 那边说好的2倍宽度？我就不
         */
        widthSize = (int) (heightSize * 0.8f);
        left = centerX - widthSize / 2;
        right = centerX + widthSize / 2;
        top = centerY - heightSize / 2;
        bottom = centerY + heightSize / 2;

        bitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.ARGB_8888);
        int bmpCenterX = bitmap.getWidth() / 2;
        int bmpCenterY = bitmap.getHeight() / 2;
        int bmpRadius = (int) (widthSize * 0.5f);
        Canvas defaultCanvas = new Canvas(bitmap);
        Paint defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 绘制Shadow
        defaultPaint.setStyle(Paint.Style.FILL);
        int barShadowRadius = (int) (bmpRadius * 0.95f);
        defaultCanvas.save();
        defaultCanvas.translate(0, bmpRadius * 0.25f);
        RadialGradient shadowGradient = new RadialGradient(bmpCenterX, bmpCenterY, barShadowRadius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        defaultPaint.setShader(shadowGradient);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, barShadowRadius, defaultPaint);
        defaultPaint.setShader(null);
        defaultCanvas.restore();
        // 绘制Body
        defaultPaint.setStyle(Paint.Style.FILL);
        defaultPaint.setColor(0xFFFFFFFF);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, bmpRadius, defaultPaint);
        // 绘制Border
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setColor(0xFFD7D7D7);
        defaultCanvas.drawCircle(bmpCenterX, bmpCenterY, bmpRadius, defaultPaint);

    }
    void onDraw(Canvas canvas){
     canvas.drawBitmap(bitmap,left,top,null);
    }
}
