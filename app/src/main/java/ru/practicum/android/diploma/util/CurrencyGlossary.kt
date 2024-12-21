package ru.practicum.android.diploma.util

object CurrencyGlossary {

    private val currencyCodesAndTheirSymbols = mapOf(
        "AZN" to "₼",
        "BYR" to "Br",
        "EUR" to "€",
        "GEL" to "₾",
        "KGS" to "⃀",
        "KZT" to "₸",
        "RUR" to "₽",
        "UAH" to "₴",
        "USD" to "$",
        "UZS" to "Soʻm"
    )

    fun getCorrectSymbolOfCurrencyByCode(currencyCode: String): String? {
        return currencyCodesAndTheirSymbols[currencyCode]
    }

}
