package ch.bildspur.vantage.ui.properties

import ch.bildspur.vantage.model.DataModel
import ch.bildspur.vantage.util.format
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import java.lang.reflect.Field

class SliderProperty(field: Field, obj: Any, val annotation: SliderParameter) : BaseProperty(field, obj) {
    private val slider = Slider(annotation.minValue, annotation.maxValue, 0.0)
    private val valueLabel = Label()

    init {
        val box = HBox(slider, valueLabel)
        box.spacing = 10.0
        children.add(box)

        val model = field.get(obj) as DataModel<Float>
        model.onChanged += {
            slider.value = model.value.toDouble()
            valueLabel.text = model.value.format(2)
        }
        model.fireLatest()

        slider.valueProperty().addListener { _, _, _ ->
            run {
                model.value = slider.value.toFloat()
                propertyChanged(this)
            }
        }
    }
}