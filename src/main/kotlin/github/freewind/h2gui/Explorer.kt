package github.freewind.h2gui

import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.control.TableColumn
import tornadofx.*
import java.sql.ResultSet

class Explorer : View() {

    private lateinit var mainPanel: Node

    override val root = hbox {
        vbox {
            button("SQL").setOnAction {
                mainPanel.replaceWith(SqlPanel().root)
            }
        }
        vbox {
            mainPanel = this
            label("main")
        }
    }

}

class SqlPanel : View() {

    private val rsColumnNames = FXCollections.observableArrayList<String>()
    private val rsRows = FXCollections.observableArrayList<List<Any>>()

    override val root = vbox {
        val x = textarea("""create table mytbl(id int primary key, name varchar(255));
insert into mytbl values(1, 'Hello');
insert into mytbl values(2, 'World');

select * from mytbl;
""")
        hbox {
            button("run update").setOnAction {
                runSqlUpdate(x.text?.trim()?.takeIf { it.isNotEmpty() })
            }
            button("run query").setOnAction {
                runSqlQuery(x.text?.trim()?.takeIf { it.isNotEmpty() })
            }
        }

        tableview<List<Any>>(rsRows) {
            println("rsColumnNames: $rsColumnNames")
            rsColumnNames.addListener { value: Observable ->
                val names = value as ObservableListWrapper<*>
                names.forEachIndexed { index, name ->
                    column(name.toString()) { cellDataFeatures: TableColumn.CellDataFeatures<List<Any>, Any> -> SimpleObjectProperty(cellDataFeatures.value[index]) }
                }
            }
        }
    }

    private fun runSqlUpdate(sql: String?) {
        if (sql == null) return
        ds?.connection?.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate(sql)
            }
        }
    }

    private fun runSqlQuery(sql: String?) {
        if (sql == null) return
        ds?.connection?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                findColumnNames(rs)
                findRsRows(rs)
            }

        }
    }

    private fun findRsRows(rs: ResultSet) {
        rsRows.clear()
        while (rs.next()) {
            rsRows.add(rsColumnNames.map(rs::getObject))
        }
    }

    private fun findColumnNames(rs: ResultSet) {
        val meta = rs.metaData
        rsColumnNames.clear()
        rsColumnNames.addAll((1..meta.columnCount).map { index ->
            meta.getColumnName(index)
        })
    }

}

