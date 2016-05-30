package me.smbduknow.transport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

public class DrawableUtil {

    public static BitmapDrawable writeOnDrawable(Context ctx, int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(12);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth()/2, bm.getHeight()/2, paint);

        return new BitmapDrawable(bm);
    }
}
