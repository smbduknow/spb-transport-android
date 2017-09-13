package me.smbduknow.transport.commons

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.ColorRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.content.res.AppCompatResources
import android.text.TextPaint
import me.smbduknow.transport.R

object DrawableUtil {

    private const val RES_VEHICLE = R.drawable.ic_vehicle

    fun createVehiclePin(ctx: Context, @ColorRes colorRes: Int, text: String, angle: Float): Bitmap {
        val drawable = AppCompatResources.getDrawable(ctx, RES_VEHICLE)
                ?: throw Resources.NotFoundException("Vehicle icon not found")
        val tintList = ResourcesCompat.getColorStateList(ctx.resources, colorRes, null)
        DrawableCompat.setTintList(drawable, tintList)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        // draw icon
        val canvas = Canvas(bitmap)
        val cWidth = canvas.width
        val cHeight = canvas.height
        drawable.setBounds(0, 0, cWidth, cHeight)
        canvas.rotate(angle, cWidth / 2f, cHeight /2f)
        drawable.draw(canvas)

        canvas.rotate(-angle, cWidth / 2f, cHeight /2f)

        // draw text
        val fontSize = ctx.resources.getDimensionPixelSize(R.dimen.text_pin).toFloat()
        val textPaint = (TextPaint(TextPaint.ANTI_ALIAS_FLAG)).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(text, (bitmap.width / 2).toFloat(), (bitmap.height / 2 + 8).toFloat(), textPaint)

        return bitmap
    }

}
