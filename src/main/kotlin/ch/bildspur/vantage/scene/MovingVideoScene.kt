package ch.bildspur.vantage.scene

import ch.bildspur.vantage.controller.timer.TimerTask
import ch.bildspur.vantage.model.Project


class MovingVideoScene(project: Project) : BaseScene(project) {
    private val task = TimerTask(0, { update() })

    override val name: String
        get() = "Moving Video Scene"

    override val timerTask: TimerTask
        get() = task

    override fun setup() {

    }

    override fun update() {

    }

    override fun stop() {

    }

    override fun dispose() {

    }
}