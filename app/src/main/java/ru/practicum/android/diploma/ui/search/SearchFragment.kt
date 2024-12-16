package ru.practicum.android.diploma.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    companion object {
        private const val CLEAR_TEXT = ""
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputEditText = binding.etSearchVacancy
        val clearButton = binding.ibClearQuery

        setupObserversState()

        inputEditText.addTextChangedListener(onTextChanged = { s: CharSequence?, _, _, _ ->
            clearButton.isVisible = !s.isNullOrEmpty()
            binding.ibClearSearch.isVisible = s.isNullOrEmpty()
        })

        clearButton.setOnClickListener {
            inputEditText.setText(CLEAR_TEXT)
            inputEditText.requestFocus()
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObserversState() {
        viewModel.screenStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state)
                is SearchState.ServerError -> showServerError()
                is SearchState.NetworkError -> showNetworkError()
                is SearchState.NotFound -> showNotFound()
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            vacancyCounter.isVisible = false
            recycleViewVacancy.isVisible = false
            mainPlaceholder.isVisible = false
            networkErrorPlaceholder.isVisible = false
            notFoundPlaceholder.isVisible = false
            serverErrorPlaceholder.isVisible = false
        }
    }

    private fun showContent(state: SearchState.Content) {
        with(binding) {
            progressBar.isVisible = false
            vacancyCounter.isVisible = true
            recycleViewVacancy.isVisible = true
            mainPlaceholder.isVisible = false
            networkErrorPlaceholder.isVisible = false
            notFoundPlaceholder.isVisible = false
            serverErrorPlaceholder.isVisible = false
        }
//        добавить обновление адаптера после его создания
    }

    private fun showServerError() {
        with(binding) {
            progressBar.isVisible = false
            recycleViewVacancy.isVisible = false
            serverErrorPlaceholder.isVisible = true
        }
    }

    private fun showNetworkError() {
        with(binding) {
            progressBar.isVisible = false
            recycleViewVacancy.isVisible = false
            networkErrorPlaceholder.isVisible = true
        }
    }

    private fun showNotFound() {
        with(binding) {
            progressBar.isVisible = false
            recycleViewVacancy.isVisible = false
            notFoundPlaceholder.isVisible = true
        }
    }

}
