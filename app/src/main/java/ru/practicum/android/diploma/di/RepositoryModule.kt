package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.FavoriteVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.search.VacanciesRepository
import ru.practicum.android.diploma.data.search.impl.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.db.FavoriteVacanciesRepository
import ru.practicum.android.diploma.data.vacancy.VacancyRepository
import ru.practicum.android.diploma.data.vacancy.impl.VacancyRepositoryImpl

val repositoryModule = module {

    factory<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    factory<FavoriteVacanciesRepository> {
        FavoriteVacanciesRepositoryImpl(get(), get())
    }

    single<VacancyRepository> {
        VacancyRepositoryImpl(get())
    }

}
