package net.frozenorb.potpvp.kt.util

import org.bukkit.entity.EntityType
import java.util.EnumMap


object EntityUtils {
    private val displayNames: EnumMap<EntityType, String> = EnumMap(EntityType::class.java)
    private var currentFakeEntityId: Int = -1

    @JvmStatic
    fun fakeEntityId(): Int {
        return currentFakeEntityId--
    }

    @JvmStatic
    fun getName(type: EntityType): String? {
        return displayNames[type]
    }

    @JvmStatic
    fun parse(input: String): EntityType? {
        for ((key, value) in displayNames) {
            if (value.replace(" ", "").equals(input, ignoreCase = true)) {
                return key
            }
        }
        for (type in EntityType.values()) {
            if (input.equals(type.toString(), ignoreCase = true)) {
                return type
            }
        }
        return null
    }

    init {
        displayNames[EntityType.ARROW] = "Arrow"
        displayNames[EntityType.BAT] = "Bat"
        displayNames[EntityType.BLAZE] = "Blaze"
        displayNames[EntityType.BOAT] = "Boat"
        displayNames[EntityType.CAVE_SPIDER] = "Cave Spider"
        displayNames[EntityType.CHICKEN] = "Chicken"
        displayNames[EntityType.COMPLEX_PART] = "Complex Part"
        displayNames[EntityType.COW] = "Cow"
        displayNames[EntityType.CREEPER] = "Creeper"
        displayNames[EntityType.DROPPED_ITEM] = "Item"
        displayNames[EntityType.EGG] = "Egg"
        displayNames[EntityType.ENDER_CRYSTAL] = "Ender Crystal"
        displayNames[EntityType.ENDER_DRAGON] = "Ender Dragon"
        displayNames[EntityType.ENDER_PEARL] = "Ender Pearl"
        displayNames[EntityType.ENDER_SIGNAL] = "Ender Signal"
        displayNames[EntityType.ENDERMAN] = "Enderman"
        displayNames[EntityType.EXPERIENCE_ORB] = "Experience Orb"
        displayNames[EntityType.FALLING_BLOCK] = "Falling Block"
        displayNames[EntityType.FIREBALL] = "Fireball"
        displayNames[EntityType.FIREWORK] = "Firework"
        displayNames[EntityType.FISHING_HOOK] = "Fishing Rod Hook"
        displayNames[EntityType.GHAST] = "Ghast"
        displayNames[EntityType.GIANT] = "Giant"
        displayNames[EntityType.HORSE] = "Horse"
        displayNames[EntityType.IRON_GOLEM] = "Iron Golem"
        displayNames[EntityType.ITEM_FRAME] = "Item Frame"
        displayNames[EntityType.LEASH_HITCH] = "Lead Hitch"
        displayNames[EntityType.LIGHTNING] = "Lightning"
        displayNames[EntityType.MAGMA_CUBE] = "Magma Cube"
        displayNames[EntityType.MINECART] = "Minecart"
        displayNames[EntityType.MINECART_CHEST] = "Chest Minecart"
        displayNames[EntityType.MINECART_FURNACE] = "Furnace Minecart"
        displayNames[EntityType.MINECART_HOPPER] = "Hopper Minecart"
        displayNames[EntityType.MINECART_MOB_SPAWNER] = "Spawner Minecart"
        displayNames[EntityType.MINECART_TNT] = "TNT Minecart"
        displayNames[EntityType.OCELOT] = "Ocelot"
        displayNames[EntityType.PAINTING] = "Painting"
        displayNames[EntityType.PIG] = "Pig"
        displayNames[EntityType.PIG_ZOMBIE] = "Zombie Pigman"
        displayNames[EntityType.PLAYER] = "Player"
        displayNames[EntityType.PRIMED_TNT] = "TNT"
        displayNames[EntityType.SHEEP] = "Sheep"
        displayNames[EntityType.SILVERFISH] = "Silverfish"
        displayNames[EntityType.SKELETON] = "Skeleton"
        displayNames[EntityType.SLIME] = "Slime"
        displayNames[EntityType.SMALL_FIREBALL] = "Fireball"
        displayNames[EntityType.SNOWBALL] = "Snowball"
        displayNames[EntityType.SNOWMAN] = "Snowman"
        displayNames[EntityType.SPIDER] = "Spider"
        displayNames[EntityType.SPLASH_POTION] = "Potion"
        displayNames[EntityType.SQUID] = "Squid"
        displayNames[EntityType.THROWN_EXP_BOTTLE] = "Experience Bottle"
        displayNames[EntityType.UNKNOWN] = "Custom"
        displayNames[EntityType.VILLAGER] = "Villager"
        displayNames[EntityType.WEATHER] = "Weather"
        displayNames[EntityType.WITCH] = "Witch"
        displayNames[EntityType.WITHER] = "Wither"
        displayNames[EntityType.WITHER_SKULL] = "Wither Skull"
        displayNames[EntityType.WOLF] = "Wolf"
        displayNames[EntityType.ZOMBIE] = "Zombie"
    }
}