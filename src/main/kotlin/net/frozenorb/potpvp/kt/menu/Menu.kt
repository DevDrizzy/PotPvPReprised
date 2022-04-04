package net.frozenorb.potpvp.kt.menu

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.util.Reflections
import org.apache.commons.lang.StringUtils
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.inventory.Inventory
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.inventory.InventoryHolder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method

abstract class Menu {

    var buttons: ConcurrentHashMap<Int, Button> = ConcurrentHashMap()
    var autoUpdate: Boolean = false
    var updateAfterClick: Boolean = false
    var placeholder: Boolean = false
    var noncancellingInventory: Boolean = false
    var async: Boolean = false
    var manualClose: Boolean = true
    private var staticTitle: String

    constructor() {
        staticTitle = " "
    }

    constructor(title: String) {
        staticTitle = title
    }

    abstract fun getButtons(player: Player): Map<Int, Button>

    open fun getTitle(player: Player): String {
        return staticTitle
    }

    open fun onOpen(player: Player) {}

    open fun onClose(player: Player, manualClose: Boolean) {}

    private fun createInventory(player: Player): Inventory {
        val invButtons = getButtons(player)
        val inv = Bukkit.createInventory(player as InventoryHolder, size(invButtons), ChatColor.translateAlternateColorCodes('&', getTitle(player)))

        for (buttonEntry in invButtons.entries) {
            buttons[buttonEntry.key] = buttonEntry.value
            inv.setItem(buttonEntry.key, buttonEntry.value.getButtonItem(player))
        }

        if (placeholder) {
            val placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, 15.toByte(), " ")

            for (index in 0 until size(invButtons)) {
                if (invButtons[index] == null) {
                    buttons[index] = placeholder
                    inv.setItem(index, placeholder.getButtonItem(player))
                }
            }
        }

        return inv
    }

    fun openMenu(player: Player) {
        if (async) {
            PotPvPSI.getInstance().server.scheduler.runTaskAsynchronously(PotPvPSI.getInstance()) {
                try {
                    asyncLoadResources { successfulLoad ->
                        if (successfulLoad) {
                            val inv = createInventory(player)

                            try {
                                openCustomInventory(player, inv)
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            player.sendMessage("${ChatColor.RED}Couldn't load menu...")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    player.sendMessage("${ChatColor.RED}Couldn't load menu...")
                }
            }
        } else {
            val inv = createInventory(player)

            try {
                openCustomInventory(player, inv)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun openCustomInventory(player: Player, inv: Inventory) {
        if (player.openInventory != null) {
            manualClose = false
            player.closeInventory()
        }

        val entityPlayer = Reflections.getHandle(player)

        if (Bukkit.isPrimaryThread()) {
            if (OPEN_CUSTOM_INVENTORY_METHOD_LEGACY != null) {
                OPEN_CUSTOM_INVENTORY_METHOD_LEGACY.invoke(player, inv, entityPlayer, 0)
            } else {
                OPEN_CUSTOM_INVENTORY_METHOD!!.invoke(player, inv, entityPlayer, getWindowType(inv.size))
            }

            update(player)
        } else {
            PotPvPSI.getInstance().server.scheduler.runTaskLater(PotPvPSI.getInstance(), {
                if (OPEN_CUSTOM_INVENTORY_METHOD_LEGACY != null) {
                    OPEN_CUSTOM_INVENTORY_METHOD_LEGACY.invoke(player, inv, entityPlayer, 0)
                } else {
                    OPEN_CUSTOM_INVENTORY_METHOD!!.invoke(player, inv, entityPlayer, getWindowType(inv.size))
                }

                update(player)
            }, 1L)
        }
    }

    private fun getWindowType(size: Int): String {
        return when (val rows = Math.ceil((size + 1) / 9.0).toInt()) {
            0 -> "minecraft:generic_9x1"
            else -> "minecraft:generic_9x$rows"
        }
    }

    fun update(player: Player) {
        // cancel check
        cancelCheck(player)

        // set open menu reference to this menu
        currentlyOpenedMenus[player.name] = this

        // call abstract onOpen
        onOpen(player)

        val runnable = object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancelCheck(player)
                    currentlyOpenedMenus.remove(player.name)
                }

                if (this@Menu.autoUpdate) {
                    player.openInventory.topInventory.contents = this@Menu.createInventory(player).contents
                }
            }
        }

        runnable.runTaskTimer(PotPvPSI.getInstance(), 6L, 6L)

        checkTasks[player.name] = runnable
    }

    open fun size(buttons: Map<Int, Button>): Int {
        var highest = 0
        for (buttonValue in buttons.keys) {
            if (buttonValue > highest) {
                highest = buttonValue
            }
        }
        return (Math.ceil((highest + 1) / 9.0) * 9.0).toInt()
    }

    fun getSlot(x: Int, y: Int): Int {
        return 9 * y + x
    }

    open fun asyncLoadResources(callback: (Boolean) -> Unit) {}

    open fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        return true
    }

    companion object {
        private val OPEN_CUSTOM_INVENTORY_METHOD_LEGACY: Method? = Reflections.getDeclaredMethod(true, Reflections.CRAFT_HUMAN_ENTITY_CLASS, "openCustomInventory", Inventory::class.java, Reflections.ENTITY_PLAYER_CLASS, Integer.TYPE)
        private val OPEN_CUSTOM_INVENTORY_METHOD: Method? = Reflections.getDeclaredMethod(true, Reflections.CRAFT_HUMAN_ENTITY_CLASS, "openCustomInventory", Inventory::class.java, Reflections.ENTITY_PLAYER_CLASS, String::class.java)

        @JvmStatic var currentlyOpenedMenus: HashMap<String, Menu> = hashMapOf()
        @JvmStatic var checkTasks: HashMap<String, BukkitRunnable> = hashMapOf()

        fun cancelCheck(player: Player) {
            if (checkTasks.containsKey(player.name)) {
                checkTasks.remove(player.name)!!.cancel()
            }
        }

        val BAR = "&7&m${StringUtils.repeat("-", 32)}"
    }

}