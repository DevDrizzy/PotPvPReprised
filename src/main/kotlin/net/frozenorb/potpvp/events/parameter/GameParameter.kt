package net.frozenorb.potpvp.events.parameter

import net.frozenorb.potpvp.events.Game

interface GameParameter {

    fun getDisplayName(): String
    fun getOptions(): List<GameParameterOption>

}