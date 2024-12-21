package ru.practicum.android.diploma.data.vacancy

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.data.dto.model.VacancyFullItemDto
import ru.practicum.android.diploma.data.dto.model.favorites.ShareData
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

interface VacancyRepository {

    fun getVacancyId(id: String): Flow<Resource<VacancyFullItemDto>>
    fun getShareData(id: String): ShareData
    suspend fun isFavorite(id: String): Boolean
    suspend fun insertFavouritesVacancyEntity(vacancy: Vacancy)
    suspend fun deleteFavouritesVacancyEntity(id: String)
}
