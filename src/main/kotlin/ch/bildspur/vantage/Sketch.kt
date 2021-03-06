package ch.bildspur.vantage

import ch.bildspur.postfx.builder.PostFX
import ch.bildspur.vantage.controller.OscController
import ch.bildspur.vantage.controller.PeasyController
import ch.bildspur.vantage.controller.RemoteController
import ch.bildspur.vantage.controller.timer.Timer
import ch.bildspur.vantage.controller.timer.TimerTask
import ch.bildspur.vantage.model.DataModel
import ch.bildspur.vantage.model.Project
import ch.bildspur.vantage.scene.SceneManager
import ch.bildspur.vantage.util.LogBook
import ch.bildspur.vantage.util.draw
import ch.bildspur.vantage.util.format
import ch.bildspur.vantage.view.IRenderer
import ch.bildspur.vantage.view.SceneRenderer
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL


/**
 * Created by cansik on 04.02.17.
 */
class Sketch : PApplet() {
    companion object {
        @JvmStatic
        val HIGH_RES_FRAME_RATE = 60f
        @JvmStatic
        val LOW_RES_FRAME_RATE = 30f

        @JvmStatic
        val WINDOW_WIDTH = 768
        @JvmStatic
        val WINDOW_HEIGHT = 576

        @JvmStatic
        val CURSOR_HIDING_TIME = 1000L * 5L

        @JvmStatic
        val LOGBOOK_UPDATE_TIME = 1000L * 60L * 10L

        @JvmStatic
        val NAME = "Moving Vantage"

        @JvmStatic
        val VERSION = "0.1"

        @JvmStatic lateinit var instance: PApplet

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        @JvmStatic
        fun currentMillis(): Int {
            return instance.millis()
        }
    }


    @Volatile
    var isInitialised = false

    var fpsOverTime = 0f

    @Volatile
    var isResetRendererProposed = false

    val peasy = PeasyController(this)

    val osc = OscController(this)

    val timer = Timer()

    val remote = RemoteController(this)

    lateinit var canvas: PGraphics

    var lastCursorMoveTime = 0

    val renderer = mutableListOf<IRenderer>()

    val project = DataModel(Project())

    lateinit var fx: PostFX

    init {
    }

    override fun settings() {
        if (project.value.isFullScreenMode.value)
            fullScreen(PConstants.P3D, project.value.fullScreenDisplay.value)
        else
            size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        PJOGL.profile = 1
        smooth()

        // retina screen
        if (project.value.highResMode.value)
            pixelDensity = 2
    }

    override fun setup() {
        Sketch.instance = this

        frameRate(if (project.value.highFPSMode.value) HIGH_RES_FRAME_RATE else LOW_RES_FRAME_RATE)
        colorMode(HSB, 360f, 100f, 100f)

        project.onChanged += {
            onProjectChanged()
        }
        project.fire()

        fx = PostFX(this)
        peasy.setup()

        // timer for cursor hiding
        timer.addTask(TimerTask(CURSOR_HIDING_TIME, {
            val current = millis()
            if (current - lastCursorMoveTime > CURSOR_HIDING_TIME)
                noCursor()
        }, "CursorHide"))

        // timer for logbook
        timer.addTask(TimerTask(LOGBOOK_UPDATE_TIME, {
            LogBook.log("Update")
        }))

        LogBook.log("Start")
    }

    override fun draw() {
        background(0)

        if (skipFirstFrames())
            return

        // setup long loading controllers
        if (initControllers())
            return

        // reset renderer if needed
        if (isResetRendererProposed)
            resetRenderer()

        canvas.draw {
            it.background(0)

            // render (update timer)
            timer.update()

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image with fx if needed
            if (project.value.highResMode.value)
                fx.render(canvas)
                        .bloom(0.0f, 20, 40f)
                        .compose()
            else
                image(canvas, 0f, 0f)
            drawFPS(g)
        }
    }

    fun onProjectChanged() {
        surface.setTitle("$NAME ($VERSION) - ${project.value.name.value}")

        // setup hooks
        setupHooks()
    }

    fun setupHooks() {

    }

    fun proposeResetRenderer() {
        if (isInitialised) {
            isResetRendererProposed = true
        }
    }

    fun resetRenderer() {
        println("reseting renderer...")

        renderer.forEach {
            timer.taskList.remove(it.timerTask)
            it.dispose()
        }

        renderer.clear()

        // add renderer
        renderer.add(SceneRenderer(canvas))
        renderer.add(SceneManager(project.value))

        isResetRendererProposed = false

        // rebuild
        // setup renderer
        renderer.forEach {
            it.setup()
            timer.addTask(it.timerTask)
        }
    }

    fun resetCanvas() {
        canvas = createGraphics(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        // retina screen
        if (project.value.highResMode.value)
            canvas.pixelDensity = 2
    }

    fun skipFirstFrames(): Boolean {
        // skip first two frames
        if (frameCount < 2) {
            peasy.hud {
                textAlign(CENTER, CENTER)
                fill(255)
                textSize(20f)
                text("${Sketch.NAME} is loading...", width / 2f, height / 2f)
            }
            return true
        }

        return false
    }

    fun initControllers(): Boolean {
        if (!osc.isSetup) {
            osc.setup()
            resetCanvas()

            timer.setup()

            prepareExitHandler()

            // setting up renderer
            resetRenderer()

            isInitialised = true
            return true
        }

        return false
    }

    fun drawFPS(pg: PGraphics) {
        // draw fps
        fpsOverTime += frameRate
        val averageFPS = fpsOverTime / frameCount.toFloat()

        pg.textAlign(PApplet.LEFT, PApplet.BOTTOM)
        pg.fill(255)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${averageFPS.format(2)}", 10f, height - 5f)
    }

    fun prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("shutting down...")
            renderer.forEach { it.dispose() }
            osc.osc.stop()

            LogBook.log("Stop")
        })
    }

    override fun keyPressed() {
        remote.processCommand(key)
    }

    override fun mouseMoved() {
        super.mouseMoved()
        cursor()
        lastCursorMoveTime = millis()
    }
}