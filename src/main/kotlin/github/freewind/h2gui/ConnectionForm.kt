package github.freewind.h2gui

import com.zaxxer.hikari.HikariDataSource
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import tornadofx.*
import javax.sql.DataSource

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
            textfield("jdbc:h2:mem:test").also { jdbcUrl = it }
        }
        row {
            label("username")
            textfield("sa").also { username = it }
        }
        row {
            label("password")
            passwordfield("sa").also { password = it }
        }
        row {
            label()
            hbox {
                button("test").setOnAction { testConnection(jdbcUrl.text, username.text, password.text) }
                button("connect").setOnAction {
                    connect(jdbcUrl.text, username.text, password.text)
                    replaceWith(Explorer())
                }
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
    val valid = try {
        val ds = createDataSource(url, username, password)
        ds.connection.isValid(3)
    } catch (e: Exception) {
        error("Failed", e.toString())
        return
    }
    if (valid) {
        information("Connection is OK")
    } else {
        warning("Connection is failed, please check your configuration")
    }
}

private fun createDataSource(url: String, username: String, password: String): HikariDataSource {
    val ds = HikariDataSource()
    ds.jdbcUrl = url
    ds.username = username
    ds.password = password
    return ds
}

var ds: DataSource? = null

private fun connect(url: String, username: String, password: String) {
    ds = createDataSource(url, username, password)
}

class ConnectionFormStyle : Stylesheet() {
    companion object {
        val title by cssclass()
    }

    init {
        root {
            prefWidth = 800.px
            prefHeight = 600.px
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