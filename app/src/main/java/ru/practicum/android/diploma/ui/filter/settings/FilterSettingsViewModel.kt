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

    fun clearIndustry() {
        interactor.clearIndustry()
    }

    fun clearRegions() {
        interactor.clearCurrentRegion()
    }

    fun currentFilter() {
        processResult(interactor.getFilterSharedPrefs())
    }

    fun saveFilterFromUi(filter: Filter, region: String?, industry: String?) {
        if (isFieldsEmpty(filter, region, industry) &&
            (filter.onlyWithSalary == false || filter.onlyWithSalary == null)) {
            interactor.deleteFilterSharedPrefs()
        } else {
            interactor.setFilterSharedPrefs(filter)
        }
    }

    fun clearFilters() {
        interactor.deleteFilterSharedPrefs()
        renderState(FilterSettingsState.Empty)
    }

    private fun processResult(result: Filter?) {
        if (result != null) {
            renderState(FilterSettingsState.FilterSettings(result))
        } else {
            renderState(FilterSettingsState.Empty)
        }
    }

    private fun isFieldsEmpty(filter: Filter, region: String?, industry: String?): Boolean {
        return region.isNullOrEmpty() && industry.isNullOrEmpty() && filter.salary == null
    }

    private fun renderState(state: FilterSettingsState) {
        _counterFilter.postValue(state)
    }
}
