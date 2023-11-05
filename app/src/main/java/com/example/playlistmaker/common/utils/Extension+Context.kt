package com.example.playlistmaker.common.utils

import android.content.Context
import android.util.TypedValue

fun Context.getThemeColor(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}