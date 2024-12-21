package ru.practicum.android.diploma.domain.favorites

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface FavoriteVacanciesInteractor {

    fun getFavoriteVacanciesList(): Flow<List<Vacancy>>

    fun getFavoriteVacancyById(vacancyId: String): Flow<Vacancy?>

    suspend fun insertVacancyInDb(vacancyForInsert: Vacancy)

    suspend fun deleteVacancyFromDb(vacancyForDelete: Vacancy)

}
