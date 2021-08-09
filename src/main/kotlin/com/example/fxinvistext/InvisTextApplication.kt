package com.example.fxinvistext

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.converter.IntegerStringConverter


class HelloApplication : Application() {

    override fun start(stage: Stage) {
        val main = BorderPane()

        val xPos: StringProperty
        val yPos: StringProperty
        val textSize: StringProperty
        val textColor: StringProperty
        val showBorder: BooleanProperty
        val settingsPane = GridPane().apply {
            add(Label("X pos"), 0, 0)
            val xPosInput = TextField()
            xPosInput.textFormatter = TextFormatter(IntegerStringConverter())
            xPos = xPosInput.textProperty()
            add(xPosInput, 1, 0)

            add(Label("Y pos"), 0, 1)
            val yPosInput = TextField()
            yPosInput.textFormatter = TextFormatter(IntegerStringConverter())
            yPos = yPosInput.textProperty()
            add(yPosInput, 1, 1)

            add(Label("Text size"), 0, 2)
            val textSizeInput = TextField()
            textSizeInput.textFormatter = TextFormatter(IntegerStringConverter())
            textSize = textSizeInput.textProperty()
            add(textSizeInput, 1, 2)

            add(Label("Text color"), 0, 3)
            val textColorInput = TextField("white")
            textColor = textColorInput.textProperty()
            add(textColorInput, 1, 3)

            val showBorderCheckbox = CheckBox("Border")
            showBorder = showBorderCheckbox.selectedProperty()
            add(showBorderCheckbox, 0, 4)
        }

        main.left = settingsPane


        val xPosInt = xPos.toInt()
        val yPosInt = yPos.toInt()
        val textSizeInt = textSize.toInt()
        xPosInt.value = 0
        yPosInt.value = 0
        textSizeInt.value = 24

        val textProperty: StringProperty
        val textArea = TextArea().apply {
            text = "Hallo"
            styleProperty().bind(Bindings.format("-fx-font: %d arial; -fx-text-fill: black;", textSizeInt))
            textProperty = textProperty()
        }
        main.center = textArea

        val writeScene = Scene(main)
        val styleProp = Bindings.format("-fx-font: %d arial; -fx-text-fill: %s;", textSizeInt, textColor)
        val invisStage = invisStage(textProperty, styleProp, showBorder)

        invisStage.x = xPosInt.value.toDouble()
        xPosInt.addListener { _, _, x ->
            if (x != null) invisStage.x = x.toDouble()
        }
        invisStage.y = yPosInt.value.toDouble()
        yPosInt.addListener { _, _, y ->
            if (y != null) invisStage.y = y.toDouble()
        }

        stage.onCloseRequest = EventHandler {
            invisStage.close()
        }
        textArea.widthProperty().addListener { _, _, newVal ->
            invisStage.width = newVal.toDouble()
        }
        textArea.heightProperty().addListener { _, _, newVal ->
            invisStage.height = newVal.toDouble()
        }
        stage.title = "Input goes here"
        stage.scene = writeScene
        stage.show()
        invisStage.show()
    }
}

fun invisStage(textProp: ObservableValue<String>, styleProp: ObservableValue<String>, showBorder: ObservableBooleanValue): Stage = Stage().apply {
    val borderPane = BorderPane().apply {
        styleProperty().bind(Bindings.`when`(showBorder).then("-fx-border-color: red;").otherwise("-fx-border-color: transparent"))
        center = TextArea().apply {
            text = "Hallo"
            styleProperty().bind(styleProp)
            isEditable = false
            textProperty().bind(textProp)
        }
    }
    scene = Scene(borderPane).apply {
        fill = Color.TRANSPARENT
        stylesheets.add("com/example/fxinvistext/style.css")
    }
    initStyle(StageStyle.TRANSPARENT)
    isAlwaysOnTop = true
}

fun StringProperty.toInt(): ObjectProperty<Int> {
    val intObserver = SimpleObjectProperty<Int>()
    this.bindBidirectional(intObserver, IntegerStringConverter())
    return intObserver
}

fun main() {
    Application.launch(HelloApplication::class.java)
}