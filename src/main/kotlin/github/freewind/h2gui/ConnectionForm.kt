package github.freewind.h2gui

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.text.TextAlignment
import tornadofx.*


class ConnectionForm : View() {
    private lateinit var jdbcUrl: TextField
    private lateinit var username: TextField
    private lateinit var password: PasswordField

    override val root = gridpane {
        row {
            hbox {
                label("H2 GUI")
                addClass(ConnectionFormStyle.title)
                gridpaneConstraints { this.columnSpan = 2 }
            }
        }
        row {
            label("JDBC url")
            textfield().also { jdbcUrl = it }
        }
        row {
            label("username")
            textfield().also { username = it }
        }
        row {
            label("password")
            passwordfield().also { password = it }
        }
        row {
            label("")
            hbox {
                button("test").setOnAction { testConnection(jdbcUrl.text, username.text, password.text) }
                button("connect").setOnAction { connect(jdbcUrl.text, username.text, password.text) }
                spacing = 5.0
            }
        }
        gridpaneConstraints {
            padding = Insets(10.0)
            hgap = 10.0
            vgap = 10.0
        }
    }
}


private fun testConnection(url: String, username: String, password: String) {
    information("testing: url: $url, username: $username, password: $password")
}

private fun connect(url: String, username: String, password: String) {
    information("connecting: url: $url, username: $username, password: $password")
}

class ConnectionFormStyle : Stylesheet() {
    companion object {
        val title by cssclass()
    }

    init {
        root {
            prefWidth = 400.px
            prefHeight = 400.px
        }
        title {
            fontSize = 40.px
            alignment = Pos.CENTER
        }
    }
}

class H2GuiApp : App(ConnectionForm::class, ConnectionFormStyle::class)

fun main(args: Array<String>) {
    launch<H2GuiApp>()
}

object H2Gui {
    @JvmStatic
    fun main(args: Array<String>) {
        launch<H2GuiApp>()
    }
}