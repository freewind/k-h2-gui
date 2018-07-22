package github.freewind.h2gui

import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.input.MouseButton
import tornadofx.*
import java.sql.ResultSet

class Explorer : View() {

    private lateinit var mainPanel: Node

    private val tables = FXCollections.observableArrayList<String>()

    init {
        loadTables()
    }

    override val root = hbox {
        vbox {
            button("SQL").setOnAction {
                mainPanel.replaceChildren(SqlPanel().root)
            }
            button("refresh tables").setOnAction {
                loadTables()
            }
            listview(tables) {
                setOnMouseClicked { event ->
                    if (event.button == MouseButton.PRIMARY && event.clickCount == 2) {
                        this.selectionModel.selectedItems.firstOrNull()?.let { table ->
                            showTableData(table)
                        }
                    }
                }
            }
        }
        vbox {
            mainPanel = this
            label("main")
        }
    }

    private fun showTableData(table: String) {
        mainPanel.replaceChildren(SqlTable().run {
            this.query("select * from $table")
            this.root
        })
    }

    private fun loadTables() {
        tables.clear()
        useConnection { conn ->
            val rs = conn.createStatement().executeQuery("show tables")
            while (rs.next()) {
                tables.add(rs.getString(1))
            }
        }
    }

}

class SqlPanel : View() {

    private var sql: String? = null

    private val sqlTable = SqlTable()

    override val root = vbox {
        textarea("""create table mytbl(id int primary key, name varchar(255));
                    insert into mytbl values(1, 'Hello');
                    insert into mytbl values(2, 'World');

                    select * from mytbl;
                """) {
            this.textProperty().addListener { _, _, newValue -> sql = newValue?.trim()?.takeIf { it.isNotEmpty() } }
        }

        hbox {
            button("run update").setOnAction {
                sql?.run(::runSqlUpdate)
            }
            button("run query").setOnAction {
                sql?.run {
                    sqlTable.query(this)
                }
            }
        }

        add(sqlTable.root)
    }

    private fun runSqlUpdate(sql: String?) {
        if (sql == null) return
        useConnection { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate(sql)
            }
        }
    }

}

class SqlTable : View() {
    private val rsColumnNames = FXCollections.observableArrayList<String>()
    private val rsRows = FXCollections.observableArrayList<List<Any>>()

    fun query(sql: String) {
        clear()
        querySql(sql)?.let { (columnNames, rows) ->
            rsColumnNames.addAll(columnNames)
            rows.forEach { rsRows.add(it) }
        }
    }

    private fun clear() {
        rsColumnNames.clear()
        rsRows.clear()
    }

    override val root = tableview<List<Any>>(rsRows) {
        rsColumnNames.addListener { value: Observable ->
            val names = value as ObservableListWrapper<*>
            names.forEachIndexed { index, name ->
                column(name.toString()) { cellDataFeatures: TableColumn.CellDataFeatures<List<Any>, Any> -> SimpleObjectProperty(cellDataFeatures.value[index]) }
            }
        }
    }

}

fun querySql(sql: String): Pair<List<String>, List<List<Any>>>? {
    return useConnection { conn ->
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            val columnNames = rs.columnNames()
            val rows = rs.rows()
            columnNames to rows
        }
    }
}

fun ResultSet.rows(): List<List<Any>> {
    val rows = mutableListOf<List<Any>>()
    val columnNames = this.columnNames()
    while (this.next()) {
        val row = columnNames.map { name -> this.getObject(name) }
        rows.add(row)
    }
    return rows.toList()
}

fun ResultSet.columnNames(): List<String> {
    val meta = this.metaData
    return (1..meta.columnCount).map { index -> meta.getColumnName(index) }
}