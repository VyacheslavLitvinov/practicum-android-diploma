package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorites.viewmodel.FavoritesViewModel
import ru.practicum.android.diploma.ui.filter.industries.ChoiceIndustryViewModel
import ru.practicum.android.diploma.ui.filter.settings.FilterSettingsViewModel
import ru.practicum.android.diploma.ui.filter.workplace.ChoiceWorkplaceViewModel
import ru.practicum.android.diploma.ui.filter.workplace.country.CountriesViewModel
import ru.practicum.android.diploma.ui.filter.workplace.region.RegionsViewModel
import ru.practicum.android.diploma.ui.search.viewmodel.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.viewmodel.VacancyViewModel

val viewModelModule = module {

    viewModelOf(::SearchViewModel)

    viewModelOf(::VacancyViewModel)

    viewModelOf(::ChoiceIndustryViewModel)

    viewModelOf(::CountriesViewModel)

    viewModelOf(::RegionsViewModel)

    viewModelOf(::FavoritesViewModel)

    viewModelOf(::FilterSettingsViewModel)

    viewModelOf(::ChoiceWorkplaceViewModel)
}
