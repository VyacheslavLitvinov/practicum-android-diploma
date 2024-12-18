package ru.practicum.android.diploma.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.practicum.android.diploma.data.db.entity.FavoriteVacanciesEntity

@Dao
interface FavouritesVacancyDao {

    @Insert(FavoriteVacanciesEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteVacancy(vacancy: FavoriteVacanciesEntity)

    @Query("SELECT * FROM favorite_vacancies_table")
    fun getFavoriteVacanciesList(): List<FavoriteVacanciesEntity>

    @Query("SELECT * FROM favorite_vacancies_table WHERE vacancy_id = :vacancyId")
    fun getFavoriteVacancyById(vacancyId: String): FavoriteVacanciesEntity?

    @Delete(FavoriteVacanciesEntity::class)
    suspend fun deleteFavoriteVacancy(vacancy: FavoriteVacanciesEntity)
}
