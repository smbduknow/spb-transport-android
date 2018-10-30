package me.smbduknow.transport.presentation.misc.map

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import me.smbduknow.transport.R


class MapIconProvider(private val context: Context) {

    private val cache = BitmapCache()

    fun getVehicleIcon(type: String, label: String, bearing: Float): Bitmap {
        val resId = resolveIconResByType(type)
        val icon = getBitmap(context, resId, R.dimen.pin_vehicle_size)
        return Bitmap.createBitmap(icon.width, icon.height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            drawIcon(canvas, icon, bearing)
            drawText(canvas, label)
        }
    }

    fun getUserIcon() = getBitmap(context, R.drawable.ic_pin_user, R.dimen.pin_user_size)

    @DrawableRes
    private fun resolveIconResByType(type: String): Int = when (type) {
        "bus" -> R.drawable.ic_vehicle_bus
        "trolley" -> R.drawable.ic_vehicle_trolley
        "tram" -> R.drawable.ic_vehicle_tram
        else -> 0
    }

    private fun getBitmap(context: Context,
                          @DrawableRes drawableId: Int,
                          @DimenRes sizeResId: Int = R.dimen.pin_vehicle_size): Bitmap =
            cache.getOrPut(drawableId) { bitmapFactory(context, drawableId, sizeResId) }

    private fun drawIcon(canvas: Canvas, icon: Bitmap, bearing: Float) {
        val bitmapPaint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }
        val matrix = Matrix().apply {
            setRotate(180 + bearing, icon.width / 2f, icon.height / 2f)
        }
        canvas.drawBitmap(icon, matrix, bitmapPaint)
    }

    private fun drawText(canvas: Canvas, text: String) {
        val fontSize = context.resources.getDimensionPixelSize(R.dimen.text_pin).toFloat()
        val textPaint = (TextPaint(TextPaint.ANTI_ALIAS_FLAG)).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }
        val dy = fontSize / 3
        canvas.drawText(text, (canvas.width / 2f), (canvas.height / 2 + dy), textPaint)
    }

}