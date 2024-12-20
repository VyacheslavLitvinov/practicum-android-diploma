package ru.practicum.android.diploma.ui.search.activity

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.domain.search.models.SearchParams
import ru.practicum.android.diploma.domain.search.models.SearchScreenState
import ru.practicum.android.diploma.ui.favorites.VacancyAdapter
import ru.practicum.android.diploma.ui.search.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private var previousTextInEditText: String = ""
    private var searchJob: Job? = null

    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputEditText = binding.etSearchVacancy
        val clearButton = binding.ibClearQuery
        val foundedVacanciesRecyclerView = binding.rvFoundedVacancies

        setupObserversState()

        inputEditText.addTextChangedListener { s ->
            clearButton.isVisible = !s.isNullOrEmpty()
            binding.ibClearSearch.isVisible = s.isNullOrEmpty()
            searchJob?.cancel()
            if (!s.isNullOrBlank() && s.toString() != previousTextInEditText) {
                hidePlaceholders()
                previousTextInEditText = s.toString()

                searchJob = lifecycleScope.launch {
                    delay(SEARCH_REQUEST_DELAY_IN_MILLISEC)
                    val searchParams = SearchParams(
                        searchQuery = s.toString(),
                        numberOfPage = "0"
                    )
                    viewModel.getVacancies(searchParams)
                }
            }
        }

        inputEditText.doAfterTextChanged { s ->
            if (s?.isNotEmpty() == true && s.toString() != previousTextInEditText && s.isNotBlank()) {
                previousTextInEditText = s.toString()
                val searchParams = SearchParams(
                    searchQuery = s.toString(),
                    numberOfPage = "0"
                )
                viewModel.getVacancies(searchParams)
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText(CLEAR_TEXT)
            inputEditText.requestFocus()
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
            showEmpty()
        }

        val onItemClickListener: (Vacancy) -> Unit = {
            val bundle = Bundle().apply {
                putString("vacancy_id", it.id)
            }
            findNavController().navigate(R.id.action_searchFragment_to_vacancyFragment, bundle)
            // Логика для выполнения по обычному нажатию на элемент
        }
        val onItemLongClickListener: (Vacancy) -> Unit = {
            // Логика для выполнения по долгому нажатию на элемент
        }

        val foundedVacanciesRecyclerViewAdapter = VacancyAdapter(
            onItemClicked = onItemClickListener,
            onLongItemClicked = onItemLongClickListener
        )

        foundedVacanciesRecyclerView.adapter = foundedVacanciesRecyclerViewAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObserversState() {
        viewModel.getSearchScreenStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> showLoading()
                is SearchScreenState.Content -> showContent(state)
                is SearchScreenState.ServerError -> showServerError()
                is SearchScreenState.NetworkError -> showNetworkError()
                is SearchScreenState.NotFound -> showNotFound()
                is SearchScreenState.Error -> showServerError()
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            vacancyCounter.isVisible = false
            rvFoundedVacancies.isVisible = false
            hidePlaceholders()
        }
    }

    private fun showContent(state: SearchScreenState.Content) {
        with(binding) {
            progressBar.isVisible = false
            vacancyCounter.isVisible = true
            rvFoundedVacancies.isVisible = true
            hidePlaceholders()
        }
        val foundedVacanciesRecyclerView = binding.rvFoundedVacancies
        val adapter = foundedVacanciesRecyclerView.adapter as? VacancyAdapter
        adapter?.setData(state.foundedVacancies)
        binding.vacancyCounter.text = "Найдено ${state.foundedVacancies.size} вакансий"
    }

    private fun showServerError() {
        with(binding) {
            progressBar.isVisible = false
            rvFoundedVacancies.isVisible = false
            serverErrorPlaceholder.isVisible = true
        }
    }

    private fun showNetworkError() {
        with(binding) {
            progressBar.isVisible = false
            vacancyCounter.isVisible = false
            rvFoundedVacancies.isVisible = false
            networkErrorPlaceholder.isVisible = true
        }
    }

    private fun showNotFound() {
        with(binding) {
            vacancyCounter.text = NOT_FOUND_VACANCY
            progressBar.isVisible = false
            vacancyCounter.isVisible = true
            rvFoundedVacancies.isVisible = false
            notFoundPlaceholder.isVisible = true
        }
    }

    private fun showEmpty() {
        with(binding) {
            progressBar.isVisible = false
            vacancyCounter.isVisible = false
            rvFoundedVacancies.isVisible = false
            hidePlaceholders()
            placeholderMain.isVisible = true
        }
    }

    private fun hidePlaceholders() {
        with(binding) {
            notFoundPlaceholder.isVisible = false
            networkErrorPlaceholder.isVisible = false
            serverErrorPlaceholder.isVisible = false
            placeholderMain.isVisible = false
        }
    }

    companion object {
        private const val CLEAR_TEXT = ""
        private const val SEARCH_REQUEST_DELAY_IN_MILLISEC = 2000L
        private const val NOT_FOUND_VACANCY = "Таких вакансий нет"
    }

}
