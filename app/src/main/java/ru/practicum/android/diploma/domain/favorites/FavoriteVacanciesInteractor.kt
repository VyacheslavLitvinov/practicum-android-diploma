package ru.practicum.android.diploma.domain.favorites

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface FavoriteVacanciesInteractor {

    fun getFavoriteVacanciesList(): Flow<List<Vacancy>>

}
