package ru.practicum.android.diploma.ui.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorites.FavoriteVacanciesInteractor

class FavoritesViewModel(
    private val favoriteVacanciesInteractor: FavoriteVacanciesInteractor
) : ViewModel() {

    private val favoritesScreenStateLiveData =
        MutableLiveData<FavoriteVacanciesScreenState>()

    fun getFavoritesScreenStateLiveData(): LiveData<FavoriteVacanciesScreenState> {
        return favoritesScreenStateLiveData
    }

    fun getFavoriteVacancies() {
        viewModelScope.launch {
            try {
                favoriteVacanciesInteractor.getFavoriteVacanciesList().collect { favoriteVacancies ->
                    favoritesScreenStateLiveData.postValue(
                        if (favoriteVacancies.isEmpty()) FavoriteVacanciesScreenState.Empty
                        else FavoriteVacanciesScreenState.Content(favoriteVacancies)
                    )
                }
            } catch (someRoomException: Exception) {
                favoritesScreenStateLiveData.postValue(FavoriteVacanciesScreenState.Error)
            }
        }
    }

}
