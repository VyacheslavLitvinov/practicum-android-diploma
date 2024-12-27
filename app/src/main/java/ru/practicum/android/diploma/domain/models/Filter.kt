package ru.practicum.android.diploma.domain.models

class Filter(
    var country: Country? = null,
    var region: Region? = null,
    var industry: Industry? = null,
    var salary: String? = null,
    var onlyWithSalary: Boolean = false
)
