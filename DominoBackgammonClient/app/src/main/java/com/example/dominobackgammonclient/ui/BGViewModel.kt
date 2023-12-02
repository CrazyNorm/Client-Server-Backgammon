package com.example.dominobackgammonclient.ui

import androidx.lifecycle.ViewModel
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.ui.common.BGColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BGViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(Game(BGColour.WHITE)) // TODO hard-coded to white
    val gameState: StateFlow<Game> = _gameState.asStateFlow()
}