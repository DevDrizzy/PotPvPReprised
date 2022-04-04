package com.qrakn.morpheus.game.parameter

import com.qrakn.morpheus.game.Game

interface GameParameter {

    fun getDisplayName(): String
    fun getOptions(): List<GameParameterOption>

}