package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorites.FavoritesViewModel
import ru.practicum.android.diploma.ui.search.viewmodel.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {

    viewModelOf(::SearchViewModel)

    viewModel {
        VacancyViewModel(get(), get())
    }

    viewModel {FavoritesViewModel(get(),get())}
}
