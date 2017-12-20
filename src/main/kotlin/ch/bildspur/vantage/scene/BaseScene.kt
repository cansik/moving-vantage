package ch.bildspur.vantage.scene

import ch.bildspur.vantage.controller.timer.TimerTask
import ch.bildspur.vantage.model.Project

abstract class BaseScene(val project: Project) {
    abstract val name: String

    abstract val timerTask: TimerTask

    abstract fun setup()
    abstract fun update()
    abstract fun stop()
    abstract fun dispose()
}