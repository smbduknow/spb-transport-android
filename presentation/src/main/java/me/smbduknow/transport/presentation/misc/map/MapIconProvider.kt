package me.smbduknow.transport.presentation.misc.map

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import me.smbduknow.transport.R


class MapIconProvider(private val context: Context) {

    private val RES_PIN_BUS = R.drawable.ic_vehicle_bus
    private val RES_PIN_TROLLEY = R.drawable.ic_vehicle_trolley
    private val RES_PIN_TRAM = R.drawable.ic_vehicle_tram

    private val cache = BitmapCache()

    fun getVehicleIcon(type: String, label: String, bearing: Float): Bitmap {

        val iconBitmap = getBitmap(context, resolveIconResByType(type), R.dimen.pin_vehicle_size)


        val bitmap = Bitmap.createBitmap(iconBitmap.width, iconBitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        // draw icon
        val bitmapPaint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }
        val matrix = Matrix().apply {
            setRotate(180 + bearing, iconBitmap.width / 2f, iconBitmap.height / 2f)
        }
        canvas.drawBitmap(iconBitmap, matrix, bitmapPaint)

        // draw text
        val fontSize = context.resources.getDimensionPixelSize(R.dimen.text_pin).toFloat()
        val textPaint = (TextPaint(TextPaint.ANTI_ALIAS_FLAG)).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(label, (bitmap.width / 2).toFloat(), (bitmap.height / 2 + fontSize / 3), textPaint)

        return bitmap
    }

    fun getUserIcon() = getBitmap(context, R.drawable.ic_pin_user, R.dimen.pin_user_size)

    @DrawableRes
    private fun resolveIconResByType(type: String): Int = when (type) {
        "bus" -> RES_PIN_BUS
        "trolley" -> RES_PIN_TROLLEY
        "tram" -> RES_PIN_TRAM
        else -> RES_PIN_BUS
    }

    private fun getBitmap(context: Context,
                          @DrawableRes drawableId: Int,
                          @DimenRes sizeResId: Int = R.dimen.pin_vehicle_size): Bitmap =
            cache.getOrPut(drawableId) { bitmapFactory(context, drawableId, sizeResId) }

}