package com.example.sensor.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baidu.location.BDLocation
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.sug.SuggestionResult
import com.baidu.mapapi.search.sug.SuggestionSearch
import com.baidu.mapapi.search.sug.SuggestionSearchOption

class MapViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val suggestionSearch = SuggestionSearch.newInstance()

    private var searchCallback: SuggestSearchCallback? = null

    private var startInfo: SuggestionResult.SuggestionInfo? = null

    private var endInfo: SuggestionResult.SuggestionInfo? = null

    private var bdLocation: BDLocation? = null

    var isStart: Boolean = false

    fun getEndInfo(): SuggestionResult.SuggestionInfo? {
        return endInfo
    }

    fun getStartInfo(): SuggestionResult.SuggestionInfo? {
        return startInfo
    }

    fun getBDLocation(): BDLocation? {
        return bdLocation
    }

    fun getStartPlanNode(): PlanNode? {
        return if (startInfo != null) {
            PlanNode.withLocation(startInfo!!.getPt())
        } else {
            if(bdLocation != null) {
                PlanNode.withLocation(LatLng(bdLocation!!.latitude, bdLocation!!.longitude))
            } else {
                null
            }
        }
    }

    fun getEndPlanNode(): PlanNode? {
        return if (endInfo != null) {
            PlanNode.withLocation(endInfo!!.getPt())
        } else {
            null
        }
    }

    init {
        suggestionSearch.setOnGetSuggestionResultListener {result ->
            Log.e("Maizi", "setOnGetSuggestionResultListener - result = ${result.allSuggestions}")
            searchCallback?.onResult(result.allSuggestions)
        }
    }

    fun setLocation(location: BDLocation) {
        this.bdLocation = location
    }

    fun setSuggestionInfo(info: SuggestionResult.SuggestionInfo) {
        if (isStart) {
            this.startInfo = info
        } else {
            this.endInfo = info
        }
    }

    fun search(value: String, city: String, isStart: Boolean, callback: SuggestSearchCallback) {
        this.isStart = isStart
        this.searchCallback = callback
        suggestionSearch.requestSuggestion(
            SuggestionSearchOption().citylimit(true)
                .city(city)
                .keyword(value)
        )
    }

    fun onDestroy() {
        suggestionSearch.destroy()
    }

}

interface SuggestSearchCallback {

    fun onResult(result: MutableList<SuggestionResult.SuggestionInfo>?)
}