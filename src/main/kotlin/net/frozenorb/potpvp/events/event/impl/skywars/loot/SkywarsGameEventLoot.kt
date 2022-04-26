package net.frozenorb.potpvp.events.event.impl.skywars.loot

import net.frozenorb.potpvp.events.event.impl.skywars.SkywarsGameEventLogic
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

interface SkywarsGameEventLoot {

    fun getWeapons(tier: SkywarsGameEventLootTier): List<ItemStack>
    fun getArmor(tier: SkywarsGameEventLootTier): List<ItemStack>
    fun getMiscellaneous(tier: SkywarsGameEventLootTier): List<ItemStack>
    fun getBlocks(tier: SkywarsGameEventLootTier): List<ItemStack>

    companion object {
        fun getItems(player: Player, logic: SkywarsGameEventLogic, loot: SkywarsGameEventLoot, block: Block): List<ItemStack> {
            val toReturn = ArrayList<ItemStack>()
            val tier = getTier(logic, block)

            val armor = loot.getArmor(tier)
            val weapons = loot.getWeapons(tier)
            val misc = loot.getMiscellaneous(tier)
            val blocks = loot.getBlocks(tier)
            val amount = Random.nextInt(4) + 5

            for (i in 0..amount) {

                // armor
                if (SkywarsGameEventLootUtils.hasFullArmor(player)) {
                    if (Random.nextInt(5) == 1) {
                        val item = SkywarsGameEventLootUtils.getBetterArmorItem(player, armor.shuffled(), toReturn)
                        if (item != null) {
                            toReturn.add(item)
                        }
                        continue
                    }
                } else {
                    if (Random.nextInt(3) == 1) {
                        val item = SkywarsGameEventLootUtils.getBetterArmorItem(player, armor.shuffled(), toReturn)
                        if (item != null) {
                            toReturn.add(item)
                        }
                        continue
                    }
                }
                // armor

                // sword
                val sword = SkywarsGameEventLootUtils.getSword(player)
                if (sword == null) {
                    if (Random.nextInt(2) == 1) {
                        val item = SkywarsGameEventLootUtils.getBetterSword(player, weapons.shuffled(), toReturn)
                        if (item != null) {
                            toReturn.add(item)
                            continue
                        }
                    }
                } else {
                    if (Random.nextInt(5) == 1) {
                        val item = SkywarsGameEventLootUtils.getBetterSword(player, weapons.shuffled(), toReturn)
                        if (item != null) {
                            toReturn.add(item)
                            continue
                        }
                    }
                }
                // sword

                // blocks
                if (!(SkywarsGameEventLootUtils.containsBlocks(toReturn))) {
                    if (Random.nextBoolean() && blocks.isNotEmpty()) {
                        toReturn.add(blocks.random())
                        continue
                    }
                }
                // blocks

                val item = misc.random()
                if (!(toReturn.contains(item))) {
                    toReturn.add(misc.random())
                }
            }

            return toReturn
        }

        private fun getTier(logic: SkywarsGameEventLogic, block: Block): SkywarsGameEventLootTier { // kinda aids but meh
            var closestSpawnPoint = -1.0
            val chestLocation = block.location.clone()
            chestLocation.y = 0.0

            for (nakedLocation in logic.game.arena.eventSpawns) {
                val location = nakedLocation.clone()
                location.y = 0.0

                if (closestSpawnPoint <= 0) {
                    closestSpawnPoint = location.distance(chestLocation)
                    continue
                }

                val distance = location.distance(chestLocation)
                if (closestSpawnPoint > distance) {
                    closestSpawnPoint = distance
                }
            }

            return when {
                closestSpawnPoint >= 30 -> SkywarsGameEventLootTier.GODLY
                closestSpawnPoint >= 15 -> SkywarsGameEventLootTier.BUFFED
                else -> SkywarsGameEventLootTier.NORMAL
            }
        }
    }

}