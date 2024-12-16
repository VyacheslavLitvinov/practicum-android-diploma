package ru.practicum.android.diploma.ui.search

import ru.practicum.android.diploma.domain.models.Vacancy

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val vacancy: List<Vacancy>) : SearchState

    object ServerError : SearchState

    object NotFound : SearchState

    object NetworkError : SearchState

}
