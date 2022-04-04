package net.frozenorb.potpvp.kt.menu.buttons

import net.frozenorb.potpvp.kt.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.ArrayList
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class BooleanButton(private val confirm: Boolean, private val callback: (Boolean) -> Unit) : Button() {

    override fun getName(player: Player): String {
        return if (confirm) "§aConfirm" else "§cCancel"
    }

    override fun getDescription(player: Player): List<String> {
        return ArrayList()
    }

    override fun getDamageValue(player: Player): Byte {
        return (if (this.confirm) 5 else 14).toByte()
    }

    override fun getMaterial(player: Player): Material {
        return Material.WOOL
    }

    override fun clicked(player: Player, i: Int, clickType: ClickType, view: InventoryView) {
        if (confirm) {
            playSuccess(player)
        } else {
            playFail(player)
        }

        player.closeInventory()

        callback.invoke(confirm)
    }

}