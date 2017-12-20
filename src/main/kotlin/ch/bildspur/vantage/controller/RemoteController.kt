package ch.bildspur.vantage.controller

import ch.bildspur.vantage.Sketch

class RemoteController(internal var sketch: Sketch) {

    fun processCommand(key: Char) {
        when (key) {
        //'i' -> sketch.isStatusViewShown = !sketch.isStatusViewShown
        }
    }
}