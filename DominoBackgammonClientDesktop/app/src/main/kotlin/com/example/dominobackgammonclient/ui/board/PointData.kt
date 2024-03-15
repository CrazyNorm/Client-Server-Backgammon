package com.example.dominobackgammonclient.ui.board

import ui.BGColour

data class PointData(
    val count: Int,
    val colour: BGColour = BGColour.WHITE,
    var pointSelected: Boolean = false,
    var pieceSelected: Boolean = false
)
