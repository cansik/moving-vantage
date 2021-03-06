package ch.bildspur.vantage.ui

import ch.bildspur.vantage.Sketch
import ch.bildspur.vantage.configuration.ConfigurationController
import ch.bildspur.vantage.model.AppConfig
import ch.bildspur.vantage.model.DataModel
import ch.bildspur.vantage.model.Project
import ch.bildspur.vantage.ui.properties.PropertiesControl
import ch.bildspur.vantage.ui.util.UITask
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TitledPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import processing.core.PApplet
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread


class PrimaryView {
    lateinit var primaryStage: Stage

    @FXML lateinit var root: BorderPane

    val configuration = ConfigurationController()

    val propertiesControl = PropertiesControl()

    lateinit var appConfig: AppConfig

    val project = DataModel(Project())

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    @FXML lateinit var propertiesPane: TitledPane

    @FXML lateinit var statusLabel: Label

    @FXML lateinit var progressIndicator: ProgressIndicator

    @FXML lateinit var iconView: ImageView

    private val appIcon = Image(javaClass.getResourceAsStream("images/AppIcon.png"))

    init {
    }

    fun setupView() {
        propertiesPane.content = propertiesControl

        // setup ui task
        UITask.status.addListener { o -> statusLabel.text = UITask.status.value }
        UITask.running.addListener { o -> progressIndicator.isVisible = UITask.running.value }

        // setup ui
        UITask.run({
            // set app icon
            iconView.image = appIcon

            // for updating the property view
            propertiesControl.propertyChanged += {
                updateUI()
            }

            // load app config
            appConfig = configuration.loadAppConfig()

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                project.value = configuration.loadProject(appConfig.projectFile)
            else
                project.value = Project()

            // start processing
            startProcessing()
        }, { updateUI() }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()

        project.onChanged += {
            sketch.project.value = project.value
        }
        project.fire()

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
            println("processing quit")
        }
    }

    fun updateUI() {

    }

    fun newProject(e: ActionEvent) {
        // show selection dialog
        UITask.run({
            appConfig.projectFile = ""
            project.value = Project()
            resetRenderer()
        }, { updateUI() }, "new project")
    }

    fun loadProject(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.title = "Select project to load"
        fileChooser.initialFileName = ""
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON", "*.json")
        )

        val result = fileChooser.showOpenDialog(primaryStage)

        if (result != null) {
            UITask.run({
                project.value = configuration.loadProject(result.path)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)

                resetRenderer()
            }, { updateUI() }, "load project")
        }
    }

    fun resetRenderer() {
        sketch.proposeResetRenderer()
    }

    fun rebuildRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun saveProjectAs(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = ""
        fileChooser.title = "Save project..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("JSON", "*.json"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                configuration.saveProject(result.path, project.value)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun saveProject(e: ActionEvent) {
        if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile))) {
            UITask.run({
                configuration.saveProject(appConfig.projectFile, project.value)
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun showProjectSettings(e: ActionEvent) {
        propertiesControl.initView(project.value)
    }
}
