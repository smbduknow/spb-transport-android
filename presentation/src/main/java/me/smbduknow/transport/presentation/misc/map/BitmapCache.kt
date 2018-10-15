package me.smbduknow.transport.presentation.misc.map

import android.graphics.Bitmap


class BitmapCache {

    private val cache = mutableMapOf<Int, Bitmap>()

    fun getOrPut(key: Int, default: () -> Bitmap): Bitmap = cache.getOrPut(key, default)

    fun clear() = cache.clear()
}