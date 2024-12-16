package ru.practicum.android.diploma.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    private val _screenStateLiveData = MutableLiveData<SearchState>()

    val screenStateLiveData: LiveData<SearchState>
        get() = _screenStateLiveData

}
