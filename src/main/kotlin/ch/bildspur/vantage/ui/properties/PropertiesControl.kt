package ch.bildspur.vantage.ui.properties

import ch.bildspur.vantage.event.Event
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import java.lang.reflect.Field

class PropertiesControl : VBox() {

    val propertyChanged = Event<BaseProperty>()

    init {
        spacing = 10.0
        alignment = Pos.TOP_CENTER
        padding = Insets(10.0, 20.0, 10.0, 10.0)
    }

    fun initView(obj: Any) {
        clearView()

        val params = readParameters(obj)

        // create view
        params.forEach {
            if (it.isAnnotationPresent(StringParameter::class.java)) {
                val annotation = it.getAnnotation(StringParameter::class.java)
                addProperty(annotation.name, StringProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(IntParameter::class.java)) {
                val annotation = it.getAnnotation(IntParameter::class.java)
                addProperty(annotation.name, IntProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(DoubleParameter::class.java)) {
                val annotation = it.getAnnotation(DoubleParameter::class.java)
                addProperty(annotation.name, DoubleProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(SliderParameter::class.java)) {
                val annotation = it.getAnnotation(SliderParameter::class.java)
                addProperty(annotation.name, SliderProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(BooleanParameter::class.java)) {
                val annotation = it.getAnnotation(BooleanParameter::class.java)
                addProperty(annotation.name, BooleanProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(PVectorParameter::class.java)) {
                val annotation = it.getAnnotation(PVectorParameter::class.java)
                addProperty(annotation.name, PVectorProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(ActionParameter::class.java)) {
                val annotation = it.getAnnotation(ActionParameter::class.java)
                addProperty(annotation.name, ActionProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(FloatParameter::class.java)) {
                val annotation = it.getAnnotation(FloatParameter::class.java)
                addProperty(annotation.name, FloatProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(ArrayParameter::class.java)) {
                val annotation = it.getAnnotation(ArrayParameter::class.java)
                addProperty(annotation.name, ArrayProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(PVectorAngleParameter::class.java)) {
                val annotation = it.getAnnotation(PVectorAngleParameter::class.java)
                addProperty(annotation.name, PVectorAngleProperty(it, obj, annotation))
            }
        }
    }

    fun clearView() {
        this.children.clear()
    }

    private fun addProperty(name: String, propertyView: BaseProperty) {
        propertyView.propertyChanged += {
            propertyChanged(propertyView)
        }

        val nameLabel = Label("$name:")
        nameLabel.prefWidth = 80.0
        nameLabel.font = Font("Arial", 12.0)
        nameLabel.isWrapText = true

        val box = HBox(nameLabel, propertyView)
        box.spacing = 10.0
        box.prefHeight = propertyView.prefHeight
        box.alignment = Pos.CENTER_LEFT
        children.add(box)
    }

    private fun readParameters(obj: Any): List<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields.filter {
            it.isAnnotationPresent(SliderParameter::class.java) ||
                    it.isAnnotationPresent(DoubleParameter::class.java) ||
                    it.isAnnotationPresent(StringParameter::class.java) ||
                    it.isAnnotationPresent(BooleanParameter::class.java) ||
                    it.isAnnotationPresent(IntParameter::class.java) ||
                    it.isAnnotationPresent(PVectorParameter::class.java) ||
                    it.isAnnotationPresent(ActionParameter::class.java) ||
                    it.isAnnotationPresent(FloatParameter::class.java) ||
                    it.isAnnotationPresent(ArrayParameter::class.java) ||
                    it.isAnnotationPresent(PVectorAngleParameter::class.java)

        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}