package com.example.playlistmaker.common.utils


fun String.countDurationInSeconds(): Long {
    val timeUnits = listOf(1, 60, 3600)
    var result = 0L
    this.split(":").reversed().mapIndexed { index, timeComponent ->
        result += (timeComponent.toIntOrNull() ?: 0) * (timeUnits.getOrElse(index) { 1 })
    }
    return result
}
