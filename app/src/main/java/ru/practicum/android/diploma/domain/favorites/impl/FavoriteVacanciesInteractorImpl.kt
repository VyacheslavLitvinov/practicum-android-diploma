package ru.practicum.android.diploma.domain.favorites.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.db.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.favorites.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.models.Vacancy

class FavoriteVacanciesInteractorImpl(
    private val favoriteVacanciesRepository: FavoriteVacanciesRepository
) : FavoriteVacanciesInteractor {

    override fun getFavoriteVacanciesList(): Flow<List<Vacancy>> {
        return favoriteVacanciesRepository.getFavoriteVacanciesList()
    }

}
