package com.example.dominobackgammonclient.ui

data class UIState(
    var connecting: Boolean = false,
    var connected: Boolean = false,
    var connectionFailed: Boolean = false,
    var started: Boolean = false,
    var waiting: Boolean = false
)