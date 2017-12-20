package ch.bildspur.vantage.view

import ch.bildspur.vantage.controller.timer.TimerTask
import processing.core.PGraphics

class SceneRenderer(val g: PGraphics) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task


    override fun setup() {
    }

    override fun render() {
        // todo: render your things
    }


    override fun dispose() {
    }
}