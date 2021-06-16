package net.smartgekko.mygooglemap.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.smartgekko.mygooglemap.model.MapObject
import net.smartgekko.mygooglemap.model.MapRepository

class GoogleMapsViewModel(private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel(), LifecycleObserver {

    fun getLiveData() = liveDataToObserve

    fun getLocationByAddress(searchText: String) = getMapObjectByAddressFromRepository(searchText)

    private fun getMapObjectByAddressFromRepository(address: String) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            fun onObjectReceived(objectRcv: MapObject) {
                liveDataToObserve.postValue(AppState.Success(objectRcv))
            }

            fun onError(t: Throwable) {
                liveDataToObserve.postValue(AppState.Error(t))
            }

            MapRepository.getLocationByAddress(
                address,
                onSuccess = ::onObjectReceived,
                onError = ::onError
            )
        }.start()
    }
}