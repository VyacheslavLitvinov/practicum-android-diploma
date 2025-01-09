package ru.practicum.android.diploma.ui.filter.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.filter.FilterSharedPreferencesInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filter
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Region

class FilterSettingsViewModel(
    private val interactor: FilterSharedPreferencesInteractor
) : ViewModel() {

    private val updatedFilter = Filter()

    private val currentFilter: Filter? = interactor.getFilterSharedPrefs()

    private val _counterFilter = MutableLiveData<FilterSettingsState>()
    val counterFilter: LiveData<FilterSettingsState> get() = _counterFilter

    private val applyResetButtonsStateLiveData = MutableLiveData<ApplyResetButtonsState>()
    val getApplyResetButtonsStateLiveData: LiveData<ApplyResetButtonsState> get() = applyResetButtonsStateLiveData

    fun setSelectedCountry(countryForSave: Country?) {
        updatedFilter.country = countryForSave
        setVisibilityOfApplyResetButtons(true)
    }

    fun setSelectedRegion(regionForSave: Region?) {
        updatedFilter.region = regionForSave
    }

    fun setSelectedIndustry(industryForSave: Industry?) {
        updatedFilter.industry = industryForSave
        setVisibilityOfApplyResetButtons(true)
    }

    fun clearIndustry() {
        updatedFilter.industry = null
        interactor.clearIndustry()
    }

    fun clearRegions() {
        updatedFilter.country = null
        updatedFilter.region = null
        interactor.clearCurrentRegion()
    }

    fun currentFilter() {
        processResult(interactor.getFilterSharedPrefs())
    }

    fun saveFilterFromUi(sal: Int?, onlyWithSal: Boolean) {
        if (isFieldsEmpty() && !onlyWithSal && sal == null) {
            interactor.deleteFilterSharedPrefs()
        } else {
            updatedFilter.salary = sal
            updatedFilter.onlyWithSalary = onlyWithSal
            interactor.setFilterSharedPrefs(updatedFilter)
        }
    }

    fun clearFilters() {
        interactor.deleteFilterSharedPrefs()
        renderState(FilterSettingsState.Empty)
    }

    private fun setVisibilityOfApplyResetButtons(visible: Boolean) {
        applyResetButtonsStateLiveData.postValue(ApplyResetButtonsState(visible))
    }

    private fun processResult(result: Filter?) {
        if (result != null) {
            renderState(FilterSettingsState.FilterSettings(result))
            setVisibilityOfApplyResetButtons(true)
        } else {
            renderState(FilterSettingsState.Empty)
            setVisibilityOfApplyResetButtons(false)
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return updatedFilter.country == null && updatedFilter.region == null && updatedFilter.industry == null
    }

    private fun renderState(state: FilterSettingsState) {
        _counterFilter.postValue(state)
    }
}
