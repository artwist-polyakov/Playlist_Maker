package com.example.playlistmaker.common.presentation

const val SECONDS_IN_SECONDS = 1
const val SECONDS_IN_MINUTE = 60
const val SECONDS_IN_HOUR = 3600

fun String.countDurationInSeconds(): Long =
    this.split(":").reversed().mapIndexed { index, timeComponent ->
        timeComponent.toInt() * (listOf(
            SECONDS_IN_SECONDS,
            SECONDS_IN_MINUTE,
            SECONDS_IN_HOUR
        ).getOrElse(index) { 0 })
    }.sum().toLong()

