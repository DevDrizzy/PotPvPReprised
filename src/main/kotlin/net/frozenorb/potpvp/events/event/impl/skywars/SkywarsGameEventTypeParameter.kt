package net.frozenorb.potpvp.events.event.impl.skywars

import net.frozenorb.potpvp.events.event.impl.skywars.loot.SkywarsGameEventLoot
import net.frozenorb.potpvp.events.event.impl.skywars.loot.SkywarsGameEventLootTier
import net.frozenorb.potpvp.events.parameter.GameParameter
import net.frozenorb.potpvp.events.parameter.GameParameterOption
import net.frozenorb.potpvp.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

object SkywarsGameEventTypeParameter : GameParameter {

    private const val NAME = "Type"
    private val options = listOf(Classic, PotPvP)

    override fun getDisplayName(): String {
        return NAME
    }

    override fun getOptions(): List<GameParameterOption> {
        return options
    }

    interface SkywarsGameEventTypeOption : GameParameterOption {
        fun getLoot(): SkywarsGameEventLoot
    }

    object Classic : SkywarsGameEventTypeOption {

        private const val NAME = "Classic"
        private val icon = ItemStack(Material.STONE_SWORD)

        private val loot = object : SkywarsGameEventLoot {
            override fun getWeapons(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.add(ItemStack(Material.STONE_SWORD))
                        toReturn.add(ItemStack(Material.WOOD_SWORD))
                        toReturn.add(ItemStack(Material.GOLD_SWORD))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.add(ItemStack(Material.IRON_SWORD))
                        toReturn.add(ItemStack(Material.DIAMOND_SWORD))
                        toReturn.add(ItemBuilder.of(Material.STONE_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build())
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        toReturn.add(ItemBuilder.of(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build())
                    }
                }

                return toReturn
            }

            override fun getArmor(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.LEATHER_HELMET),
                                ItemStack(Material.LEATHER_CHESTPLATE),
                                ItemStack(Material.LEATHER_LEGGINGS),
                                ItemStack(Material.LEATHER_BOOTS),

                                ItemStack(Material.CHAINMAIL_HELMET),
                                ItemStack(Material.CHAINMAIL_CHESTPLATE),
                                ItemStack(Material.CHAINMAIL_LEGGINGS),
                                ItemStack(Material.CHAINMAIL_BOOTS),
                                
                                ItemStack(Material.GOLD_HELMET),
                                ItemStack(Material.GOLD_CHESTPLATE),
                                ItemStack(Material.GOLD_LEGGINGS),
                                ItemStack(Material.GOLD_BOOTS),

                                ItemStack(Material.IRON_HELMET),
                                ItemStack(Material.IRON_CHESTPLATE),
                                ItemStack(Material.IRON_LEGGINGS),
                                ItemStack(Material.IRON_BOOTS)
                        ))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.DIAMOND_HELMET),
                                ItemStack(Material.DIAMOND_CHESTPLATE),
                                ItemStack(Material.DIAMOND_LEGGINGS),
                                ItemStack(Material.DIAMOND_BOOTS)
                        ))
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        toReturn.add(ItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                        toReturn.add(ItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                        toReturn.add(ItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                        toReturn.add(ItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    }

                }

                return toReturn
            }

            override fun getMiscellaneous(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.IRON_PICKAXE),
                                ItemStack(Material.IRON_AXE),
                                ItemStack(Material.BAKED_POTATO, 16),
                                ItemStack(Material.COOKED_BEEF, 16),
                                ItemStack(Material.FISHING_ROD),
                                ItemStack(Material.BOW),
                                ItemStack(Material.ARROW, Random.nextInt(5) + 5),
                                ItemStack(Material.FLINT_AND_STEEL),
                                ItemStack(Material.WATER_BUCKET),
                                ItemStack(Material.LAVA_BUCKET)
                        ))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.addAll(arrayOf(
                                ItemBuilder.of(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 1).build(),
                                ItemBuilder.of(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 1).build(),

                                ItemStack(Material.ENDER_PEARL, 1),
                                ItemStack(Material.GOLDEN_APPLE, Random.nextInt(3) + 1),
                                ItemStack(Material.ARROW, Random.nextInt(10) + 5)
                        ))
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.GOLDEN_APPLE, Random.nextInt(3) + 1),
                                ItemStack(Material.ENDER_PEARL, 2),
                                ItemStack(Material.TNT, Random.nextInt(5) + 3),
                                ItemBuilder.of(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).build()
                        ))
                    }
                }

                return toReturn
            }

            override fun getBlocks(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.add(ItemStack(Material.WOOD, Random.nextInt(10) + 10))
                        toReturn.add(ItemStack(Material.COBBLESTONE, Random.nextInt(10) + 10))
                        toReturn.add(ItemStack(Material.STONE, Random.nextInt(10) + 10))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.add(ItemStack(Material.WOOD, Random.nextInt(20) + 12))
                        toReturn.add(ItemStack(Material.COBBLESTONE, Random.nextInt(20) + 12))
                    }
                }

                return toReturn
            }

        }

        override fun getDisplayName(): String {
            return NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }

        override fun getLoot(): SkywarsGameEventLoot {
            return loot
        }

    }

    object PotPvP : SkywarsGameEventTypeOption {

        private const val NAME = "PotPvP"
        private val icon = ItemStack(Material.POTION, 1, 16421)

        private val loot = object : SkywarsGameEventLoot {
            override fun getWeapons(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.add(ItemStack(Material.DIAMOND_SWORD))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.add(ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build())
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        toReturn.add(ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).build())
                        toReturn.add(ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.FIRE_ASPECT, 1).build())
                    }
                }

                return toReturn
            }

            override fun getArmor(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.DIAMOND_HELMET),
                                ItemStack(Material.DIAMOND_CHESTPLATE),
                                ItemStack(Material.DIAMOND_LEGGINGS),
                                ItemStack(Material.DIAMOND_BOOTS)
                        ))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        val armor = getArmor(SkywarsGameEventLootTier.NORMAL)

                        armor.forEach { it.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1) }

                        toReturn.addAll(armor)
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        val armor = getArmor(SkywarsGameEventLootTier.NORMAL)

                        armor.forEach { it.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2) }

                        toReturn.addAll(armor)
                    }

                }

                return toReturn
            }

            override fun getMiscellaneous(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.addAll(arrayOf(
                                ItemStack(Material.DIAMOND_PICKAXE),
                                ItemStack(Material.DIAMOND_AXE),
                                ItemStack(Material.BAKED_POTATO, 16),
                                ItemStack(Material.COOKED_BEEF, 16),
                                ItemStack(Material.GOLDEN_CARROT, 16),
                                ItemStack(Material.FISHING_ROD),
                                ItemBuilder.of(Material.POTION).data(16421).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(2).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(2).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(3).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(4).build(),
                                ItemBuilder.of(Material.POTION).data(8226).build(),
                                ItemBuilder.of(Material.POTION).data(8226).build()
                        ))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.addAll(arrayOf(
                                ItemBuilder.of(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).build(),
                                ItemBuilder.of(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 3).build(),
                                ItemBuilder.of(Material.POTION).data(16421).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(2).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(3).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(4).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(2).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(3).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(4).build(),
                                ItemBuilder.of(Material.POTION).data(8226).build(),
                                ItemBuilder.of(Material.POTION).data(16420).build(),
                                ItemBuilder.of(Material.POTION).data(16426).build(),
                                ItemBuilder.of(Material.POTION).data(8259).build(),
                                ItemStack(Material.ENDER_PEARL, Random.nextInt(2) + 1)
                        ))
                    }

                    SkywarsGameEventLootTier.GODLY -> {
                        toReturn.addAll(arrayOf(
                                ItemBuilder.of(Material.POTION).data(16421).amount(3).build(),
                                ItemBuilder.of(Material.POTION).data(16421).amount(4).build(),
                                ItemBuilder.of(Material.POTION).data(16388).build(),
                                ItemBuilder.of(Material.POTION).data(16458).build(),
                                ItemBuilder.of(Material.POTION).data(8226).build(),
                                ItemStack(Material.TNT, Random.nextInt(4) + 1),
                                ItemStack(Material.ENDER_PEARL, Random.nextInt(4) + 1),
                                ItemStack(Material.GOLDEN_APPLE, 1),
                                ItemStack(Material.GOLDEN_APPLE, 2)
                        ))
                    }
                }

                return toReturn
            }

            override fun getBlocks(tier: SkywarsGameEventLootTier): List<ItemStack> {
                val toReturn = ArrayList<ItemStack>()

                when (tier) {
                    SkywarsGameEventLootTier.NORMAL -> {
                        toReturn.add(ItemStack(Material.WOOD, Random.nextInt(20) + 10))
                        toReturn.add(ItemStack(Material.COBBLESTONE, Random.nextInt(20) + 10))
                    }

                    SkywarsGameEventLootTier.BUFFED -> {
                        toReturn.add(ItemStack(Material.WOOD, 32))
                        toReturn.add(ItemStack(Material.COBBLESTONE, 32))
                    }
                }

                return toReturn
            }

        }

        override fun getDisplayName(): String {
            return NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }

        override fun getLoot(): SkywarsGameEventLoot {
            return loot
        }

    }
}