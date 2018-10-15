package me.smbduknow.transport.presentation.misc.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat
import me.smbduknow.transport.presentation.misc.isPreLollipop


val bitmapFactory = { context: Context, drawableId: Int, sizeResId: Int ->
    val size = context.resources.getDimensionPixelSize(sizeResId)
    context.getDrawableCompat(drawableId)
            ?.let { if (isPreLollipop) it.wrapCompat() else it }
            ?.toBitmap(size, size)!!
}

fun Drawable.toBitmap(height: Int, width: Int): Bitmap = createBitmap(height, width).applyCanvas {
    this@toBitmap.setBounds(0, 0, this.width, this.height)
    this@toBitmap.draw(this)
}

fun Drawable.wrapCompat(): Drawable = DrawableCompat.wrap(this).mutate()

fun Context.getDrawableCompat(@DrawableRes drawableId: Int): Drawable? = AppCompatResources.getDrawable(this, drawableId)
