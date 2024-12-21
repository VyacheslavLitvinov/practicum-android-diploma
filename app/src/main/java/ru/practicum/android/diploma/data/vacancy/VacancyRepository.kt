package ru.practicum.android.diploma.data.vacancy

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

interface VacancyRepository {

    fun getVacancyId(id: String): Flow<Resource<Vacancy>>

}
