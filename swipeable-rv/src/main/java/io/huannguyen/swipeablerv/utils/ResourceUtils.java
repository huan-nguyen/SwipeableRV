package io.huannguyen.swipeablerv.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by huannguyen
 */

public class ResourceUtils {

    public static final int NO_COLOR = 0;

    public static int getColor(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    public static String getString(Context context, TypedArray array, int attribute,
                                   @StringRes int defaultString) {
        String result = array.getString(attribute);
        if (result == null) {
            result = context.getString(defaultString);
        }

        return result;
    }

    public static float getDimension(Context context, @DimenRes int dimen) {
        return context.getResources().getDimension(dimen);
    }

    /**
     * Change color of a bitmap
     *
     * @param resources
     *         Resource instance to create bitmap
     * @param iconRes
     *         Icon resource
     * @param color
     *         The new color of the bitmap
     * @param height
     *         desired height of the bitmap
     * @param width
     *         desired width of the bitmap
     *
     * @return A bitmap with new color
     */
    public static Bitmap createBitmap(Resources resources, @DrawableRes int iconRes, int color, int
            height, int width) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, iconRes);

        if (height != 0 && width != 0 &&
                (bitmap.getHeight() != height || bitmap.getWidth() != width)) {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }

        if (color != ResourceUtils.NO_COLOR) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            Paint paint = new Paint();
            paint.setColorFilter(filter);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }

        return bitmap;
    }

    /**
     * Create a bitmap from a text
     *
     * @param text
     *         Text which the bitmap is created for
     * @param textSize
     *         Text size
     * @param textColor
     *         Text color
     * @param typeface
     *         Typeface of text
     *
     * @return a bitmap on which is text is drawn
     */
    public static Bitmap createBitmapFromText(String text, float textSize, int textColor,
                                              Typeface typeface) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, baseline, paint);
        return bitmap;
    }
}
