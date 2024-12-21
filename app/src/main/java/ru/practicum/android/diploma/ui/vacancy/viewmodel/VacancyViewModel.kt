package ru.practicum.android.diploma.ui.vacancy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.domain.vacancy.VacancyInteractor

class VacancyViewModel(
    application: Application,
    private val interactor: VacancyInteractor,
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor
) : AndroidViewModel(application) {

    private var currentVacancy: Vacancy? = null
    private var currentVacancyId: String? = null

    private val vacancyScreenStateLiveData = MutableLiveData<VacancyState>()
    private val favoriteVacancyButtonStateLiveData = MutableLiveData<FavoriteVacancyButtonState>()

    val getVacancyScreenStateLiveData: LiveData<VacancyState> = vacancyScreenStateLiveData

    val getFavoriteVacancyButtonStateLiveData: LiveData<FavoriteVacancyButtonState> = favoriteVacancyButtonStateLiveData

    fun getVacancyResources(id: String) {
        renderState(VacancyState.Loading)
        currentVacancyId = id
        viewModelScope.launch {
            interactor.getVacancyId(id).collect { pair ->
                processResult(pair.first, pair.second)
                currentVacancy = pair.first
            }
        }
    }

    fun insertVacancyInFavorites() {
        viewModelScope.launch {
            if (currentVacancy != null) {
                favoriteVacanciesInteractor.insertVacancyInDb(currentVacancy!!)
            }
        }
        favoriteVacancyButtonStateLiveData.postValue(FavoriteVacancyButtonState.VacancyIsFavorite)
    }

    fun deleteVacancyFromFavorites() {
        viewModelScope.launch {
            if (currentVacancy != null) {
                favoriteVacanciesInteractor.deleteVacancyFromDb(currentVacancy!!)
            }
        }
        favoriteVacancyButtonStateLiveData.postValue(FavoriteVacancyButtonState.VacancyIsNotFavorite)
    }

    private fun processResult(vacancy: Vacancy?, errorMessage: String?) {
        if (vacancy != null) {
            renderState(VacancyState.Content(vacancy))

            viewModelScope.launch {
                favoriteVacanciesInteractor
                    .getFavoriteVacancyById(vacancy.id)
                    .collect { foundedVacancy ->
                        favoriteVacancyButtonStateLiveData.postValue(
                            if (foundedVacancy == null) FavoriteVacancyButtonState.VacancyIsNotFavorite
                            else FavoriteVacancyButtonState.VacancyIsFavorite
                        )
                    }
            }
        } else {
            getVacancyInfoFromDb(currentVacancyId!!, errorMessage!!)
        }
    }

    private fun generateErrorState(errorMessage: String) {
        when (errorMessage) {
            "Network Error" -> {
                renderState(VacancyState.NetworkError)
            }

            "Bad Request" -> {
                renderState(VacancyState.BadRequest)
            }

            "Not Found" -> {
                renderState(VacancyState.Empty)
            }

            "Unknown Error" -> {
                renderState(VacancyState.ServerError)
            }

            "Server Error" -> {
                renderState(VacancyState.ServerError)
            }
        }
    }

    private fun renderState(state: VacancyState) {
        vacancyScreenStateLiveData.postValue(state)
    }

    private fun getVacancyInfoFromDb(savedVacancyId: String, errorMessage: String) {
        viewModelScope.launch {
            favoriteVacanciesInteractor
                .getFavoriteVacancyById(savedVacancyId)
                .collect { foundedVacancy ->
                    if (foundedVacancy != null) {
                        renderState(VacancyState.Content(foundedVacancy))
                        favoriteVacancyButtonStateLiveData.postValue(FavoriteVacancyButtonState.VacancyIsFavorite)
                    } else {
                        generateErrorState(errorMessage)
                        favoriteVacancyButtonStateLiveData.postValue(FavoriteVacancyButtonState.VacancyIsNotFavorite)
                    }
                }
        }
    }

}
