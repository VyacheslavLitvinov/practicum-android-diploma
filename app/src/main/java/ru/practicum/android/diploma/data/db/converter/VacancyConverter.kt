package ru.practicum.android.diploma.data.db.converter

import ru.practicum.android.diploma.data.db.entity.FavoriteVacanciesEntity
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyConverter {

    fun map(vacancy: Vacancy): FavoriteVacanciesEntity {
        return FavoriteVacanciesEntity(
            vacancyId = vacancy.id,
            pictureOfCompanyLogoUri = vacancy.employerLogoUrl,
            titleOfVacancy = vacancy.titleOfVacancy,
            companyName = vacancy.employerName,
            salary = vacancy.salary
        )
    }

    fun map(vacancyEntity: FavoriteVacanciesEntity): Vacancy {
        return Vacancy(
            id = vacancyEntity.vacancyId,
            titleOfVacancy = vacancyEntity.titleOfVacancy,
            regionName = "",
            salary = null,
            employerName = vacancyEntity.companyName,
            employerLogoUrl = vacancyEntity.pictureOfCompanyLogoUri
        )
    }

}
