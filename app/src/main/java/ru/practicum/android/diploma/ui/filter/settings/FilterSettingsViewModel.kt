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

    private var updatedFilter = Filter(onlyWithSalary = false)

    private val _counterFilter = MutableLiveData<FilterSettingsState>()
    val counterFilter: LiveData<FilterSettingsState> get() = _counterFilter

    private val applyResetButtonsStateLiveData = MutableLiveData<ApplyResetButtonsState>()
    val getApplyResetButtonsStateLiveData: LiveData<ApplyResetButtonsState> get() = applyResetButtonsStateLiveData

    fun setSelectedCountry(countryForSave: Country?) {
        updatedFilter.country = countryForSave
        if (_counterFilter.value is FilterSettingsState.Empty) {
            checkVisibilityOfButtons()
        }
    }

    fun setSelectedRegion(regionForSave: Region?) {
        updatedFilter.region = regionForSave
    }

    fun setSelectedIndustry(industryForSave: Industry?) {
        updatedFilter.industry = industryForSave
        if (_counterFilter.value is FilterSettingsState.Empty) {
            checkVisibilityOfButtons()
        }
    }

    fun setSelectedOnlyWithSalary(withSalary: Boolean) {
        updatedFilter.onlyWithSalary = withSalary
        if (_counterFilter.value is FilterSettingsState.Empty) {
            checkVisibilityOfButtons()
        }
    }

    fun setSelectedSalary(sal: Int?) {
        updatedFilter.salary = sal
        if (_counterFilter.value is FilterSettingsState.Empty) {
            checkVisibilityOfButtons()
        }
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
        val filterFromSharPref = interactor.getFilterSharedPrefs()
        if (filterFromSharPref != null) {
            updatedFilter = filterFromSharPref
            setVisibilityOfApplyResetButtons(true)
        }
        processResult(filterFromSharPref)
    }

    fun saveFilterFromUi() {
        if (isFieldsEmpty() && isOnlyWithSalaryEmpty() && updatedFilter.salary == null) {
            interactor.deleteFilterSharedPrefs()
        } else {
            interactor.setFilterSharedPrefs(updatedFilter)
        }
    }

    fun clearFilters() {
        interactor.deleteFilterSharedPrefs()
        updatedFilter = Filter()
        renderState(FilterSettingsState.Empty)
        setVisibilityOfApplyResetButtons(false)
    }

    fun checkVisibilityOfButtons() {
        applyResetButtonsStateLiveData.postValue(
            ApplyResetButtonsState(
                updatedFilter.country != null || updatedFilter.region != null || updatedFilter.industry != null ||
                    updatedFilter.salary != null || updatedFilter.onlyWithSalary == true
            )
        )
    }

    private fun isOnlyWithSalaryEmpty(): Boolean {
        return updatedFilter.onlyWithSalary == false || updatedFilter.onlyWithSalary == null
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
