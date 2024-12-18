package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.favorites.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.favorites.impl.FavoriteVacanciesInteractorImpl
import ru.practicum.android.diploma.domain.search.SearchInteractor
import ru.practicum.android.diploma.domain.search.impl.SearchInteractorImpl

val interactorModule = module {

    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<FavoriteVacanciesInteractor> {
        FavoriteVacanciesInteractorImpl(get())
    }

}
