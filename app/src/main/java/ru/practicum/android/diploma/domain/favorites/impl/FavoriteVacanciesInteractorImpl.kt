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

    override fun getFavoriteVacancyById(vacancyId: String): Flow<Vacancy?> {
        return favoriteVacanciesRepository.getFavoriteVacancyById(vacancyId)
    }

    override suspend fun insertVacancyInDb(vacancyForInsert: Vacancy) {
        favoriteVacanciesRepository.insertFavoriteVacancy(vacancyForInsert)
    }

    override suspend fun deleteVacancyFromDb(vacancyForDelete: Vacancy) {
        favoriteVacanciesRepository.deleteFavoriteVacancy(vacancyForDelete)
    }

}
