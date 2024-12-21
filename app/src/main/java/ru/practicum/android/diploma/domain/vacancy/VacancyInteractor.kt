package ru.practicum.android.diploma.domain.vacancy

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface VacancyInteractor {

    fun getVacancyId(id: String): Flow<Pair<Vacancy?, String?>>

}
