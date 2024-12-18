package ru.practicum.android.diploma.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.db.AppDatabase
import ru.practicum.android.diploma.data.db.converter.VacancyConverter
import ru.practicum.android.diploma.domain.db.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.models.Vacancy

class FavoriteVacanciesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val vacancyConverter: VacancyConverter
) : FavoriteVacanciesRepository {

    override suspend fun insertFavoriteVacancy(vacancyForInsert: Vacancy) {
        appDatabase.favouritesVacancyDao().insertFavoriteVacancy(vacancyConverter.map(vacancyForInsert))
    }

    override suspend fun deleteFavoriteVacancy(vacancyForDelete: Vacancy) {
        appDatabase.favouritesVacancyDao().deleteFavoriteVacancy(vacancyConverter.map(vacancyForDelete))
    }

    override fun getFavoriteVacanciesList(): Flow<List<Vacancy>> {
        return flow {
            val favoriteVacancies = appDatabase.favouritesVacancyDao().getFavoriteVacanciesList()
            emit(favoriteVacancies.map(vacancyConverter::map))
        }
    }

    override fun getFavoriteVacancyById(vacancyId: String): Flow<Vacancy?> {
        return flow {
            val foundedVacancy = appDatabase.favouritesVacancyDao().getFavoriteVacancyById(vacancyId)
            emit(if (foundedVacancy == null) null else vacancyConverter.map(foundedVacancy))
        }
    }
}
