package net.frozenorb.potpvp.kt.menu.pagination

import net.frozenorb.potpvp.kt.menu.Button
import net.frozenorb.potpvp.kt.menu.Menu
import net.frozenorb.potpvp.kt.menu.buttons.BackButton
import java.util.HashMap
import org.bukkit.entity.Player

class ViewAllPagesMenu(private val menu: PaginatedMenu) : Menu() {

    init {
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Jump to page"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()

        buttons[0] = BackButton {
            menu.openMenu(player)
        }

        var index = 10
        for (i in 1..menu.getPages(player)) {
            buttons[index++] = JumpToPageButton(i, menu)
            if ((index - 8) % 9 == 0) {
                index += 2
            }
        }

        return buttons
    }

}