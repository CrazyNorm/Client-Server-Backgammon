package com.example.dominobackgammonclient.ui.board

import com.example.dominobackgammonclient.ui.common.BGColour

data class PointData(
    val count: Int,
    val colour: BGColour = BGColour.WHITE,
    var pointSelected: Boolean = false,
    var pieceSelected: Boolean = false
)
