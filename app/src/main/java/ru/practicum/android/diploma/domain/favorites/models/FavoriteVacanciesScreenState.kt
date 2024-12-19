package ru.practicum.android.diploma.domain.favorites.models

import ru.practicum.android.diploma.domain.models.Vacancy

sealed class FavoriteVacanciesScreenState {

    data object Error : FavoriteVacanciesScreenState()

    data class Content(val favoriteVacanciesList: List<Vacancy>) : FavoriteVacanciesScreenState()

    data object Empty : FavoriteVacanciesScreenState()

}
