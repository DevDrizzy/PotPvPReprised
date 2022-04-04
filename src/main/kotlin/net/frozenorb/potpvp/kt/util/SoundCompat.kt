package net.frozenorb.potpvp.kt.util

import org.bukkit.Sound
import org.bukkit.entity.Player
import java.lang.Exception

enum class SoundCompat(
        legacyId: String,
        legacyVolume: Float,
        legacyPitch: Float,
        latestId: String,
        latestVolume: Float,
        latestPitch: Float) {

    NEUTRAL_CLICK(
            "CLICK",
            20.0F,
            1.0F,
            "UI_BUTTON_CLICK",
            1.0F,
            1.0F
    ),
    SUCCESSFUL_CLICK(
            "NOTE_PIANO",
            20.0F,
            15.0F,
            "BLOCK_NOTE_HARP",
            20.0F,
            15.0F
    ),
    FAILED_CLICK(
            "DIG_GRASS",
            20.0F,
            0.1F,
            "BLOCK_GRASS_BREAK",
            20.0F,
            0.1F
    ),
    MESSAGE_RECEIVED(
            "SUCCESSFUL_HIT",
            1.0F,
            0.1F,
            "ENTITY_ARROW_HIT_PLAYER",
            1.0F,
            0.1F
    );

    private var sound: Sound
    private var volume: Float
    private var pitch: Float

    init {
        try {
            sound = Sound.valueOf(legacyId)
            volume = legacyVolume
            pitch = legacyPitch
        } catch (e: Exception) {
            sound = Sound.valueOf(latestId)
            volume = latestVolume
            pitch = latestPitch
        }
    }

    fun playSound(player: Player) {
        player.playSound(player.location, sound, volume, pitch)
    }

}