package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.FavoriteVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.search.VacanciesRepository
import ru.practicum.android.diploma.data.search.impl.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.db.FavoriteVacanciesRepository

val repositoryModule = module {

    factory<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    factory<FavoriteVacanciesRepository> {
        FavoriteVacanciesRepositoryImpl(get(), get())
    }

}
