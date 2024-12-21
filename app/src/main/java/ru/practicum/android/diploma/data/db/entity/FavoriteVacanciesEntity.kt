package ru.practicum.android.diploma.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_vacancies_table")
data class FavoriteVacanciesEntity(
    @PrimaryKey @ColumnInfo(name = "vacancy_id") val vacancyId: String,
    @ColumnInfo(name = "picture_of_company_logo_uri") val pictureOfCompanyLogoUri: String?,
    @ColumnInfo(name = "title_of_vacancy") val titleOfVacancy: String,
    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "salary") val salary: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "experience") val experience: String?,
    @ColumnInfo(name = "employment_type") val employmentType: String?,
    @ColumnInfo(name = "schedule_type") val scheduleType: String?,
    @ColumnInfo(name = "key_skills") val keySkills: String?,
    @ColumnInfo(name = "vacancy_description") val vacancyDescription: String?
)
