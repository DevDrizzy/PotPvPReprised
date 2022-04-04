package com.qrakn.morpheus

import com.qrakn.morpheus.game.GameQueue
import org.bukkit.plugin.java.JavaPlugin

class Morpheus(val plugin: JavaPlugin) {

    init {
        GameQueue.run(plugin)
    }

}