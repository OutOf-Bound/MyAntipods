package net.smartgekko.mygooglemap.viewmodel

import net.smartgekko.mygooglemap.model.MapObject

sealed class AppState {
    data class Success(val mapObject: MapObject) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}