package net.frozenorb.potpvp.kt.menu.buttons

import net.frozenorb.potpvp.kt.menu.Button
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import java.util.ArrayList

class BackButton(private val callback: (Player) -> Unit) : Button() {

    override fun getMaterial(player: Player): Material {
        return Material.BED
    }

    override fun getName(player: Player): String {
        return "§cGo back"
    }

    override fun getDescription(player: Player): List<String> {
        return ArrayList()
    }

    override fun clicked(player: Player, i: Int, clickType: ClickType, view: InventoryView) {
        playNeutral(player)
        player.closeInventory()

        callback.invoke(player)
    }

}