package com.example.playlistmaker.common.presentation


fun String.countDurationInSeconds(): Long =
    this.split(":").reversed().mapIndexed { index, timeComponent ->
        timeComponent.toInt() * (listOf(1, 60, 3600).getOrElse(index) { 0 })
    }.sum().toLong()

