package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkplaceFilter(
    val nameRegion: String? = null,
    val nameCountry: String? = null,
) : Parcelable

