package ru.practicum.android.diploma.data.search.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import ru.practicum.android.diploma.data.dto.VacancySearchRequest
import ru.practicum.android.diploma.data.dto.VacancySearchResponse
import ru.practicum.android.diploma.data.dto.model.SalaryDto
import ru.practicum.android.diploma.data.dto.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.search.VacanciesRepository
import ru.practicum.android.diploma.domain.NetworkClient
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.domain.search.models.SearchParams
import ru.practicum.android.diploma.util.CurrencyGlossary
import java.io.IOException
import java.util.Locale

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient
) : VacanciesRepository {

    override fun getVacancies(searchParams: SearchParams): Flow<Pair<List<Vacancy>, Long>> {
        return flow {
            val response = networkClient.doRequest(VacancySearchRequest(searchParams))
            when (response.code) {
                RetrofitNetworkClient.HTTP_OK_CODE -> {
                    with(response as VacancySearchResponse) {
                        val listOfFoundedVacancies = items.map {
                            Vacancy(
                                it.id,
                                it.name,
                                it.area.name,
                                getCorrectFormOfSalaryText(it.salary),
                                it.employer.name,
                                it.employer.logoUrls?.original,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                        }
                        emit(Pair(listOfFoundedVacancies, response.found))
                    }
                }
                RetrofitNetworkClient.HTTP_PAGE_NOT_FOUND_CODE -> {
                    throw HttpException(
                        Response.error<Any>(
                            RetrofitNetworkClient.HTTP_PAGE_NOT_FOUND_CODE,
                            ResponseBody.create(null, "Not Found")
                        )
                    )
                }
                RetrofitNetworkClient.HTTP_INTERNAL_SERVER_ERROR_CODE -> {
                    throw HttpException(
                        Response.error<Any>(
                            RetrofitNetworkClient.HTTP_INTERNAL_SERVER_ERROR_CODE,
                            ResponseBody.create(null, "Server Error")
                        )
                    )
                }
                RetrofitNetworkClient.HTTP_BAD_REQUEST_CODE -> {
                    throw HttpException(
                        Response.error<Any>(
                            RetrofitNetworkClient.HTTP_BAD_REQUEST_CODE,
                            ResponseBody.create(null, "Bad Request")
                        )
                    )
                }
                RetrofitNetworkClient.HTTP_CODE_0 -> {
                    throw HttpException(
                        Response.error<Any>(
                            RetrofitNetworkClient.HTTP_CODE_0,
                            ResponseBody.create(null, "Unknown Error")
                        )
                    )
                }
                RetrofitNetworkClient.INTERNET_NOT_CONNECT -> {
                    throw IOException("Network Error")
                }
                else -> {
                    throw HttpException(
                        Response.error<Any>(
                            response.code,
                            ResponseBody.create(null, "Unexpected Error: ${response.code}")
                        )
                    )
                }
            }
        }
    }

    private fun getCorrectFormOfSalaryText(salary: SalaryDto?): String {
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
}
