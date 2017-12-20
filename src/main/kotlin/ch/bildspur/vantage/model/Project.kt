package ch.bildspur.vantage.model

import ch.bildspur.vantage.Sketch
import ch.bildspur.vantage.ui.properties.BooleanParameter
import ch.bildspur.vantage.ui.properties.IntParameter
import ch.bildspur.vantage.ui.properties.StringParameter
import com.google.gson.annotations.Expose

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose
    @StringParameter("Name")
    var name = DataModel("${Sketch.NAME} Project")

    @Expose
    @BooleanParameter("High Res Mode*")
    var highResMode = DataModel(true)

    @Expose
    @BooleanParameter("High FPS Mode*")
    var highFPSMode = DataModel(true)

    @Expose
    @BooleanParameter("Fullscreen Mode*")
    var isFullScreenMode = DataModel(false)

    @Expose
    @IntParameter("Fullscreen Display*")
    var fullScreenDisplay = DataModel(0)
}