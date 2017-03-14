package com.yufa.smell.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.yufa.smell.R;

/**
 * Created by Administrator on 2017/1/16.
 * Bitmap叠加类
 */

public class BitmapAdd {

    private Bitmap add(Bitmap bitmap1, Bitmap bitmap2) {
        // 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap newBitmap = null;
        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();
        int w_2 = bitmap2.getWidth();
        int h_2 = bitmap2.getHeight();
        paint.setColor(Color.GRAY);
        paint.setAlpha(125);
        paint = new Paint();
        canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,
                Math.abs(h - h_2) / 2-25, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储新合成的图片
        canvas.restore();
        return newBitmap;
    }

    public Bitmap getBitmap(Context context) {
        Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.smellimage).copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmap2 = ((BitmapDrawable) context.getResources().getDrawable(
                R.drawable.image)).getBitmap();
        return add(bitmap1,toRoundBitmap(toSmall(bitmap2)));
    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2 - 5;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2 - 5;
            float clip = (width - height)/2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst_left + 20, dst_top + 20, dst_right - 20, dst_bottom - 20);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
    private static Bitmap toSmall(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.18f,0.18f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

}
