package net.frozenorb.potpvp.kt.menu.pagination

import net.frozenorb.potpvp.kt.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.ArrayList
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PageButton(private val mod: Int, private val menu: PaginatedMenu) : Button() {

    override fun clicked(player: Player, i: Int, clickType: ClickType, view: InventoryView) {
        when {
            clickType == ClickType.RIGHT -> {
                ViewAllPagesMenu(menu).openMenu(player)
                playNeutral(player)
            }
            hasNext(player) -> {
                menu.modPage(player, mod)
                playNeutral(player)
            }
            else -> playFail(player)
        }
    }

    private fun hasNext(player: Player): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getName(player: Player): String {
        if (!this.hasNext(player)) {
            return if (this.mod > 0) "§7Last page" else "§7First page"
        }

        val str = "(§e" + (menu.page + mod) + "/§e" + menu.getPages(player) + "§a)"
        return (if (this.mod > 0) "§a\u27f6" else "§c\u27f5") + str
    }

    override fun getDescription(player: Player): List<String> {
        return ArrayList()
    }

    override fun getDamageValue(player: Player): Byte {
        return (if (this.hasNext(player)) 11 else 7).toByte()
    }

    override fun getMaterial(player: Player): Material {
        return Material.CARPET
    }

}