package github.freewind.h2gui

import javafx.geometry.Insets
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import tornadofx.*


class ConnectionForm : View() {
    private lateinit var jdbcUrl: TextField
    private lateinit var username: TextField
    private lateinit var password: PasswordField

    override val root = gridpane {
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

class HelloWorldStyle : Stylesheet() {
    init {
        root {
            prefWidth = 400.px
            prefHeight = 400.px
        }
    }
}

class HelloWorldApp : App(ConnectionForm::class, HelloWorldStyle::class)

fun main(args: Array<String>) {
    launch<HelloWorldApp>()
}