package net.frozenorb.potpvp.kt.menu.pagination

import net.frozenorb.potpvp.kt.menu.Button
import net.frozenorb.potpvp.kt.menu.Menu
import org.bukkit.entity.Player
import java.util.HashMap

abstract class PaginatedMenu : Menu() {

    internal var page: Int = 1

    override fun getTitle(player: Player): String {
        return getPrePaginatedTitle(player) + " - " + page + "/" + getPages(player)
    }

    fun modPage(player: Player, mod: Int) {
        page += mod
        buttons.clear()
        openMenu(player)
    }

    internal fun getPages(player: Player): Int {
        val buttonAmount = getAllPagesButtons(player).size
        return if (buttonAmount == 0) {
            1
        } else Math.ceil(buttonAmount / getMaxItemsPerPage(player).toDouble()).toInt()
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val minIndex = ((page - 1) * getMaxItemsPerPage(player).toDouble()).toInt()
        val maxIndex = (page * getMaxItemsPerPage(player).toDouble()).toInt()
        val buttons = HashMap<Int, Button>()
        buttons[0] = PageButton(-1, this)
        buttons[8] = PageButton(1, this)

        for (entry in getAllPagesButtons(player).entries) {
            var ind = entry.key
            if (ind in minIndex until maxIndex) {
                ind -= (getMaxItemsPerPage(player) * (page - 1).toDouble()).toInt() - 9
                buttons[ind] = entry.value
            }
        }

        val global = getGlobalButtons(player)
        if (global != null) {
            for ((key, value) in global) {
                buttons[key] = value
            }
        }

        return buttons
    }

    open fun getMaxItemsPerPage(player: Player): Int {
        return 18
    }

    open fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return null
    }

    abstract fun getPrePaginatedTitle(player: Player): String

    abstract fun getAllPagesButtons(player: Player): Map<Int, Button>

}