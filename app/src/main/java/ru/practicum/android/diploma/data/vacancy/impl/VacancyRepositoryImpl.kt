package ru.practicum.android.diploma.data.vacancy.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.dto.VacancyRequest
import ru.practicum.android.diploma.data.dto.VacancyResponse
import ru.practicum.android.diploma.data.dto.model.KeySkillsDto
import ru.practicum.android.diploma.data.dto.model.SalaryDto
import ru.practicum.android.diploma.data.dto.model.VacancyFullItemDto
import ru.practicum.android.diploma.data.dto.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.vacancy.VacancyRepository
import ru.practicum.android.diploma.domain.NetworkClient
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.util.CurrencyGlossary
import ru.practicum.android.diploma.util.Resource
import java.util.Locale

class VacancyRepositoryImpl(
    private val networkClient: NetworkClient
) : VacancyRepository {

    override fun getVacancyId(id: String): Flow<Resource<Vacancy>> {
        return flow {
            val response = networkClient.doRequest(VacancyRequest(id))
            emit(
                when (response.code) {
                    RetrofitNetworkClient.INTERNET_NOT_CONNECT -> Resource.Error("Network Error")
                    RetrofitNetworkClient.HTTP_CODE_0 -> Resource.Error("Unknown Error")
                    RetrofitNetworkClient.HTTP_BAD_REQUEST_CODE -> Resource.Error("Bad Request")
                    RetrofitNetworkClient.HTTP_PAGE_NOT_FOUND_CODE -> Resource.Error("Not Found")
                    RetrofitNetworkClient.HTTP_INTERNAL_SERVER_ERROR_CODE -> Resource.Error("Server Error")
                    RetrofitNetworkClient.HTTP_OK_CODE -> {
                        with(response as VacancyResponse) {
                            Resource.Success(convertToAppEntity(response.items))
                        }
                    }

                    else -> {
                        Resource.Error("Server Error")
                    }
                }
            )
        }
    }

    private fun convertToAppEntity(vacancyForConvert: VacancyFullItemDto): Vacancy {
        return Vacancy(
            id = vacancyForConvert.id,
            titleOfVacancy = vacancyForConvert.name,
            regionName = getAddressOfEmployer(vacancyForConvert),
            salary = getCorrectFormOfSalary(vacancyForConvert.salary),
            employerName = vacancyForConvert.employer.name,
            employerLogoUrl = vacancyForConvert.employer.logoUrls?.original,
            experience = vacancyForConvert.experience.name,
            employmentType = vacancyForConvert.employment.name,
            scheduleType = vacancyForConvert.schedule.name,
            keySkills = convertKeySkillsToString(vacancyForConvert.keySkills),
            description = vacancyForConvert.description
        )
    }

    private fun getCorrectFormOfSalary(salary: SalaryDto?): String {
        return if (salary == null) {
            "Зарплата не указана"
        } else {
            if (salary.from == null && salary.to != null) {
                String.format(
                    Locale.getDefault(),
                    "до %d %s",
                    salary.to,
                    CurrencyGlossary.getCorrectSymbolOfCurrencyByCode(salary.currency!!)
                )
            } else if (salary.from != null && salary.to == null) {
                String.format(
                    Locale.getDefault(),
                    "от %d %s",
                    salary.from,
                    CurrencyGlossary.getCorrectSymbolOfCurrencyByCode(salary.currency!!)
                )
            } else if (salary.from == salary.to) {
                String.format(
                    Locale.getDefault(),
                    "%d %s",
                    salary.to,
                    CurrencyGlossary.getCorrectSymbolOfCurrencyByCode(salary.currency!!)
                )
            } else {
                String.format(
                    Locale.getDefault(),
                    "от %d до %d %s",
                    salary.from,
                    salary.to,
                    CurrencyGlossary.getCorrectSymbolOfCurrencyByCode(salary.currency!!)
                )
            }
        }
    }

    private fun getAddressOfEmployer(vacancy: VacancyFullItemDto): String {
        return if (vacancy.address == null) {
            vacancy.area.name
        } else {
            vacancy.address.raw
        }
    }

    private fun convertKeySkillsToString(keySkills: List<KeySkillsDto>): String? {
        return if (keySkills.isNotEmpty()) {
            keySkills.joinToString(separator = "\n") { itemKey ->
                "• ${itemKey.name.replace(",", ",\n")}"
            }
        } else {
            null
        }
    }

}
