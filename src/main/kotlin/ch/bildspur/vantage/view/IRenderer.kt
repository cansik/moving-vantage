package ch.bildspur.vantage.view

import ch.bildspur.vantage.controller.timer.TimerTask

interface IRenderer {
    val timerTask: TimerTask

    fun setup()

    fun render()

    fun dispose()
}