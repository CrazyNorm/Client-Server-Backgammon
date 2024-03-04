package com.example.dominobackgammonclient.ui

data class UIState(
    var colourScheme: Int = 1,
    // connection
    var connecting: Boolean = false,
    var connected: Boolean = false,
    var connectionFailed: Boolean = false,
    var playerName: String = "",
    val nameDefault: String = "Player",
    var opponentName: String = "",
    // waiting for server messages
    var started: Boolean = false,
    var waiting: Boolean = false,
    // game end conditions
    var gameOver: Boolean = false,
    var winType: Int = 0,
    var clientWin: Boolean = false,
    var opponentWin: Boolean = false,
    var clientDisconnect: Boolean = false,
    var opponentDisconnect: Boolean = false
)