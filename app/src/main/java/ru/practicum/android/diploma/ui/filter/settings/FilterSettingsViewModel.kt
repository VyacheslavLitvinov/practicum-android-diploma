package ru.practicum.android.diploma.ui.filter.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.filter.FilterSharedPreferencesInteractor
import ru.practicum.android.diploma.domain.models.Filter

class FilterSettingsViewModel(
    private val interactor: FilterSharedPreferencesInteractor
) : ViewModel() {

    private val _counterFilter = MutableLiveData<FilterSettingsState>()
    val counterFilter: LiveData<FilterSettingsState> get() = _counterFilter

    fun currentFilter() {
        processResult(interactor.getFilterSharedPrefs())
    }

    private fun processResult(result: Filter?) {
        if (result != null) {
            renderState(FilterSettingsState.FilterSettings(result))
        } else {
            renderState(FilterSettingsState.Empty)
        }
    }

    fun saveFilterFromUi(filter: Filter) {
        val isRegionNull = filter.country == null && filter.region == null
        if (isRegionNull && filter.industry == null && filter.salary == null && filter.onlyWithSalary == false) {
            interactor.deleteFilterSharedPrefs()
        } else {
            interactor.setFilterSharedPrefs(filter)
        }
    }

    fun clearFilters() {
        interactor.deleteFilterSharedPrefs()
        renderState(FilterSettingsState.Empty)
    }

    private fun renderState(state: FilterSettingsState) {
        _counterFilter.postValue(state)
    }
}
