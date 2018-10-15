package me.smbduknow.transport.presentation.misc

import android.os.Build

val isPreLollipop
    get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
