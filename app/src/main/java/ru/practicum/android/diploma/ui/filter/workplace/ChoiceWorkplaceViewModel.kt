package ru.practicum.android.diploma.ui.filter.workplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.filter.FilterSharedPreferencesInteractor
import ru.practicum.android.diploma.domain.models.Filter

class ChoiceWorkplaceViewModel(
    private val interactor: FilterSharedPreferencesInteractor
) : ViewModel() {

    private val choiceWorkplaceScreenState = MutableLiveData<ChoiceWorkplaceScreenState>()
    val getChoiceWorkplaceScreenStateLiveData: LiveData<ChoiceWorkplaceScreenState> get() = choiceWorkplaceScreenState

    fun getFilter() {
        val currentFilter = interactor.getFilterSharedPrefs()
        choiceWorkplaceScreenState.postValue(
            ChoiceWorkplaceScreenState(currentFilter?.country?.name, currentFilter?.region?.name)
        )
    }

    fun setFilter(filter: Filter) {
        interactor.setFilterSharedPrefs(filter)
    }

    fun clearRegion(filter: Filter) {
        interactor.clearRegions(filter)
    }
}
