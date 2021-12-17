package com.humiditytemperature.skytechzone.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;

public class BitmapsDrawable {


    public static int[] getGradientFromColor(int color) {
        int topColor;
        int bottomColor;
        float max;
        float[] hsv = new float[3];
        if (1.0d - ((((0.299d * ((double) Color.red(color))) + (0.587d * ((double) Color.green(color)))) + (0.114d * ((double) Color.blue(color)))) / 255.0d) > 0.5d) {
            Color.colorToHSV(color, hsv);
            hsv[2] = Math.min(1.0f, 0.9f * hsv[2]);
            topColor = Color.HSVToColor(hsv);
            Color.colorToHSV(color, hsv);
            if (hsv[0] > 45.0f) {
                max = hsv[0] - (0.15f * hsv[0]);
            } else {
                max = (((float) (hsv[0] < 2.0f ? 10 : 1)) * 1.5f * Math.max(hsv[0], 1.0f)) + hsv[0];
            }
            hsv[0] = max;
            hsv[2] = Math.min(1.0f, 1.25f * hsv[2]);
            bottomColor = Color.HSVToColor(hsv);
        } else {
            Color.colorToHSV(color, hsv);
            hsv[2] = Math.min(1.0f, 0.3f * hsv[2]);
            topColor = Color.HSVToColor(hsv);
            Color.colorToHSV(color, hsv);
            hsv[2] = Math.min(1.0f, 0.9f * hsv[2]);
            bottomColor = Color.HSVToColor(hsv);
        }
        return new int[]{topColor, bottomColor};
    }

    public static Drawable getRoundedRectangleDrawableForColor(int color, Context ctx) {
        return getRoundedRectangleDrawableForColor(color, 0f, ctx);
    }

    public static GradientDrawable getGradientDrawableForColor(int color, GradientDrawable.Orientation orientation) {
        return new GradientDrawable(orientation, getGradientFromColor(color));
    }

    public static Drawable getRoundedRectangleDrawableForColor(int color, float cornerRadius, Context ctx) {
        return getRoundedRectangleDrawableForColor(color, cornerRadius, ctx, GradientDrawable.Orientation.TOP_BOTTOM);
    }

    public static Drawable getRoundedRectangleDrawableForColor(int color, float cornerRadius, Context ctx, GradientDrawable.Orientation orientation) {
        GradientDrawable gd = getGradientDrawableForColor(color, orientation);
        gd.setCornerRadius((float) getPixelsForDp(cornerRadius, ctx));
        return gd;
    }




    public static Bitmap createShadowBitmap(Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(8.0f, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setMaskFilter(blurFilter);
        int[] offsetXY = new int[2];
        Bitmap shadowImage32 = originalBitmap.extractAlpha(shadowPaint, offsetXY).copy(Bitmap.Config.ARGB_8888, true);
        setPremultiplied(shadowImage32);
        new Canvas(shadowImage32).drawBitmap(originalBitmap, (float) (-offsetXY[0]), (float) (-offsetXY[1]), (Paint) null);
        return shadowImage32;
    }

    @TargetApi(19)
    public static void setPremultiplied(Bitmap b) {
        if (Build.VERSION.SDK_INT >= 19) {
            b.setPremultiplied(true);
        }
    }



    public static Bitmap writeOnBitmap(Bitmap bm, String text, float textSize, Context context) {
        return writeOnBitmap(bm, text, textSize, TypefacesFonts.REGULAR, context);
    }





    public static Bitmap writeOnBitmap(Bitmap bm, String text, float textSize, String typeFace, Context context) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(-1);
        paint.setTextSize((float) getPixelsForDp(textSize, context));
        paint.setAntiAlias(true);
        paint.setTypeface(TypefacesFonts.get(context, typeFace));
        Canvas canvas = new Canvas(bm);
        Rect boundsText = new Rect();
        Rect degTextBounds = new Rect();
        paint.getTextBounds(text, 0, (!text.endsWith("°") || text.length() != 2) ? text.length() : text.length() - 1, boundsText);
        if (text.indexOf(176) >= 0) {
            paint.getTextBounds(text, text.indexOf(176), text.length(), degTextBounds);
        }
        float x = (float) ((bm.getWidth() - boundsText.width()) / 2);
        float y = (float) ((bm.getHeight() + boundsText.height()) / 2);
        if (text.startsWith("1") && text.endsWith("°") && text.length() == 2) {
            x -= (float) (boundsText.width() / 2);
        }
        if (text.length() == 3) {
            x += (float) (degTextBounds.width() / 2);
        }
        canvas.drawText(text, x, y, paint);
        return bm;
    }



    public static int getPixelsForDp(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, context.getResources().getDisplayMetrics());
    }




}
