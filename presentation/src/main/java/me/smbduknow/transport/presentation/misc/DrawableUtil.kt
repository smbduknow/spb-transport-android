package me.smbduknow.transport.presentation.misc

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import me.smbduknow.transport.R


object DrawableUtil {

    private val drawableCache = mutableMapOf<Int, Bitmap>()

    fun createVehiclePin(ctx: Context, @DrawableRes iconRes: Int, text: String, angle: Float): Bitmap {

        val iconBitmap = getBitmapFromVectorDrawable(ctx, iconRes)

        val bitmap = Bitmap.createBitmap(iconBitmap.width, iconBitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        // draw icon
        val bitmapPaint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }
        val matrix = Matrix().apply{
            setRotate(180 + angle, iconBitmap.width / 2f, iconBitmap.height / 2f)
        }
        canvas.drawBitmap(iconBitmap, matrix, bitmapPaint)

        // draw text
        val fontSize = ctx.resources.getDimensionPixelSize(R.dimen.text_pin).toFloat()
        val textPaint = (TextPaint(TextPaint.ANTI_ALIAS_FLAG)).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(text, (bitmap.width / 2).toFloat(), (bitmap.height / 2 + fontSize/3), textPaint)

        return bitmap
    }

    fun getBitmapFromVectorDrawable(context: Context,
                                    @DrawableRes drawableId: Int,
                                    @DimenRes sizeResId: Int = R.dimen.pin_vehicle_size): Bitmap {
        val cached = if(drawableCache.containsKey(drawableId)) drawableCache[drawableId] else null
        if(cached != null) return cached

        var drawable = AppCompatResources.getDrawable(context, drawableId)
                ?: throw Resources.NotFoundException("Vehicle icon not found")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }

        val size = context.resources.getDimensionPixelSize(sizeResId)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        drawableCache[drawableId] = bitmap

        return bitmap
    }

}
