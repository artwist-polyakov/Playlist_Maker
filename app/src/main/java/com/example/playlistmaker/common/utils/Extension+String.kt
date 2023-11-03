package com.example.playlistmaker.common.utils

fun String.countDurationInSeconds(): Long {
    val timeUnits = listOf(1, 60, 3600)
    return this.split(":").reversed().mapIndexed { index, timeComponent ->
        timeComponent.toIntOrNull() ?: 0 * (timeUnits.getOrElse(index) { 0 })
    }.sum().toLong()
}
