package ch.bildspur.vantage.scene

import ch.bildspur.vantage.controller.timer.Timer
import ch.bildspur.vantage.controller.timer.TimerTask
import ch.bildspur.vantage.model.Project
import ch.bildspur.vantage.view.IRenderer

class SceneManager(val project: Project) : IRenderer {
    val movingVideoScene = MovingVideoScene(project)

    var activeScene: BaseScene = movingVideoScene

    private val task = TimerTask(0, { render() }, "SceneManager")

    override val timerTask: TimerTask
        get() = task

    private val timer = Timer()

    override fun setup() {
        timer.setup()
        initScene(movingVideoScene)
    }

    override fun render() {
        timer.update()
    }

    override fun dispose() {
        movingVideoScene.dispose()
    }

    fun initScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.setup()
        timer.addTask(activeScene.timerTask)
    }
}