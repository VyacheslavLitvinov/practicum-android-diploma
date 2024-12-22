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
            salary = vacancy.salary,
            city = vacancy.regionName,
            experience = vacancy.experience,
            employmentType = vacancy.employmentType,
            scheduleType = vacancy.scheduleType,
            keySkills = vacancy.keySkills,
            vacancyDescription = vacancy.description,
            vacancyUrl = vacancy.alternateUrl
        )
    }

    fun map(vacancyEntity: FavoriteVacanciesEntity): Vacancy {
        return Vacancy(
            id = vacancyEntity.vacancyId,
            titleOfVacancy = vacancyEntity.titleOfVacancy,
            regionName = vacancyEntity.city,
            salary = vacancyEntity.salary,
            employerName = vacancyEntity.companyName,
            employerLogoUrl = vacancyEntity.pictureOfCompanyLogoUri,
            experience = vacancyEntity.experience,
            employmentType = vacancyEntity.employmentType,
            scheduleType = vacancyEntity.scheduleType,
            keySkills = vacancyEntity.keySkills,
            description = vacancyEntity.vacancyDescription,
            alternateUrl = vacancyEntity.vacancyUrl
        )
    }

}
