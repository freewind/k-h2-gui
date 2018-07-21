package github.freewind.h2gui

import tornadofx.View
import tornadofx.hbox
import tornadofx.label
import tornadofx.vbox

class Explorer : View() {

    override val root = hbox {
        vbox {
            label("left")
        }
        vbox {
            label("main")
        }
    }

}
